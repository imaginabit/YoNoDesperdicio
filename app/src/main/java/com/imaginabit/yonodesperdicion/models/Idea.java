package com.imaginabit.yonodesperdicion.models;

/**
 * Created by fer2015julio on 19/11/15.
 */
public class Idea {
    public static final String TAG = "Idea";

    private String id;
    private String category;
    private String title;
    private String introduction;
    private String ingredients;
    private String body;
    private String imageUrl;
    private String userId;
    private String tagLists; // I am not going to use this now just save the string


    public Idea(
                    String title,
                    String id,
                    String tagLists,
                    String category,
                    String userId,
                    String imageUrl,
                    String body,
                    String ingredients,
                    String introduction
              ) {
        this.title = title;
        this.id = id;
        this.tagLists = tagLists;
        this.category = category;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.body = body;
        this.ingredients = ingredients;
        this.introduction = introduction;
    }

    public Idea(
                    String title,
                    String id,
                    String category,
                    String imageUrl,
                    String introduction
               ) {
        this.title = title;
        this.id = id;
        this.category = category;
        this.imageUrl = imageUrl;
        this.body = introduction;
        this.introduction = introduction;

        this.ingredients = "";
        this.userId = "";
        this.tagLists ="";
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getBody() {
        return body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getTagLists() {
        return tagLists;
    }
}


