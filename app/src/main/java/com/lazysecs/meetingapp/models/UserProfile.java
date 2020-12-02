package com.lazysecs.meetingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class UserProfile implements Parcelable {

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    private int id;
    @SerializedName("firebase_uid")
    private String firebaseUid;
    @SerializedName("date_of_birth")
    private String dateOfBirth;
    @SerializedName("token")
    private String token;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("city")
    private String city;
    @SerializedName("education")
    private String education;
    @SerializedName("job")
    private String job;
    @Ignore
    @SerializedName("photo")
    private ProfilePhoto photo;
    @SerializedName("sex")
    private String sex;
    @Ignore
    @SerializedName("categories")
    private List<Category> categories;
    @SerializedName("is_confirmed")
    private Boolean confirmed;
    @SerializedName("is_filled")
    private Boolean filled;
    @Ignore
    @SerializedName("chats")
    private List<ShortChat> shortChats;
    @Ignore
    @SerializedName("requests")
    private List<ShortRequest> shortRequests;
    @SerializedName("new_messages_count")
    private int newMessagesCount;
    @SerializedName("new_requests_count")
    private int newRequestsCount;

    public UserProfile(int id,
                       String firebaseUid,
                       String dateOfBirth,
                       String token,
                       String username,
                       String email,
                       String firstName,
                       String lastName,
                       String city,
                       String education,
                       String job,
                       ProfilePhoto photo,
                       String sex,
                       List<Category> categories,
                       Boolean confirmed,
                       Boolean filled,
                       int newMessagesCount,
                       int newRequestsCount) {
        this.id = id;
        this.firebaseUid = firebaseUid;
        this.dateOfBirth = dateOfBirth;
        this.token = token;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.education = education;
        this.job = job;
        this.photo = photo;
        this.sex = sex;
        this.categories = categories;
        this.confirmed = confirmed;
        this.filled = filled;
        this.newMessagesCount = newMessagesCount;
        this.newRequestsCount = newRequestsCount;
    }

    public UserProfile(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserProfile(int id) {
        this.id = id;
    }
    public UserProfile() {
    }

    protected UserProfile(Parcel in) {
        id = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        photo = in.readParcelable(getClass().getClassLoader());
    }

    public List<ShortChat> getShortChats() {
        return shortChats;
    }

    public void setShortChats(List<ShortChat> shortChats) {
        this.shortChats = shortChats;
    }

    public List<ShortRequest> getShortRequests() {
        return shortRequests;
    }

    public void setShortRequests(List<ShortRequest> shortRequests) {
        this.shortRequests = shortRequests;
    }

    public int getNewMessagesCount() {
        return newMessagesCount;
    }

    public void setNewMessagesCount(int newMessagesCount) {
        this.newMessagesCount = newMessagesCount;
    }

    public int getNewRequestsCount() {
        return newRequestsCount;
    }

    public void setNewRequestsCount(int newRequestsCount) {
        this.newRequestsCount = newRequestsCount;
    }

    public Boolean getFilled() {
        return filled;
    }

    public void setFilled(Boolean filled) {
        this.filled = filled;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProfilePhoto getPhoto() {
        return photo;
    }

    public void setPhoto(ProfilePhoto photo) {
        this.photo = photo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeParcelable(photo, flags);
    }
}