package com.example.doubtfire.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doubtfire.Adapters.FeedItemAdapter;
import com.example.doubtfire.Models.ImageModel;
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

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FeedContainingFragment extends Fragment {
    View view;
    FirebaseUser fbUser;
    RecyclerView feedRV;
    FeedItemAdapter adapter;
    ArrayList<ImageModel> data = new ArrayList<>();
    ArrayList<ImageModel> populateData = new ArrayList<>();
    DatabaseReference databaseReference;
    LinearLayout feedBack;

    boolean isLoading = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.feed_fragment,container,false);
        feedBack = view.findViewById(R.id.feed_back);
        data.clear();
        //Recycler view work
        feedRV = view.findViewById(R.id.feed_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        feedRV.setLayoutManager(linearLayoutManager);
        feedRV.setItemAnimator(new DefaultItemAnimator());


        //setting up firebase needs
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        Query imageQuery = databaseReference.child("imagesinfo").orderByKey();
        imageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final ImageModel image = dataSnapshot.getValue(ImageModel.class);
                databaseReference.child("users/"+image.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        image.user = user;
                        Log.i(TAG, "onuserAdded >>>>>>>>>>> "+image.user.name);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                data.add(image);
//                Log.i(TAG, "onChildAdded: "+data.size());
//                for(int i=0;(i<3 && i<data.size()) ;i++)
//                {
//                    adapter.addImage(data.get(i));
//                    adapter.notifyDataSetChanged();
//                }
                adapter.addImage(image);
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

        Log.i(TAG, "onDataLoaded >>>>>>>>>>>>>>>>>>  "+data.size());



        adapter = new FeedItemAdapter(populateData,this);
        feedRV.setAdapter(adapter);

//        initScrollListener();

        return view;
    }


    private void initScrollListener() {
        feedRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == populateData.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });


    }


    private void loadMore() {
        populateData.add(null);
        adapter.notifyItemInserted(data.size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                populateData.remove(populateData.size() - 1);
                int scrollPosition = populateData.size();
                adapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 2;

                while ((currentSize - 1 )< nextLimit && (currentSize - 1 )<data.size()) {
                    populateData.add(data.get(currentSize));
                    currentSize++;
                }

//                adapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);


    }

}
