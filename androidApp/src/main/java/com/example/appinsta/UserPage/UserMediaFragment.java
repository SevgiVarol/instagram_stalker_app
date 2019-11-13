package com.example.appinsta.UserPage;


import android.annotation.SuppressLint;
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
import com.example.appinsta.service.InstagramService;

import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;


public class UserMediaFragment extends Fragment {

    InstagramService service = InstagramService.getInstance();
    GridView gridView;
    TextView infoTv;
    public List<InstagramFeedItem> mediaList = new ArrayList<>();
    InstagramUser user;
    Boolean isLoadingNextMedias = false;
    ImageAdapter imageListAdapter;
    ProgressBar footerLoadingView;
    AsyncTask getUserMedia;

    public UserMediaFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public UserMediaFragment(InstagramUser user) {
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_media, container, false);
        gridView = view.findViewById(R.id.userMediasGridView);
        footerLoadingView = view.findViewById(R.id.footerLoadingView);
        infoTv = view.findViewById(R.id.nullMediaInfo);

        getUserMedia= new getUserMedia().execute();

        return view;
    }

    private class getUserMedia extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            if (mediaList.isEmpty()) {
                mediaList = service.getUserMedias(user.getPk());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            footerLoadingView.setVisibility(View.GONE);
            try {
                if (mediaList.size() != 0){
                    imageListAdapter = new ImageAdapter(getActivity(), mediaList);
                    gridView.setAdapter(imageListAdapter);
                }else{
                    infoTv.setVisibility(View.VISIBLE);
                }
            }catch (Exception e){
                infoTv.setText("Fotoğraf ve videolarını görmek için bu hesabı takip et.");
                infoTv.setVisibility(View.VISIBLE);
            }

            gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        }
    }

    private class getUserMediasNextPage extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            List<InstagramFeedItem> nextMedias = service.getUserMedias(user.getPk(), InstagramService.mediasNextMaxId);
            if (nextMedias != null){mediaList.addAll(nextMedias);}

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //imageListAdapter.setData(mediaList);
//            imageListAdapter.notifyDataSetChanged();
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
