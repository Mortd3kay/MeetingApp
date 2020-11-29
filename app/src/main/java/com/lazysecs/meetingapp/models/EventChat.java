package com.lazysecs.meetingapp.models;

public class EventChat {

    private String id;
    private long eventId;

    public EventChat(String id, long eventId) {
        this.id = id;
        this.eventId = eventId;
    }

    public EventChat() {

    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
