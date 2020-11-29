package com.lazysecs.meetingapp.models;

import com.google.gson.annotations.SerializedName;

public class LastSeenMessage {
    @SerializedName("chat_id")
    private int chatId;
    @SerializedName("message_id")
    private int messageId;

    public LastSeenMessage(int chatId, int messageId) {
        this.chatId = chatId;
        this.messageId = messageId;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
