package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class ForgetPassword {

    @SerializedName("email")
    private String email;
    @SerializedName("code")
    private String code;
    @SerializedName("new_password")
    private String newPassword;

    public ForgetPassword(String email, String code, String newPassword) {
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }


}
