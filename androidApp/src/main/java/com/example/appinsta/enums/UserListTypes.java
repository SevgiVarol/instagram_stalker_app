package com.example.appinsta.enums;

public enum UserListTypes {
    FOR_MY_FOLLOWERS(0),
    FOR_MY_FOLLOWINGS(1),
    FOR_MY_STALKERS(2),
    FOR_MY_STALKINGS(3),
    FOR_MY_LAST_PHOTO_LIKERS(4),
    FOR_USERS_FOLLOWERS(5),
    FOR_USERS_FOLLOWINGS(6),
    FOR_USERS_STALKERS(7),
    FOR_USERS_STALKINGS(8);

    private final int value;

    UserListTypes(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
