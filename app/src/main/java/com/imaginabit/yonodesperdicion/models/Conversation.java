package com.imaginabit.yonodesperdicion.models;

import java.util.Date;

/**
 * Created by Fernando Ramírez on 13/01/16.
 */
public class Conversation {
    private static final String TAG = "Conversation Model";


    private int mId;
    private String mSubject;
    private Date mCreatedAt;
    private Date mUpdatedAt;
    private int mThreadId;

    public Conversation(int id, String subject, Date createdAt, Date updatedAt, int threadId) {
        mId = id;
        mSubject = subject;
        mCreatedAt = createdAt;
        mUpdatedAt = updatedAt;
        mThreadId = threadId;
    }

    public static String getTAG() {
        return TAG;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }

    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public int getThreadId() {
        return mThreadId;
    }

    public void setThreadId(int threadId) {
        mThreadId = threadId;
    }
}
