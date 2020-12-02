package com.lazysecs.meetingapp.dao;

import com.lazysecs.meetingapp.models.UserProfile;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface userDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserProfile user);

    @Update
    void update(UserProfile user);

    @Delete
    void delete(UserProfile user);

    @Query("delete from user where id=:id")
    void deleteById(int id);

    @Query("delete from user")
    void deleteAll();

    @Query("select * from user where id=:id")
    LiveData<UserProfile> getById(int id);
}
