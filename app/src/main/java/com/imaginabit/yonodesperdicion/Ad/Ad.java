package com.imaginabit.yonodesperdicion.Ad;

import java.util.Date;

/**
 * Created by fer2015julio on 2/09/15.
 */
public class Ad {

//    public static enum Type { GIVE, WANT };
    public static enum Status { AVAILABLE, BOOKED, DELIVERED };

    private int _id;
    private String title;
    private String body;

    private String username;
    private int postal_code;


    private int woeid;
    private String date_created;
    private String image_file_name;

    private int weight;
    private Date expiration;

    private Ad.Status status;
    private boolean comments_enabled;
    private boolean favorite;

    //private Ad.Type type;

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public int getWoeid() {
        return woeid;
    }

    public void setWoeid(int woeid) {
        this.woeid = woeid;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getImage_file_name() {
        return image_file_name;
    }

    public void setImage_file_name(String image_file_name) {
        this.image_file_name = image_file_name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isComments_enabled() {
        return comments_enabled;
    }

    public void setComments_enabled(boolean comments_enabled) {
        this.comments_enabled = comments_enabled;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
