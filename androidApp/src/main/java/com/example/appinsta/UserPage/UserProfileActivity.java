package com.example.appinsta.UserPage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.appinsta.CustomView;
import com.example.appinsta.R;
import com.example.appinsta.SearchActivity;

import com.example.appinsta.service.InstagramService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

import static com.example.appinsta.Compare.compare;

public class UserProfileActivity extends AppCompatActivity {

    ImageView profilPic;
    TextView tvFollowingCount, tvFollowersCount, tvMediaCount, tvFullname;
    public InstagramUserSummary user;
    CustomView customViewUserStalkers, customViewUserStalking;
    ViewPager viewPager;
    List<InstagramUserSummary> userStalkingList = new ArrayList<>();
    List<InstagramUserSummary> userStalkersList = new ArrayList<>();
    InstagramService service = InstagramService.getInstance();
    TabLayout tabLayout;
    private UserProfilePagerAdapter userProfilePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        user = (InstagramUserSummary) getIntent().getSerializableExtra("user");
        InstagramUser userSum = service.getUser(user.getUsername());

        initComponents();

        Glide.with(getApplicationContext()) //1
                .load(user.getProfile_pic_url()).into(profilPic);

        tvFullname.setText(userSum.getFull_name());
        tvMediaCount.setText(String.valueOf(userSum.getMedia_count()));
        tvFollowersCount.setText(String.valueOf(userSum.getFollower_count()));
        tvFollowingCount.setText(String.valueOf(userSum.getFollowing_count()));

        tabLayout.addTab(tabLayout.newTab().setText("gönderiler"));
        tabLayout.addTab(tabLayout.newTab().setText("beğendiği gönderilerim"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        userProfilePagerAdapter = new UserProfilePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), userSum);
        viewPager.setAdapter(userProfilePagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        showUserStalkersAndStalking();

    }


    private void initComponents() {

        tvFullname = (TextView) findViewById(R.id.tvFullname);
        profilPic = (CircleImageView) findViewById(R.id.userProfilPic);
        tvFollowingCount = (TextView) findViewById(R.id.tvFollowingNum);
        tvFollowersCount = (TextView) findViewById(R.id.tvFollowersNum);
        tvMediaCount = (TextView) findViewById(R.id.tvMediaNum);

        customViewUserStalkers = (CustomView) findViewById(R.id.customViewUsersStalkers);
        customViewUserStalking = (CustomView) findViewById(R.id.customViewUsersStalkings);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

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

    private class userStalkingTask extends AsyncTask<String, String, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(UserProfileActivity.this);
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
            Intent i = new Intent(getApplicationContext(), SearchActivity.class);
            i.putExtra("userList", (Serializable) userStalkingList);
            startActivity(i);

        }
    }

    private class userStalkersTask extends AsyncTask<String, String, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(UserProfileActivity.this);
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
            Intent i = new Intent(getApplicationContext(), SearchActivity.class);
            i.putExtra("userList", (Serializable) userStalkersList);
            startActivity(i);
        }
    }

}
