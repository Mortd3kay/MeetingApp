package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class ShortRequest {

    @SerializedName("id")
    private int id;

    @SerializedName("from_user")
    private int fromUser;

    @SerializedName("to_user")
    private int toUser;

    private String decision;
    private boolean seen;

    public ShortRequest(int id, int fromUser, int toUser, String decision, boolean seen) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.decision = decision;
        this.seen = seen;
    }

    public ShortRequest() {
    }

    public int getFromUser() {
        return fromUser;
    }

    public void setFromUser(int fromUser) {
        this.fromUser = fromUser;
    }

    public int getToUser() {
        return toUser;
    }

    public void setToUser(int toUser) {
        this.toUser = toUser;
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

}
