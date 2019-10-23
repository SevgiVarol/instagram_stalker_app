package com.example.appinsta.MediaLog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.appinsta.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MediaLogs extends AppCompatActivity {
    ArrayList<Uri> listUri;
    TabLayout tabLayout;
    ViewPager pagerImage, pagerUser;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_logs);
        SetParameters();

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
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    public void SetParameters() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        listUri = (ArrayList<Uri>) getIntent().getSerializableExtra("listUri");
        tabLayout = findViewById(R.id.tabLayout);
        pagerImage = findViewById(R.id.pager);
        pagerImage.setOffscreenPageLimit(listUri.size() - 1);
        MyImagePager myImagePager = new MyImagePager(getApplicationContext(), listUri);
        pagerImage.setAdapter(myImagePager);
        pagerImage.setCurrentItem(0);

        pagerUser = findViewById(R.id.pager2);
        pagerUser.setOffscreenPageLimit(2);
        MyUserPager myUserPager = new MyUserPager(getApplicationContext());
        pagerUser.setAdapter(myUserPager);
        pagerUser.setCurrentItem(0);
        pagerUser.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }
}