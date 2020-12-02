package com.lazysecs.meetingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProfilePhoto implements Parcelable {
    public static final Creator<ProfilePhoto> CREATOR = new Creator<ProfilePhoto>() {
        @Override
        public ProfilePhoto createFromParcel(Parcel in) {
            return new ProfilePhoto(in);
        }

        @Override
        public ProfilePhoto[] newArray(int size) {
            return new ProfilePhoto[size];
        }
    };
    @PrimaryKey(autoGenerate = false)
    private String photo;

    public ProfilePhoto(String photo) {
        this.photo = photo;
    }

    public ProfilePhoto() {

    }

    protected ProfilePhoto(Parcel in) {
        photo = in.readString();
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photo);
    }
}
