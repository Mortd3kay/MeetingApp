package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class CommonMessage {
    private int id;
    @SerializedName("from_user")
    private UserProfile fromUser;
    private String text;
    private String created;
    @SerializedName("is_systemic")
    private boolean isSystemic;

    public CommonMessage(String text, String created) {
        this.text = text;
        this.created = created;
    }

    public CommonMessage(int id, UserProfile fromUser, String text, String created, boolean isSystemic) {
        this.fromUser = fromUser;
        this.text = text;
        this.created = created;
        this.isSystemic = isSystemic;
    }

    public CommonMessage() {
    }

    public boolean isSystemic() {
        return isSystemic;
    }

    public void setSystemic(boolean systemic) {
        isSystemic = systemic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserProfile getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserProfile fromUser) {
        this.fromUser = fromUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
