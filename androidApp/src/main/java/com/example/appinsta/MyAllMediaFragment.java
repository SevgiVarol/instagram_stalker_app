package com.example.appinsta;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.appinsta.medialog.MediaLogs;
import com.example.appinsta.models.DataWithOffsetIdModel;
import com.example.appinsta.service.InstagramService;
import com.example.appinsta.userpage.ImageAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;

public class MyAllMediaFragment extends Fragment {

    static GridView gridView;
    ProgressBar footerLoadingView;
    List<InstagramFeedItem> myMediaList;
    InstagramService service = InstagramService.getInstance();
    ArrayList<Uri> mediaUrlList;
    ArrayList<String> mediaIdList;
    Long userid;
    ImageAdapter imageListAdapter;
    Boolean isLoadingNextMedias = false;
    DataWithOffsetIdModel dataWithOffsetIdModel;

    public MyAllMediaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_media, container, false);
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setPadding(10, 0, 10, 30);
        gridView = (GridView) view.findViewById(R.id.userMediasGridView);
        footerLoadingView = view.findViewById(R.id.footerLoadingView);

        mediaUrlList = new ArrayList<>();
        mediaIdList = new ArrayList<>();
        new init().execute();

        return view;

    }

    private class init extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                dataWithOffsetIdModel = service.getLoggedUserMedias();
                myMediaList = dataWithOffsetIdModel.items;
                if (mediaUrlList.size() == 0) {
                    for (int counter = 0; counter < myMediaList.size(); counter++) {
                        try {
                            if (myMediaList.get(counter).getVideo_versions() != null) {
                                mediaUrlList.add(Uri.parse(myMediaList.get(counter).getVideo_versions().get(0).getUrl()));
                            } else {
                                mediaUrlList.add(Uri.parse(myMediaList.get(counter).getImage_versions2().getCandidates().get(0).getUrl()));
                            }
                        } catch (Exception e) {
                            if (myMediaList.get(counter).getVideo_versions() != null) {
                                //if is post (multiple sharing)
                                mediaUrlList.add(Uri.parse(myMediaList.get(counter).getCarousel_media().get(0).getVideo_versions().get(0).getUrl()));
                            } else {
                                //if is post (multiple sharing)
                                mediaUrlList.add(Uri.parse(myMediaList.get(counter).getCarousel_media().get(0).getImage_versions2().getCandidates().get(0).getUrl()));
                            }
                        }
                        mediaIdList.add(String.valueOf(myMediaList.get(counter).pk));
                    }
                }
                userid = service.getLoggedUser().pk;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            footerLoadingView.setVisibility(View.GONE);

            imageListAdapter = new ImageAdapter(getActivity(), myMediaList);
            gridView.setAdapter(imageListAdapter);

            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    itemLongClickPopup(position);
                    return true;
                }
            });

            gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    if (totalItemCount - 1 == view.getLastVisiblePosition()) {

                        try {
                            if (totalItemCount < service.getLoggedUser().getMedia_count() && !isLoadingNextMedias) {
                                footerLoadingView.setVisibility(View.VISIBLE);
                                isLoadingNextMedias = true;
                                new getNextMedias().execute();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }
            });

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            });
        }
    }

    private class getNextMedias extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            try {
                dataWithOffsetIdModel = service.getLoggedUserMedias(dataWithOffsetIdModel.nextMaxId);

                List<InstagramFeedItem> nextMedias = dataWithOffsetIdModel.items;
                if (mediaUrlList.size() != 0) {
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
                }
                if (nextMedias != null) {
                    myMediaList.addAll(nextMedias);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //imageListAdapter.setData(mediaList);
            imageListAdapter.notifyDataSetChanged();
            footerLoadingView.setVisibility(View.GONE);
            isLoadingNextMedias = false;
        }
    }

    public void itemLongClickPopup(int position) {
        ConstraintLayout layout = new ConstraintLayout(getActivity());
        ConstraintLayout.LayoutParams clp = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        VideoView videoView = new VideoView(getActivity());
        ImageView imageView = new ImageView(getActivity());
        Dialog dialog = new Dialog(getActivity());
        if (myMediaList.get(position).getVideo_versions() != null) {

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
