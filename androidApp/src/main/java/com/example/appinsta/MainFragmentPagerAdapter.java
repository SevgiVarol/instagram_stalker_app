package com.example.appinsta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class MainFragmentPagerAdapter extends PagerAdapter {
    private Context context;

    public MainFragmentPagerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int resId=0;
        switch (position){
            case 0:
                resId=R.id.layoutStaticks;
                break;
            case 1:
                resId=R.id.layoutMedia;
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
