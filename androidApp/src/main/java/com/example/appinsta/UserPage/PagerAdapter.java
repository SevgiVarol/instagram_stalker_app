package com.example.appinsta.UserPage;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.appinsta.UserPage.MyMediasFragment;
import com.example.appinsta.UserPage.UserMediaFragment;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;


public class PagerAdapter extends FragmentStatePagerAdapter {

    int noOfTab;
    InstagramUser user;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior, InstagramUser user) {
        super(fm);
        this.noOfTab=behavior;
        this.user=user;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                UserMediaFragment userMediaFragment = new UserMediaFragment(user);
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
