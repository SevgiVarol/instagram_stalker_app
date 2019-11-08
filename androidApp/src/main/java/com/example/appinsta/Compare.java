package com.example.appinsta;

import com.example.appinsta.service.InstagramService;

import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class Compare {


    Instagram4Android instagram;

    InstagramService service;

    public Compare(Instagram4Android instagram) {
        this.instagram = instagram;
    }
    public Compare() {

    }

    public static List<InstagramUserSummary> compare(List<InstagramUserSummary> list1, List<InstagramUserSummary> list2) {

        List<InstagramUserSummary> results = new ArrayList<>();

        for (InstagramUserSummary user1 : list1) {

            if (!list2.contains(user1)) {
                results.add(user1);
            }

        }

        return results;

    }

    public static List<InstagramUser> compareWatchedStoryAndUnfollowing(List<InstagramUser> list1, List<InstagramUserSummary> list2) {

        List<InstagramUser> results = new ArrayList<>();

        for (InstagramUser user1: list1) {

            boolean found = false;

            for (InstagramUserSummary user2 : list2) {
                if (user1.username.equals(user2.username)) {
                    found = true;
                }
            }
            if (!found) {
                results.add(user1);
            }
        }

        return results;

    }

    public static List<InstagramUserSummary> compareUnwatchedStoryAndFollowing(List<InstagramUserSummary> list1, List<InstagramUser> list2) {

        List<InstagramUserSummary> results = new ArrayList<>();

        for (InstagramUserSummary user1: list1) {

            boolean found = false;

            for (InstagramUser user2 : list2) {
                if (user1.username.equals(user2.username)) {
                    found = true;
                }
            }
            if (!found) {
                results.add(user1);
            }
        }

        return results;

    }
    public List<InstagramUserSummary> peopleIdontFollow(long userId) {
        service = new InstagramService(instagram);

        //benim takip etmediklerim
        return compare(service.getFollowers(userId), service.getFollowing(userId));


    }

    public List<InstagramUserSummary> peopledontFollowme(long userId) {

        service = new InstagramService(instagram);
        //beni takip etmeyenler
        return compare(service.getFollowing(userId), service.getFollowers(userId));


    }

   /* public List<InstagramUserSummary> peopleIdontFollow(long userId) {

        user = new User(instagram);
        //benim takip etmediklerim
        return compare(user.getFollowers(userId), user.getFollowing(userId));


    }

    public List<InstagramUserSummary> peopledontFollowme(long userId) {

        user = new User(instagram);
        //beni takip etmeyenler
        return compare(user.getFollowing(userId), user.getFollowers(userId));


    }




    public List<InstagramUserSummary> unwatchedStoryAndFollowing(long userId, int storyIndex) {

        story = new Story(instagram);
        user = new User(instagram);

        String storyId = story.getStory(userId, storyIndex).id;

        return compareUnwatchedStoryAndFollowing(user.getFollowers(userId), story.getStoryViewers(userId, storyId));

    }

    public List<InstagramUserSummary> mediaLikersAndUnfollowing(long userId, int photoIndex) {

        media = new Media(instagram);
        user = new User(instagram);

        long mediaId = media.getMedia(userId, photoIndex).getPk();

        return compare(media.getMediaLikers(mediaId), user.getFollowers(userId));

    }

    public List<InstagramUser> wachedStoryAndUnfollowing(long userId, int storyIndex) {


        story = new Story(instagram);
        user = new User(instagram);

        String storyId = story.getStory(userId, storyIndex).id;

        return compareWatchedStoryAndUnfollowing(story.getStoryViewers(userId, storyId), user.getFollowers(userId));


    }


*/
}