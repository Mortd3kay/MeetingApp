package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class EmailConfirmation {

    @SerializedName("email")
    private String email;

    @SerializedName("code")
    private long code;

    public EmailConfirmation(String email, long code) {
        this.email = email;
        this.code = code;
    }

    public EmailConfirmation(String email) {
        this.email = email;
    }

    public EmailConfirmation() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }
}
