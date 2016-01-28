package com.imaginabit.yonodesperdicion.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.utils.Utils;

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

    // Statuses
    public static enum Status {
                                AVAILABLE,
                                BOOKED,
                                DELIVERED
                              };

    private int id;
    private String title;
    private String body;
    private String imageUrl;
    private int weightGrams;
    private Date expiration;
    private int postalCode;
    private Status status;
    private int userId;
    private String userName;
    private boolean favorite;
    private Location location;
    private int lastDistance;

    // Constructors

    public Ad(
                String title,
                String body,
                String imageUrl,
                int weightGrams,
                Date expiration,
                int postalCode,
                Status status,
                int userId,
                boolean favorite,
                String userName
             ) {
        // Set the properties
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.weightGrams = weightGrams;
        this.expiration = expiration;
        this.postalCode = postalCode;
        this.status = status;
        this.userId = userId;
        this.favorite = favorite;
        this.userName = userName;
    }

    public Ad(
                String title,
                String body,
                String imageUrl,
                int weightGrams,
                String expiration,
                String postalCode,
                int status,
                int userId,
                String userName
             ) throws ParseException {
        // Set properties
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.weightGrams = weightGrams;

        Log.d(TAG, "Ad: expiration " + expiration);

        if (Utils.isNotEmptyOrNull(expiration) && ! "null".equals(expiration)) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            this.expiration = format.parse(expiration);
        } else {
            // if there is no date (this must not happends! but...) set date to now
            this.expiration = null;
        }

        this.postalCode = Integer.parseInt(postalCode);
        this.status = Status.values()[status-1];
        this.userId = userId;
        this.userName = userName;
        this.favorite = false;
    }

    public Ad(
                int id,
                String title,
                String body,
                String imageUrl,
                int weightGrams,
                String expiration,
                String postalCode,
                int status,
                int userId,
                String userName
             ) throws ParseException {
        // Set the properties
        this.id = id;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.weightGrams = weightGrams;

        Log.d(TAG, "Ad: expiration "+expiration);

        if (Utils.isNotEmptyOrNull(expiration) && expiration!="null" ) {
            //2015-12-15
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            this.expiration = format.parse(expiration);
        } else {
            // if there is no date (this must not happends! but...) set date to now
            this.expiration = null;
        }

        this.postalCode = Integer.parseInt(postalCode);
        this.status = Status.values()[status - 1];
        this.userId = userId;
        this.userName = userName;
        this.favorite = false;
    }

    public Ad(
                String title,
                String body,
                String imageUrl,
                int weightGrams,
                Date expiration,
                int postalCode,
                Status status,
                int userId,
                String userName
             ) {
        // Set properties
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.weightGrams = weightGrams;
        this.expiration = expiration;
        this.postalCode = postalCode;
        this.status = status;
        this.userId = userId;
        this.userName = userName;
        this.favorite = false;
    }
    public Ad(
            String title,
            String body
    ) {
        // Set properties
        this.title = title;
        this.body = body;
        this.imageUrl = "";
        this.weightGrams = 0;
        this.expiration = null;
        this.postalCode = 0;
        this.status = Status.AVAILABLE;
        this.userId = 0;
        this.userName = "";
        this.favorite = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getWeightGrams() {
        return weightGrams;
    }

    public String getWeightKgStr(){
        float kilos = (float) (weightGrams / 1000.0);

        DecimalFormat df = new DecimalFormat("0.0#");
        df.setRoundingMode(RoundingMode.HALF_DOWN);

        String txt =  " Kg";
        String strKilos = df.format(kilos);
        txt = strKilos + txt;

//        Log.d(TAG, "getWeightKgStr: "+mTitle + " :" + Integer.toString(mWeightGrams) + " " + kilos + " "+ txt );

        return txt;
    }

    public void setWeightGrams(int weightGrams) {
        this.weightGrams = weightGrams;
    }

    public Date getExpiration() {
        return expiration;
    }

    // Polymorphic

    public String getExpirationDateLong() {
        return getExpirationDate("hasta el ");
    }

    public String getExpirationDate() {
        return getExpirationDate("");
    }

    public String getExpirationDate(String prefix) {
        if (expiration != null) {
            // Default prefix
            if (Utils.isEmptyOrNull(prefix)) {
                prefix = "";
            }
            // Format expiration date
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return prefix + df.format(expiration);
        }
        return "";
    }

    /**
     * Return string that indicate the date relative to today
     * pe. : In 2 days, 5 days ago...
     */
    public String getExpirationDateRelative() {
        return getExpirationDate("Disponible hasta ");
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public Status getStatus() {
        return status;
    }

    public int getStatusInt() {
        switch (status){
            case AVAILABLE:
                return 0;

            case BOOKED:
                return 1;

            case DELIVERED:
                return 2;
        }
        return 0;
    }

    public String getStatusStr() {
        switch (status){
            case AVAILABLE:
                return "disponible";

            case BOOKED:
                return "reservado";

            case DELIVERED:
                return "entregado";
        }
        return "";
    }

    public int getStatusColor() {
        switch (status){
            case AVAILABLE:
                return R.color.ad_disponible;

            case BOOKED:
                return R.color.ad_reservado;
        }

        return R.color.white;
    }


    public int getStatusImage(){
        int r;
        String t;
        switch (status){
            case AVAILABLE:
                r = R.drawable.circle_available;
                t= "disponible";
                break;
            case BOOKED:
                r = R.drawable.circle_booked;
                t = "reservado";
                break;
            default:
                t = "otro";
                r = R.color.white;
        }
        Log.d(TAG, "getStatusImage: "+ t + " " +r );
        return r;
    }


    public void setStatus(Status status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Ad{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", weightGrams=" + weightGrams +
                ", expiration=" + expiration +
                ", postalCode=" + postalCode +
                ", status=" + status +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", favorite=" + favorite +
                ", weightKgStr='" + getWeightKgStr() + '\'' +
                ", expirationDateLong='" + getExpirationDateLong() + '\'' +
                ", expirationDateRelative='" + getExpirationDateRelative() + '\'' +
                ", statusInt=" + getStatusInt() +
                ", statusStr='" + getStatusStr() + '\'' +
                ", statusColor=" + getStatusColor() +
                ", scribeContents=" + describeContents() +
                '}';
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getLastDistance() {
        return lastDistance;
    }

    public void setLastDistance(int lastDistance) {
        this.lastDistance = lastDistance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeString(this.imageUrl);
        dest.writeInt(this.weightGrams);
        dest.writeLong(expiration != null ? expiration.getTime() : -1);
        dest.writeInt(this.postalCode);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeInt(this.userId);
        dest.writeString(this.userName);
        dest.writeByte(favorite ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.location, 0);
        dest.writeInt(this.lastDistance);
    }

    protected Ad(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.body = in.readString();
        this.imageUrl = in.readString();
        this.weightGrams = in.readInt();
        long tmpExpiration = in.readLong();
        this.expiration = tmpExpiration == -1 ? null : new Date(tmpExpiration);
        this.postalCode = in.readInt();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Status.values()[tmpStatus];
        this.userId = in.readInt();
        this.userName = in.readString();
        this.favorite = in.readByte() != 0;
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.lastDistance = in.readInt();
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
