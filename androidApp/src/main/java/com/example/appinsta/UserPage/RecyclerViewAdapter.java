package com.example.appinsta.UserPage;

import android.content.Context;
import android.provider.ContactsContract;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.appinsta.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{
    public List<InstagramFeedItem> feedList = null;
    public List<InstagramUserSummary> userListFull = null;
    Context context;


    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        RecyclerViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view_post);



        }
    }

    RecyclerViewAdapter(List<InstagramFeedItem> exampleList, Context context) {

        this.feedList = exampleList;
        this.context = context;
    }





    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_images,parent,false);

        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        InstagramFeedItem userItem = feedList.get(position);

        if (userItem.image_versions2 == null) {
            Glide.with(context).load(userItem.carousel_media.get(0).image_versions2.candidates.get(0).url).centerCrop()
                    .into((holder.imageView));



        } else {

            Glide.with(context).load(userItem.image_versions2.candidates.get(0).url).centerCrop()
                    .into((holder.imageView) );


        }


    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }




}