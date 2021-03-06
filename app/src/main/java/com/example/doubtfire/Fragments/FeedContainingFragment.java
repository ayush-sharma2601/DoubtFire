package com.example.doubtfire.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import com.google.android.material.chip.Chip;
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
    RelativeLayout feedBack;
    Chip math,phy,cs,bio,chem,es;
    ArrayList<String> subjectList = new ArrayList<>();
    ArrayList<ImageModel> alldata = new ArrayList<>();

    boolean isLoading = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.feed_fragment,container,false);
        init();

        chipListener();


        //setting up firebase needs
        alldata = loadFromFirebase(subjectList);
        Log.i(TAG, "onCreateView: "+alldata.size());

//        initScrollListener();

        return view;
    }

    private ArrayList<ImageModel> loadFromFirebase(ArrayList<String> subjectList) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        ArrayList<ImageModel> data = new ArrayList<>();
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
                Log.i(TAG, "onChildAdded: "+data.size());
//                for(int i=0;(i<3 && i<data.size()) ;i++)
//                {
//                    adapter.addImage(data.get(i));
//                    adapter.notifyDataSetChanged();
//                }
                if(subjectList.size()==0)
                {
                    adapter.addImage(image);
                }
                else if(subjectList.contains(image.subject))
                {
                    adapter.addImage(image);
                }
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
        return data;
    }

    private void init() {
        feedBack = view.findViewById(R.id.feed_back);
        math = view.findViewById(R.id.math_chip);
        phy = view.findViewById(R.id.phy_chip);
        cs = view.findViewById(R.id.cs_chip);
        bio = view.findViewById(R.id.bio_chip);
        chem = view.findViewById(R.id.chem__chip);
        es = view.findViewById(R.id.es_chip);
//        data.clear();
        //Recycler view work
        feedRV = view.findViewById(R.id.feed_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        feedRV.setLayoutManager(linearLayoutManager);
        feedRV.setItemAnimator(new DefaultItemAnimator());


    }

    private void chipListener() {
        singleChipListener(math,"Maths");
        singleChipListener(phy,"Physics");
        singleChipListener(cs,"Computer Science");
        singleChipListener(bio,"Biology");
        singleChipListener(es,"Electrical Sciences");
        singleChipListener(chem,"Chemistry");
    }

    public void singleChipListener(Chip chip,String subject)
    {
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                subjectList.add(subject);
            else {
                if (subjectList.contains(subject)) {
                    subjectList.remove(subject);
                }
            }
            Log.i(TAG, "chipListener: " + subjectList);
            populateData.clear();
            loadFromFirebase(subjectList);
        });
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
