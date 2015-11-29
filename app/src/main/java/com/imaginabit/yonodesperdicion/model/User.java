package com.imaginabit.yonodesperdicion.model;

/**
 * Created by Fernando on 29/11/15.
 * User Model
 */
public class User {


    private int mUserId;
    private String mName;
    private String mUserName;
    private String mAddress;
    private String mZipCode;
    private int mGrams;
    private float mRatting;

    public User(int userId, String name, String userName, String address, String zipCode, int grams, float ratting) {
        mUserId = userId;
        mName = name;
        mUserName = userName;
        mAddress = address;
        mZipCode = zipCode;
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

    public float getRatting() {
        return mRatting;
    }

    public void setRatting(float ratting) {
        mRatting = ratting;
    }
}