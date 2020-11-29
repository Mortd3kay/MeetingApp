package com.lazysecs.meetingapp.interfaces;

public interface TicketManager extends BaseContentManager {

    void saveName(String name);

    String getName();

    void savePrice(int price);

    int getPrice();
}
