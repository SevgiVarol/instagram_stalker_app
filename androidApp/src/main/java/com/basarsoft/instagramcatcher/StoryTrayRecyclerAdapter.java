package com.basarsoft.instagramcatcher;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;

/**
 * Created by User on 2/12/2018.
 */

public class StoryTrayRecyclerAdapter extends RecyclerView.Adapter<StoryTrayRecyclerAdapter.ViewHolder> {

    private ArrayList<InstagramUser> mImageUrls = new ArrayList<>();
    private Context mContext;
    public OnListener mListener;

    public interface OnListener {
        void onClick(View view,int position);
    }

    public void setOnItemClickListener(OnListener listener) {
        mListener = listener;
    }
    public StoryTrayRecyclerAdapter(Context context, ArrayList<InstagramUser> imageUrls) {
        mImageUrls = imageUrls;
        mContext = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_story_tray, parent, false);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Glide.with(mContext)
                .asBitmap()
                .load(mImageUrls.get(position).profile_pic_url)
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;

        public ViewHolder(View itemView, OnListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.image_viewProfilPic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) listener.onClick(itemView,position);
                    }
                }
            });

        }
    }
}