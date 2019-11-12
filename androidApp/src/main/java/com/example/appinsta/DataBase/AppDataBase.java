package com.example.appinsta.DataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {LoggedUsersEntity.class}, version = 1)
abstract class AppDatabase extends RoomDatabase {
    public abstract LoggedUserDao loggedUserDao();
    private static AppDatabase INSTANCE;
    public static AppDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "loggedUserDb")
                    .allowMainThreadQueries()
                    .build();
        }
        return  INSTANCE;
    }

    public  static  void destroyInstance(){
        INSTANCE = null;
    }
}