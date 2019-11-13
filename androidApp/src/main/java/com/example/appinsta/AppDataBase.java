package com.example.appinsta;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.appinsta.DataBase.LoggedUserDao;
import com.example.appinsta.DataBase.LoggedUserItem;

@Database(entities = {LoggedUserItem.class}, version = 1)
abstract class AppDatabase extends RoomDatabase {
    public abstract LoggedUserDao loggedUserDao();
}