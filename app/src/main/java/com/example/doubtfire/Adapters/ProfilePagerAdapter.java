package com.example.doubtfire.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.doubtfire.Fragments.CompletedDoubtsFragment;
import com.example.doubtfire.Fragments.MyDoubtsFragment;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_COUNT = 2;

    public ProfilePagerAdapter(@NonNull FragmentManager fm, int behaviour) {
        super(fm,behaviour);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new MyDoubtsFragment();
            case 1: return new CompletedDoubtsFragment();
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
                return "My Doubts";
            case 1:
                return "Completed";
        }
        return null;
    }
}
