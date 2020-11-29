package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class User {
    private String token;

    @SerializedName("firebase_uid")
    private String firebaseUid;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }
}
