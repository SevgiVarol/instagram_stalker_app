package com.example.appinsta.models;

import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;

public class MediaModel {
    public List<InstagramFeedItem> feedItems;
    public String nextMaxId;


    public void setFeedItems(List<InstagramFeedItem> feedItems){
        this.feedItems = feedItems;
    }
    public void setNextMaxId(String nextMaxId){
        this.nextMaxId = nextMaxId;
    }
}
