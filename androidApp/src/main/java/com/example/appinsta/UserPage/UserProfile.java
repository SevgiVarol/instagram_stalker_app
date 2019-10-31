package com.example.appinsta.UserPage;


import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.appinsta.CustomView;
import com.example.appinsta.MainFragment;
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

import static com.example.appinsta.Compare.compare;


public class UserProfile extends Fragment {


    ImageView profilPic, latestPhoto;
    TextView takipTv, takipciTv, gönderiTv;
    GridView gridView;
    InstagramUserSummary user;
    CustomView usersStalkers, usersStalking;
    ViewPager viewPager;

    UserMediaFragment userMediaFragment = new UserMediaFragment();
    MyMediasFragment myMediasFragment = new MyMediasFragment();


    List<InstagramFeedItem> media = new ArrayList<>();
    InstagramService service = InstagramService.getInstance();
    public final static ArrayList<String> urlOfUserPhotos = new ArrayList<>();
    public final static ArrayList<String> urlOfMyPhotos = new ArrayList<>();
    TabLayout tabLayout;
    ArrayList<Uri> listUri;
    List<InstagramFeedItem> stories;

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
        List<InstagramUserSummary> followers, following, stalking, stalkers = new ArrayList<>();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            InstagramUser userSum = service.getUser(user.username);
            listUri = service.getStories(user.username);

            Glide.with(getActivity()) //1
                    .load(user.getProfile_pic_url()).into(profilPic);


            gönderiTv.setText(String.valueOf(userSum.getMedia_count()));
            takipciTv.setText(String.valueOf(userSum.getFollower_count()));
            takipTv.setText(String.valueOf(userSum.getFollowing_count()));


            tabLayout.addTab(tabLayout.newTab().setText("gönderiler"));

            tabLayout.addTab(tabLayout.newTab().setText("demo"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(pagerAdapter);
            viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());

                    if (tab.getPosition() == 1) {

                        new secAsync().execute();
//
                    }


                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {


                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            profilPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent storyIntent = new Intent(getContext(), UserStoryIntent.class);
                    storyIntent.putExtra("listUri", listUri);
                    if (listUri!=null &listUri.size()!=0) {
                        startActivity(storyIntent);
                    }
                }
            });

        }

        @Override
        protected String doInBackground(String... strings) {


            followers = service.getFollowers(user.getPk());
            following = service.getFollowing(user.getPk());
            stalking = compare(following, followers);
            stalkers = compare(followers, following);

            if (urlOfUserPhotos.size() == 0) {
                media = service.getMedias(user.getPk());


                for (int i = 0; i < media.size(); i++) {

                    if (media.get(i).image_versions2 == null) {
                        urlOfUserPhotos.add(media.get(i).carousel_media.get(0).image_versions2.candidates.get(0).url);
                    } else {
                        urlOfUserPhotos.add(media.get(i).image_versions2.candidates.get(0).url);
                    }
                }

                /*
                 */


           /* if(urlOfMyPhotos.size()==0) {
                media = service.urlOfMyPhotos(user.getUsername());


                for (int i = 0; i < media.size(); i++) {

                    if (media.get(i).image_versions2 == null) {
                        urlOfMyPhotos.add(media.get(i).carousel_media.get(0).image_versions2.candidates.get(0).url);
                    } else {
                        urlOfMyPhotos.add(media.get(i).image_versions2.candidates.get(0).url);
                    }
                }

            }*/
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            usersStalkers.setNumberText(String.valueOf(stalkers.size()));
            usersStalking.setNumberText(String.valueOf(stalking.size()));


            userMediaFragment.gridView.setAdapter(new ImageAdapter(getActivity(), urlOfUserPhotos));
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

    class secAsync extends AsyncTask<String, String, String> {

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


            for (int i = 0; i < media.size(); i++) {

                if (media.get(i).image_versions2 == null) {
                    urlOfMyPhotos.add(media.get(i).carousel_media.get(0).image_versions2.candidates.get(0).url);
                } else {
                    urlOfMyPhotos.add(media.get(i).image_versions2.candidates.get(0).url);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            pd.dismiss();
            myMediasFragment.gridView.setAdapter(new ImageAdapter(getActivity(), urlOfMyPhotos));

        }
    }
}
