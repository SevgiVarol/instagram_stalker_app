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

public class UserListAdapter<T> extends RecyclerView.Adapter<UserListAdapter<T>.RecyclerViewHolder> implements Filterable {

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
        CircleImageView profilPic;
        TextView tvFullName;
        TextView tvUsername;

        RecyclerViewHolder(View itemView, OnListener listener) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            profilPic = itemView.findViewById(R.id.userProfilPic);

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

    public UserListAdapter(List<T> userList, Context context) {

        this.userList = userList;
        userListFull = new ArrayList<>(userList);
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_tab_layout,
                parent, false);
        return new RecyclerViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        if (userList.get(0) instanceof InstagramUserSummary) {
            InstagramUserSummary user = (InstagramUserSummary) userList.get(position);
            Picasso.with(context)
                    .load(user.profile_pic_url)
                    .fit()
                    .centerCrop()
                    .into(holder.profilPic);

            holder.tvFullName.setText(user.getFull_name());
            holder.tvUsername.setText(user.getUsername());
        } else if (userList.get(0) instanceof InstagramUser) {
            InstagramUser userItem = (InstagramUser) userList.get(position);
            Picasso.with(context)
                    .load(userItem.profile_pic_url)
                    .fit()
                    .centerCrop()
                    .into(holder.profilPic);

            holder.tvFullName.setText(userItem.getFull_name());
            holder.tvUsername.setText(userItem.getUsername());
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
            } else {
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