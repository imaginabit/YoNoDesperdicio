package com.imaginabit.yonodesperdicion.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fernando on 29/11/15.
 * User Model
 */
public class User implements Parcelable {


    private int mUserId;
    private String mName;
    private String mUserName;
    private String mAddress;
    private String mZipCode;
    private int mGrams;
    private Double mRatting;

    public User(int userId, String name, String userName, String address, String zipCode, int grams, Double ratting) {
        mUserId = userId;
        mName = name;
        mUserName = userName;
        mAddress = address;
        mZipCode = zipCode;
        mGrams = grams;
        mRatting = ratting;
    }

    public User(int userId, String userName, int grams, Double ratting) {
        mUserId = userId;
        mUserName = userName;
        mGrams = grams;
        mRatting = ratting;
    }



    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getZipCode() {
        return mZipCode;
    }

    public void setZipCode(String zipCode) {
        mZipCode = zipCode;
    }

    public int getGrams() {
        return mGrams;
    }

    public void setGrams(int grams) {
        mGrams = grams;
    }

    public Double getRatting() {
        return mRatting;
    }

    public void setRatting(Double ratting) {
        mRatting = ratting;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mUserId);
        dest.writeString(this.mName);
        dest.writeString(this.mUserName);
        dest.writeString(this.mAddress);
        dest.writeString(this.mZipCode);
        dest.writeInt(this.mGrams);
        dest.writeValue(this.mRatting);
    }

    protected User(Parcel in) {
        this.mUserId = in.readInt();
        this.mName = in.readString();
        this.mUserName = in.readString();
        this.mAddress = in.readString();
        this.mZipCode = in.readString();
        this.mGrams = in.readInt();
        this.mRatting = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
