package com.example.appinsta;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appinsta.MediaLog.MediaLogs;
import com.example.appinsta.service.InstagramService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.InstagramConstants;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

import static com.example.appinsta.Compare.compare;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }

    CustomView mutedStory, latestPhotoLikers, storyStalkers, photoStalkers, usersStalkers, usersStalking, userAction;


    EditText editText;
    ImageView profilPic, latestPhoto;
    TextView takipTv, takipciTv;

    RelativeLayout theLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar mProgress=null;
    Drawable drawable = null;
    InstagramService service = InstagramService.getInstance();
    private Handler mHandler = new Handler();
    private int i = 0;
    ArrayList<Uri> listUri;
    ArrayList<String> storyIds;
    List<InstagramFeedItem> stories;

    public static long pk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragmentStrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View view = inflater.inflate(R.layout.main_fragment, container, false);

        initComponent(view);
        new loginAsynTask().execute();

        return view;
    }


    private void initComponent(View view) {

        mProgress = view.findViewById(R.id.progress_bar);
        Resources res = getResources();
        drawable = res.getDrawable(R.drawable.circle_shape);

        profilPic = (CircleImageView) view.findViewById(R.id.userProfilPic);
        takipTv = (TextView) view.findViewById(R.id.takipTv);
        takipciTv = (TextView) view.findViewById(R.id.takipciTv);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        latestPhoto = (ImageView) view.findViewById(R.id.latestPhoto);

        theLayout = (RelativeLayout) view.findViewById(R.id.layoutLastestPhoto);

        mutedStory = (CustomView) view.findViewById(R.id.mutedStory);
        storyStalkers = (CustomView) view.findViewById(R.id.storyStalkers);
        photoStalkers = (CustomView) view.findViewById(R.id.photoStalkers);
        latestPhotoLikers = (CustomView) view.findViewById(R.id.latestPhotoLikers);
        userAction = (CustomView) view.findViewById(R.id.userAction);
        usersStalkers = (CustomView) view.findViewById(R.id.userStalkers);
        usersStalking = (CustomView) view.findViewById(R.id.userStalking);

    }


    private class loginAsynTask extends AsyncTask<String, String, String> {

        List<InstagramUserSummary> mediaLikers, myFollowers, myFollowing, myStalkers, myStalking;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            stories = service.getStories(service.getLoggedUser().pk);
            listUri=new ArrayList<>();
            storyIds =new ArrayList<>();
            try {
                for (int i = 0; i < stories.size(); i++) {
                    if (stories.get(i).getVideo_versions() != null) {
                        listUri.add(Uri.parse(stories.get(i).getVideo_versions().get(0).getUrl()));
                    } else {
                        listUri.add(Uri.parse(stories.get(i).getImage_versions2().getCandidates().get(0).getUrl()));
                    }
                    storyIds.add(String.valueOf(stories.get(i).pk));
                }
            }catch (Exception e){
                Log.e("null object reference",e.getMessage());
            }


            mProgress.setVisibility(View.VISIBLE);

            if(InstagramConstants.islogged){

                takipTv.setText(String.valueOf(service.getLoggedUser().following_count));
                takipciTv.setText(String.valueOf(service.getLoggedUser().follower_count));

            }

            profilPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    long userid= service.getLoggedUser().pk;
                    Intent mediaLogIntent = new Intent(getContext(), MediaLogs.class);
                    mediaLogIntent.putExtra("listUri", listUri);
                    mediaLogIntent.putExtra("userId", userid);
                    mediaLogIntent.putExtra("storyIds", storyIds);
                    mediaLogIntent.putExtra("followers", (Serializable) myFollowers);

                    if (listUri!=null & listUri.size()!=0) {

                        startActivity(mediaLogIntent);
                    }
                }
            });

        }

        @Override
        protected String doInBackground(String... strings) {

            /*if(InstagramConstants.log) {
                service.login();

            }*/

                myFollowers = service.getMyFollowers();
                myFollowing = service.getMyFollowing();

            if (service.myMedia(0) != null) {
                mediaLikers = compare(myFollowers, service.getMyMediaLikers(service.myMedia(0).pk));
                pk = InstagramService.instagram.getUserId();
            }

            myStalking = compare(myFollowers, myFollowing);
            myStalkers = compare(myFollowing, myFollowers);

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgress.setVisibility(View.GONE);


            Glide.with(getActivity()) //1
                    .load(service.getLoggedUser().profile_pic_url).into(profilPic);

            latestPhoto.setAlpha(0.3f);

            if (service.myMedia(0) != null) {
                Glide.with(getActivity()).load(service.myMedia(0).image_versions2.candidates.get(0).url).transform(new CenterCrop(), new VignetteFilterTransformation(new PointF(0.5f, 0.0f), new float[]{0f, 0f, 0f}, 0.5f, 0.9f)).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                            latestPhoto.setBackground(resource);
                        }
                    }
                });
            }

            takipTv.setText(String.valueOf(service.getLoggedUser().following_count));
            takipciTv.setText(String.valueOf(service.getLoggedUser().follower_count));

            if (mediaLikers != null) {
                latestPhotoLikers.setNumberText(String.valueOf(mediaLikers.size()));
            } else latestPhotoLikers.setNumberText(String.valueOf(0));

            usersStalkers.setNumberText(String.valueOf(myStalkers.size()));
            usersStalking.setNumberText(String.valueOf(myStalking.size()));

            takipciTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchFragment mainFragment = new SearchFragment(myFollowers);
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.linearLayout, mainFragment).addToBackStack("tag").commit();
                }
            });

            latestPhotoLikers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchFragment mainFragment = new SearchFragment(mediaLikers);
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.linearLayout, mainFragment).addToBackStack("tag").commit();
                }
            });

            takipTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchFragment mainFragment = new SearchFragment(myFollowing);
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.linearLayout, mainFragment).addToBackStack("tag").commit();
                }
            });

            usersStalkers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchFragment mainFragment = new SearchFragment(myStalkers);
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.linearLayout, mainFragment).addToBackStack("tag").commit();
                }
            });
            usersStalking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchFragment mainFragment = new SearchFragment(myStalking);
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.linearLayout, mainFragment).addToBackStack("tag").commit();
                }
            });

        }
    }


}
