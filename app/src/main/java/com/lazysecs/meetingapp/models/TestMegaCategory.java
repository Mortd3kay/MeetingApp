package com.lazysecs.meetingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TestMegaCategory implements Parcelable {

    public final String chipGroup;

    public TestMegaCategory(String chipGroup){
        this.chipGroup = chipGroup;
    }

    protected TestMegaCategory(Parcel in) {
        chipGroup = in.readString();
    }

    public static final Creator<TestMegaCategory> CREATOR = new Creator<TestMegaCategory>() {
        @Override
        public TestMegaCategory createFromParcel(Parcel in) {
            return new TestMegaCategory(in);
        }

        @Override
        public TestMegaCategory[] newArray(int size) {
            return new TestMegaCategory[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chipGroup);
    }
}
