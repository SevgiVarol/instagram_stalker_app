package com.example.appinsta.userpage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appinsta.R;
import com.example.appinsta.SearchActivity;
import com.example.appinsta.service.InstagramService;
import com.example.appinsta.uiComponent.CustomView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
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
    List<InstagramUserSummary> userFollowingList = new ArrayList<>();
    List<InstagramUserSummary> userFollowersList = new ArrayList<>();
    InstagramService service = InstagramService.getInstance();
    TabLayout tabLayout;
    LinearLayout lyFollowingNum,lyFollowersNum;
    ProgressDialog dialog;
    ArrayList<Uri> storyUrlList;
    List<InstagramFeedItem> stories;
    ProgressBar cycleProgressBar;

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
        lyFollowingNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getUserFollowingsTask().execute();
            }
        });
        lyFollowersNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getUserFollowersTask().execute();
            }
        });

        showUserStalkersAndStalking();
        showStories();

    }


    private void initComponents() {

        tvFullname = (TextView) findViewById(R.id.tvFullname);
        profilPic = (CircleImageView) findViewById(R.id.userProfilPic);
        tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
        tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        tvMediaCount = (TextView) findViewById(R.id.tvMediaNum);

        customViewUserStalkers = (CustomView) findViewById(R.id.customViewUsersStalkers);
        customViewUserStalking = (CustomView) findViewById(R.id.customViewUsersStalkings);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        cycleProgressBar = findViewById(R.id.progressBar);

        lyFollowersNum=findViewById(R.id.lyFollowersCount);
        lyFollowingNum=findViewById(R.id.lyFollowingCount);

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

    private class getUserFollowingsTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog =new ProgressDialog(UserProfileActivity.this);
            dialog.setMessage("Takip edilenler yükleniyor..");
            dialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            if(userFollowingList.isEmpty()){
                userFollowingList=service.getFollowing(user.getPk());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            showList(userFollowingList);

        }
    }

    private class getUserFollowersTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog =new ProgressDialog(UserProfileActivity.this);
            dialog.setMessage("Takipçiler yükleniyor..");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(userFollowersList.isEmpty()){
                userFollowersList=service.getFollowers(user.getPk());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            showList(userFollowersList);
        }
    }

    private class userStalkersTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(UserProfileActivity.this);
            dialog.setMessage("geri takip etmedikleri yükleniyor...");
            dialog.show();

        }


        @Override
        protected String doInBackground(String... strings) {

            if(userFollowingList.isEmpty()){
                getUserFollowingsAndFollowers();
            }
            if (userStalkersList.isEmpty()) {
                userStalkersList = compare(userFollowersList,userFollowingList);
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            showList(userStalkersList);

        }
    }

    private class userStalkingTask extends AsyncTask<String, String, String> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(UserProfileActivity.this);
            dialog.setMessage("geri takip etmeyenler yükleniyor...");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            if(userFollowingList.isEmpty()){
                getUserFollowingsAndFollowers();
            }

            if (userStalkingList.isEmpty()) {
                userStalkingList = compare(userFollowingList,userFollowersList);
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            showList(userStalkingList);
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
            Intent storyIntent = new Intent(getApplicationContext(), StoryViewer.class);
            storyIntent.putExtra("storyUrlList", storyUrlList);
            if (storyUrlList !=null & storyUrlList.size()!=0) {
                startActivity(storyIntent);
            }else {
                Toast.makeText(getApplicationContext(),"Hiçbir hikaye bulunamadı",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getUserFollowingsAndFollowers() {
        userFollowingList = service.getFollowing(user.getPk());
        userFollowersList = service.getFollowers(user.getPk());
    }

    public void showList(List<InstagramUserSummary> userList){
        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
        i.putExtra("userList", (Serializable) userList);
        startActivity(i);
    }
}
