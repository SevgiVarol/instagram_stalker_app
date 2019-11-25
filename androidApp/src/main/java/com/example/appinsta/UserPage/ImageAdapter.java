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

    private List<InstagramFeedItem> mediaList;
    ImageView imageView;

    public ImageAdapter(Context context, List<InstagramFeedItem> mediaList) {
        super(context, R.layout.user_image, mediaList);

        this.context = context;
        this.mediaList = mediaList;
        inflater = LayoutInflater.from(context);

    }

    public void setData(List<InstagramFeedItem> mediaList) {
        this.mediaList = mediaList;
    }

    @Override
    public int getCount() {
        return this.mediaList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.user_image, parent, false);
        }

        imageView=convertView.findViewById(R.id.user_image_view);

        Glide.with(context).load(getImageUrl(position)).centerCrop()
                .into((imageView));

        return convertView;
    }

    private String getImageUrl(int position) {

            if (mediaList.get(position).image_versions2 == null) {
                return mediaList.get(position).carousel_media.get(0).image_versions2.candidates.get(1).url;
            } else {
                return mediaList.get(position).image_versions2.candidates.get(1).url;
            }

    }

}
