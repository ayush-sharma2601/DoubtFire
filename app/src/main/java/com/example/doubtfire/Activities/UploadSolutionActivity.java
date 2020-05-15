package com.example.doubtfire.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doubtfire.Models.ImageModel;
import com.example.doubtfire.Models.SolutionImage;
import com.example.doubtfire.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UploadSolutionActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 777;
    private static final int GALLERY_REQUEST_CODE = 107;
    private static final int CAMERA_REQUEST_CODE = 123;
    String uploadSubject = "Subject";
    ImageView uploadSample;
    EditText uploadDesc;
    Boolean isImageThere = false;
    Button galleryBtn, cameraBtn, uploadBtn;
    FirebaseUser fbUser;
    DatabaseReference database;
    ProgressBar progressBar;
    Spinner spinner;
    String downloadUrl;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_solution);
        init();
        isImageThere = false;
        takephoto();
    }

    private void takephoto() {
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(UploadSolutionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    openGallery();
                } else {
                    EasyPermissions.requestPermissions(UploadSolutionActivity.this, "Allow this app to access your storage", PERMISSION_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(UploadSolutionActivity.this, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openCamera();
                } else {
                    EasyPermissions.requestPermissions(UploadSolutionActivity.this, "Allow Camera Access", PERMISSION_REQUEST_CODE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE);
                }


            }
        });
    }

    public void init() {
        uploadBtn = findViewById(R.id.upload_btn);
        uploadSample = findViewById(R.id.upload_sample);
        uploadDesc = findViewById(R.id.upload_desc);
        galleryBtn = findViewById(R.id.gallery_btn);
        cameraBtn = findViewById(R.id.camera_btn);
        progressBar = findViewById(R.id.pgbar);
        spinner = findViewById(R.id.subject_spinner);
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
    }

    private void openCamera() {
        Log.i("ACTIVITY", "opencamera started");
        Intent cameraIntet = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntet, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Log.i("ACTIVITY", "opengallery started");
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri= data.getData() ;
        if ((requestCode == GALLERY_REQUEST_CODE) && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Picasso.get().load(data.getData()).into(uploadSample);
            isImageThere = true;
            uri = data.getData();
        }
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            uploadSample.setImageBitmap(bitmap);
            isImageThere = true;
            uri = getImageUri(this,bitmap);
        }
        isImageThere=true;


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imagesRef = storageRef.child("Solutions");
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
                        Toast.makeText(UploadSolutionActivity.this, "Solution Upload failed!\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                        Toast.makeText(UploadSolutionActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();
                                        // save image to database
                                        String description;
                                        if(uploadDesc.getText().toString().isEmpty()){description = "My answer";}
                                        else {description = uploadDesc.getText().toString().trim();}
                                        final String key = getIntent().getStringExtra("doubtkey");
                                        String uniqueKey = filename;
                                        SolutionImage image1 = new SolutionImage(key,fbUser.getUid(),downloadUrl,description,uniqueKey);
                                        Log.i(TAG, "onSolutionsSent: "+downloadUrl);
                                        database.child("solutions").child(key).child(uniqueKey).setValue(image1);
                                        database.child("imagesinfo").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                ImageModel imageModel = dataSnapshot.getValue(ImageModel.class);
                                                imageModel.addedSolution();
                                                database.child("imagesinfo").child(key).setValue(imageModel);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }
                        }

                    }
                });
            }
        });


    }
}
