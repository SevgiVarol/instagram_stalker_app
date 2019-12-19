package com.example.appinsta.userpage;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.appinsta.R;
import com.example.appinsta.medialog.MediaLogs;
import com.example.appinsta.models.DataWithOffsetIdModel;
import com.example.appinsta.service.InstagramService;

import java.io.IOException;
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
    ArrayList<Uri> mediaUrlList;
    ArrayList<String> mediaIdList;
    Long userid;

    public UserMediaFragment() {
        // Required empty public constructor
    }

    public UserMediaFragment(InstagramUser user) {
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_media, container, false);
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setPadding(10,0,10,30);
        mediasGridView = view.findViewById(R.id.userMediasGridView);
        footerLoadingView = view.findViewById(R.id.footerLoadingView);
        tvInfoText = view.findViewById(R.id.nullMediaInfo);

        getUserMedia= new getUserMediaTask().execute();
        mediaUrlList = new ArrayList<>();
        mediaIdList = new ArrayList<>();
        return view;
    }

    private class getUserMediaTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

                dataWithOffsetIdModel = service.getUserMedias(user.getPk());
                mediaList = dataWithOffsetIdModel.items;
                if (mediaUrlList.size() == 0) {
                    try {
                        for (int counter = 0; counter < mediaList.size(); counter++) {
                            if (mediaList.get(counter).getVideo_versions() != null) {
                                try {
                                    mediaUrlList.add(Uri.parse(mediaList.get(counter).getVideo_versions().get(0).getUrl()));
                                } catch (Exception e) {
                                    //if is post (multiple sharing)
                                    mediaUrlList.add(Uri.parse(mediaList.get(counter).getCarousel_media().get(0).getVideo_versions().get(0).getUrl()));
                                }
                            } else {
                                try {
                                    mediaUrlList.add(Uri.parse(mediaList.get(counter).getImage_versions2().getCandidates().get(0).getUrl()));
                                } catch (Exception e) {
                                    //if is post (multiple sharing)
                                    mediaUrlList.add(Uri.parse(mediaList.get(counter).getCarousel_media().get(0).getImage_versions2().getCandidates().get(0).getUrl()));
                                }

                            }
                            mediaIdList.add(String.valueOf(mediaList.get(counter).pk));
                        }
                    } catch (Exception e) {
                        Log.e("null object reference", e.getMessage());
                    }
                }
                userid = service.getLoggedUser().pk;

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
                if (mediaList.size() != 0) {
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
                    mediasGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            itemLongClickPopup(position);
                            return true;
                        }
                    });

                    if (user.username.equals(service.getLoggedUser().username)){
                    mediasGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mediaUrlList != null & mediaUrlList.size() != 0) {

                                Intent mediaLogIntent = new Intent(getActivity(), MediaLogs.class);
                                mediaLogIntent.putExtra("storyUrlList", mediaUrlList);
                                mediaLogIntent.putExtra("userId", userid);
                                mediaLogIntent.putExtra("storyIds", mediaIdList);
                                mediaLogIntent.putExtra("key", "MEDIA");
                                mediaLogIntent.putExtra("position", position);
                                startActivity(mediaLogIntent);
                            } else {
                                Toast.makeText(getActivity(), R.string.media_not_found, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });}

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
            List<InstagramFeedItem> nextMedias = dataWithOffsetIdModel.items;
            if (mediaUrlList.size() != 0) {
                try {
                    for (int counter = 0; counter < nextMedias.size(); counter++) {
                        if (nextMedias.get(counter).getVideo_versions() != null) {
                            try {
                                mediaUrlList.add(Uri.parse(nextMedias.get(counter).getVideo_versions().get(0).getUrl()));
                            } catch (Exception e) {
                                //if is post (multiple sharing)
                                mediaUrlList.add(Uri.parse(nextMedias.get(counter).getCarousel_media().get(0).getVideo_versions().get(0).getUrl()));
                            }
                        } else {
                            try {
                                mediaUrlList.add(Uri.parse(nextMedias.get(counter).getImage_versions2().getCandidates().get(0).getUrl()));
                            } catch (Exception e) {
                                //if is post (multiple sharing)
                                mediaUrlList.add(Uri.parse(nextMedias.get(counter).getCarousel_media().get(0).getImage_versions2().getCandidates().get(0).getUrl()));
                            }

                        }
                        mediaIdList.add(String.valueOf(nextMedias.get(counter).pk));
                    }
                } catch (Exception e) {
                    Log.e("null object reference", e.getMessage());
                }
            }
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
    public void itemLongClickPopup(int position) {
        ConstraintLayout layout = new ConstraintLayout(getActivity());
        ConstraintLayout.LayoutParams clp = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        VideoView videoView = new VideoView(getActivity());
        ImageView imageView = new ImageView(getActivity());
        Dialog dialog = new Dialog(getActivity());
        if (mediaList.get(position).getVideo_versions() != null) {

            Uri videoUri = mediaUrlList.get(position);
            videoView.setVideoURI(videoUri);
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            videoView.requestFocus();
            layout.addView(videoView, clp);
            videoView.start();
        } else {
            Uri imageUri = mediaUrlList.get(position);

            Glide.with(getActivity()).load(imageUri).into(imageView);
            videoView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            layout.addView(imageView, clp);
        }


        dialog.setContentView(layout, clp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }
}
