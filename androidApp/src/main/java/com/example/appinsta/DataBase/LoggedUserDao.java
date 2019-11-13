package com.example.appinsta.DataBase;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LoggedUserDao {
    @Query("SELECT * FROM loggedUserTable")
    List<LoggedUserItem> getLastUser();

    @Insert
    void insertLastLogged(LoggedUserItem loggedusersentity);

    @Delete
    void deleteLogged(LoggedUserItem loggedusersentity);
}