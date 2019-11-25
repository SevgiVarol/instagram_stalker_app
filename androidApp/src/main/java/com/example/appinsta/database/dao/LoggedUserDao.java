package com.example.appinsta.database.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.appinsta.database.model.LoggedUserItem;

@Dao
public interface LoggedUserDao {
    @Query("SELECT * FROM loggedUserTable")
    LoggedUserItem getLastUser();

    @Insert
    void insertLastLogged(LoggedUserItem loggedUserItem);

    @Query("DELETE FROM loggedUserTable")
    void deleteLogged();
}