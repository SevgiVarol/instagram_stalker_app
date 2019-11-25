package com.example.appinsta.uiComponent;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.example.appinsta.R;
import com.example.appinsta.service.InstagramService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryViewer extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    public int storyCount = 0, storyCountForVideo = 0, oldStoryCount = 0;
    public int videoDuration;
    VideoView videoView;
    ImageView storyImageView;
    ArrayList<Uri> storyUrlList;
    int duration;
    View reverse, skip;
    private StoriesProgressView storiesProgressView;
    InstagramService service = InstagramService.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_popup);
        videoView = findViewById(R.id.videoView);
        storyImageView = findViewById(R.id.storyImageView);

        Intent intent = getIntent();
        storyUrlList = (ArrayList<Uri>) intent.getSerializableExtra("storyUrlList");
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        reverse = findViewById(R.id.reverse);
        skip = findViewById(R.id.skip);

        storiesProgressView.setStoriesCount(storyUrlList.size()); // <- set stories
        storiesProgressView.setStoriesListener(this); // <- set listener
        VideoViewTouchListener();

        final long[] pressTime = {0L};
        long limit = 500L;

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            int currentPosition;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressTime[0] = System.currentTimeMillis();
                        if (videoView.getVisibility() == View.VISIBLE) {
                            videoView.pause();
                            currentPosition = videoView.getCurrentPosition();
                        }
                        storiesProgressView.pause();
                        return false;
                    case MotionEvent.ACTION_UP:
                        long now = System.currentTimeMillis();
                        if (videoView.getVisibility() == View.VISIBLE) {
                            videoView.seekTo(currentPosition);
                            videoView.start();
                        }
                        storiesProgressView.resume();
                        return limit < now - pressTime[0];
                }
                return false;
            }
        };
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.setStoryDuration(3000000L);
                if (videoView.getVisibility() == View.VISIBLE) {
                    videoView.pause();
                }
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.setStoryDuration(3000000L);
                if (videoView.getVisibility() == View.VISIBLE) {
                    videoView.pause();
                }
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

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
        if (storyCount < storyUrlList.size() & storyCount >= 0) {
            oldStoryCount = storyCount;
            Picasso
                    .with(getApplicationContext())
                    .load((storyUrlList.get(storyCount)).toString())
                    .into(storyImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            storiesProgressView.setStoryDuration(4000L);
                            storiesProgressView.startStories(storyCount);
                            videoView.setVisibility(View.INVISIBLE);
                            storyImageView.setVisibility(View.VISIBLE);
                            storyCount++;
                        }

                        @Override
                        public void onError() {
                            storyCountForVideo = storyCount;
                            videoView.setVisibility(View.VISIBLE);
                            storyImageView.setVisibility(View.INVISIBLE);
                            videoView.setVideoURI(storyUrlList.get(storyCount));
                            videoView.seekTo(1);
                            videoView.requestFocus();
                            videoView.start();
                            storyCount++;
                        }
                    });
        } else if (storyCount < 0) {
            storyCount = 0;
            VideoViewTouchListener();
        } else {
            finish();
        }
    }

    @Override
    public void onNext() {
        VideoViewTouchListener();
    }

    @Override
    public void onPrev() {
        storyCount = storyCount - 2;
        VideoViewTouchListener();
    }

    @Override
    public void onComplete() {
        onNext();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}
