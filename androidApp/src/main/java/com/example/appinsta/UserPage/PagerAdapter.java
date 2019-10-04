package com.example.appinsta.UserPage;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.appinsta.UserPage.MyMediasFragment;
import com.example.appinsta.UserPage.UserMediaFragment;


public class PagerAdapter extends FragmentStatePagerAdapter {

    int noOfTab;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm);
        this.noOfTab=behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                UserMediaFragment userMediaFragment = new UserMediaFragment();
                return userMediaFragment;

            case 1:
                MyMediasFragment myMediasFragment = new MyMediasFragment();
                return myMediasFragment;
            default:
                return null;

        }

    }


    @Override
    public int getCount() {
        return noOfTab;
    }
}
