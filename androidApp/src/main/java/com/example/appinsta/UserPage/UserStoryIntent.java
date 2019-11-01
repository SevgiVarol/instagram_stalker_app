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
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appinsta.R;
import com.example.appinsta.service.InstagramService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class UserStoryIntent extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    public int storyCount = 0, storyCountForVideo = 0,oldStoryCount=0;
    public int videoDuration;
    VideoView videoView;
    ImageView storyImageView;
    ArrayList<Uri> listUri;
    int duration;
    private StoriesProgressView storiesProgressView;
    InstagramService service = InstagramService.getInstance();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_popup);
        videoView = findViewById(R.id.videoView);
        storyImageView = findViewById(R.id.storyImageView);

        Intent intent = getIntent();
        listUri = (ArrayList<Uri>) intent.getSerializableExtra("listUri");
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(listUri.size()); // <- set stories
        storiesProgressView.setStoryDuration(3000L); // <- set a story duration
        storiesProgressView.setStoriesListener(this); // <- set listener
        VideoViewTouchListener();

        videoView.setOnTouchListener(new View.OnTouchListener() {
            float y_down, y_up, x_down;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        y_down = event.getRawY();
                        x_down = event.getRawX();
                        break;
                    case MotionEvent.ACTION_UP:
                        y_up = event.getRawY();
                        if (y_down - y_up > 500 & y_up != 0) {
                        } else if (y_up - y_down > 500 & y_up != 0) {
                            finish();
                        } else if (x_down > 800) {
                            storiesProgressView.skip();
                            storiesProgressView.pause();
                            VideoViewTouchListener();
                        } else if (x_down < 200) {
                            storyCount = storyCount - 2;
                            try {
                                storiesProgressView.startStories(storyCount);
                                storiesProgressView.reverse();
                            }catch (Exception e){}

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
                storiesProgressView.pause();
                VideoViewTouchListener();
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = mp.getDuration();
                videoDuration = videoView.getDuration();
                Log.d("tag", String.format("onPrepared: duration=%d, videoDuration=%d", duration, videoDuration));
                storiesProgressView.setStoryDuration(duration);
                storiesProgressView.startStories(storyCountForVideo);
            }
        });
    }

    private void VideoViewTouchListener() {
        if (storyCount < listUri.size() & storyCount >= 0) {
            oldStoryCount=storyCount;
            Picasso
                    .with(getApplicationContext())
                    .load((listUri.get(storyCount)).toString())
                    .into(storyImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            storiesProgressView.startStories(storyCount);
                            videoView.setVisibility(View.GONE);
                            videoView.setVisibility(View.VISIBLE);
                            storyImageView.setVisibility(View.VISIBLE);
                            storyCount++;
                        }

                        @Override
                        public void onError() {
                            storyCountForVideo = storyCount;
                            storyImageView.setVisibility(View.INVISIBLE);
                            videoView.setVideoURI(listUri.get(storyCount));
                            videoView.requestFocus();
                            videoView.start();
                            storyCount++;
                        }
                    });
        } else if(storyCount<0) {
           storyCount=0;VideoViewTouchListener();
        }
        else { finish();}
    }

    @Override
    public void onNext() {
        VideoViewTouchListener();
    }

    @Override
    public void onPrev() {
    }

    @Override
    public void onComplete() {

        //onNext();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}
