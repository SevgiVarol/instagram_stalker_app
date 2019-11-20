package com.example.appinsta.medialog;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.appinsta.R;

import java.util.ArrayList;
import java.util.List;


public class StoryImagePagerAdapter extends PagerAdapter {
    private Context context;
    public ArrayList<Uri> list;
    public ViewGroup container;

    public StoryImagePagerAdapter(Context context, ArrayList<Uri> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        this.container = container;
        View view = LayoutInflater.from(context).inflate(R.layout.story_imageview_item, null);
        ImageView imageView = view.findViewById(R.id.imageView1);
        Glide
                .with(context.getApplicationContext())
                .load((list.get(position)).toString())
                .into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    public List<Object> getAll() {
        List<Object> allList = new ArrayList<Object>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            allList.add(this.instantiateItem(container, i));
        }
        return allList;
    }

}

