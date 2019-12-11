package com.example.appinsta.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.appinsta.database.dao.LoggedUserDao;
import com.example.appinsta.database.model.LoggedUserItem;

@Database(entities = {LoggedUserItem.class}, version = 1)
public abstract class InstaDatabase extends RoomDatabase {
    private static InstaDatabase mInstance;

    public static synchronized InstaDatabase getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(mCtx.getApplicationContext(), InstaDatabase.class, "instaDb").fallbackToDestructiveMigration().build();
        }
        return mInstance;
    }

    public abstract LoggedUserDao loggedUserDao();
}