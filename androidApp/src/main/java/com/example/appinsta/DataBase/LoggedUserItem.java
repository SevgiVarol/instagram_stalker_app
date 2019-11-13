package com.example.appinsta.DataBase;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "loggedUserTable")
public class LoggedUserItem {
    @PrimaryKey(autoGenerate = true)
    public int uid=0;

    @ColumnInfo(name = "username")
    public String username=null;

    @ColumnInfo(name = "password")
    public String password=null;
}