package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class Complaint {
    @SerializedName("id")
    private int id;
    @SerializedName("suspected")
    private UserProfile suspected;
    @SerializedName("message")
    private String message;
    @SerializedName("content_id")
    private int contentId;
    @SerializedName("content_type")
    private String contentType;
    @SerializedName("created")
    private String created;
    @SerializedName("reviewed")
    private boolean reviewed;

    public Complaint(int id, UserProfile suspected, String message, int contentId, String contentType, String created, boolean reviewed) {
        this.id = id;
        this.suspected = suspected;
        this.message = message;
        this.contentId = contentId;
        this.contentType = contentType;
        this.created = created;
        this.reviewed = reviewed;
    }

    public Complaint(String message, int contentId, String contentType) {
        this.message = message;
        this.contentId = contentId;
        this.contentType = contentType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserProfile getSuspected() {
        return suspected;
    }

    public void setSuspected(UserProfile suspected) {
        this.suspected = suspected;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
