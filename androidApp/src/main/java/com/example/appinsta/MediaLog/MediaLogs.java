package com.example.appinsta.MediaLog;

import android.annotation.SuppressLint;
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
    ArrayList<Uri> listUri;
    long userId;
    ArrayList<String> storyIds;
    TabLayout tabLayout;
    EditText searchEdit;
    ViewPager pagerImage, pagerUser;
    ArrayList<List<InstagramUser>> observer_list;
    List<InstagramUser> observers;
    List<InstagramUserSummary> followers;
    RecyclerView recycler_all,recycler_not_follow,recycler_not_watch;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_logs);
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
                SetLayouts(position);
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

        for (int i=0;i<storyIds.size();i++){
            observers=null;
            try {
                observers=service.getStoryViewers(userId,storyIds.get(i));
                observer_list.add(observers);
            }catch (Exception e){
                Log.e(e.getMessage()," StoryViewer returning null object");
            }
        }
        SetLayouts(0);

    }

    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        listUri = (ArrayList<Uri>) getIntent().getSerializableExtra("listUri");
        userId=getIntent().getLongExtra("userId", 0);
        storyIds=getIntent().getStringArrayListExtra("storyIds");
        followers= (List<InstagramUserSummary>) getIntent().getSerializableExtra("followers");
        observer_list=new ArrayList<List<InstagramUser>>();
        service = InstagramService.getInstance();
        tabLayout = findViewById(R.id.tabLayout);

        //ViewPager configrations for image(story)
        pagerImage = findViewById(R.id.pager);
        pagerImage.setOffscreenPageLimit(listUri.size() - 1);
        StoryImagePagerAdapter myImagePager = new StoryImagePagerAdapter(getApplicationContext(), listUri);
        pagerImage.setAdapter(myImagePager);
        pagerImage.setCurrentItem(0);

        //ViewPager configrations for userlist(storyviewers)
        pagerUser = findViewById(R.id.pager2);
        pagerUser.setOffscreenPageLimit(2);
        StoryUserPagerAdapter myUserPager = new StoryUserPagerAdapter(getApplicationContext());
        pagerUser.setAdapter(myUserPager);
        pagerUser.setCurrentItem(0);
        pagerUser.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        recycler_all = findViewById(R.id.recycler_view);
        recycler_not_follow = findViewById(R.id.recycler_view2);
        recycler_not_watch = findViewById(R.id.recycler_view3);
        recycler_all=ConfigureRecyclerView(recycler_all);
        recycler_not_follow=ConfigureRecyclerView(recycler_not_follow);
        recycler_not_watch=ConfigureRecyclerView(recycler_not_watch);
        searchEdit= (EditText) findViewById(R.id.editTextSearch);
        searchEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                int index=tabLayout.getSelectedTabPosition();
                switch (index){
                    case 0:
                        adapter= (UserListAdapter) recycler_all.getAdapter();
                        break;
                    case 1:
                        adapter= (UserListAdapter) recycler_not_follow.getAdapter();
                        break;
                    case 2:
                        adapter= (UserListAdapter) recycler_not_watch.getAdapter();
                }
                adapter.getFilter().filter(editable);
            }
        });

        searchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


    }

    public void SetLayouts(int pos){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        if(observers!=null) {
            adapter = new UserListAdapter(observer_list.get(pos), this);
            recycler_all.setLayoutManager(layoutManager);
            recycler_all.setAdapter(adapter);

            recycler_not_follow.setLayoutManager(new LinearLayoutManager(this));
            List<InstagramUser> resultNotFollow= Compare.compareWatchedStoryAndUnfollowing(observer_list.get(pos),followers);
            adapter = new UserListAdapter(resultNotFollow, this);
            recycler_not_follow.setAdapter(adapter);

            recycler_not_watch.setLayoutManager(new LinearLayoutManager(this));
            List<InstagramUserSummary> resultNotWatch= Compare.compareUnwatchedStoryAndFollowing(followers,observer_list.get(pos));
            adapter = new UserListAdapter(resultNotWatch, this);
            recycler_not_watch.setAdapter(adapter);
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
        }


    }

    public RecyclerView ConfigureRecyclerView(RecyclerView recyc){
        recyc.setHasFixedSize(true);
        recyc.setItemViewCacheSize(8);
        recyc.setDrawingCacheEnabled(true);
        recyc.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        return recyc;
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}