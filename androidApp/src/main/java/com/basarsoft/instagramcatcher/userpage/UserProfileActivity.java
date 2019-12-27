package com.basarsoft.instagramcatcher.userpage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.basarsoft.instagramcatcher.R;
import com.basarsoft.instagramcatcher.SearchActivity;
import com.basarsoft.instagramcatcher.enums.UserListTypes;
import com.basarsoft.instagramcatcher.service.InstagramService;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

import static com.basarsoft.instagramcatcher.utils.Util.ConvertShortenNumber;

public class UserProfileActivity extends AppCompatActivity {

    ImageView profilPic;
    TextView tvFollowingCount, tvFollowersCount, tvMediaCount, tvFullname;
    public InstagramUserSummary user;
    Button btnStalking, btnStalkers;
    ViewPager viewPager;
    List<InstagramUserSummary> userStalkingList = null;
    List<InstagramUserSummary> userStalkersList = null;
    List<InstagramUserSummary> userFollowingList = null;
    List<InstagramUserSummary> userFollowersList = null;
    InstagramService service = InstagramService.getInstance();
    TabLayout tabLayout;
    LinearLayout lyFollowingCount, lyFollowersCount;
    ProgressDialog dialog;
    ArrayList<Uri> storyUrlList;
    List<InstagramFeedItem> stories;
    ProgressBar cycleProgressBar;

    private UserProfilePagerAdapter userProfilePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_user_profile);

        user = (InstagramUserSummary) getIntent().getSerializableExtra("myUser");
        InstagramUser userSum = null;

        actionBar.setTitle(user.username);

        initComponents();

        Glide.with(getApplicationContext()) //1
                .load(user.getProfile_pic_url()).into(profilPic);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.medias));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.user_liked_my_posts));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        try {
            userSum = service.getUser(user.getUsername());
            if (userSum.getFull_name().isEmpty()) {
                tvFullname.setText(userSum.getUsername());
            } else {
                tvFullname.setText(userSum.getFull_name());
            }

            tvMediaCount.setText(String.valueOf(ConvertShortenNumber(userSum.media_count)));
            tvFollowersCount.setText(String.valueOf(ConvertShortenNumber(userSum.follower_count)));
            tvFollowingCount.setText(String.valueOf(ConvertShortenNumber(userSum.following_count)));
            userProfilePagerAdapter = new UserProfilePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), userSum);
        } catch (UnknownHostException e) {
            Toast.makeText(getApplicationContext(), R.string.check_network_connection, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        lyFollowingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserListTypes userListTypes = UserListTypes.FOR_USERS_FOLLOWINGS;
                startSearchActivityWithEnum(userListTypes, user.getPk());
            }
        });
        lyFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserListTypes userListTypes = UserListTypes.FOR_USERS_FOLLOWERS;
                startSearchActivityWithEnum(userListTypes, user.getPk());
            }
        });

        showUserStalkersAndStalking();
        showStories();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    private void initComponents() {

        tvFullname = (TextView) findViewById(R.id.tvFullname);
        profilPic = (CircleImageView) findViewById(R.id.userProfilPic);
        tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
        tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        tvMediaCount = (TextView) findViewById(R.id.tvMediaNum);

        btnStalkers = findViewById(R.id.btnStalkers);
        btnStalking = findViewById(R.id.btnStalking);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        cycleProgressBar = findViewById(R.id.progressBar);

        lyFollowersCount = findViewById(R.id.lyFollowersCount);
        lyFollowingCount = findViewById(R.id.lyFollowingCount);

    }

    private void showUserStalkersAndStalking() {

        btnStalkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserListTypes userListTypes = UserListTypes.FOR_USERS_STALKERS;
                startSearchActivityWithEnum(userListTypes, user.getPk());
            }
        });

        btnStalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserListTypes userListTypes = UserListTypes.FOR_USERS_STALKINGS;
                startSearchActivityWithEnum(userListTypes, user.getPk());
            }
        });
    }

    public void startSearchActivityWithEnum(UserListTypes listType, long pk) {
        Intent searchActivity = new Intent(getApplicationContext(), SearchActivity.class);
        searchActivity.putExtra("listType", listType);
        searchActivity.putExtra("userId", pk);
        startActivity(searchActivity);
    }

    private void showStories() {
        profilPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilPic.setClickable(false);
                new storyTask().execute();
            }
        });
    }

    private class storyTask extends AsyncTask<String, String, ArrayList<Uri>> {

        @Override
        protected void onPreExecute() {
            //Cycle progress bar
            cycleProgressBar.setIndeterminate(true);

        }

        @Override
        protected ArrayList<Uri> doInBackground(String... strings) {
            if (storyUrlList == null) {
                try {
                    return service.getStories(user.username);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Uri> urlList) {
            storyUrlList = urlList;
            Intent storyIntent = new Intent(getApplicationContext(), StoryViewer.class);
            storyIntent.putExtra("storyUrlList", storyUrlList);
            if (storyUrlList != null & storyUrlList.size() != 0) {
                startActivity(storyIntent);
            } else {
                Toast.makeText(getApplicationContext(), R.string.story_not_found, Toast.LENGTH_SHORT).show();
            }
            cycleProgressBar.setIndeterminate(false);
            profilPic.setClickable(true);

        }
    }
    public void showList(List<InstagramUserSummary> userList) {
        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
        i.putExtra("userList", (Serializable) userList);
        startActivity(i);
    }
}
