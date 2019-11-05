package com.example.appinsta.UserPage;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.appinsta.MediaLog.MediaLogs;
import com.example.appinsta.R;
import com.example.appinsta.service.InstagramService;

import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;

public class MyMediasFragment extends Fragment {

    static GridView gridView;
    List<InstagramFeedItem> myMediaList;
    InstagramService service = InstagramService.getInstance();
    ArrayList<Uri> mediaUrlList;
    ArrayList<String> mediaIdList;
    Long userid;
    public MyMediasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_media, container, false);

        gridView=(GridView)view.findViewById(R.id.gridView);


        mediaUrlList=new ArrayList<>();
        mediaIdList=new ArrayList<>();
        new loadAsynTask().execute();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new mediaLogIntent(position).execute();
            }
        });

       // gridView.setAdapter(new ImageAdapter(getActivity(), UserProfile.urlOfMyPhotos));
        return view;

    }
    private class loadAsynTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            myMediaList= service.getMyMedias();
            if (mediaUrlList.size() == 0) {
                try {
                    for (int counter = 0; counter < myMediaList.size(); counter++) {
                        if (myMediaList.get(counter).getVideo_versions() != null) {
                            mediaUrlList.add(Uri.parse(myMediaList.get(counter).getVideo_versions().get(0).getUrl()));
                        } else {
                            mediaUrlList.add(Uri.parse(myMediaList.get(counter).getImage_versions2().getCandidates().get(0).getUrl()));
                        }
                        mediaIdList.add(String.valueOf(myMediaList.get(counter).pk));
                    }
                } catch (Exception e) {
                    Log.e("null object reference", e.getMessage());
                }
            }
            userid = service.getLoggedUser().pk;
            return null;
        }
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            gridView.setAdapter(new ImageAdapter(getActivity(), myMediaList));
        }
    }
    private class mediaLogIntent extends AsyncTask<String,String,String>{
        int position=0;
        public mediaLogIntent(int position) {
            this.position= position;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            Intent mediaLogIntent = new Intent(getContext(), MediaLogs.class);
            mediaLogIntent.putExtra("storyUrlList", mediaUrlList);
            mediaLogIntent.putExtra("userId", userid);
            mediaLogIntent.putExtra("storyIds", mediaIdList);
            mediaLogIntent.putExtra("key","MEDIA");
            mediaLogIntent.putExtra("position",position);

            if (mediaUrlList != null & mediaUrlList.size() != 0) {

                startActivity(mediaLogIntent);
            } else {
                Toast.makeText(getActivity(), "Hiçbir hikaye bulunamadı", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
