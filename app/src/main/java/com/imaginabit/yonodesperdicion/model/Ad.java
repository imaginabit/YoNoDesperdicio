package com.imaginabit.yonodesperdicion.model;

import android.util.Log;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.util.AppUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by fer2015julio on 24/11/15.
 * Model of Ads Data
 */
public class Ad {

    private static final String TAG = "Ad Model";

    //disponible, reservado, entregado
    public static enum Status {AVAILABLE, BOOKED, DELIVERED}

    private String mTitle;
    private String mBody;
    private String mImageUrl;
    private int mWeightGrams;
    private Date mExpiration;
    private int mPostalCode;
    private Status mStatus;
    private int mUserId;
    private boolean mFavorite;


    public Ad(String title, String body, String imageUrl, int weightGrams, Date expiration, int postalCode, Status status, int userId, boolean favorite, String userName) {
        mTitle = title;
        mBody = body;
        mImageUrl = imageUrl;
        mWeightGrams = weightGrams;
        mExpiration = expiration;
        mPostalCode = postalCode;
        mStatus = status;
        mUserId = userId;
        mFavorite = favorite;
        mUserName = userName;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "mTitle='" + mTitle + '\'' +
                ", mBody='" + mBody + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", mWeightGrams=" + mWeightGrams +
                ", mExpiration=" + mExpiration +
                ", mPostalCode=" + mPostalCode +
                ", mStatus=" + mStatus +
                ", mUserId=" + mUserId +
                ", mFavorite=" + mFavorite +
                ", mUserName='" + mUserName + '\'' +
                '}';
    }

    public Ad(String title, String body, String imageUrl, int weightGrams, String expiration, String postalCode, int status, int userId, String userName) throws ParseException {
        mTitle = title;
        mBody = body;
        mImageUrl = imageUrl;
        mWeightGrams = weightGrams;
        Log.d(TAG, "Ad: expiration "+expiration);
        if (AppUtils.isNotEmptyOrNull(expiration) && expiration!="null" ) {

            //2015-12-15
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            mExpiration = format.parse(expiration);
        } else {
            //if there is no date (this must not happends! but...) set date to now
            //mExpiration = new Date();
            mExpiration = null;

        }
        mPostalCode = Integer.parseInt(postalCode);
        mStatus = Status.values()[status-1];
        mUserId = userId;
        mUserName = userName;
        mFavorite = false;
    }

    public Ad(String title, String body, String imageUrl, int weightGrams, Date expiration, int postalCode, Status status, int userId, String userName) {
        mTitle = title;
        mBody = body;
        mImageUrl = imageUrl;
        mWeightGrams = weightGrams;
        mExpiration = expiration;
        mPostalCode = postalCode;
        mStatus = status;
        mUserId = userId;
        mUserName = userName;
        mFavorite = false;
    }

    private String mUserName;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public int getWeightGrams() {
        return mWeightGrams;
    }
    public String getWeightKgStr(){
        float kilos = mWeightGrams / 1000;
        String txt =  " Kg";
        return kilos+txt;
    }

    public void setWeightGrams(int weightGrams) {
        mWeightGrams = weightGrams;
    }

    public Date getExpiration() {
        return mExpiration;
    }

    public String getExpirationStrLong(){
        if( mExpiration != null) {
            //            String txt = "Fecha límite de entrega ";
            String txt = "hasta el ";
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String t = df.format(mExpiration);

            return txt + t;
        }
        return "";
    }

    /**
     * Return string that indicate the date relative to today
     * pe. : In 2 days, 5 days ago...
     * @return
     */
    public String getExpirationStrRelative(){
        if( mExpiration != null) {
//            String txt = "Fecha límite de entrega ";
            String txt = "Disponible hasta ";
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String t = df.format(mExpiration);

            return txt + t;
        }
        return "";
    }

    public void setExpiration(Date expiration) {
        mExpiration = expiration;
    }

    public int getPostalCode() {
        return mPostalCode;
    }

    public void setPostalCode(int postalCode) {
        mPostalCode = postalCode;
    }

    public Status getStatus() {
        return mStatus;
    }

    public String getStatusStr() {
        String r;
        switch (mStatus){
            case AVAILABLE:
                r = "disponible";
                break;
            case BOOKED:
                r = "reservado";
                break;
            case DELIVERED:
                r = "entregado";
                break;
            default:
                r="";
        }
        return r;
    }

    public int getStatusColor() {
        int r;
        switch (mStatus){
            case AVAILABLE:
                r = R.color.ad_disponible;
                break;
            case BOOKED:
                r = R.color.ad_reservado;
                break;
            default:
                r = R.color.white;;
        }
        return r;
    }


    public void setStatus(Status status) {
        mStatus = status;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }
}