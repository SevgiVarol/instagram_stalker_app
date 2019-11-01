package com.example.appinsta.UserPage;


import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appinsta.CustomView;
import com.example.appinsta.R;
import com.example.appinsta.SearchFragment;
import com.example.appinsta.service.InstagramService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

import static com.example.appinsta.Compare.compare;


public class UserProfile extends Fragment {

    ImageView profilPic;
    TextView tvFollowingCount, tvFollowersCount, tvMediaCount, tvFullname;
    public InstagramUserSummary user;
    CustomView customViewUserStalkers, customViewUserStalking;
    ViewPager viewPager;
    List<InstagramUserSummary> userStalkingList = new ArrayList<>();
    List<InstagramUserSummary> userStalkersList = new ArrayList<>();
    InstagramService service = InstagramService.getInstance();
    TabLayout tabLayout;

    ArrayList<Uri> storyUrlList;
    List<InstagramFeedItem> stories;
    ProgressBar cycleProgressBar;

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
        View view = inflater.inflate(R.layout.user_profile_page, container, false);

       initComponents(view);

        InstagramUser userSum = service.getUser(user.getUsername());
        Glide.with(getActivity()) //1
                .load(user.getProfile_pic_url()).into(profilPic);

        tvFullname.setText(userSum.getFull_name());
        tvMediaCount.setText(String.valueOf(userSum.getMedia_count()));
        tvFollowersCount.setText(String.valueOf(userSum.getFollower_count()));
        tvFollowingCount.setText(String.valueOf(userSum.getFollowing_count()));

        tabLayout.addTab(tabLayout.newTab().setText("gönderiler"));
        tabLayout.addTab(tabLayout.newTab().setText("beğendiği gönderilerim"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        UserProfilePagerAdapter userProfilePagerAdapter = new UserProfilePagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(), userSum);

        viewPager.setAdapter(userProfilePagerAdapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        showUserStalkersAndStalking();
        showStories();

        return view;
    }

    private void initComponents(View view) {

        tvFullname = (TextView) view.findViewById(R.id.tvFullname);
        profilPic = (CircleImageView) view.findViewById(R.id.userProfilPic);
        tvFollowingCount = (TextView) view.findViewById(R.id.tvFollowingNum);
        tvFollowersCount =(TextView) view.findViewById(R.id.tvFollowersNum);
        tvMediaCount = (TextView) view.findViewById(R.id.tvMediaNum);

        customViewUserStalkers = (CustomView) view.findViewById(R.id.customViewUsersStalkers);
        customViewUserStalking = (CustomView) view.findViewById(R.id.customViewUsersStalkings);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        cycleProgressBar = view.findViewById(R.id.progressBar);

    }
    private void showUserStalkersAndStalking() {

        customViewUserStalkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new userStalkersTask().execute();

            }
        });

        customViewUserStalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new userStalkingTask().execute();
            }
        });
    }
    private void showStories(){
        profilPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new storyTask().execute();
            }
        });
    }

    private class userStalkingTask extends AsyncTask<String, String, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getContext());
            pd.setMessage("geri takip etmedikleri yükleniyor...");
            pd.show();

        }


        @Override
        protected String doInBackground(String... strings) {

            if (userStalkingList.isEmpty()) {
                userStalkingList = compare(service.getFollowers(user.getPk()), service.getFollowing(user.getPk()));
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            SearchFragment fragment = new SearchFragment(userStalkingList);
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.linearLayout, fragment).addToBackStack("tag").commit();

        }
    }

    private class userStalkersTask extends AsyncTask<String, String, String> {


        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getContext());
            pd.setMessage("geri takip etmeyenler yükleniyor...");
            pd.show();

        }


        @Override
        protected String doInBackground(String... strings) {

            if (userStalkersList.isEmpty()) {
                userStalkersList = compare(service.getFollowing(user.getPk()), service.getFollowers(user.getPk()));
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            SearchFragment fragment = new SearchFragment(userStalkersList);
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.linearLayout, fragment).addToBackStack("tag").commit();
        }
    }

    private class storyTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute(){
            //Cycle progress bar
            cycleProgressBar.setIndeterminate(true);

        }
        @Override
        protected String doInBackground(String... strings) {
            if (storyUrlList ==null){
                storyUrlList = service.getStories(user.username);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s){
            cycleProgressBar.setIndeterminate(false);
            Intent storyIntent = new Intent(getContext(), StoryViewer.class);
            storyIntent.putExtra("storyUrlList", storyUrlList);
            if (storyUrlList !=null & storyUrlList.size()!=0) {
                startActivity(storyIntent);
            }
        }
    }

}


   /*class myMediaAsync extends AsyncTask<String, String, String> {

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

            media = service.urlOfMyPhotos(user.getUsername());

            if (urlOfMyPhotos.size() == 0) {
                myMedia = service.urlOfMyPhotos(user.getUsername());

            for (int i = 0; i < media.size(); i++) {

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
