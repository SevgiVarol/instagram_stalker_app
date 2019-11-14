package com.example.appinsta;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.appinsta.database.LoggedUserDao;
import com.example.appinsta.database.LoggedUserItem;

@Database(entities = {LoggedUserItem.class}, version = 2)
abstract class AppDatabaseForLogin extends RoomDatabase {
    public abstract LoggedUserDao loggedUserDao();
}