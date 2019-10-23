package com.example.appinsta.MediaLog;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.appinsta.R;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyImagePager extends PagerAdapter {
    private Context context;
    public ArrayList<Uri>list;

    public MyImagePager(Context context, ArrayList<Uri> list) {
        this.context = context;
        this.list=list;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.imageview_item, null);
        ImageView imageView = view.findViewById(R.id.imageView1);
        Glide
                .with(getApplicationContext())
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

}

