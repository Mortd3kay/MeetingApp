package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class Feedback {
    @SerializedName("text")
    private String text;
    @SerializedName("user_id")
    private int userId;

    public Feedback(String text, int userId) {
        this.text = text;
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
