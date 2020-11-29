package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class PrivateMessage extends CommonMessage {

    @SerializedName("user")
    private int user;

    public PrivateMessage(String text, String created) {
        super(text, created);
    }

    public PrivateMessage(int user, String text, String created, boolean seen) {
        super(0, null, text, created, seen);
        this.user = user;
    }

    public PrivateMessage() {
        super();
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
