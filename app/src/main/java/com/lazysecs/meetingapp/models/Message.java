package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class Message extends CommonMessage {
    private int event;

    @SerializedName("chat")
    private ShortChat shortChat;

    public Message(String text, String created, int event) {
        super(text, created);
        this.event = event;
    }

    public Message(int id, UserProfile fromUser, String text, String created, boolean seen) {
        super(id, fromUser, text, created, seen);
    }

    public Message(int id, UserProfile fromUser, String text, String created, boolean seen, ShortChat shortChat) {
        super(id, fromUser, text, created, seen);
        this.shortChat = shortChat;
    }

    public Message() {
    }

    public ShortChat getShortChat() {
        return shortChat;
    }

    public void setShortChat(ShortChat shortChat) {
        this.shortChat = shortChat;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }
}
