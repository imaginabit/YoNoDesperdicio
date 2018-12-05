
package com.imaginabit.yonodesperdicion.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imaginabit.yonodesperdicion.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Offer {
    private static final String TAG = "Offer Model";


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("store")
    @Expose
    private String store;
    @SerializedName("until")
    @Expose
    private String until;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("user_id")
    @Expose
    private Integer userID;

    @SerializedName("image")
    @Expose
    private Image image;

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    private Date expiration;

//    public Offer(Parcel in) {
//        if (in.readByte() == 0) {
//            id = null;
//        } else {
//            id = in.readInt();
//        }
//        title = in.readString();
//        address = in.readString();
//        store = in.readString();
//        until = in.readString();
//        status = in.readString();
//    }

//    public static final Creator<Offer> CREATOR = new Creator<Offer>() {
//        @Override
//        public Offer createFromParcel(Parcel in) {
//            return new Offer(in);
//        }
//
//        @Override
//        public Offer[] newArray(int size) {
//            return new Offer[size];
//        }
//    };

    public Offer(){

    }

    public Offer(
        String title,
        String address,
        String store,
        String until,
        String status,
        Date expiration
    ) {
        this.title = title;
        this.address = address;
        this.store = store;
        this.until = until;
        this.expiration = expiration;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getUntil() {
        return until;
    }

    public void setUntil(String until) {
        this.until = until;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) { this.description = description; }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getExpirationDateLong() {
        return getExpirationDate("hasta el ");
    }

    public String getExpirationDate() {
        return getExpirationDate("");
    }

    public String getExpirationDate(String prefix) {
        if (this.getExpiration() != null) {
            // Default prefix
            if (Utils.isEmptyOrNull(prefix)) {
                prefix = "";
            }
            // Format expiration date
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return prefix + df.format(this.expiration);
        }
        return "";
    }

    public Date getExpiration() {

        if (Utils.isNotEmptyOrNull(until) && ! "null".equals(until)) {
//            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );

            try {
                this.expiration = format.parse( until );
            } catch (ParseException e) {
                e.printStackTrace();
                this.expiration = null;
            }
        } else {
            // if there is no date (this must not happends! but...) set date to now
            this.expiration = null;
        }

        return expiration;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }

//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        if (id == null) {
//            dest.writeByte((byte) 0);
//        } else {
//            dest.writeByte((byte) 1);
//            dest.writeInt(id);
//        }
//        dest.writeString(title);
//        dest.writeString(address);
//        dest.writeString(store);
//        dest.writeString(until);
//        dest.writeString(status);
//    }



//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(this.id);
//        dest.writeString(this.title);
//        dest.writeString(this.address);
//        dest.writeString(this.store);
//        dest.writeString(this.until);
//        dest.writeString(this.status);
//
//
//        @SerializedName("image")
//        @Expose
//        private Image image;
//
//        private Date expiration;
//
//    }


}
