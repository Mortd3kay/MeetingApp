package com.lazysecs.meetingapp.services;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lazysecs.meetingapp.activities.MainActivity;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.utils.PreferenceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserProfileManager {
    private static UserProfileManager instance;
    private static UserProfile myProfile = null;

    private UserProfileManager() {
    }

    public static UserProfileManager getInstance() {
        if (instance == null)
            instance = new UserProfileManager();

        return instance;
    }

    public UserProfile getMyProfile() {
        return myProfile;
    }

    public void initialize(UserProfile userProfile) {
        myProfile = userProfile;
    }

    public void meProfile(Context context) {
        Call<UserProfile> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(context))
                .getApi()
                .meProfile();

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                if (response.body() != null) {
                    initialize(response.body());
                    PreferenceUtils.saveUserId(response.body().getId(), context);

                    MainActivity instance = MainActivity.instance;
                    instance.initNotificationBadge();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {

            }
        });
    }
}
