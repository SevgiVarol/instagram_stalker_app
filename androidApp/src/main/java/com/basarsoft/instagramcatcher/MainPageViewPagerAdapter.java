package com.basarsoft.instagramcatcher;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


public class MainPageViewPagerAdapter extends PagerAdapter {

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int resId = 0;
        switch (position) {
            case 0:
                resId = R.id.layoutStats;
                break;
            case 1:
                resId = R.id.layoutMedia;
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
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }
}
