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
import java.util.StringJoiner;

import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;

public class ImageAdapter extends ArrayAdapter {

    private Context context;
    private LayoutInflater inflater;

    private List<InstagramFeedItem> media;

    public ArrayList<String> urlOfUserPhotos = new ArrayList<>();

    public ImageAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ImageAdapter(Context context, List<InstagramFeedItem> media) {
        super(context, R.layout.user_images, media);

        this.context = context;

        this.media = media;

        inflater = LayoutInflater.from(context);
    }

    public void setData(List<InstagramFeedItem> media) {
        this.media = media;
    }

    @Override
    public int getCount() {
        return this.media.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.user_images, parent, false);
        }

        addImages();

        Glide.with(context).load(urlOfUserPhotos.get(position)).centerCrop()
                .into((ImageView) convertView);

        urlOfUserPhotos.clear();

        return convertView;
    }

    private void addImages() {

        for (int i = 0; i < media.size(); i++) {


            if (media.get(i).image_versions2 == null) {
                urlOfUserPhotos.add(media.get(i).carousel_media.get(0).image_versions2.candidates.get(1).url);
            } else {
                urlOfUserPhotos.add(media.get(i).image_versions2.candidates.get(1).url);
            }
        }

    }

}
