package com.imaginabit.yonodesperdicion.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.util.AppUtils;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by fer2015julio on 24/11/15.
 * Model of Ads Data
 */
public class Ad implements Parcelable {


    private static final String TAG = "Ad Model";

    //disponible, reservado, entregado
    public static enum Status {AVAILABLE, BOOKED, DELIVERED}

    private int mId;
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
    public Ad(int Id, String title, String body, String imageUrl, int weightGrams, String expiration, String postalCode, int status, int userId, String userName) throws ParseException {
        mId = Id;
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
        float kilos = (float) (mWeightGrams / 1000.0);

        DecimalFormat df = new DecimalFormat("0.0#");
        df.setRoundingMode(RoundingMode.HALF_DOWN);

        String txt =  " Kg";
        String strKilos = df.format(kilos);
        txt = strKilos + txt;

//        Log.d(TAG, "getWeightKgStr: "+mTitle + " :" + Integer.toString(mWeightGrams) + " " + kilos + " "+ txt );

        return txt;
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
    public int getStatusInt() {
        int r;
        switch (mStatus){
            case AVAILABLE:
                r = 0;
                break;
            case BOOKED:
                r = 1;
                break;
            case DELIVERED:
                r = 2;
                break;
            default:
                r=0;
        }
        return r;
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
        String t;
        switch (mStatus){
            case AVAILABLE:
                r = R.color.ad_disponible;
                t = "disponible";
                break;
            case BOOKED:
                r = R.color.ad_reservado;
                t = "reservado";
                break;
            default:
                r = R.color.white;
                t = "blanco ";

        }
        Log.d(TAG, "getStatusColor: "+ r );
        return r;
    }
    public int getStatusImage(){
        int r;
        String t;
        switch (mStatus){
            case AVAILABLE:
                r = R.drawable.circle_available;
                t= "disponible";
                break;
            case BOOKED:
                r = R.drawable.circle_booked;
                t = "reservado";
                break;
            default:
                r = R.drawable.circle_white;
                t = "blanco";
        }
        Log.d(TAG, "getStatusImage: "+ r);
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

    @Override
    public String toString() {
        return "Ad{" +
                "mId='" + mId + '\'' +
                ", mTitle='" + mTitle + '\'' +
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

    public int getId() {
        return mId;
    }
    public void setId(int id) {
        mId = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mBody);
        dest.writeString(this.mImageUrl);
        dest.writeInt(this.mWeightGrams);
        dest.writeLong(mExpiration != null ? mExpiration.getTime() : -1);
        dest.writeInt(this.mPostalCode);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
        dest.writeInt(this.mUserId);
        dest.writeByte(mFavorite ? (byte) 1 : (byte) 0);
        dest.writeString(this.mUserName);
    }

    protected Ad(Parcel in) {
        this.mId = in.readInt();
        this.mTitle = in.readString();
        this.mBody = in.readString();
        this.mImageUrl = in.readString();
        this.mWeightGrams = in.readInt();
        long tmpMExpiration = in.readLong();
        this.mExpiration = tmpMExpiration == -1 ? null : new Date(tmpMExpiration);
        this.mPostalCode = in.readInt();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : Status.values()[tmpMStatus];
        this.mUserId = in.readInt();
        this.mFavorite = in.readByte() != 0;
        this.mUserName = in.readString();
    }

    public static final Creator<Ad> CREATOR = new Creator<Ad>() {
        public Ad createFromParcel(Parcel source) {
            return new Ad(source);
        }

        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };
}
