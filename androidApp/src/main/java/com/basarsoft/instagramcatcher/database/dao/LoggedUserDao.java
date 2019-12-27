package com.basarsoft.instagramcatcher.database.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.basarsoft.instagramcatcher.database.model.LoggedUserItem;

@Dao
public interface LoggedUserDao {
    @Query("SELECT * FROM loggedUserTable")
    LoggedUserItem getLastUser();

    @Insert
    void insertLastLogged(LoggedUserItem loggedUserItem);

    @Query("DELETE FROM loggedUserTable")
    void deleteLogged();
}