package com.example.appinsta.UserPage;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.appinsta.CustomView;
import com.example.appinsta.R;
import com.example.appinsta.SearchFragment;
import com.example.appinsta.service.InstagramService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.InstagramConstants;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;


public class UserProfile extends Fragment {


    ImageView profilPic;
    TextView takipTv, takipciTv, gönderiTv;
    public InstagramUserSummary user;
    CustomView usersStalkers, usersStalking;
    ViewPager viewPager;

    List<InstagramUserSummary> stalking = new ArrayList<>();
    List<InstagramUserSummary> stalkers = new ArrayList<>();


    public static List<InstagramFeedItem> myMedia = new ArrayList<>();
    InstagramService service = InstagramService.getInstance();
    TabLayout tabLayout;


    public UserProfile() {
        // Required empty public constructor
    }


    @SuppressLint("ValidFragment")
    public UserProfile(InstagramUserSummary user) {
        this.user = user;

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);


        initComponents(view);
        new myAsycnTask().execute();
        return view;
    }


    private void initComponents(View view) {


        profilPic = (CircleImageView) view.findViewById(R.id.profilPic);
        takipTv = (TextView) view.findViewById(R.id.takipTv);
        takipciTv = (TextView) view.findViewById(R.id.takipciTv);
        gönderiTv = (TextView) view.findViewById(R.id.gonderi);

        usersStalkers = (CustomView) view.findViewById(R.id.stalkersUser);
        usersStalking = (CustomView) view.findViewById(R.id.stalkingUser);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);


    }


    private class myAsycnTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            InstagramUser userSum = service.getUser(user.getUsername());


            Glide.with(getActivity()) //1
                    .load(user.getProfile_pic_url()).into(profilPic);


            gönderiTv.setText(String.valueOf(userSum.getMedia_count()));
            takipciTv.setText(String.valueOf(userSum.getFollower_count()));
            takipTv.setText(String.valueOf(userSum.getFollowing_count()));


            tabLayout.addTab(tabLayout.newTab().setText("gönderiler"));

            tabLayout.addTab(tabLayout.newTab().setText("beğendiği gönderilerim"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(), userSum);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());

                    if (tab.getPosition() == 0) {

                        UserMediaFragment userMediaFragment = new UserMediaFragment(userSum);

                        FragmentManager manager = getFragmentManager();
                        manager.beginTransaction().replace(R.id.mediaFragment, userMediaFragment).commit();

                    }
                    if (tab.getPosition() == 1) {
                        //new myMediaAsync().execute();
                    }


                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

        }

        @Override
        protected String doInBackground(String... strings) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            // userMediaFragment.gridView.setAdapter(new ImageAdapter(getActivity(), urlOfUserPhotos));
            // myMediasFragment.gridView.setAdapter(new ImageAdapter(getActivity(),urlOfMyPhotos));


            usersStalkers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    InstagramConstants.userProfile = false;
                    SearchFragment fragment = new SearchFragment(stalkers);
                    FragmentManager manager = getFragmentManager();

                    manager.beginTransaction().replace(R.id.linearLayout, fragment).addToBackStack("tag").commit();

                }
            });

            usersStalking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    InstagramConstants.userProfile = false;
                    SearchFragment fragment = new SearchFragment(stalking);
                    FragmentManager manager = getFragmentManager();

                    manager.beginTransaction().replace(R.id.linearLayout, fragment).addToBackStack("tag").commit();

                }
            });

        }
    }


   /* class myMediaAsync extends AsyncTask<String, String, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getContext());
            pd.setMessage("Loading...");


            pd.show();

        }

        @Override
        protected String doInBackground(String... strings) {


            if (urlOfMyPhotos.size() == 0) {
                myMedia = service.urlOfMyPhotos(user.getUsername());


                for (int i = 0; i < myMedia.size(); i++) {

                    if (myMedia.get(i).image_versions2 == null) {
                        urlOfMyPhotos.add(myMedia.get(i).carousel_media.get(0).image_versions2.candidates.get(0).url);
                    } else {
                        urlOfMyPhotos.add(myMedia.get(i).image_versions2.candidates.get(0).url);
                    }
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            pd.dismiss();
            //myMediasFragment.gridView.setAdapter(new ImageAdapter(getActivity(),urlOfMyPhotos));

        }
    }*/

}
