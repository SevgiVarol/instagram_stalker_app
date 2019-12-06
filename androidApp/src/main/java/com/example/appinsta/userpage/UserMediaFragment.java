package com.example.appinsta.userpage;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.appinsta.R;
import com.example.appinsta.models.DataWithOffsetIdModel;
import com.example.appinsta.service.InstagramService;

import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;


public class UserMediaFragment extends Fragment {

    InstagramService service = InstagramService.getInstance();
    GridView mediasGridView;
    TextView tvInfoText;
    public List<InstagramFeedItem> mediaList = new ArrayList<>();
    InstagramUser user;
    Boolean isLoadingNextMedias = false;
    ImageAdapter imageListAdapter;
    ProgressBar footerLoadingView;
    AsyncTask getUserMedia;
    DataWithOffsetIdModel dataWithOffsetIdModel;

    public UserMediaFragment() {
        // Required empty public constructor
    }

    public UserMediaFragment(InstagramUser user) {
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_media, container, false);
        mediasGridView = view.findViewById(R.id.userMediasGridView);
        footerLoadingView = view.findViewById(R.id.footerLoadingView);
        tvInfoText = view.findViewById(R.id.nullMediaInfo);

        getUserMedia= new getUserMediaTask().execute();
        return view;
    }

    private class getUserMediaTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            if (mediaList.isEmpty()) {
                dataWithOffsetIdModel = service.getUserMedias(user.getPk());
                mediaList = dataWithOffsetIdModel.Items;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            footerLoadingView.setVisibility(View.GONE);
            if (mediaList == null) {
                tvInfoText.setText(R.string.follow_if_want_to_show_profile);
                tvInfoText.setVisibility(View.VISIBLE);
            } else {
                if (mediaList.size() != 0){
                    imageListAdapter = new ImageAdapter(getActivity(), mediaList);
                    mediasGridView.setAdapter(imageListAdapter);

                    mediasGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                            if (totalItemCount - 1 == view.getLastVisiblePosition()) {

                                if (totalItemCount < user.getMedia_count() && !isLoadingNextMedias) {
                                    footerLoadingView.setVisibility(View.VISIBLE);
                                    isLoadingNextMedias = true;
                                    new getUserMediasNextPage().execute();
                                }
                            }
                        }

                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {

                        }
                    });

                } else {
                    tvInfoText.setVisibility(View.VISIBLE);
                }
            }


        }
    }

    private class getUserMediasNextPage extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            dataWithOffsetIdModel = service.getUserMedias(user.getPk(), dataWithOffsetIdModel.nextMaxId);
            List<InstagramFeedItem> nextMedias = dataWithOffsetIdModel.Items;
            if (nextMedias != null){mediaList.addAll(nextMedias);}

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                imageListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("null object reference", e.getMessage().toString());
            }

            footerLoadingView.setVisibility(View.GONE);
            isLoadingNextMedias = false;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        getUserMedia.cancel(true);
    }
}
