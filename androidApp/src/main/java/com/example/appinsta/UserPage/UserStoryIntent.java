package com.example.appinsta.UserPage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
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

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class UserStoryIntent extends AppCompatActivity {
    private String username;
    public int storyCount = 0;
    public int videoDuration;
    VideoView videoView;
    ImageView storyImageView;
    SeekBar seekBar;
    ArrayList<Uri> listUri;
    InstagramService service = InstagramService.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_popup);
        videoView = findViewById(R.id.videoView);
        storyImageView = findViewById(R.id.storyImageView);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        username = intent.getExtras().getString("username");
        listUri = service.getStories(username).get(0);
        seekBar.setMax(listUri.size());
        VideoViewTouchListener();
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                VideoViewTouchListener();
                return false;
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
                int duration = mp.getDuration();
                videoDuration = videoView.getDuration();
                Log.d("tag", String.format("onPrepared: duration=%d, videoDuration=%d", duration, videoDuration));
            }
        });
    }

    private void VideoViewTouchListener() {
        if (storyCount < listUri.size()) {
            Picasso
                    .with(getApplicationContext())
                    .load((listUri.get(storyCount)).toString())
                    .into(storyImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            seekBar.setProgress(storyCount);
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
