package com.lazysecs.meetingapp.models;

public class Test3 {
    private String event;
    private String to_user_id;

    public Test3(String event, String to_user_id) {
        this.event = event;
        this.to_user_id = to_user_id;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
