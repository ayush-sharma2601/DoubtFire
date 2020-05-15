package com.example.doubtfire.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.doubtfire.Adapters.MainPagerAdapter;
import com.example.doubtfire.R;
import com.google.android.material.tabs.TabLayout;

public class FeedFragment extends Fragment {
    View view;
    ViewPager viewPager;
    MainPagerAdapter adapter;
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.feed_fragment_container,container,false);
        setViewPager();
        return view;
    }

    private void setViewPager() {

        viewPager = view.findViewById(R.id.feed_view_pager);
        adapter = new MainPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        tabLayout = view.findViewById(R.id.feed_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

}
