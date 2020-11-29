package com.lazysecs.meetingapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;


public class KillAppService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        Main2Activity main2Activity = new Main2Activity();
//        main2Activity.initFireBase();
//        main2Activity.status("offline");

        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//        Main2Activity main2Activity = new Main2Activity();
//        main2Activity.initFireBase();
//        main2Activity.status("offline");

        Log.e("ClearFromRecentService", "END");
        stopSelf();
    }
}
