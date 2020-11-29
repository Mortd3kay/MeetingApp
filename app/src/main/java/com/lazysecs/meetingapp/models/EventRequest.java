package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class EventRequest {

    @SerializedName("id")
    private int id;

    @SerializedName("from_user")
    private UserProfile fromUser;

    @SerializedName("to_user")
    private String toUser;

    @SerializedName("event")
    private long event;

    private String decision;
    private boolean seen;
    private String created;

    public EventRequest(UserProfile fromUser, String toUser, long event) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.event = event;
    }

    public EventRequest(int id, UserProfile fromUser, String toUser, long event, String created) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.event = event;
        this.created = created;
    }

    public EventRequest(String decision) {
        this.decision = decision;
    }

    public EventRequest() {
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

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public long getEvent() {
        return event;
    }

    public void setEvent(long event) {
        this.event = event;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
