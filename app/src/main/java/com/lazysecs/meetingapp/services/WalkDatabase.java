package com.lazysecs.meetingapp.services;

import android.content.Context;

import com.lazysecs.meetingapp.activities.ConfirmCodeActivity;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.dao.userDao;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserProfile.class}, version = 1)
public abstract class WalkDatabase extends RoomDatabase {

    public abstract userDao userDao();

    private static WalkDatabase instance;

    public static synchronized WalkDatabase getInstance(Context context){
        if (instance==null){
            instance = Room
                    .databaseBuilder(context, WalkDatabase.class, "walk_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
