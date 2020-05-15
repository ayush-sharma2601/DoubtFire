package com.example.doubtfire.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.doubtfire.Models.ImageModel;
import com.example.doubtfire.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class DoubtUploadFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    View view;
    private static final int PERMISSION_REQUEST_CODE = 777;
    private static final int GALLERY_REQUEST_CODE = 107;
    private static final int CAMERA_REQUEST_CODE = 1;
    String uploadSubject = "Subject";
    ImageView uploadSample;
    EditText uploadDesc;
    Button galleryBtn, cameraBtn, uploadBtn;
    FirebaseUser fbUser;
    DatabaseReference database;
    ProgressBar progressBar;
    Spinner spinner;
    Uri uri;
    String mCurrentPhotoPath;
    String[] subjects={"Subject","Maths","Physics","Computer Science","Biology","Electrical Sciences","Chemistry"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.doubt_upload_fragment, container, false);
        init();
        ArrayAdapter subjectAdapter = new ArrayAdapter(getContext(),R.layout.subject_dropdown,subjects);
        subjectAdapter.setDropDownViewResource(R.layout.subject_dropdown);
        spinner.setAdapter(subjectAdapter);
        spinner.setOnItemSelectedListener(this);
        photopermissions();
        return view;
    }


    private void photopermissions() {
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    openGallery();
                } else {
                    EasyPermissions.requestPermissions(getActivity(), "Allow this app to access your storage", PERMISSION_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openCamera();
                } else {
                    EasyPermissions.requestPermissions(getActivity(), "Allow Camera Access", PERMISSION_REQUEST_CODE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE);
                }


            }
        });
    }

    public void init() {
        uploadBtn = view.findViewById(R.id.upload_btn);
        uploadSample = view.findViewById(R.id.upload_sample);
        uploadDesc = view.findViewById(R.id.upload_desc);
        galleryBtn = view.findViewById(R.id.gallery_btn);
        cameraBtn = view.findViewById(R.id.camera_btn);
        progressBar = view.findViewById(R.id.pgbar);
        spinner = view.findViewById(R.id.subject_spinner);
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
    }

    private void openCamera() {
        Log.i("ACTIVITY", "opencamera started");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(view.getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.i(TAG, "openCamera: >>>>>>>>>>>>>>>.. file created");
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "openCamera: >>>>>>>>>>>>>>>>> error occurred while creating file");
//            ...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(view.getContext(),
                        "com.example.doubtfire.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void openGallery() {
        Log.i("ACTIVITY", "opengallery started");
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == GALLERY_REQUEST_CODE) && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Picasso.get().load(data.getData()).into(uploadSample);
            uri = data.getData();
        }
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK ) {


            try {
                            File file = new File(mCurrentPhotoPath);
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(view.getContext().getContentResolver(), Uri.fromFile(file));
                Log.i(TAG, "onActivityResult: >>>>>>>>>>>>>>>>. got bitmap");
                            if (bitmap != null) {
//                        ...Do your stuffs
                                Log.i(TAG, "onActivityResult: >>>>>>>>>>>> bitmap was not null");
                                uploadSample.setImageBitmap(bitmap);
                                uri = getImageUri(getContext(),bitmap);

                }

            } catch (Exception error) {
                error.printStackTrace();
                Log.i(TAG, "onActivityResult: >>>>>>>>>>>>>>>. camera error");
            }
        }


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imagesRef = storageRef.child("Doubts");
                StorageReference userRef = imagesRef.child(fbUser.getUid());
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                final String filename = fbUser.getUid() + "_" + timeStamp;
                StorageReference fileRef = userRef.child(filename);
                final UploadTask uploadTask = fileRef.putFile(uri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    String downloadUrl;
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getMetadata() != null) {
                            if (taskSnapshot.getMetadata().getReference() != null) {
                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadUrl = uri.toString();
                                        Log.i(TAG, "onSuccess: >>>>>>>>>>>>>>"+downloadUrl);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getContext(), "Upload finished!", Toast.LENGTH_SHORT).show();
                                        // save image to database
                                        String description = "";
                                        description = uploadDesc.getText().toString().trim();
//                                        String key = database.child("imagesinfo").push().getKey();
                                        String key = filename;
                                        ImageModel image1 = new ImageModel(key,fbUser.getUid(),downloadUrl,description,uploadSubject);
                                        Log.i(TAG, "onImageSent: "+downloadUrl);
                                        database.child("imagesinfo").child(key).setValue(image1);
                                    }
                                });
                            }
                        }

                    }
                });
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        uploadSubject = subjects[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
