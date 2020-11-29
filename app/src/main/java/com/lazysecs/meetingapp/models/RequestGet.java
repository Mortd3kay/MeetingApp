package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class RequestGet {

    @SerializedName("id")
    private int id;

    @SerializedName("from_user")
    private UserProfile fromUser;

    @SerializedName("to_user")
    private UserProfile toUser;

    @SerializedName("event")
    private int event;
    @SerializedName("title")
    private String title;
    private String decision;
    private boolean seen;
    private String created;
    public RequestGet(int id, UserProfile fromUser, UserProfile toUser, int event, String created, String title) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.event = event;
        this.created = created;
        this.title = title;
    }
    public RequestGet() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public UserProfile getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserProfile fromUser) {
        this.fromUser = fromUser;
    }

    public UserProfile getToUser() {
        return toUser;
    }

    public void setToUser(UserProfile toUser) {
        this.toUser = toUser;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
