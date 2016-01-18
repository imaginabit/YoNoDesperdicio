package com.imaginabit.yonodesperdicion.models;

import java.util.Date;

/**
 * Created by Fernando Ramírez on 15/01/16.
 */
public class Message {

//    "id": 17,
//            "body": "soetnuhasonehusanoeu",
//            "subject": "prueba",
//            "sender_id": 44,
//            "sender_type": "User",
//            "conversation_id": 11,
//            "draft": false,
//            "updated_at": "2015-12-29T14:07:27.000Z",
//            "created_at": "2015-12-29T14:07:27.000Z",
//            "notified_object_id": null,
//            "notified_object_type": null,
//            "notification_code": null,
//            "attachment": {
//        "url": null
//    },
//            "global": false,
//            "expires": null


    private static final String TAG = "Message Model";

    private int mId;
    private String  mBody;
    private String  mSubject;
    private int mSender_id;
    private int mSender_type;
    private int mConversation_id;
    private Boolean mDraft;
    private Date mUpdated_at;
    private Date mCreated_at;
    private int mNotified_object_id;
    private int mNotified_object_type;
    private int mNotification_code;
    private String mAttachment;
    private Boolean mGlobal;
    private String mExpires;

    public Message(int id, String body, String subject, int sender_id, int sender_type, int conversation_id, Boolean draft, Date updated_at, Date created_at, int notified_object_id, int notified_object_type, int notification_code, String attachment, Boolean global, String expires) {
        mId = id;
        mBody = body;
        mSubject = subject;
        mSender_id = sender_id;
        mSender_type = sender_type;
        mConversation_id = conversation_id;
        mDraft = draft;
        mUpdated_at = updated_at;
        mCreated_at = created_at;
        mNotified_object_id = notified_object_id;
        mNotified_object_type = notified_object_type;
        mNotification_code = notification_code;
        mAttachment = attachment;
        mGlobal = global;
        mExpires = expires;
    }

    public Message(int id, String body, String subject, int sender_id, int sender_type, int conversation_id, Boolean draft, Date updated_at, Date created_at) {
        mId = id;
        mBody = body;
        mSubject = subject;
        mSender_id = sender_id;
        mSender_type = sender_type;
        mConversation_id = conversation_id;
        mDraft = draft;
        mUpdated_at = updated_at;
        mCreated_at = created_at;

        mNotified_object_id = -1;
        mNotified_object_type = -1;
        mNotification_code = -1;

        mAttachment = null;
        mGlobal = false;
        mExpires = null;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public int getSender_id() {
        return mSender_id;
    }

    public void setSender_id(int sender_id) {
        mSender_id = sender_id;
    }

    public int getSender_type() {
        return mSender_type;
    }

    public void setSender_type(int sender_type) {
        mSender_type = sender_type;
    }

    public int getConversation_id() {
        return mConversation_id;
    }

    public void setConversation_id(int conversation_id) {
        mConversation_id = conversation_id;
    }

    public Boolean getDraft() {
        return mDraft;
    }

    public void setDraft(Boolean draft) {
        mDraft = draft;
    }

    public Date getUpdated_at() {
        return mUpdated_at;
    }

    public void setUpdated_at(Date updated_at) {
        mUpdated_at = updated_at;
    }

    public Date getCreated_at() {
        return mCreated_at;
    }

    public void setCreated_at(Date created_at) {
        mCreated_at = created_at;
    }

    public int getNotified_object_id() {
        return mNotified_object_id;
    }

    public void setNotified_object_id(int notified_object_id) {
        mNotified_object_id = notified_object_id;
    }

    public int getNotified_object_type() {
        return mNotified_object_type;
    }

    public void setNotified_object_type(int notified_object_type) {
        mNotified_object_type = notified_object_type;
    }

    public int getNotification_code() {
        return mNotification_code;
    }

    public void setNotification_code(int notification_code) {
        mNotification_code = notification_code;
    }

    public String getAttachment() {
        return mAttachment;
    }

    public void setAttachment(String attachment) {
        mAttachment = attachment;
    }

    public Boolean getGlobal() {
        return mGlobal;
    }

    public void setGlobal(Boolean global) {
        mGlobal = global;
    }

    public String getExpires() {
        return mExpires;
    }

    public void setExpires(String expires) {
        mExpires = expires;
    }

    @Override
    public String toString() {
        return "Message{" +
                "mId=" + mId +
                ", mBody='" + mBody + '\'' +
                ", mSubject='" + mSubject + '\'' +
                ", mSender_id=" + mSender_id +
                ", mSender_type=" + mSender_type +
                ", mConversation_id=" + mConversation_id +
                ", mDraft=" + mDraft +
                ", mUpdated_at=" + mUpdated_at +
                ", mCreated_at=" + mCreated_at +
                ", mNotified_object_id=" + mNotified_object_id +
                ", mNotified_object_type=" + mNotified_object_type +
                ", mNotification_code=" + mNotification_code +
                ", mAttachment='" + mAttachment + '\'' +
                ", mGlobal=" + mGlobal +
                ", mExpires='" + mExpires + '\'' +
                '}';
    }
}
