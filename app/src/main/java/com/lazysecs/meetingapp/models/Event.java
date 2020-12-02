package com.lazysecs.meetingapp.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity
public class Event implements Parcelable {
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
    @Ignore
    @SerializedName("geo_point")
    private GeoPoint geoPoint;
    @PrimaryKey
    private int id;
    @Ignore
    private UserProfile creator;
    private String name;
    private String description;
    private boolean requested;
    private String date;
    private String time;
    @Ignore
    private List<Category> categories;
    @Ignore
    private List<UserProfile> members;
    private boolean ended;

    public Event(int id, UserProfile creator, String name, String description, boolean requested, String date, String time, GeoPoint geoPoint, List<Category> categories, List<UserProfile> members, boolean ended) {
        this.id = id;
        this.creator = creator;
        this.name = name;
        this.description = description;
        this.requested = requested;
        this.date = date;
        this.time = time;
        this.geoPoint = geoPoint;
        this.categories = categories;
        this.members = members;
        this.ended = ended;
    }

    public Event() {

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Event(Parcel in) {
        id = in.readInt();
        description = in.readString();
        date = in.readString();
        time = in.readString();
        geoPoint = in.readParcelable(getClass().getClassLoader());
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public boolean isRequested() {
        return requested;
    }

    public void setRequested(boolean requested) {
        this.requested = requested;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserProfile getCreator() {
        return creator;
    }

    public void setCreator(UserProfile creator) {
        this.creator = creator;
    }

    public List<UserProfile> getMembers() {
        return members;
    }

    public void setMembers(List<UserProfile> members) {
        this.members = members;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeParcelable(geoPoint, flags);
    }
}
