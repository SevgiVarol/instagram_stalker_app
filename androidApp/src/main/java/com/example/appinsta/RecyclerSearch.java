package com.example.appinsta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class RecyclerSearch extends RecyclerView.Adapter<RecyclerSearch.RecyclerViewHolder> implements Filterable {
    public List<InstagramUserSummary> userList=null;
    public List<InstagramUserSummary> userListFull=null;
    Context context;

    private OnListener mListener;

    public interface OnListener{
        void onClick(int position);
    }
    public void setOnItemClickListener(OnListener listener){
        mListener=listener;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView fullName;
        TextView username;

        RecyclerViewHolder(View itemView,OnListener listener) {
            super(itemView);
            fullName = itemView.findViewById(R.id.text_view_name);
            username=itemView.findViewById(R.id.text_view_name2);
            imageView = itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                            listener.onClick(position);
                    }
                }
            });


        }
    }

    RecyclerSearch(List<InstagramUserSummary> exampleList,Context context) {

        this.userList = exampleList;
        userListFull = new ArrayList<>(exampleList);
        this.context=context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search,
                parent, false);
        return new RecyclerViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position)  {

        InstagramUserSummary userItem = userList.get(position);

        Picasso.with(context)
                .load(userItem.profile_pic_url)
                .fit()
                .centerCrop()
                .into(holder.imageView);

        holder.fullName.setText(userItem.getFull_name());
        holder.username.setText(userItem.getUsername());

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<InstagramUserSummary> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(userListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (InstagramUserSummary item : userListFull) {
                    if (item.full_name.toLowerCase().contains(filterPattern) || item.username.toLowerCase().contains(filterPattern)) {

                        filteredList.add(item);
                    }
                }


            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userList.clear();
            userList.addAll((List) results.values);
            notifyDataSetChanged();


        }
    };

}