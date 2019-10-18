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
import dev.niekirk.com.instagram4android.requests.payload.InstagramSearchUsernameResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserStoryFeedResult;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class InstagramService {


    public static Instagram4Android instagram;
    public static String mediasNextMaxId;
    private static InstagramService myService;
    List<InstagramUserSummary> myFollowing = new ArrayList<>();
    List<InstagramUserSummary> myMediaLikers = new ArrayList<>();

    private List<InstagramUser> storyViewers = null;
    List<InstagramFeedItem> myMedia = new ArrayList<>();

    private List<InstagramFeedItem> story=null;

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


    public void login() {

        InstagramConstants.log = false;
        instagram = Instagram4Android.builder().username("simge.keser").password("Sim152901073854.").build();

        instagram.setup();
        try {
            instagram.login();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InstagramService(Instagram4Android instagram) {
        this.instagram = instagram;
    }

    public InstagramUser myInfo() {

        InstagramSearchUsernameResult result = null;
        try {
            result = instagram.sendRequest(new InstagramSearchUsernameRequest(instagram.getUsername()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result.getUser();

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

        if (!myFollowers.isEmpty()) return myFollowers;
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


        if (!myFollowing.isEmpty()) return myFollowing;
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


    public List<InstagramFeedItem> getMedias(long userId) {

        try {
            userFeedResult = instagram.sendRequest(new InstagramUserFeedRequest(userId, null, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediasNextMaxId = userFeedResult.getNext_max_id();

        return userFeedResult.getItems();

    }

    public List<InstagramFeedItem> getMedias(long userId,String nextMaxId) {

        try {
            userFeedResult = instagram.sendRequest(new InstagramUserFeedRequest(userId, nextMaxId, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediasNextMaxId = userFeedResult.getNext_max_id();

        return userFeedResult.getItems();

    }




    public List<InstagramFeedItem> getMyMedias() {


        if (!myMedia.isEmpty())
            return myMedia;

        else {
            String nextMaxId = null;

          do {

                try {
                    myUserFeedResult = instagram.sendRequest(new InstagramUserFeedRequest(instagram.getUserId(), nextMaxId,0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (myUserFeedResult.getItems() != null && !myUserFeedResult.getItems().isEmpty()) {
                    for (InstagramFeedItem item : myUserFeedResult.getItems()) {
                        myMedia.add(item);

                    }
                }

                nextMaxId = myUserFeedResult.getNext_max_id();
            } while (nextMaxId != null);


            return myMedia;
        }


    }

    public InstagramFeedItem getMedia(long userId, int mediaIndex) {
        return getMedias(userId).get(mediaIndex);
    }


    public InstagramFeedItem myMedia(int mediaIndex) {
        return getMyMedias().get(mediaIndex);
    }


    public List<InstagramUserSummary> getMediaLikers(long mediaId) {

            List<InstagramUserSummary> mediaLikers = new ArrayList<>();

            InstagramGetMediaLikersResult getMediaLikersResult = null;

            String nextMaxId = null;
            do {

                try {


                    getMediaLikersResult = instagram.sendRequest(new InstagramGetMediaLikersRequest(mediaId, nextMaxId));


                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (getMediaLikersResult.getUsers() != null && !getMediaLikersResult.getUsers().isEmpty()) {
                    for (InstagramUserSummary user : getMediaLikersResult.getUsers()) {
                        mediaLikers.add(user);

                    }
                }
                nextMaxId =myUserFeedResult.getNext_max_id();
            } while (nextMaxId != null);

            return mediaLikers;


    }
    public List<InstagramUserSummary> getMyMediaLikers(long mediaId) {

       if(!myMediaLikers.isEmpty()){
           return myMediaLikers;
       }else {

           InstagramGetMediaLikersResult getMediaLikersResult = null;

           String nextMaxId = null;
           do {

               try {


                   getMediaLikersResult = instagram.sendRequest(new InstagramGetMediaLikersRequest(mediaId, nextMaxId));


               } catch (IOException e) {
                   e.printStackTrace();
               }
               if (getMediaLikersResult.getUsers() != null && !getMediaLikersResult.getUsers().isEmpty()) {
                   for (InstagramUserSummary user : getMediaLikersResult.getUsers()) {
                       myMediaLikers.add(user);

                   }
               }
               nextMaxId = myUserFeedResult.getNext_max_id();
           } while (nextMaxId != null);

           return myMediaLikers;
       }

    }


    public List<InstagramFeedItem> urlOfMyPhotos(String username) {


            List<InstagramFeedItem> likedMediaList = new ArrayList<>();
            List<InstagramUserSummary> medialikers = new ArrayList<>();

            int mediaNum = getMyMedias().size();

            for (int i = 0; i < mediaNum; i++) {

                long mediaId = myMedia(i).getPk();
                medialikers= getMediaLikers(mediaId);

                for ( int j =0 ; j < medialikers.size(); j++) {

                    if (username.equals(medialikers.get(j).username)) {

                        likedMediaList.add(myMedia(i));

                    }
                }

            }

            return likedMediaList;


    }
}
