package com.lazysecs.meetingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GeoPoint implements Parcelable {
    public static final Parcelable.Creator<GeoPoint> CREATOR = new Parcelable.Creator<GeoPoint>() {
        public GeoPoint createFromParcel(Parcel in) {
            return new GeoPoint(in);
        }

        public GeoPoint[] newArray(int size) {
            return new GeoPoint[size];
        }
    };
    @PrimaryKey(autoGenerate = true)
    private String id;
    private double latitude;
    private double longitude;
    private String address;

    public GeoPoint(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    private GeoPoint(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address);
    }
}
