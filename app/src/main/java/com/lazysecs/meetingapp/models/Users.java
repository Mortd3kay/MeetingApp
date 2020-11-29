package com.lazysecs.meetingapp.models;

public class Users {
    private String email;
    private String username;
    private String password;

    public Users(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
