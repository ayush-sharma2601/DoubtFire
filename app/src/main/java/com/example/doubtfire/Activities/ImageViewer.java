package com.example.doubtfire.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doubtfire.Models.SolutionImage;
import com.example.doubtfire.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ImageViewer extends AppCompatActivity {
    DatabaseReference databaseReference;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imageView = findViewById(R.id.image_big);
       String key = getIntent().getStringExtra("key");
       String magnifierKey = getIntent().getStringExtra("magnifier");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("solutions").child(key).child(magnifierKey);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SolutionImage image = dataSnapshot.getValue(SolutionImage.class);
                Picasso.get().load(image.downurl).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
