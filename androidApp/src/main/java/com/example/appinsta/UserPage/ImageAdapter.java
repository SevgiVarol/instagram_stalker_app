package com.example.appinsta.UserPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.appinsta.R;

import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;

public class ImageAdapter extends ArrayAdapter {

    private Context context;
    private LayoutInflater inflater;

    private ArrayList<String> imageUrls;


    public ImageAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ImageAdapter(Context context, ArrayList<String> url) {
        super(context, R.layout.user_images, url);

        this.context = context;
        this.imageUrls = url;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.user_images, parent, false);
        }

        Glide.with(context).load(imageUrls.get(position)).centerCrop()
                .into((ImageView) convertView);


        return convertView;
    }

}
