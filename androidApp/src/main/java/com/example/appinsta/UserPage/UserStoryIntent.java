package com.example.appinsta.UserPage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appinsta.R;
import com.example.appinsta.service.InstagramService;

import java.util.ArrayList;

public class UserStoryIntent extends AppCompatActivity {
    private String username;
    public int storyCount;
    VideoView videoView;
    InstagramService service=InstagramService.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent= getIntent();
        username=intent.getExtras().getString("username");
        setContentView(R.layout.story_popup);
        storyCount=1;

        ArrayList<Uri> listUri=service.getStories(username).get(0);
        setContentView(R.layout.story_popup);
        videoView=findViewById(R.id.videoView);
        videoView.setVideoURI(listUri.get(0));
        videoView.requestFocus();
        videoView.start();
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (storyCount<listUri.size()){
                    videoView.setVideoURI(listUri.get(storyCount));
                    videoView.requestFocus();
                    videoView.start();
                    storyCount++;
                }
                else {
                    finish();
                }

                return false;
            }
        });
    }
}
