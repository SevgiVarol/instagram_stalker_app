package com.example.appinsta.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LoggedUserDao {
    @Query("SELECT * FROM loggedusersentity")
    List<LoggedUsersEntity> getLastUser();

    @Insert
    void insertLastLogged(LoggedUsersEntity loggedusersentity);

    @Delete
    void deleteLogged(LoggedUsersEntity loggedusersentity);
}