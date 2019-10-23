package com.example.appinsta.UserPage;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appinsta.R;
import com.example.appinsta.service.InstagramService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class UserStoryIntent extends AppCompatActivity {
    public int storyCount = 0;
    public int videoDuration;
    VideoView videoView;
    ImageView storyImageView;
    SeekBar seekBar;
    ArrayList<Uri> listUri;
    int duration;
    InstagramService service = InstagramService.getInstance();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_popup);
        videoView = findViewById(R.id.videoView);
        storyImageView = findViewById(R.id.storyImageView);
        seekBar = findViewById(R.id.seekBar);
        seekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        Intent intent = getIntent();
        listUri = (ArrayList<Uri>) intent.getSerializableExtra("listUri");
        seekBar.setMax(listUri.size());
        VideoViewTouchListener();

        videoView.setOnTouchListener(new View.OnTouchListener() {
            float y_down, y_up, x_down;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        y_down = event.getRawY();
                        x_down = event.getRawX();
                        System.out.println("xdown:" + x_down);
                        break;
                    case MotionEvent.ACTION_UP:
                        y_up = event.getRawY();
                        if (y_down - y_up > 500 & y_up != 0) {
                            System.out.println("yukarı");
                        } else if (y_up - y_down > 500 & y_up != 0) {
                            System.out.println("aşağı");
                            finish();
                        } else if (x_down > 800) {
                            VideoViewTouchListener();
                        } else if (x_down < 200) {
                            storyCount = storyCount - 2;
                            VideoViewTouchListener();
                        }
                        break;
                }
                return true;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                VideoViewTouchListener();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = mp.getDuration();
                videoDuration = videoView.getDuration();
                Log.d("tag", String.format("onPrepared: duration=%d, videoDuration=%d", duration, videoDuration));
            }
        });
    }

    private void VideoViewTouchListener() {
        if (storyCount < listUri.size() & storyCount >= 0) {
            Picasso
                    .with(getApplicationContext())
                    .load((listUri.get(storyCount)).toString())
                    .into(storyImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            seekBar.setProgress(storyCount);
                            videoView.setVisibility(View.GONE);
                            videoView.setVisibility(View.VISIBLE);
                            storyImageView.setVisibility(View.VISIBLE);
                            storyCount++;
                        }

                        @Override
                        public void onError() {
                            storyImageView.setVisibility(View.INVISIBLE);
                            seekBar.setProgress(storyCount);
                            videoView.setVideoURI(listUri.get(storyCount));
                            videoView.requestFocus();
                            videoView.start();
                            storyCount++;
                        }
                    });
        } else {
            finish();
        }
    }
}
