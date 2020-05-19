package com.example.doubtfire.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doubtfire.Adapters.SolutionAdapter;
import com.example.doubtfire.Models.ImageModel;
import com.example.doubtfire.Models.SolutionImage;
import com.example.doubtfire.Models.UserModel;
import com.example.doubtfire.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedOnClick extends AppCompatActivity {
    String key;
    String name = "default";
    DatabaseReference database,solutionbase;
    RecyclerView solutionsRV;
    SolutionAdapter solutionAdapter;
    FirebaseUser fbUser;
    ArrayList<SolutionImage> solutionSet = new ArrayList<>();
    TextView posterTV,isSolvedTV,descTV,solutionTV;
    ImageView imageView,phoneBtn;
    Button giveAnswer;
    LinearLayout status;
    RelativeLayout head;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_on_click);
        init();

        giveAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedOnClick.this,UploadSolutionActivity.class);
                intent.putExtra("doubtkey",key);
                startActivity(intent);
            }
        });

        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FeedOnClick.this,number,Toast.LENGTH_LONG).show();
            }
        });

        solutionbase = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            Toast.makeText(FeedOnClick.this,"No User Detected",Toast.LENGTH_SHORT).show();
        }
        Query imageQuery = solutionbase.child("solutions").child(key).orderByKey().limitToFirst(100);
        imageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final SolutionImage solnimage = dataSnapshot.getValue(SolutionImage.class);
                solutionbase.child("users").child(solnimage.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel user = dataSnapshot.getValue(UserModel.class);
//                        Toast.makeText(FeedOnClick.this,user.name,Toast.LENGTH_LONG).show();
                        solnimage.user = user;
                        solutionAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                solutionAdapter.addSolutionImage(solnimage);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void init() {
        key = getIntent().getStringExtra("imagekey");
        name = getIntent().getStringExtra("name");
//        Toast.makeText(this,name,Toast.LENGTH_LONG).show();
        database = FirebaseDatabase.getInstance().getReference().child("imagesinfo").child(key);
        posterTV = findViewById(R.id.feed_poster);
        isSolvedTV = findViewById(R.id.is_solved);
        solutionsRV = findViewById(R.id.feed_expanded_rv);
        descTV = findViewById(R.id.feed_expanded_desc);
        imageView = findViewById(R.id.feed_expanded_image);
        giveAnswer = findViewById(R.id.give_answer);
        head = findViewById(R.id.head);
        solutionTV = findViewById(R.id.solutions);
        status = findViewById(R.id.status);
        phoneBtn= findViewById(R.id.phone_btn);
        solutionsRV.setLayoutManager(new LinearLayoutManager(this));
        solutionAdapter = new SolutionAdapter(solutionSet,FeedOnClick.this);
        solutionsRV.setAdapter(solutionAdapter);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ImageModel singlefeed = dataSnapshot.getValue(ImageModel.class);
                if(getIntent().getAction().equals("mydoubt"))
                {
                    head.setVisibility(View.GONE);
                    status.setVisibility(View.GONE);
                }
                posterTV.setText(name);
                isSolvedTV.setText(singlefeed.isSolved);
                descTV.setText(singlefeed.description);
                solutionTV.setText(" Solutions ("+singlefeed.solutions+")");
                Picasso.get().load(singlefeed.downurl).into(imageView);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(singlefeed.userId);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        number = user.number;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FeedOnClick.this,"Failed to load info",Toast.LENGTH_LONG).show();
            }
        });
    }

}
