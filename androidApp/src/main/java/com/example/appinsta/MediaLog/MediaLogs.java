package com.example.appinsta.MediaLog;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.appinsta.Compare;
import com.example.appinsta.R;
import com.example.appinsta.UserListAdapter;
import com.example.appinsta.service.InstagramService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class MediaLogs extends AppCompatActivity {
    InstagramService service;
    private UserListAdapter adapter;
    ArrayList<Uri> storyUrlList;
    long userId;
    ArrayList<String> storyIds;
    TabLayout tabLayout;
    EditText searchEdit;
    ViewPager pagerImage, pagerUser;
    ArrayList<List<InstagramUser>> observerList;
    List<InstagramUser> observers;
    List<InstagramUserSummary> followers;
    RecyclerView recyclerAll, recyclerNotFollow, recyclerNotWatch;
    String key = null;
    int positionMedia;
    ArrayList<List<InstagramUserSummary>> observerListForMedia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_logs);
        init();

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
                setLayouts(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        pagerImage.setClipToPadding(false);
        pagerImage.setPageMargin(100);
        pagerImage.setPadding(350, 0, 350, 0);
        pagerImage.setPageTransformer(true, new ViewPager.PageTransformer() {
            public static final float MAX_SCALE = 1.0f;
            public static final float MIN_SCALE = 0.7f;

            @Override
            public void transformPage(@NonNull View page, float position) {
                int paddingLeft = pagerImage.getPaddingLeft();
                int paddingRight = pagerImage.getPaddingRight();
                int pageWidth = pagerImage.getMeasuredWidth() - paddingLeft - paddingRight;

                float transformPos = (float) (page.getLeft() -
                        (pagerImage.getScrollX() + paddingLeft)) / pageWidth;

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

                    page.setScaleX(0.8f);
                    page.setScaleY(0.8f);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        page.getParent().requestLayout();
                    }
                }


            }
        });

        if (key == null) {
            observerList = new ArrayList<List<InstagramUser>>();
            for (int i = 0; i < storyIds.size(); i++) {
                observers = null;
                try {
                    observers = service.getStoryViewers(userId, storyIds.get(i));
                    observerList.add(observers);
                } catch (Exception e) {
                    Log.e(e.getMessage(), " StoryViewer returning null object");
                }
            }
            setLayouts(0);
        } else if (key.equals("MEDIA")) {
            observerListForMedia = new ArrayList<List<InstagramUserSummary>>();
            for (int i = 0; i < storyIds.size(); i++) {
                List<InstagramUserSummary> observersForMedia = null;
                try {
                    observersForMedia = service.getMyMediaLikers(Long.parseLong(storyIds.get(i)));
                    observerListForMedia.add(observersForMedia);
                } catch (Exception e) {
                    Log.e(e.getMessage(), " StoryViewer returning null object");
                }
            }System.out.println("tüm listelerin sayısı:"+observerListForMedia.size());
            for (int j=0;j<observerListForMedia.size();j++){
                System.out.println(j+". listenin beğenen sayısı:"+observerListForMedia.get(j).size());
                for (int k=0; k<observerListForMedia.get(j).size();k++){
                    System.out.println(j+".listenin "+k+". elemanının adı:"+observerListForMedia.get(j).get(k).username);
                }
            }
            pagerImage.setCurrentItem(positionMedia);
            setLayouts(positionMedia);

        }

    }

    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        storyUrlList = (ArrayList<Uri>) getIntent().getSerializableExtra("storyUrlList");
        userId = getIntent().getLongExtra("userId", 0);
        storyIds = getIntent().getStringArrayListExtra("storyIds");
        key = getIntent().getStringExtra("key");
        positionMedia = getIntent().getIntExtra("position",0);

        service = InstagramService.getInstance();
        tabLayout = findViewById(R.id.tabLayout);
        followers = service.getMyFollowers();

        //ViewPager configrations for image(story)
        pagerImage = findViewById(R.id.pager);
        pagerImage.setOffscreenPageLimit(storyUrlList.size() - 1);
        StoryImagePagerAdapter myImagePager = new StoryImagePagerAdapter(getApplicationContext(), storyUrlList);
        pagerImage.setAdapter(myImagePager);
        pagerImage.setCurrentItem(0);

        //ViewPager configrations for userlist(storyviewers)
        pagerUser = findViewById(R.id.pager2);
        pagerUser.setOffscreenPageLimit(2);
        StoryUserPagerAdapter myUserPager = new StoryUserPagerAdapter(getApplicationContext());
        pagerUser.setAdapter(myUserPager);
        pagerUser.setCurrentItem(0);
        pagerUser.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        recyclerAll = findViewById(R.id.recycler_view);
        recyclerNotFollow = findViewById(R.id.recycler_view2);
        recyclerNotWatch = findViewById(R.id.recycler_view3);
        recyclerAll = configureSizeRecyclerView(recyclerAll);
        recyclerNotFollow = configureSizeRecyclerView(recyclerNotFollow);
        recyclerNotWatch = configureSizeRecyclerView(recyclerNotWatch);
        searchEdit = (EditText) findViewById(R.id.editTextSearch);

        TextWatcher textListener = new TextWatcher() {
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
                Log.d("aaa", "burada");
                adapter.getFilter().filter(s);
            }
        };
        searchEdit.addTextChangedListener(textListener);
        searchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


    }

    public void setLayouts(int pos) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        if (observerList != null) {
            adapter = new UserListAdapter(observerList.get(pos), this);
            recyclerAll.setLayoutManager(layoutManager);
            recyclerAll.setAdapter(adapter);

            recyclerNotFollow.setLayoutManager(new LinearLayoutManager(this));
            List<InstagramUser> resultNotFollow = Compare.compareWatchedStoryAndUnfollowing(observerList.get(pos), followers);
            adapter = new UserListAdapter(resultNotFollow, this);
            recyclerNotFollow.setAdapter(adapter);

            recyclerNotWatch.setLayoutManager(new LinearLayoutManager(this));
            List<InstagramUserSummary> resultNotWatch = Compare.compareUnwatchedStoryAndFollowing(followers, observerList.get(pos));
            adapter = new UserListAdapter(resultNotWatch, this);
            recyclerNotWatch.setAdapter(adapter);
            /*adapter.setOnItemClickListener(new RecyclerSearch.OnListener() {
                @Override
                public void onClick(int position) {

                    UserProfile.urlOfUserPhotos.clear();
                    InstagramConstants.userProfile=true;
                    UserProfile fragment = new UserProfile(observers.get(position));
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.linearLayout, fragment).addToBackStack("tag").commit();

                }
            });*/
        }else if (observerListForMedia !=null){
            adapter = new UserListAdapter(observerListForMedia.get(pos), this);
            recyclerAll.setLayoutManager(layoutManager);
            recyclerAll.setAdapter(adapter);

            recyclerNotFollow.setLayoutManager(new LinearLayoutManager(this));
            List<InstagramUserSummary> resultNotFollow = Compare.compare(observerListForMedia.get(pos), followers);
            adapter = new UserListAdapter(resultNotFollow, this);
            recyclerNotFollow.setAdapter(adapter);

            recyclerNotWatch.setLayoutManager(new LinearLayoutManager(this));
            List<InstagramUserSummary> resultNotWatch = Compare.compare(followers, observerListForMedia.get(pos));
            adapter = new UserListAdapter(resultNotWatch, this);
            recyclerNotWatch.setAdapter(adapter);
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

}