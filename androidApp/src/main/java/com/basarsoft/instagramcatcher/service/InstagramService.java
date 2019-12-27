package com.basarsoft.instagramcatcher.service;

import android.net.Uri;

import com.basarsoft.instagramcatcher.models.DataWithOffsetIdModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.InstagramConstants;
import dev.niekirk.com.instagram4android.requests.InstagramGetMediaLikersRequest;
import dev.niekirk.com.instagram4android.requests.InstagramGetStoryViewersRequest;
import dev.niekirk.com.instagram4android.requests.InstagramGetUserFollowersRequest;
import dev.niekirk.com.instagram4android.requests.InstagramGetUserFollowingRequest;
import dev.niekirk.com.instagram4android.requests.InstagramReelsTrayRequest;
import dev.niekirk.com.instagram4android.requests.InstagramSearchUsernameRequest;
import dev.niekirk.com.instagram4android.requests.InstagramUserFeedRequest;
import dev.niekirk.com.instagram4android.requests.InstagramUserStoryFeedRequest;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramGetStoryViewersResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramGetUserFollowersResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramReelsTrayFeedResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramSearchUsernameResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramStoryTray;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserStoryFeedResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class InstagramService {


    public static Instagram4Android instagram;
    private static InstagramService myService;
    InstagramUser loggedUser;
    private List<InstagramUserSummary> myFollowing = new ArrayList<>();
    List<InstagramUserSummary> myFollowers = new ArrayList<>();
    private List<InstagramUser> storyViewers = new ArrayList<>();

    List<InstagramUserSummary> usersFollowers;
    List<InstagramUserSummary> usersFollowings;

    long lastPkFollower, lastPkFollowing;

    private List<InstagramFeedItem> story = null;
    String loggedUserLatestMediaUrl;
    InstagramFeedResult userFeedResult = null;
    InstagramFeedResult myUserFeedResult = null;


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

    public void logout() {
        loggedUser = null;
        myFollowing.clear();
        storyViewers = null;
        story = null;
        userFeedResult = null;
        myUserFeedResult = null;
        myFollowers.clear();

    }

    public InstagramService(Instagram4Android instagram) {
        this.instagram = instagram;
    }


    public List<InstagramUserSummary> getFollowers(long pk) throws IOException {
        if (usersFollowers == null || lastPkFollower != pk) {
            lastPkFollower = pk;
            usersFollowers = new ArrayList<>();
            InstagramGetUserFollowersResult followersResult = null;
            String nextMaxId = null;
            do {
                followersResult = instagram.sendRequest(new InstagramGetUserFollowersRequest(pk, nextMaxId));

                if (followersResult.getUsers() != null && !followersResult.getUsers().isEmpty()) {
                    for (InstagramUserSummary userSummary : followersResult.getUsers()) {
                        usersFollowers.add(userSummary);

                    }
                }

                nextMaxId = followersResult.getNext_max_id();
            } while (nextMaxId != null);

            if (followersResult.getStatus().equals("fail")) {
                usersFollowers = null;
            }
        }
        return usersFollowers;


    }

    public List<InstagramUserSummary> getMyFollowers() throws IOException {

        if (!myFollowers.isEmpty()) return myFollowers;
        else {
            InstagramGetUserFollowersResult followersResult = null;
            String nextMaxId = null;
            do {
                followersResult = instagram.sendRequest(new InstagramGetUserFollowersRequest(instagram.getUserId(), nextMaxId));
                if (followersResult.getUsers() != null && !followersResult.getUsers().isEmpty()) {
                    for (InstagramUserSummary userSummary : followersResult.getUsers()) {
                        myFollowers.add(userSummary);

                    }
                }

                nextMaxId = followersResult.getNext_max_id();
            } while (nextMaxId != null);
            if (followersResult.getStatus().equals("fail")) {
                return null;
            }
            return myFollowers;
        }
    }

    public List<InstagramUserSummary> getFollowing(long pk) throws IOException {

        if (usersFollowings == null || lastPkFollowing != pk) {
            lastPkFollowing = pk;
            usersFollowings = new ArrayList<>();
            InstagramGetUserFollowersResult followingResult = null;
            String nextMaxId = null;
            do {
                followingResult = instagram.sendRequest(new InstagramGetUserFollowingRequest(pk, nextMaxId));
                if (followingResult.getUsers() != null && !followingResult.getUsers().isEmpty()) {
                    for (InstagramUserSummary userSummary : followingResult.getUsers()) {
                        usersFollowings.add(userSummary);

                    }
                }
                nextMaxId = followingResult.getNext_max_id();
            } while (nextMaxId != null);
            if (followingResult.getStatus().equals("fail")) {
                usersFollowings = null;
            }
        }
        return usersFollowings;
    }

    public List<InstagramUserSummary> getMyFollowing() throws IOException {


        if (!myFollowing.isEmpty()) return myFollowing;
        else {
            InstagramGetUserFollowersResult followingResult = null;
            String nextMaxId = null;
            do {
                followingResult = instagram.sendRequest(new InstagramGetUserFollowingRequest(instagram.getUserId(), nextMaxId));
                if (followingResult.getUsers() != null && !followingResult.getUsers().isEmpty()) {
                    for (InstagramUserSummary userSummary : followingResult.getUsers()) {
                        myFollowing.add(userSummary);

                    }
                }
                nextMaxId = followingResult.getNext_max_id();
            } while (nextMaxId != null);

            if (followingResult.getStatus().equals("fail")) {
                return null;
            }

            return myFollowing;
        }
    }

    public InstagramUser getUser(String username) throws IOException {

        return instagram.sendRequest(new InstagramSearchUsernameRequest(username)).getUser();
    }

    public InstagramUser getLoggedUser() throws IOException {

        if (loggedUser != null) return loggedUser;
        else {
            InstagramSearchUsernameResult result = null;
            result = instagram.sendRequest(new InstagramSearchUsernameRequest(instagram.getUsername()));
            loggedUser = result.getUser();
            return loggedUser;
        }
    }

    public String getLoggedUserLastMediaUrl() throws IOException {

        if (loggedUserLatestMediaUrl == null) {
            InstagramFeedItem firstItem = (InstagramFeedItem) getLoggedUserMedias(null).items.get(0);
            loggedUserLatestMediaUrl = firstItem.image_versions2.candidates.get(1).url;
        }
        return loggedUserLatestMediaUrl;
    }

    public List<InstagramUser> getStoryViewers(long userId, String storyId) throws IOException {
        if (!storyViewers.isEmpty()) {
            storyViewers.clear();
        }
        InstagramUserStoryFeedResult storyFeedResult = null;
        InstagramGetStoryViewersResult userStoryViewers = null;
        String nextMaxId = null;
        do {
            storyFeedResult = instagram.sendRequest(new InstagramUserStoryFeedRequest(String.valueOf(userId)));
            if (storyFeedResult.getReel() != null) {

                userStoryViewers = instagram.sendRequest(new InstagramGetStoryViewersRequest(storyId, nextMaxId));
                for (InstagramUser user : userStoryViewers.getUsers()) {
                    storyViewers.add(user);
                }
            }
            nextMaxId = userStoryViewers.getNext_max_id();
        } while (nextMaxId != null);
        return storyViewers;
    }

    public List<InstagramFeedItem> getStories(long userId) throws IOException {

        if (story != null) {
            return story;
        } else {
            story = new ArrayList<>();
            InstagramUserStoryFeedResult storyFeedResult = null;

            storyFeedResult = instagram.sendRequest(new InstagramUserStoryFeedRequest(String.valueOf(userId)));
            if (storyFeedResult.getReel() != null) {
                if (storyFeedResult.getReel().getItems() != null && !storyFeedResult.getReel().getItems().isEmpty()) {
                    for (InstagramFeedItem item : storyFeedResult.getReel().getItems()) {
                        story.add(item);

                    }
                }
            }
            return story;
        }


    }

    public InstagramFeedItem getStory(long userId, int storyIndex) throws IOException {

        return getStories(userId).get(storyIndex);
    }

    public DataWithOffsetIdModel getUserMedias(long userId) throws IOException {
        return getUserMedias(userId, null);
    }

    public DataWithOffsetIdModel getUserMedias(long userId, String nextMaxId) throws IOException {
        userFeedResult = instagram.sendRequest(new InstagramUserFeedRequest(userId, nextMaxId, 0));
        return new DataWithOffsetIdModel(userFeedResult.getItems(), userFeedResult.getNext_max_id());

    }

    public DataWithOffsetIdModel getLoggedUserMedias() throws IOException {
        return getLoggedUserMedias(null);
    }

    public DataWithOffsetIdModel getLoggedUserMedias(String nextMaxId) throws IOException {

        myUserFeedResult = instagram.sendRequest(new InstagramUserFeedRequest(instagram.getUserId(), nextMaxId, 0));
        return new DataWithOffsetIdModel(myUserFeedResult.getItems(), myUserFeedResult.getNext_max_id());
    }


    public List<InstagramUserSummary> getMediaLikers(long mediaId) throws IOException {
        return instagram.sendRequest(new InstagramGetMediaLikersRequest(mediaId, null)).getUsers();

    }

    public DataWithOffsetIdModel getMyLikedMediaByUser(String username) throws IOException {
        return getMyLikedMediaByUser(username, null);
    }

    public DataWithOffsetIdModel getMyLikedMediaByUser(String username, String nextMaxId) throws IOException {

        List<InstagramFeedItem> likedNextMediaList = new ArrayList<>();
        List<InstagramFeedItem> mediaList = new ArrayList<>();
        List<InstagramUserSummary> photoLikers = new ArrayList<>();
        DataWithOffsetIdModel dataWithOffsetIdModel = getLoggedUserMedias(nextMaxId);
        mediaList = dataWithOffsetIdModel.items;


        for (int i = 0; i < mediaList.size(); i++) {

            photoLikers = getMediaLikers(mediaList.get(i).getPk());

            for (int j = 0; j < photoLikers.size(); j++) {

                if (username.equals(photoLikers.get(j).username)) {

                    likedNextMediaList.add(mediaList.get(i));
                    break;
                }
            }
        }
        return new DataWithOffsetIdModel(likedNextMediaList, dataWithOffsetIdModel.nextMaxId);
    }

    public ArrayList<Uri> getStories(String username) throws IOException {
        ArrayList<Uri> userStoriesUri = new ArrayList<>();
        InstagramReelsTrayFeedResult result = instagram.sendRequest(new InstagramReelsTrayRequest());
        List<InstagramStoryTray> trays = result.getTray();
        InstagramUserStoryFeedResult userTray = null;
        for (InstagramStoryTray tray : trays) {
            if (tray != null & tray.getUser().username.equals(username)) {
                userTray = instagram.sendRequest(new InstagramUserStoryFeedRequest(String.valueOf(tray.getUser().getPk())));
                break;
            }
        }
        if (userTray != null && userTray.getReel() != null) {
            if (username.equals(userTray.getReel().getUser().username)) {
                userStoriesUri.clear();
                List<InstagramFeedItem> stories = userTray.getReel().getItems();
                for (InstagramFeedItem story : stories) {
                    Uri uri;
                    if (story.getVideo_versions() != null) {
                        uri = Uri.parse(story.getVideo_versions().get(0).getUrl());
                    } else {
                        uri = Uri.parse(story.getImage_versions2().getCandidates().get(0).getUrl());
                    }
                    userStoriesUri.add(uri);
                }
            }
        }
        return userStoriesUri;
    }

    public List<InstagramStoryTray> getTrayStories() {
        InstagramReelsTrayFeedResult result= null;
        try {
            result = instagram.sendRequest(new InstagramReelsTrayRequest());
            return result.getTray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}