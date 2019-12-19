package com.example.appinsta.enums;

import com.example.appinsta.R;

public enum UserListTypes {
    FOR_MY_FOLLOWERS(R.string.follower),
    FOR_MY_FOLLOWINGS(R.string.follow),
    FOR_MY_STALKERS(R.string.who_not_follow_me_back),
    FOR_MY_STALKINGS(R.string.who_i_am_not_follow_back),
    FOR_MY_LAST_PHOTO_LIKERS(R.string.who_do_not_like_my_last_photo),
    FOR_USERS_FOLLOWERS(R.string.follower),
    FOR_USERS_FOLLOWINGS(R.string.follow),
    FOR_USERS_STALKERS(R.string.user_stalkers),
    FOR_USERS_STALKINGS(R.string.user_stalkings);
    private final int descriptionResId;
    UserListTypes(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }
    public int getDescriptionResId() {
        return descriptionResId;
    }
}
