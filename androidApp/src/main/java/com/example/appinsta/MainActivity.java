package com.example.appinsta;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appinsta.database.InstaDatabase;
import com.example.appinsta.enums.UserListTypes;
import com.example.appinsta.medialog.MediaLogs;
import com.example.appinsta.service.InstagramService;
import com.example.appinsta.uiComponent.CustomView;
import com.example.appinsta.userpage.StoryViewer;
import com.example.appinsta.userpage.UserMediaFragment;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramStoryTray;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

import static com.example.appinsta.utils.Util.ConvertShortenNumber;

public class MainActivity extends AppCompatActivity implements Serializable {

    InstagramService service = InstagramService.getInstance();
    CustomView latestPhotoLikers, usersStalkers, usersStalking;

    RecyclerView storyTrayRecyclerView;
    ImageView profilPic, latestPhoto;
    LinearLayout followingLayout, followersLayout;
    TextView tvFollowing, tvFollowers;

    ProgressBar mProgress = null, storyProgress;
    ProgressBar userStoryProgress;
    Drawable drawable = null;
    InstagramUser myUser;
    InstaDatabase instaDatabase = InstaDatabase.getInstance(this);

    ArrayList<Uri> storyUrlList = new ArrayList<>();

    ArrayList<InstagramUser> userStoriesUrlList = new ArrayList<>();
    ArrayList<String> storyIds;
    List<InstagramFeedItem> stories;
    BottomNavigationView bottomNavigationView;
    ViewPager mainViewPager;
    MainPageViewPagerAdapter mainPagerAdapter;
    Button collapsedMenuButton;
    List<InstagramStoryTray> userStoriesTrayList = new ArrayList<>();
    StoryTrayRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();

        new getLoggedUserBasicInfoTask().execute();
        initRecyclerView();

        adapter.setOnItemClickListener(new StoryTrayRecyclerAdapter.OnListener() {
            @Override
            public void onClick(View view, int position) {
                if (storyTrayRecyclerView.isClickable()) {
                    storyTrayRecyclerView.setClickable(false);
                    userStoryProgress = view.findViewById(R.id.progress_bar_story);
                    getUsersStoriesTask userStoryTask = new getUsersStoriesTask(position);
                    userStoryTask.execute();
                }
            }
        });

        followersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchActivity = new Intent(getApplicationContext(), SearchActivity.class);
                searchActivity.putExtra("listType", UserListTypes.FOR_MY_FOLLOWERS);
                startActivity(searchActivity);
            }
        });

        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchActivity = new Intent(getApplicationContext(), SearchActivity.class);
                searchActivity.putExtra("listType", UserListTypes.FOR_MY_FOLLOWINGS);
                startActivity(searchActivity);
            }
        });

        latestPhotoLikers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchActivity = new Intent(getApplicationContext(), SearchActivity.class);
                searchActivity.putExtra("listType", UserListTypes.FOR_MY_LAST_PHOTO_LIKERS);
                startActivity(searchActivity);
            }
        });

        usersStalkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchActivity = new Intent(getApplicationContext(), SearchActivity.class);
                searchActivity.putExtra("listType", UserListTypes.FOR_MY_STALKERS);
                startActivity(searchActivity);
            }
        });
        usersStalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchActivity = new Intent(getApplicationContext(), SearchActivity.class);
                searchActivity.putExtra("listType", UserListTypes.FOR_MY_STALKINGS);
                startActivity(searchActivity);
            }
        });
        profilPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilPic.setClickable(false);
                new storyTask().execute();
            }
        });
        collapsedMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpsideOptionMenu(view);
            }
        });
    }

    private void initRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        storyTrayRecyclerView = findViewById(R.id.storyTrayRecyclerView);
        storyTrayRecyclerView.setClickable(true);
        storyTrayRecyclerView.setLayoutManager(layoutManager);
        adapter = new StoryTrayRecyclerAdapter(this, userStoriesUrlList);
        storyTrayRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (isTaskRoot()) {
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeIntent);
                    return true;
                } else {
                    super.onKeyDown(keyCode, event);
                    return false;
                }

            default:
                super.onKeyDown(keyCode, event);
                return false;
        }

    }

    public void showUpsideOptionMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main_options_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.logout_option) {
                    new logout().execute();
                }
                return false;
            }
        });
    }

    private void initComponent() {

        mProgress = findViewById(R.id.progress_bar);
        Resources res = getResources();
        drawable = res.getDrawable(R.drawable.circle_shape);

        profilPic = (CircleImageView) findViewById(R.id.userProfilPic);
        tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        tvFollowers = (TextView) findViewById(R.id.tvFollowers);

        followingLayout = (LinearLayout) findViewById(R.id.followingLayout);
        followersLayout = (LinearLayout) findViewById(R.id.followersLayout);

        latestPhoto = (ImageView) findViewById(R.id.latestPhoto);

        latestPhotoLikers = (CustomView) findViewById(R.id.latestPhotoLikers);

        usersStalkers = (CustomView) findViewById(R.id.userStalkers);
        usersStalking = (CustomView) findViewById(R.id.userStalking);

        storyProgress = findViewById(R.id.progressBar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        mainViewPager = findViewById(R.id.main_pager);
        mainPagerAdapter = new MainPageViewPagerAdapter();
        collapsedMenuButton = findViewById(R.id.collapsedMenu);

    }

    private class getLoggedUserBasicInfoTask extends AsyncTask<String, String, InstagramUser> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
            storyUrlList = new ArrayList<>();
            storyIds = new ArrayList<>();

            mainViewPager.setAdapter(mainPagerAdapter);
            mainViewPager.setCurrentItem(0);
        }

        @Override
        protected InstagramUser doInBackground(String... strings) {

            try {
                userStoriesTrayList = service.getTrayStories();
                for (int i = 0; i < userStoriesTrayList.size(); i++){
                    userStoriesUrlList.add(userStoriesTrayList.get(i).getUser());}
                return service.getLoggedUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(InstagramUser loggedUser) {
            super.onPostExecute(loggedUser);
            myUser = loggedUser;
            UserMediaFragment myAllMediaFragment = new UserMediaFragment(myUser);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.layoutMedia, myAllMediaFragment).commit();

            tvFollowing.setText(String.valueOf(ConvertShortenNumber(myUser.following_count)));
            tvFollowers.setText(String.valueOf(ConvertShortenNumber(myUser.follower_count)));

            latestPhoto.setAlpha(0.3f);

            try {
                if (service.getLoggedUser().getMedia_count() != 0) {
                    Glide.with(getApplication()).load(service.getLoggedUserLastMediaUrl()).transform(new CenterCrop(), new VignetteFilterTransformation(new PointF(0.5f, 0.0f), new float[]{0f, 0f, 0f}, 0.5f, 0.9f)).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                latestPhoto.setBackground(resource);
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Glide.with(getApplication()) //1
                    .load(myUser.getProfile_pic_url()).into(profilPic);
            mProgress.setVisibility(View.GONE);
            storyProgress.setVisibility(View.VISIBLE);

            new getMainViewPagerComponents().execute();
        }

    }

    private class logout extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            instaDatabase.loggedUserDao().deleteLogged();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Intent backToLogin = new Intent(getApplicationContext(), LoginPage.class);
            service.logout();
            finish();
            startActivity(backToLogin);
        }
    }

    private class storyTask extends AsyncTask<String, String, String> {
        long userId;

        @Override
        protected void onPreExecute() {
            storyProgress.setIndeterminate(true);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                stories = service.getStories(service.getLoggedUser().pk);
                if (storyUrlList.size() == 0) {
                        for (int counter = 0; counter < stories.size(); counter++) {
                            if (stories.get(counter).getVideo_versions() != null) {
                                storyUrlList.add(Uri.parse(stories.get(counter).getVideo_versions().get(0).getUrl()));
                            } else {
                                storyUrlList.add(Uri.parse(stories.get(counter).getImage_versions2().getCandidates().get(0).getUrl()));
                            }
                            storyIds.add(String.valueOf(stories.get(counter).pk));
                        }
                }
                userId = service.getLoggedUser().pk;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String userPk) {
            Intent mediaLogIntent = new Intent(getApplicationContext(), MediaLogs.class);
            mediaLogIntent.putExtra("storyUrlList", storyUrlList);
            mediaLogIntent.putExtra("userId", userId);
            mediaLogIntent.putExtra("storyIds", storyIds);

            if (storyUrlList != null & storyUrlList.size() != 0) {

                startActivity(mediaLogIntent);
            } else {
                Toast.makeText(getApplicationContext(), R.string.story_not_found, Toast.LENGTH_SHORT).show();
            }
            storyProgress.setIndeterminate(false);
            profilPic.setClickable(true);
        }
    }

    private class getMainViewPagerComponents extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.action_home) {
                        mainViewPager.setCurrentItem(0);
                    } else if (item.getItemId() == R.id.action_media) {
                        mainViewPager.setCurrentItem(1);
                    }
                    return false;
                }
            });
            mainViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

    }

    private class getUsersStoriesTask extends AsyncTask<String, String, ArrayList<Uri>> {
        ArrayList<Uri> userStoryUrlList = new ArrayList<>();
        int position;

        public getUsersStoriesTask(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            userStoryProgress.setIndeterminate(true);
        }

        @Override
        protected ArrayList<Uri> doInBackground(String... strings) {
            try {
                return service.getStories(userStoriesTrayList.get(position).getUser().username);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Uri> storyUrlList) {
            super.onPostExecute(storyUrlList);
            userStoryUrlList = storyUrlList;
            Intent storyIntent = new Intent(getApplicationContext(), StoryViewer.class);

            storyIntent.putExtra("storyUrlList", userStoryUrlList);
            if (userStoryUrlList != null & userStoryUrlList.size() != 0) {
                startActivity(storyIntent);
            } else {
                Toast.makeText(getApplicationContext(), R.string.story_not_found, Toast.LENGTH_SHORT).show();
            }
            userStoryProgress.setIndeterminate(false);
            storyTrayRecyclerView.setClickable(true);
        }

    }
}

