package com.example.appinsta.service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.InstagramConstants;
import dev.niekirk.com.instagram4android.requests.InstagramGetMediaLikersRequest;
import dev.niekirk.com.instagram4android.requests.InstagramGetStoryViewersRequest;
import dev.niekirk.com.instagram4android.requests.InstagramGetUserFollowersRequest;
import dev.niekirk.com.instagram4android.requests.InstagramGetUserFollowingRequest;
import dev.niekirk.com.instagram4android.requests.InstagramSearchUsernameRequest;
import dev.niekirk.com.instagram4android.requests.InstagramUserFeedRequest;
import dev.niekirk.com.instagram4android.requests.InstagramUserStoryFeedRequest;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramGetMediaLikersResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramGetStoryViewersResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramGetUserFollowersResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramSearchUsernameResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserStoryFeedResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class InstagramService {


    public static Instagram4Android instagram;
    public static String mediasNextMaxId;
    public static String myMediasNextMaxId;
    private static InstagramService myService;
    InstagramUser loggedUser;
    private List<InstagramUserSummary> myFollowing = new ArrayList<>();
    private List<InstagramUser> storyViewers = null;

    private List<InstagramFeedItem> story = null;
    String loggedUserLatestMediaUrl;
    InstagramFeedResult userFeedResult = null;
    InstagramFeedResult myUserFeedResult = null;

    List<InstagramUserSummary> myFollowers = new ArrayList<>();


    private InstagramService() {

    }

    public static InstagramService getInstance() {
        if (myService == null) { //if there is no instance available... create new one
            myService = new InstagramService();
        }

        return myService;
    }


    public InstagramLoginResult login(String username, String password) throws IOException {


        InstagramConstants.islogged = true;
        instagram = Instagram4Android.builder().username(username).password(password).build();

        instagram.setup();
        return instagram.login();
    }

    public InstagramService(Instagram4Android instagram) {
        this.instagram = instagram;
    }


    public List<InstagramUserSummary> getFollowers(long pk) {


        List<InstagramUserSummary> followers = new ArrayList<>();
        InstagramGetUserFollowersResult followersResult = null;
        String nextMaxId = null;

        do {

            try {
                followersResult = instagram.sendRequest(new InstagramGetUserFollowersRequest(pk, nextMaxId));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (followersResult.getUsers() != null && !followersResult.getUsers().isEmpty()) {
                for (InstagramUserSummary userSummary : followersResult.getUsers()) {
                    followers.add(userSummary);

                }
            }

            nextMaxId = followersResult.getNext_max_id();
        } while (nextMaxId != null);


        return followers;


    }

    public List<InstagramUserSummary> getMyFollowers() {

        if (!myFollowers.isEmpty())
            return myFollowers;
        else {
            InstagramGetUserFollowersResult followersResult = null;
            String nextMaxId = null;

            do {

                try {
                    followersResult = instagram.sendRequest(new InstagramGetUserFollowersRequest(instagram.getUserId(), nextMaxId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (followersResult.getUsers() != null && !followersResult.getUsers().isEmpty()) {
                    for (InstagramUserSummary userSummary : followersResult.getUsers()) {
                        myFollowers.add(userSummary);

                    }
                }

                nextMaxId = followersResult.getNext_max_id();
            } while (nextMaxId != null);


            return myFollowers;
        }
    }

    public List<InstagramUserSummary> getFollowing(long pk) {

        List<InstagramUserSummary> following = new ArrayList<>();
        InstagramGetUserFollowersResult followingResult = null;
        String nextMaxId = null;
        do {


            try {
                followingResult = instagram.sendRequest(new InstagramGetUserFollowingRequest(pk, nextMaxId));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (followingResult.getUsers() != null && !followingResult.getUsers().isEmpty()) {
                for (InstagramUserSummary userSummary : followingResult.getUsers()) {
                    following.add(userSummary);

                }
            }
            nextMaxId = followingResult.getNext_max_id();
        } while (nextMaxId != null);

        return following;


    }

    public List<InstagramUserSummary> getMyFollowing() {


        if (!myFollowing.isEmpty())
            return myFollowing;
        else {
            InstagramGetUserFollowersResult followingResult = null;
            String nextMaxId = null;
            do {


                try {
                    followingResult = instagram.sendRequest(new InstagramGetUserFollowingRequest(instagram.getUserId(), nextMaxId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (followingResult.getUsers() != null && !followingResult.getUsers().isEmpty()) {
                    for (InstagramUserSummary userSummary : followingResult.getUsers()) {
                        myFollowing.add(userSummary);

                    }
                }
                nextMaxId = followingResult.getNext_max_id();
            } while (nextMaxId != null);

            return myFollowing;
        }
    }


    public InstagramUser getUser(String username) {

        InstagramSearchUsernameResult result = null;
        try {
            result = instagram.sendRequest(new InstagramSearchUsernameRequest(username));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result.getUser();

    }

    public InstagramUser getLoggedUser() {

        if(loggedUser!=null)return loggedUser;
        else {
        InstagramSearchUsernameResult result = null;
        try {
            result = instagram.sendRequest(new InstagramSearchUsernameRequest(instagram.getUsername()));
        } catch (IOException e) {
            e.printStackTrace();
        }
            loggedUser = result.getUser();
            return loggedUser;
        }
    }

    public String getLoggedUserLastMediaUrl(){


        if(loggedUserLatestMediaUrl==null){
            loggedUserLatestMediaUrl= getLoggedUserMedias(null).get(0).image_versions2.candidates.get(1).url;
            return loggedUserLatestMediaUrl;
        }
        else
            return loggedUserLatestMediaUrl;
    }

    public List<InstagramUser> getStoryViewers(long userId, String storyId) {

        if (storyViewers != null) {
            return storyViewers;
        } else {
            InstagramUserStoryFeedResult story = null;
            InstagramGetStoryViewersResult userStoryViewers = null;

            try {
                story = instagram.sendRequest(new InstagramUserStoryFeedRequest("" + userId));
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                if (story.getReel() != null) {

                    userStoryViewers = instagram.sendRequest(new InstagramGetStoryViewersRequest(storyId, null));

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            storyViewers = userStoryViewers.getUsers();
            return storyViewers;
        }

    }

    public List<InstagramFeedItem> getStories(long userId) {

        if (story != null) {
            return story;
        } else {
            InstagramUserStoryFeedResult storyFeedResult = null;

            try {
                storyFeedResult = instagram.sendRequest(new InstagramUserStoryFeedRequest("" + userId));
            } catch (IOException e) {
                e.printStackTrace();

                if (userFeedResult.getItems() != null && !userFeedResult.getItems().isEmpty()) {
                    for (InstagramFeedItem item : userFeedResult.getItems()) {
                        story.add(item);

                    }
                }

            }
            story = storyFeedResult.getReel().getItems();
            if (storyFeedResult.getReel() != null) {
                return story;
            } else return null;
        }


    }

    public InstagramFeedItem getStory(long userId, int storyIndex) {

        return getStories(userId).get(storyIndex);

    }


    public List<InstagramFeedItem> getUserMedias(long userId) {

        try {
            userFeedResult = instagram.sendRequest(new InstagramUserFeedRequest(userId, null, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediasNextMaxId = userFeedResult.getNext_max_id();

        return userFeedResult.getItems();

    }

    public List<InstagramFeedItem> getUserMedias(long userId, String nextMaxId) {

        try {
            userFeedResult = instagram.sendRequest(new InstagramUserFeedRequest(userId, nextMaxId, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediasNextMaxId = userFeedResult.getNext_max_id();
        return userFeedResult.getItems();

    }


    public List<InstagramFeedItem> getLoggedUserMedias() {


                try {
                    myUserFeedResult = instagram.sendRequest(new InstagramUserFeedRequest(instagram.getUserId(), null, 0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myMediasNextMaxId = myUserFeedResult.getNext_max_id();
                return myUserFeedResult.getItems();


    }
    public List<InstagramFeedItem> getLoggedUserMedias(String nextMaxId) {

            try {
                myUserFeedResult = instagram.sendRequest(new InstagramUserFeedRequest(instagram.getUserId(), nextMaxId, 0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            myMediasNextMaxId = myUserFeedResult.getNext_max_id();
            return myUserFeedResult.getItems();
    }


    public List<InstagramUserSummary> getMediaLikers(long mediaId) {

       InstagramGetMediaLikersResult getMediaLikersResult = null;

            try {
                getMediaLikersResult = instagram.sendRequest(new InstagramGetMediaLikersRequest(mediaId, null));
            } catch (IOException e) {
                e.printStackTrace();
            }

        return getMediaLikersResult.getUsers();
    }



    public List<InstagramFeedItem> getMyLikedMediaByUser(String username) {


        List<InstagramFeedItem> likedMediaList = new ArrayList<>();
        List<InstagramUserSummary> medialikers = new ArrayList<>();
        List<InstagramFeedItem> myMedia = getLoggedUserMedias();

        for (int i = 0; i < myMedia.size(); i++) {

            medialikers = getMediaLikers(myMedia.get(i).getPk());

            for (int j = 0; j < medialikers.size(); j++) {

                if (username.equals(medialikers.get(j).username)) {

                    likedMediaList.add(myMedia.get(i));
                        break;
                }
            }
        }
        return likedMediaList;
    }
    public List<InstagramFeedItem> getMyLikedNextMediaByUser(String username,String nextMaxId) {

        List<InstagramFeedItem> likedNextMediaList = new ArrayList<>();
        List<InstagramFeedItem> mediaList = new ArrayList<>();
        List<InstagramUserSummary> photoLikers = new ArrayList<>();

       mediaList= getLoggedUserMedias(nextMaxId);


        for (int i = 0; i < mediaList.size(); i++) {

            photoLikers = getMediaLikers(mediaList.get(i).getPk());

            for (int j = 0; j < photoLikers.size(); j++) {

                if (username.equals(photoLikers.get(j).username)) {

                    likedNextMediaList.add(mediaList.get(i));
                    break;
                }
            }
        }
        return likedNextMediaList;
    }
}
