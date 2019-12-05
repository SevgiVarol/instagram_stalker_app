package com.example.appinsta.userpage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.appinsta.R;
import com.example.appinsta.models.MediaModel;
import com.example.appinsta.service.InstagramService;

import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;

public class LikedMediasByUserFragment extends Fragment {

    GridView gridView;
    InstagramService service = InstagramService.getInstance();
    public List<InstagramFeedItem> myLikedMediaList = new ArrayList<>();
    String username;
    ImageAdapter imageListAdapter;
    ProgressBar footerLoadingView;
    Boolean isLoadingNextMedias = false;
    AsyncTask getLoggedUserLikedMediaTask;
    MediaModel mediaModel = new MediaModel();
    String nextMaxId;

    public LikedMediasByUserFragment(String username) {
        this.username=username;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_media, container, false);

        gridView = (GridView) view.findViewById(R.id.myLikedMediasGridView);
        footerLoadingView = view.findViewById(R.id.likedMediasfooterLoadingView);

        getLoggedUserLikedMediaTask = new getLoggedUserLikedMediaTask().execute();

        return view;
    }

    public class getLoggedUserLikedMediaTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            if(myLikedMediaList.isEmpty()) {
                mediaModel = service.getMyLikedMediaByUser(username);
                myLikedMediaList = mediaModel.feedItems;
                nextMaxId = mediaModel.nextMaxId;
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            footerLoadingView.setVisibility(View.GONE);
            imageListAdapter = new ImageAdapter(getActivity(), myLikedMediaList);
            gridView.setAdapter(imageListAdapter);

            gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    if (totalItemCount - 1 == view.getLastVisiblePosition()) {

                        if (!isLoadingNextMedias && nextMaxId!=null)
                        {
                            footerLoadingView.setVisibility(View.VISIBLE);
                            isLoadingNextMedias = true;
                            new getLikedMediasNextPage().execute();
                        }
                    }
                }

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }
            });
        }
    }

    private class getLikedMediasNextPage extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            mediaModel = service.getMyLikedNextMediaByUser(username,nextMaxId);
            List<InstagramFeedItem> nextMedias = mediaModel.feedItems;
            nextMaxId = mediaModel.nextMaxId;
            myLikedMediaList.addAll(nextMedias);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            imageListAdapter.setData(myLikedMediaList);
            imageListAdapter.notifyDataSetChanged();
            footerLoadingView.setVisibility(View.GONE);
            isLoadingNextMedias = false;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        getLoggedUserLikedMediaTask.cancel(true);
    }
}
