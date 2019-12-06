package com.example.appinsta;

import android.app.FragmentManager;
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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.example.appinsta.medialog.MediaLogs;
import com.example.appinsta.service.InstagramService;
import com.example.appinsta.uiComponent.CustomView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

import static com.example.appinsta.Compare.compare;

public class MainActivity extends AppCompatActivity implements Serializable {

    InstagramService service = InstagramService.getInstance();
    CustomView mutedStory, latestPhotoLikers, storyStalkers, usersStalkers, usersStalking, userAction;
    List<InstagramUserSummary> mediaLikersList, followersList, followingList, stalkersList, stalkingList;

    ImageView profilPic, latestPhoto;
    LinearLayout followingLayout, followersLayout;
    TextView tvFollowing, tvFollowers;

    ProgressBar mProgress = null, storyProgress;
    Drawable drawable = null;
    InstagramUser user;
    InstaDatabase instaDatabase = InstaDatabase.getInstance(this);

    ArrayList<Uri> storyUrlList;
    ArrayList<String> storyIds;
    List<InstagramFeedItem> stories;
    BottomNavigationView bottomNavigationView;
    ViewPager mainViewPager;
    MainPageViewPagerAdapter mainPagerAdapter;
    Button collapsedMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();

        new setLoggedUserBasicInfoTask().execute();

        followersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("userList", (Serializable) followersList);
                startActivity(i);
            }
        });

        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("userList", (Serializable) followingList);
                startActivity(i);
            }
        });

        latestPhotoLikers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("userList", (Serializable) mediaLikersList);
                startActivity(i);
            }
        });

        usersStalkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("userList", (Serializable) stalkersList);
                startActivity(i);
            }
        });
        usersStalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("userList", (Serializable) stalkingList);
                startActivity(i);
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

        mutedStory = (CustomView) findViewById(R.id.mutedStory);
        storyStalkers = (CustomView) findViewById(R.id.storyStalkers);
        latestPhotoLikers = (CustomView) findViewById(R.id.latestPhotoLikers);
        userAction = (CustomView) findViewById(R.id.userAction);
        usersStalkers = (CustomView) findViewById(R.id.userStalkers);
        usersStalking = (CustomView) findViewById(R.id.userStalking);

        storyProgress = findViewById(R.id.progressBar);

        profilPic = (CircleImageView) findViewById(R.id.userProfilPic);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        mainViewPager = findViewById(R.id.main_pager);
        mainPagerAdapter = new MainPageViewPagerAdapter();
        collapsedMenuButton = findViewById(R.id.collapsedMenu);

    }

    private class setLoggedUserBasicInfoTask extends AsyncTask<String, String, String> {

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
        protected String doInBackground(String... strings) {

            user = service.getLoggedUser();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MyAllMediaFragment myAllMediaFragment = new MyAllMediaFragment();
            FragmentManager manager = getFragmentManager();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                manager.beginTransaction().replace(R.id.layoutMedia, myAllMediaFragment).commitNow();
            }

            tvFollowing.setText(String.valueOf(withSuffix(user.following_count)));
            tvFollowers.setText(String.valueOf(withSuffix(user.follower_count)));

            latestPhoto.setAlpha(0.3f);

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

            Glide.with(getApplication()) //1
                    .load(user.getProfile_pic_url()).into(profilPic);
            mProgress.setVisibility(View.GONE);
            storyProgress.setVisibility(View.VISIBLE);

            new getFollowingAndFollowersTask().execute();
            new getMainViewPagerComponents().execute();
        }

    }

    private class getFollowingAndFollowersTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            followersList = service.getMyFollowers();
            followingList = service.getMyFollowing();

            stalkingList = compare(followersList, followingList);
            stalkersList = compare(followingList, followersList);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (service.getLoggedUser().getMedia_count() != 0) {
                InstagramFeedItem item = (InstagramFeedItem) service.getLoggedUserMedias(null).Items.get(0);
                mediaLikersList = compare(followersList, service.getMediaLikers(item.pk));
            }
            if (mediaLikersList != null) {
                latestPhotoLikers.setNumberText(String.valueOf(mediaLikersList.size()));
            } else latestPhotoLikers.setNumberText(String.valueOf(0));

            usersStalkers.setNumberText(String.valueOf(stalkersList.size()));
            usersStalking.setNumberText(String.valueOf(stalkingList.size()));

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
        long userid;

        @Override
        protected void onPreExecute() {
            storyProgress.setIndeterminate(true);
        }

        @Override
        protected String doInBackground(String... strings) {
            stories = service.getStories(service.getLoggedUser().pk);
            if (storyUrlList.size() == 0) {
                try {
                    for (int counter = 0; counter < stories.size(); counter++) {
                        if (stories.get(counter).getVideo_versions() != null) {
                            storyUrlList.add(Uri.parse(stories.get(counter).getVideo_versions().get(0).getUrl()));
                        } else {
                            storyUrlList.add(Uri.parse(stories.get(counter).getImage_versions2().getCandidates().get(0).getUrl()));
                        }
                        storyIds.add(String.valueOf(stories.get(counter).pk));
                    }
                } catch (Exception e) {
                    Log.e("null object reference", e.getMessage());
                }
            }
            userid = service.getLoggedUser().pk;

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Intent mediaLogIntent = new Intent(getApplicationContext(), MediaLogs.class);
            mediaLogIntent.putExtra("storyUrlList", storyUrlList);
            mediaLogIntent.putExtra("userId", userid);
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
    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp-1));
    }
}
