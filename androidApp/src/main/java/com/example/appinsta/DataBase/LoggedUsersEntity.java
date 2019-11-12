package com.example.appinsta.DataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LoggedUsersEntity {
    @PrimaryKey(autoGenerate = true)
    public int uid=0;

    @ColumnInfo(name = "username")
    public String username=null;

    @ColumnInfo(name = "password")
    public String password=null;
}