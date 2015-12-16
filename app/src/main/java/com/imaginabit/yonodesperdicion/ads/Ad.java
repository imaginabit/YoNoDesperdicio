package com.imaginabit.yonodesperdicion.ads;

import android.database.Cursor;

import java.util.Date;

/**
 * Created by fer2015julio on 2/09/15.
 */
public class Ad {

//    public static enum Type { GIVE, WANT };
    public static enum Status { AVAILABLE, BOOKED, DELIVERED };

    private int id;
    private String title;
    private String body;

    private String username;
    private int postalCode;

    private int woeid;
    private String dateCreated;
    private String imageFilename;

    private int weight;
    private Date expiration;

    private Ad.Status status;
    private boolean commentsEnabled;
    private boolean favorite;

    //private Ad.Type type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isCommentsEnabled() {
        return commentsEnabled;
    }

    public void setCommentsEnabled(boolean commentsEnabled) {
        this.commentsEnabled = commentsEnabled;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    /**
     * Constructor
     */
    public Ad(Cursor cursor) {

    }
}
