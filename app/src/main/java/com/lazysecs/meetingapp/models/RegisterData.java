package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class RegisterData extends LoginData {
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;

    public RegisterData(String email, String firstName, String lastName, String password) {
        super(email, password);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
