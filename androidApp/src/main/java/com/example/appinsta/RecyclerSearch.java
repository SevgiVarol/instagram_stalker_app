package com.example.appinsta;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class RecyclerSearch<T> extends RecyclerView.Adapter<RecyclerSearch<T>.RecyclerViewHolder> implements Filterable {
    private List<T> userList = null;
    private List<T> userListFull = null;
    private Context context;

    private OnListener mListener;

    public interface OnListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnListener listener) {
        mListener = listener;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView fullName;
        TextView username;

        RecyclerViewHolder(View itemView, OnListener listener) {
            super(itemView);
            fullName = itemView.findViewById(R.id.text_view_name);
            username = itemView.findViewById(R.id.text_view_name2);
            imageView = itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onClick(position);
                    }
                }
            });


        }
    }


    public RecyclerSearch(List<T> list, Context context) {
        this.userList = list;
        userListFull = new ArrayList<>(list);
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search,
                parent, false);
        return new RecyclerViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        if (userList.get(0) instanceof InstagramUserSummary) {
            InstagramUserSummary userItem = (InstagramUserSummary) userList.get(position);
            Picasso.with(context)
                    .load(userItem.profile_pic_url)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);

            holder.fullName.setText(userItem.getFull_name());
            holder.username.setText(userItem.getUsername());
        } else if (userList.get(0) instanceof InstagramUser) {
            InstagramUser userItem = (InstagramUser) userList.get(position);
            Picasso.with(context)
                    .load(userItem.profile_pic_url)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);

            holder.fullName.setText(userItem.getFull_name());
            holder.username.setText(userItem.getUsername());
        }
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public Filter getFilter() {
        return userSummaryFilter;
    }

    private Filter userSummaryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<T> filteredList = new ArrayList<>();

            if (constraint != null && constraint.length() > 0) {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (T item : userListFull) {
                    if (item instanceof InstagramUser) {
                        if (((InstagramUser) item).full_name.toLowerCase().contains(filterPattern) || ((InstagramUser) item).username.toLowerCase().contains(filterPattern)) {

                            filteredList.add(item);
                        }
                    } else if (item instanceof InstagramUserSummary) {
                        {
                            if (((InstagramUserSummary) item).full_name.toLowerCase().contains(filterPattern) || ((InstagramUserSummary) item).username.toLowerCase().contains(filterPattern)) {

                                filteredList.add(item);
                            }
                        }

                    }

                }
            }else {
                filteredList = new ArrayList<>(userListFull);
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