package com.example.appinsta.MediaLog;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.appinsta.Compare;
import com.example.appinsta.R;
import com.example.appinsta.UserListAdapter;
import com.example.appinsta.service.InstagramService;

import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class MediaLogs<T> extends AppCompatActivity {
    InstagramService service;
    private UserListAdapter adapter;
    ArrayList<Uri> getIntentUrlList;
    long userId;
    ArrayList<String> getIntentIdList;
    TabLayout tabLayout;
    EditText searchEdit;
    ViewPager pagerImage, pagerUser;
    List<List<T>> observerList;
    List<T> observers;
    List<InstagramUserSummary> followers;
    RecyclerView recyclerAll, recyclerNotFollow, recyclerNotWatch;
    String key = null;
    int positionMedia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_logs);

        new init().execute();

        if (key == null) {
            new setObserversForStory().execute();
        } else if (key.equals("MEDIA")) {
            new setObserversForMedia().execute();

        }
        pagerImage.setCurrentItem(positionMedia);
        setLayouts(positionMedia);
    }

    private class init extends AsyncTask<String, String, String> {
        StoryImagePagerAdapter myImagePager;
        StoryUserPagerAdapter myUserPager;
        TextWatcher textListener;

        @Override
        protected void onPreExecute() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

            observerList = new ArrayList<List<T>>();
            getIntentUrlList = (ArrayList<Uri>) getIntent().getSerializableExtra("storyUrlList");
            userId = getIntent().getLongExtra("userId", 0);
            getIntentIdList = getIntent().getStringArrayListExtra("storyIds");
            key = getIntent().getStringExtra("key");
            positionMedia = getIntent().getIntExtra("position", 0);

            service = InstagramService.getInstance();
            tabLayout = findViewById(R.id.tabLayout);


            //ViewPager configrations for image(story)
            pagerImage = findViewById(R.id.pager);
            pagerImage.setClipToPadding(false);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int paddingToSet = width / 5; //set this ratio according to how much of the next and previos screen you want to show.
            pagerImage.setPadding(paddingToSet, 0, paddingToSet, 0);


            pagerImage.setPageTransformer(true, new ViewPager.PageTransformer() {
                public static final float MAX_SCALE = 0.7f;
                public static final float MIN_SCALE = 1.0f;

                @Override
                public void transformPage(@NonNull View page, float position) {
                    int paddingLeft = pagerImage.getPaddingLeft();
                    int paddingRight = pagerImage.getPaddingRight();
                    int pageWidth = pagerImage.getMeasuredWidth() - paddingLeft - paddingRight;

                    float transformPos = (float) (page.getLeft() - (pagerImage.getScrollX() + paddingLeft)) / pageWidth;

                    if (transformPos <= 1) {
                        position = position < -1 ? -1 : position;
                        position = position > 1 ? 1 : position;

                        float tempScale = position < 0 ? 1 + position : 1 - position;

                        float slope = (MAX_SCALE - MIN_SCALE) / 1;
                        float scaleValue = MAX_SCALE - tempScale * slope;
                        page.setScaleX(scaleValue);
                        page.setScaleY(scaleValue);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            page.getParent().requestLayout();
                        }
                    } else {

                        page.setScaleX(0.7f);
                        page.setScaleY(0.7f);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            page.getParent().requestLayout();
                        }
                    }


                }
            });
            //ViewPager configrations for userlist(storyviewers)
            pagerUser = findViewById(R.id.pager2);
            pagerUser.setOffscreenPageLimit(2);
            pagerUser.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            myImagePager = new StoryImagePagerAdapter(getApplicationContext(), getIntentUrlList);
            myUserPager = new StoryUserPagerAdapter(getApplicationContext());
            pagerImage.setAdapter(myImagePager);
            pagerUser.setAdapter(myUserPager);


            recyclerAll = findViewById(R.id.recycler_view);
            recyclerNotFollow = findViewById(R.id.recycler_view2);
            recyclerNotWatch = findViewById(R.id.recycler_view3);

            searchEdit = (EditText) findViewById(R.id.editTextSearch);

        }


        @Override
        protected String doInBackground(String... strings) {
            followers = service.getMyFollowers();
            textListener = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int index = tabLayout.getSelectedTabPosition();
                    switch (index) {
                        case 0:
                            adapter = (UserListAdapter) recyclerAll.getAdapter();
                            break;
                        case 1:
                            adapter = (UserListAdapter) recyclerNotFollow.getAdapter();
                            break;
                        case 2:
                            adapter = (UserListAdapter) recyclerNotWatch.getAdapter();
                    }
                    adapter.getFilter().filter(s);
                }
            };

            recyclerAll = configureSizeRecyclerView(recyclerAll);
            recyclerNotFollow = configureSizeRecyclerView(recyclerNotFollow);
            recyclerNotWatch = configureSizeRecyclerView(recyclerNotWatch);


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            searchEdit.addTextChangedListener(textListener);
            searchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int index = tab.getPosition();
                    pagerUser.setCurrentItem(index);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
            pagerImage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    //Code Block (Get User Lists)
                    searchEdit.setText(null);
                    setLayouts(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }


    }

    public void setLayouts(int pos) {

        if (observerList != null) {
            try {
                recyclerAll.setLayoutManager(new LinearLayoutManager(this));
                adapter = new UserListAdapter(observerList.get(pos), this);
                recyclerAll.setAdapter(adapter);

                if (key == null) {
                    recyclerNotFollow.setLayoutManager(new LinearLayoutManager(this));
                    List<InstagramUser> resultNotFollow = Compare.compareWatchedStoryAndUnfollowing((List<InstagramUser>) observerList.get(pos), followers);
                    adapter = new UserListAdapter(resultNotFollow, this);
                    recyclerNotFollow.setAdapter(adapter);

                    recyclerNotWatch.setLayoutManager(new LinearLayoutManager(this));
                    List<InstagramUserSummary> resultNotWatch = Compare.compareUnwatchedStoryAndFollowing(followers, (List<InstagramUser>) observerList.get(pos));
                    adapter = new UserListAdapter(resultNotWatch, this);
                    recyclerNotWatch.setAdapter(adapter);

                } else if (key.equals("MEDIA")) {

                    recyclerNotFollow.setLayoutManager(new LinearLayoutManager(this));
                    List<InstagramUserSummary> resultNotFollow = Compare.compare((List<InstagramUserSummary>) observerList.get(pos), followers);
                    adapter = new UserListAdapter(resultNotFollow, this);
                    recyclerNotFollow.setAdapter(adapter);

                    recyclerNotWatch.setLayoutManager(new LinearLayoutManager(this));
                    List<InstagramUserSummary> resultNotWatch = Compare.compare(followers, (List<InstagramUserSummary>) observerList.get(pos));
                    adapter = new UserListAdapter(resultNotWatch, this);
                    recyclerNotWatch.setAdapter(adapter);
                }
            } catch (Exception e) {
                Log.e("error", "Somethings happened when layouts set. Retrying ");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setLayouts(pagerImage.getCurrentItem());
                    }
                }, 100);
            }

        } else {
            return;
        }


    }

    public RecyclerView configureSizeRecyclerView(RecyclerView recyc) {
        recyc.setHasFixedSize(true);
        recyc.setItemViewCacheSize(8);
        recyc.setDrawingCacheEnabled(true);
        recyc.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        return recyc;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class setObserversForMedia extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            tabLayout.getTabAt(0).setText("Tüm \nBeğenmeler");
            tabLayout.getTabAt(1).setText("Takip \nEtmeyenler");
            tabLayout.getTabAt(2).setText("Beğenmeyen \nTakipçilerim");
            for (int i = 0; i < positionMedia; i++) {
                observerList.add(null);
            }

            try {
                observers = new ArrayList<>((List<T>) service.getMediaLikers(Long.parseLong(getIntentIdList.get(positionMedia))));
                observerList.add(positionMedia, observers);
            } catch (Exception e) {
                Log.e(e.getMessage(), " MediaLikers returning null object");
            }
            for (int i = positionMedia + 1; i < getIntentIdList.size(); i++) {
                observerList.add(null);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            for (int i = 0; i < getIntentIdList.size(); i++) {

                try {
                    observers = new ArrayList<>((List<T>) service.getMediaLikers(Long.parseLong(getIntentIdList.get(i))));
                    observerList.set(i, observers);
                } catch (Exception e) {
                    Log.e(e.getMessage(), " MediaLikers returning null object");
                }
            }
            return null;
        }
    }

    private class setObserversForStory extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            tabLayout.getTabAt(0).setText("Tüm \nGörüntülemeler");
            tabLayout.getTabAt(1).setText("Takip \nEtmeyenler");
            tabLayout.getTabAt(2).setText("Bakmayan \nTakipçilerim");
            for (int i = 0; i < 2; i++) {
                try {
                    observers = (List<T>) service.getStoryViewers(userId, getIntentIdList.get(i));
                    observerList.add(observers);
                } catch (Exception e) {
                    Log.e(e.getMessage(), " StoryViewer returning null object");
                }
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            if (getIntentIdList.size() > 2) {
                for (int i = 2; i < getIntentIdList.size(); i++) {

                    try {
                        observers = (List<T>) service.getStoryViewers(userId, getIntentIdList.get(i));
                        observerList.add(i, observers);
                    } catch (Exception e) {
                        Log.e(e.getMessage(), " StoryViewer returning null object");
                    }
                }
            }
            return null;
        }

    }

}