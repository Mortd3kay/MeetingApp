package com.lazysecs.meetingapp.interfaces;

import com.lazysecs.meetingapp.models.GeoPoint;

import java.util.ArrayList;

public interface BaseContentManager {

    void saveAction(String action);

    String getAction();

    void saveId(int id);

    int getId();

    void saveDescription(String description);

    String getDescription();

    void saveDate(String date);

    String getDate();

    void saveTime(String time);

    String getTime();

    void saveCategories(ArrayList<String> categories);

    ArrayList<String> getCategories();

    void saveLocation(GeoPoint location);

    GeoPoint getLocation();
}
