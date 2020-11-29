package com.lazysecs.meetingapp.models;

public class WebSocketEvent {
    private String type;

    public WebSocketEvent(String type) {
        this.type = type;
    }

    public boolean isRequestEvent(){
        String typeRequest = "request_event";
        return type.equals(typeRequest);
    }

    public boolean isMessageEvent(){
        String typeMessage = "message_event";
        return type.equals(typeMessage);
    }

    public boolean isPrivateMessageEvent(){
        String typeMessage = "private_message_event";
        return type.equals(typeMessage);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
