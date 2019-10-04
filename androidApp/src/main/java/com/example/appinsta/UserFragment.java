package com.example.appinsta;

import android.annotation.SuppressLint;
import androidx.fragment.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.adprogressbarlib.AdCircleProgress;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appinsta.service.InstagramService;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.InstagramConstants;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

import static com.example.appinsta.Compare.compare;


public class UserFragment extends Fragment {

    InstagramUserSummary instagramUserSummary;

    CustomView mutedStory, latestPhotoLikers, storyStalkers, photoStalkers, usersStalkers, usersStalking, userAction;
    Instagram4Android instagram;

    ImageView profilPic, latestPhoto;
    TextView takipTv, takipciTv;
    long userId;
    int pStatus = 0;
    private Handler handler = new Handler();
    TextView tv;

    RelativeLayout theLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    Drawable drawable = null;

    private Handler mHandler = new Handler();
    private int i = 0;

    @SuppressLint("ValidFragment")
    public UserFragment(InstagramUserSummary instagramUserSummary) {
        this.instagramUserSummary=instagramUserSummary;

        // Required empty public constructor
    }
    public UserFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.user_fragment, container, false);
        initComponent(v);



        //latestPhotoLikers.setNumberText(String.valueOf(compare.dislikedMediaandFollowing(instagramUserSummary.getPk(), 0).size()));
        new myAsycnTask().execute();



        /*mutedStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchFragment fragment = new SearchFragment(finalObjs);
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.linearLayout, fragment).addToBackStack("tag").commit();
            }
        });*/





        return v;
    }

    private class myAsycnTask extends AsyncTask<String, String, String>{

        List<InstagramUserSummary> followers,following=new ArrayList<>();
        InstagramService service = InstagramService.getInstance();
        @Override
        protected String doInBackground(String... strings) {



                followers = service.getFollowers(instagramUserSummary.getPk());
                following = service.getFollowing(instagramUserSummary.getPk());




            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Glide.with(getActivity()) //1
                    .load(instagramUserSummary.getProfile_pic_url()).into(profilPic);



            takipciTv.setText(String.valueOf(followers.size()));
            takipTv.setText(String.valueOf(following.size()));

            usersStalkers.setNumberText(String.valueOf(compare(followers,following).size()));
            usersStalking.setNumberText(String.valueOf(compare(following,followers).size()));


            usersStalkers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SearchFragment fragment = new SearchFragment(compare(followers,following));
                    FragmentManager manager = getFragmentManager();

                    manager.beginTransaction().replace(R.id.linearLayout, fragment).addToBackStack("tag").commit();

                }
            });

            usersStalking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchFragment fragment = new SearchFragment(compare(following,followers));
                    FragmentManager manager = getFragmentManager();

                    manager.beginTransaction().replace(R.id.linearLayout, fragment).addToBackStack("tag").commit();

                }
            });


        }
    }

    private void initComponent(View view) {

        Resources res = getResources();
        drawable = res.getDrawable(R.drawable.circle_shape);


        profilPic = (CircleImageView) view.findViewById(R.id.profilPic);
        takipTv = (TextView) view.findViewById(R.id.takipTv);
        takipciTv = (TextView) view.findViewById(R.id.takipciTv);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        latestPhoto = (ImageView) view.findViewById(R.id.latestPhotoUser);

        theLayout = (RelativeLayout) view.findViewById(R.id.layoutLastestPhoto);


        usersStalkers = (CustomView) view.findViewById(R.id.stalkingUser);
        usersStalking = (CustomView) view.findViewById(R.id.stalkersUser);
    }

}
