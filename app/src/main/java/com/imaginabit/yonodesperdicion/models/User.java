package com.imaginabit.yonodesperdicion.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.imaginabit.yonodesperdicion.utils.Utils;

/**
 * Created by Fernando on 29/11/15.
 * User Model
 */
public class User implements Parcelable {
    private static final String TAG = "User Model";

    private int mUserId;
    private String mName;
    private String mUserName;
    private String mAddress;
    private String mZipCode;
    private int mGrams;
    private float mRatting;
    private String mAvatar;

    public User(int userId, String name, String userName, String address, String zipCode, int grams, float ratting) {
        Log.d(TAG, "User() called with: " + "userId = [" + userId + "], name = [" + name + "], userName = [" + userName + "], address = [" + address + "], zipCode = [" + zipCode + "], grams = [" + grams + "], ratting = [" + ratting + "]");
        mUserId = userId;
        mName = name;
        mUserName = userName;
        mAddress = address;
        mZipCode = zipCode;
        mGrams = grams;
        mRatting = ratting;
    }

    public User(int userId, String name, String userName, String address, String zipCode, int grams, float ratting, String avatar) {
        mUserId = userId;
        mName = name;
        mUserName = userName;
        mAddress = address;
        mZipCode = zipCode;
        mGrams = grams;
        mRatting = ratting;
        mAvatar = avatar;
    }

    public User(int userId) {
        mUserId = userId;
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

    public String getAvatar() {
        if (Utils.isNotEmptyOrNull(mAvatar)){
            return mAvatar;
        } else {
            return "";
        }
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    @Override
    public String toString() {
        return "User{" +
                "mUserId=" + mUserId +
                ", mName='" + mName + '\'' +
                ", mUserName='" + mUserName + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mZipCode='" + mZipCode + '\'' +
                ", mGrams=" + mGrams +
                ", mRatting=" + mRatting +
                ", mAvatar='" + mAvatar + '\'' +
                '}';
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
        dest.writeFloat(this.mRatting);
        dest.writeString(this.mAvatar);
    }

    protected User(Parcel in) {
        this.mUserId = in.readInt();
        this.mName = in.readString();
        this.mUserName = in.readString();
        this.mAddress = in.readString();
        this.mZipCode = in.readString();
        this.mGrams = in.readInt();
        this.mRatting = in.readFloat();
        this.mAvatar = in.readString();
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
