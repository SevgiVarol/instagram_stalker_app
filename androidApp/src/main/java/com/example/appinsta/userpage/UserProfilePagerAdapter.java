package com.example.appinsta.userpage;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;


public class UserProfilePagerAdapter extends FragmentStatePagerAdapter {

    private int noOfTab;
    private InstagramUser user;

    UserProfilePagerAdapter(@NonNull FragmentManager fm, int behavior, InstagramUser user) {
        super(fm);
        this.noOfTab = behavior;
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new UserMediaFragment(user);
                break;
            case 1:
                fragment = new LikedMediasByUserFragment(user.getUsername());
                break;
            default:
                fragment = new Fragment();
                break;
        }
        return fragment;
    }


    @Override
    public int getCount() {
        return noOfTab;
    }
}
