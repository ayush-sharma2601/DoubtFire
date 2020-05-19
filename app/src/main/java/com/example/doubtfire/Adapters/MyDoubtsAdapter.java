package com.example.doubtfire.Adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doubtfire.Activities.FeedOnClick;
import com.example.doubtfire.Models.ImageModel;
import com.example.doubtfire.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MyDoubtsAdapter extends RecyclerView.Adapter<MyDoubtsAdapter.MDVH> {

    private ArrayList<ImageModel> mDataset;
    FirebaseUser user;
    int code;
    // flag for footer ProgressBar (i.e. last item of list)
    private boolean isLoadingAdded = false;
    DatabaseReference database,db;

    public MyDoubtsAdapter(ArrayList<ImageModel> mDataset,int code) {
        this.mDataset = mDataset;
        this.code = code;
    }

    @NonNull
    @Override
    public MyDoubtsAdapter.MDVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MDVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_doubt_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyDoubtsAdapter.MDVH holder, int position) {
        holder.populate(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addImage(ImageModel image) {
        mDataset.add(0, image);
        Log.i(TAG, "addImage:>>>>>>>>>>>>>>>>added image to adapter " + image.downurl);
//        notifyDataSetChanged();
        notifyItemInserted(mDataset.size()-1);
    }

    public class MDVH extends RecyclerView.ViewHolder {
        ImageView doubtImage;
        TextView doubtStatus,doubtDesc,doubtSubject;
        public MDVH(@NonNull final View itemView) {
            super(itemView);
            doubtImage = itemView.findViewById(R.id.doubt_item_image);
            doubtStatus = itemView.findViewById(R.id.my_doubt_status);
            doubtSubject = itemView.findViewById(R.id.my_doubt_subject);
            doubtDesc = itemView.findViewById(R.id.doubt_desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent expand = new Intent(itemView.getContext(), FeedOnClick.class);
                    expand.putExtra("imagekey",doubtImage.getTag().toString());
//                    expand.putExtra("name",feedPoster.getText().toString());
                    Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>onClick: sent image ID "+doubtImage.getTag().toString());
                    expand.setAction("mydoubt");
                    itemView.getContext().startActivity(expand);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(itemView.getContext(),"long pressed",Toast.LENGTH_SHORT).show();
                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(itemView.getContext());
                    dialog.setBackground(itemView.getContext().getDrawable(R.drawable.feed_back));
                    dialog.setTitle("Options");
                    dialog.setCancelable(true);
                    dialog.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            database = FirebaseDatabase.getInstance().getReference().child("imagesinfo").child(doubtImage.getTag().toString());
                            database.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ImageModel doubt = dataSnapshot.getValue(ImageModel.class);
                                    if (doubt.solutions != 0) {
                                        //remove info from solutions database
                                        db = FirebaseDatabase.getInstance().getReference().child("solutions").child(doubtImage.getTag().toString());
                                        db.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i(TAG, "onSuccess: solutions deleted");
                                            }
                                        });

                                        //remove solutions images from storage
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                        StorageReference imagesRef = storageRef.child("Solutions");
                                        StorageReference userRef = imagesRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        StorageReference imageRef = userRef.child(doubtImage.getTag().toString());
                                        imageRef.delete();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            //remove info from upload database
                            database = FirebaseDatabase.getInstance().getReference().child("imagesinfo").child(doubtImage.getTag().toString());
                            database.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "onSuccess: Deleted Successfully");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(itemView.getContext(), "Deletion Unsuccessfull", Toast.LENGTH_SHORT).show();
                                }
                            });
                            //remove image from upload storage
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                            StorageReference imagesRef = storageRef.child("Doubts");
                            StorageReference userRef = imagesRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            StorageReference imageRef = userRef.child(doubtImage.getTag().toString());
                            imageRef.delete();


                        }
                    });
                    dialog.setPositiveButton("Toggle Solve Status", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            database = FirebaseDatabase.getInstance().getReference().child("imagesinfo").child(doubtImage.getTag().toString());
                            database.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ImageModel mydout = dataSnapshot.getValue(ImageModel.class);
                                    mydout.isSolved = (mydout.isSolved.equals("solved")) ? "unsolved" : "solved";
                                    database.setValue(mydout);
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                    dialog.show();
                    return true;
                }
            });
        }

        public void populate(ImageModel imageModel){
            if (imageModel.user != null) {
                doubtSubject.setText(imageModel.subject);
                doubtImage.setTag(imageModel.key);
                doubtDesc.setText(imageModel.description);
                doubtStatus.setText(imageModel.isSolved);
            }
            if(code == 1)
                doubtStatus.setText(imageModel.solutions + " solution(s)");
            Picasso.get().load(imageModel.downurl).resize(200,150).centerCrop().into(doubtImage);
        }
    }
}
