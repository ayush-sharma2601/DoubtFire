package com.example.doubtfire.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.doubtfire.Fragments.FeedContainingFragment;
import com.example.doubtfire.Fragments.FeedFragment;
import com.example.doubtfire.Fragments.SearchFeedFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_COUNT = 2;

    public MainPagerAdapter(@NonNull FragmentManager fm,int behaviour) {
        super(fm,behaviour);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new FeedContainingFragment();
            case 1: return new SearchFeedFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Feed";
            case 1:
                return "Search";
        }
        return null;
    }
}
