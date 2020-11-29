package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class Chat {
    @SerializedName("content_type")
    private String contentType;
    @SerializedName("content_id")
    private int contentId;
    @SerializedName("last_message")
    private String lastMessage;
    @SerializedName("from_user")
    private UserProfile fromUser;
    @SerializedName("title")
    private String title;
    @SerializedName("last_message_id")
    private int lastMessageId;
    @SerializedName("last_seen_message_id")
    private int lastSeenMessageId;
    @SerializedName("last_message_created")
    private String lastMessageCreated;
    @SerializedName("last_message_from_user_name")
    private String lastMessageFromUserName;

    public Chat(String contentType, int contentId, String lastMessage, int lastMessageId, int lastSeenMessageId, UserProfile fromUser, String title, String lastMessageCreated, String lastMessageFromUserName) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.lastMessage = lastMessage;
        this.lastMessageId = lastMessageId;
        this.lastSeenMessageId = lastSeenMessageId;
        this.fromUser = fromUser;
        this.title = title;
        this.lastMessageCreated = lastMessageCreated;
        this.lastMessageFromUserName = lastMessageFromUserName;
    }

    public Chat() {

    }

    public int getLastSeenMessageId() {
        return lastSeenMessageId;
    }

    public void setLastSeenMessageId(int lastSeenMessageId) {
        this.lastSeenMessageId = lastSeenMessageId;
    }

    public String getLastMessageFromUserName() {
        return lastMessageFromUserName;
    }

    public void setLastMessageFromUserName(String lastMessageFromUserName) {
        this.lastMessageFromUserName = lastMessageFromUserName;
    }

    public String getLastMessageCreated() {
        return lastMessageCreated;
    }

    public void setLastMessageCreated(String lastMessageCreated) {
        this.lastMessageCreated = lastMessageCreated;
    }

    public int getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(int lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public UserProfile getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserProfile fromUser) {
        this.fromUser = fromUser;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
