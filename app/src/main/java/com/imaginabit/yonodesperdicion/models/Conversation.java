package com.imaginabit.yonodesperdicion.models;

import java.util.Date;
import java.util.List;

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
    public boolean loaded= false;

    private List<Message> mMessages;

    public Conversation(int id, String subject, Date createdAt, Date updatedAt, int threadId) {
        mId = id;
        mSubject = subject;
        mCreatedAt = createdAt;
        mUpdatedAt = updatedAt;
        mThreadId = threadId;


//        MessagesUtils.getConversationMessages(id, new MessagesUtils.MessagesCallback() {
//            @Override
//            public void onFinished(List<Message> messages, Exception e) {
//                Log.d(TAG, "onFinished: getConversation messages");
//                mMessages = messages;
//                loaded= true;
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//
//            }
//        });
    }

    public Conversation(int id, String subject) {
        mId = id;
        mSubject = subject;
        mCreatedAt = new Date();
        mUpdatedAt = new Date();
        mThreadId = 0;
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

    public List<Message> getMessages() {
        return mMessages;
    }

    public void setMessages(List<Message> messages) {
        mMessages = messages;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "mId=" + mId +
                ", mSubject='" + mSubject + '\'' +
                ", mCreatedAt=" + mCreatedAt +
                ", mUpdatedAt=" + mUpdatedAt +
                ", mThreadId=" + mThreadId +
                ", loaded=" + loaded +
                ", mMessages=" + mMessages +
                '}';
    }
}
