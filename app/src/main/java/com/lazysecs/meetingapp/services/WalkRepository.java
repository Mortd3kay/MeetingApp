package com.lazysecs.meetingapp.services;

import android.app.Application;

import com.lazysecs.meetingapp.dao.userDao;
import com.lazysecs.meetingapp.models.UserProfile;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WalkRepository {

    private WalkDatabase database;
    private userDao userDao;

    public WalkRepository(Application app) {
        database = WalkDatabase.getInstance(app.getApplicationContext());
        userDao = database.userDao();
    }

    void insert(UserProfile user){
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
                userDao.insert(user);
        });
    }

    void update(UserProfile user){
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            userDao.update(user);
        });
    }

    void delete(UserProfile user){
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            userDao.delete(user);
        });
    }

    void deleteAll(){
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            userDao.deleteAll();
        });
    }

    void deleteById(int id){
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            userDao.deleteById(id);
        });
    }
}
