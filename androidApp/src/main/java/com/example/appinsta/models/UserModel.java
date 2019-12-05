package com.example.appinsta.models;

import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

//fonksiyonlara gönderilecek değerlerin objesi, yanlış gibi dökümanlardan bak
public class UserModel<T> {
    List<T> user;
    String nextMaxId;

    public void setUser(List<T> user) {
        this.user = user;
    }

    public void setNextMaxId(String nextMaxId) {
        this.nextMaxId = nextMaxId;
    }
}
