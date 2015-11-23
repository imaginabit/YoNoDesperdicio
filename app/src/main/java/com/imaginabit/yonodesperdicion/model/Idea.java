package com.imaginabit.yonodesperdicion.model;

/**
 * Created by fer2015julio on 19/11/15.
 */
public class Idea {
    public static final String TAG = "Idea";

    private final String mId;
    private final String mCategory;
    private final String mTitle;
    private final String mIntroduction;
    private final String mIngredients;
    private final String mBody;
    private final String mImageUrl;
    private final String mUserId;
    private final String mTagLists; //i dont going to use this now just save the string


    public Idea( String title, String id, String tag_list, String category, String user_id,
                 String image_url, String body, String ingredients, String introduction) {
        mTitle = title;
        mId = id;
        mTagLists = tag_list;
        mCategory = category;
        mUserId = user_id;
        mImageUrl = image_url;
        mBody = body;
        mIngredients = ingredients;
        mIntroduction = introduction;
    }

    public Idea( String title,
                 String id,
                 String category,
                 String image_url,
                 String introduction) {
        mTitle = title;
        mId = id;
        mCategory = category;
        mImageUrl = image_url;
        mBody = introduction;
        mIntroduction = introduction;

        mIngredients= "";
        mUserId= "";
        mTagLists="";
    }

    public String getId() {
        return mId;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getIntroduction() {
        return mIntroduction;
    }

    public String getIngredients() {
        return mIngredients;
    }

    public String getBody() {
        return mBody;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getTagLists() {
        return mTagLists;
    }

    @Override
    public String toString() {
        return "Idea{" +
                "mId='" + mId + '\'' +
                ", mCategory='" + mCategory + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mIntroduction='" + mIntroduction + '\'' +
                ", mIngredients='" + mIngredients + '\'' +
                ", mBody='" + mBody + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", mUserId='" + mUserId + '\'' +
                ", mTagLists='" + mTagLists + '\'' +
                '}';
    }
}


