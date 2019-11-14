package com.example.appinsta.medialog;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.appinsta.R;

public class StoryUserPagerAdapter extends PagerAdapter {
    private Context context;

    public StoryUserPagerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int resId=0;
        switch (position){
            case 0:
                resId=R.id.layout_all;
                break;
            case 1:
                resId=R.id.layout_notfollow;
                break;
            case 2:
                resId=R.id.layout_follow;
                break;
        }
        return container.findViewById(resId);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

}

