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
import android.widget.Button;
import android.widget.ImageView;
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
    Button btnStalking,btnStalkers;
    ViewPager viewPager;
    List<InstagramUserSummary> userStalkingList = new ArrayList<>();
    List<InstagramUserSummary> userStalkersList = new ArrayList<>();
    InstagramService service = InstagramService.getInstance();
    TabLayout tabLayout;

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



        if(userSum.getFull_name().isEmpty()){
            tvFullname.setText(userSum.getUsername());
        }else {
            tvFullname.setText(userSum.getFull_name());
        }

        tvMediaCount.setText(String.valueOf(withSuffix(userSum.media_count)));
        tvFollowersCount.setText(String.valueOf(withSuffix(userSum.follower_count)));
        tvFollowingCount.setText(String.valueOf(withSuffix(userSum.following_count)));

        tabLayout.addTab(tabLayout.newTab().setText(R.string.user_media));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.user_liked_my_posts));
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
        showStories();

    }


    private void initComponents() {

        tvFullname = (TextView) findViewById(R.id.tvFullname);
        profilPic = (CircleImageView) findViewById(R.id.userProfilPic);
        tvFollowingCount = (TextView) findViewById(R.id.tvFollowingNum);
        tvFollowersCount = (TextView) findViewById(R.id.tvFollowersNum);
        tvMediaCount = (TextView) findViewById(R.id.tvMediaNum);

        btnStalkers=findViewById(R.id.btnStalkers);
        btnStalking=findViewById(R.id.btnStalking);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        cycleProgressBar = findViewById(R.id.progressBar);

    }

    private void showUserStalkersAndStalking() {

        btnStalkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new userStalkersTask().execute();
            }
        });

        btnStalking.setOnClickListener(new View.OnClickListener() {
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

    private class userStalkersTask extends AsyncTask<String, String, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(UserProfileActivity.this);
            String s = String.valueOf(R.string.user_stalkers_loading_message);
            pd.setMessage(s);
            pd.show();

        }


        @Override
        protected String doInBackground(String... strings) {

            if (userStalkersList.isEmpty()) {
                userStalkersList = compare(service.getFollowers(user.getPk()),service.getFollowing(user.getPk()));
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

    private class userStalkingTask extends AsyncTask<String, String, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(UserProfileActivity.this);
            String s = String.valueOf(R.string.user_stalkings_loading_message);
            pd.setMessage(s);
            pd.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            if (userStalkingList.isEmpty()) {
                userStalkingList = compare(service.getFollowing(user.getPk()),service.getFollowers(user.getPk()));
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
                Toast.makeText(getApplicationContext(),R.string.no_stories_toast,Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp-1));
    }

}
