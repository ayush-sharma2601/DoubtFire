package com.example.doubtfire.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.doubtfire.Activities.LoginActivity;
import com.example.doubtfire.Adapters.ProfilePagerAdapter;
import com.example.doubtfire.Models.UserModel;
import com.example.doubtfire.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileFragment extends Fragment {
    View view;
    ViewPager viewPager;
    ProfilePagerAdapter adapter;
    TabLayout tabLayout;
    TextView nameTV,numberTV;
    ImageView logout,lockToggle;
    FirebaseAuth fAuth;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment,container,false);
        setViewPager();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.getInstance().signOut();
                Toast.makeText(view.getContext(),"Logout Successful",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                Log.i(TAG, "onDataChange: >>>>>>>>> "+userModel.name);
                nameTV.setText(userModel.name);
                numberTV.setText(userModel.number);
                lockToggle.setImageResource(userModel.privacy?R.drawable.ic_lock:R.drawable.ic_unlock);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lockToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder toggleDialog = new MaterialAlertDialogBuilder(view.getContext());
                toggleDialog.setTitle("Toggle Privacy");
                toggleDialog.setMessage("Toggle number's visibility to others?")
                        .setCancelable(true)
                        .setNegativeButton("Nah,I'm fine", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(view.getContext(),"No Change",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("Yes please", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                        if(userModel.privacy == false)
                                        {
                                            userModel.privacy = true;
                                            lockToggle.setImageResource(R.drawable.ic_lock);
                                        }
                                        else
                                        {
                                            userModel.privacy = false;
                                            lockToggle.setImageResource(R.drawable.ic_unlock);
                                        }
                                        reference.setValue(userModel);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }).setBackground(view.getContext().getDrawable(R.drawable.feed_back)).show();
            }
        });
        return view;

    }

    private void changeNumber() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
    }


    private void setViewPager() {
        viewPager = view.findViewById(R.id.profile_view_pager);
        adapter = new ProfilePagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        tabLayout = view.findViewById(R.id.profile_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        nameTV = view.findViewById(R.id.profile_name);
        numberTV = view.findViewById(R.id.profile_number);
        logout = view.findViewById(R.id.logout_btn);
        lockToggle = view.findViewById(R.id.privacy_toggle);
    }


}

