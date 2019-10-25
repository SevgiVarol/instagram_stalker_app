package com.example.appinsta;

import android.content.Context;
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
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.RecyclerViewHolder> implements Filterable {

    private List<InstagramUserSummary> userList=null;
    private Context context;
    private OnListener mListener;

    public interface OnListener{
        void onClick(int position);
    }
    public void setOnItemClickListener(OnListener listener){
        mListener=listener;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilPic;
        TextView tvFullName;
        TextView tvUsername;

        RecyclerViewHolder(View itemView,OnListener listener) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvUsername =itemView.findViewById(R.id.tvUsername);
            profilPic = itemView.findViewById(R.id.userProfilPic);

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

    UserListAdapter(List<InstagramUserSummary> userList, Context context) {

        this.userList = userList;
        this.context=context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_tab_layout,
                parent, false);
        return new RecyclerViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position)  {

        InstagramUserSummary user = userList.get(position);

        Picasso.with(context)
                .load(user.profile_pic_url)
                .fit()
                .centerCrop()
                .into(holder.profilPic);

        holder.tvFullName.setText(user.getFull_name());
        holder.tvUsername.setText(user.getUsername());
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
                filteredList.addAll(userList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (InstagramUserSummary item : userList) {
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