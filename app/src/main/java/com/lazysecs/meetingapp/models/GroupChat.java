package com.lazysecs.meetingapp.models;

public class GroupChat {
    private String message;
    private String user;
    private String messageTime;

    public GroupChat() {
    }

    public GroupChat(String message, String user, String messageTime) {
        this.message = message;
        this.user = user;
        this.messageTime = messageTime;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
