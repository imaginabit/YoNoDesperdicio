package com.imaginabit.yonodesperdicion.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.helpers.VolleyErrorHelper;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.models.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Fernando Ramírez on 12/01/16.
 */
public class MessagesUtils {
    private static final String TAG = "MessagesUtils";
    private static Context context;
    private static ProgressDialog pd;

    public static Activity mCurrentActivity;

    //Users Credentials needed
    /*
    Get list of user conversations and last message of this conversation
     */
    public static void getConversations(final Context context, final ConversationsCallback callback, Activity activity){
        MessagesUtils.context = context;
        // Show Loading dialog
        MessagesUtils.pd = ProgressDialog.show(context, "", context.getString(R.string.loading));
        mCurrentActivity = activity;

        try{
            JSONObject jsonRequest = new JSONObject();

//           Mailbox_id puede ser: “inbox”, “sent” o “trash”

            RequestQueue queue = VolleySingleton.getRequestQueue();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    Constants.CONVERSATIONS_API_URL,
                    jsonRequest,
                    MessagesUtils.createResponseSuccessListener(callback),
                    MessagesUtils.createReqErrorListener(callback, activity)
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {return authHeaders();}
            };
            queue.add(request);

        } catch (Exception e){
            Utils.dismissProgressDialog(pd);
            e.printStackTrace();
            //callback.onErrror(e.getMessage());
        }
    }



    /*
    get last message from conversation
     returm message
     */
//    public static Message getLastMessage(Conversation c){
//        Message m;
//        return m;
//    }


    /* get messages */
    public static List<Message> getMessages(int conversationId){
        List<Message> messages = null;
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Constants.CONVERSATIONS_API_URL + "/" + conversationId + "/messages", null, future, future){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {  return MessagesUtils.authHeaders(); }
        };
        RequestQueue queue = VolleySingleton.getRequestQueue();
        queue.add(request);

        try {
            Date d = new Date();
            Log.d(TAG, "getMessages: "+ Constants.DATE_JSON_FORMAT.format(d.getTime()) );
            JSONObject response = future.get(10, TimeUnit.SECONDS); // this will block (forever)
            Log.d(TAG, "getMessages: " + response.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Log.d(TAG, "getMessages: TIMEOUT");
            Date d = new Date();
            Log.d(TAG, "getMessages: "+ Constants.DATE_JSON_FORMAT.format( d.getTime()) );
            e.printStackTrace();
        }
        return messages;
    }


    /*
    get messages from coversation async
     */
    public static void getConversationMessages( List<Conversation> conversations, final MessagesCallback callback ){
        Activity activity = mCurrentActivity;
        Conversation conversation;
        for (int i = 0; i < conversations.size(); i++) {
            conversation = conversations.get(i);

            try {
                JSONObject jsonRequest = new JSONObject();
                RequestQueue queue = VolleySingleton.getRequestQueue();

                final Conversation finalConversation = conversation;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                        Constants.CONVERSATIONS_API_URL + "/" + conversation.getId() + "/messages",
                        jsonRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("--->", "authenticate:" + response.toString());
                                Utils.dismissProgressDialog(MessagesUtils.pd);
                                List<Message> messages = null;
                                Exception error = null;

                                if (response.has("messages")) {
                                    JSONArray jsonItems = null;
                                    try {
                                        jsonItems = response.getJSONArray("messages");
                                    } catch (JSONException e) {
                                        error = e;
                                        //e.printStackTrace();
                                    }
                                    if (jsonItems.length() > 0) {
                                        ResultMessages result;
                                        result = createMessageList(jsonItems);
                                        messages = result.getMessages();
                                        finalConversation.setMessages(messages);
                                        if (result.e != null) error = result.e;
                                        Log.d(TAG, "onResponse: ConversationsMessages size" + messages.size());
                                        callback.onFinished(messages, error);
                                    }
                                }
                            }

                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "getConversationMessages onErrorResponse: ");

                            }
                        }
                        //MessagesUtils.createReqErrorListener((ConversationsCallback) callback, activity)
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return MessagesUtils.authHeaders();
                    }
                };
                queue.add(request);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Map authHeaders(){
        Map headers = new HashMap();
        String token = AppSession.getCurrentUser().authToken;
        headers.put("Authorization", token);
        Log.d(TAG, "getHeaders: authToken " + token);
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }


    public interface ConversationsCallback {
        public void onFinished(List<Conversation> conversation, Exception e);
        public void onError(String errorMessage);
    }
    public interface MessagesCallback {
        public void onFinished(List<Message> messages, Exception e);
        public void onError(String errorMessage);
    }

    private static Response.Listener<JSONObject> createResponseSuccessListener(final ConversationsCallback callback){
        return new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.i("--->", "authenticate:" + response.toString());
                Utils.dismissProgressDialog(MessagesUtils.pd);
                List<Conversation> conversations = null;
                Exception error = null;

                if(response.has("conversations")){
                    JSONArray jsonItems = null;
                    try {
                        jsonItems = response.getJSONArray("conversations");
                    } catch (JSONException e) {
                        error = e;
                        //e.printStackTrace();
                    }
                    if (jsonItems.length() > 0) {
                        ResultConversations resultConversations;
                        resultConversations = createConversationList(jsonItems);

                        String lastMessage;

                        //para cada conversacion obtener el ultimo mensaje y modificarlo en resultConversation

                        //lastmessage title
//                        getConversationMessages(id, new MessagesCallback() {
//                            @Override
//                            public void onFinished(List<Message> messages, Exception e) {
//                                int last =  messages.size()-1;
//                                Message m = messages.get(last);
//                                lastMessage = m.getSubject().toString();
//                            }
//
//                            @Override
//                            public void onError(String errorMessage) {
//
//                            }
//                        });

                        conversations = resultConversations.getConversations();
                        if (resultConversations.e!=null) error = resultConversations.e;
                        Log.d(TAG, "onResponse: conversations size" + conversations.size());

                        //lastconversations.get(conversations.size()-1);

                        //get last message


                        callback.onFinished(conversations, error);
                    }
                }
            }
        };
    }
    private static Response.ErrorListener createReqErrorListener(ConversationsCallback callback, final Activity activity){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error");
                Utils.dismissProgressDialog(MessagesUtils.pd);
                String errorMessage = VolleyErrorHelper.getMessage(context, error);

                String errorDialogMsg = Utils.showErrorsJson(errorMessage, activity);

                Log.d(TAG, "onErrorResponse: error message:" + errorMessage);
            }
        };
    }

    private static class ResultConversations {
        public List<Conversation> mConversations;
        public Exception e;

        public ResultConversations(List<Conversation> c, Exception e) {
            mConversations = c;
            this.e = e;
        }
        public List<Conversation> getConversations() {
            return mConversations;
        }
    }
    private static class ResultMessages {
        public List<Message> mMessages;
        public Exception e;

        public ResultMessages(List<Message> c, Exception e) {
            mMessages = c;
            this.e = e;
        }
        public List<Message> getMessages() {
            return mMessages;
        }
    }

    private static ResultConversations createConversationList(JSONArray jsonItems){
        List<Conversation> conversations = new ArrayList<>();
        Exception e = null;
        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject jsonItem = null;
            try {
                jsonItem = jsonItems.getJSONObject(i);
            } catch (JSONException e1) {
                e = e1;
            }

            java.text.DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            Date dateCreatedAt = null;
            try {
                dateCreatedAt = format.parse(jsonItem.optString("created_at", "2000-01-01T00:00:00.000Z"));
            } catch (ParseException e1) {
                e = e1;
            }
            Date dateUpdateAt = null;
            try {
                dateUpdateAt = format.parse(jsonItem.optString("updated_at", "2000-01-01T00:00:00.000Z"));
            } catch (ParseException e1) {
                e = e1;
            }
            Log.d(TAG, "createConversationList: " + dateCreatedAt);
            Log.d(TAG, "createConversationList: " + dateUpdateAt);

            int id = jsonItem.optInt("id", 0);
            String subject = jsonItem.optString("subject", "");
            Date createdAt = dateCreatedAt;
            Date updatedAt = dateUpdateAt;
            int threadId = jsonItem.optInt("thread_id", 0);

            try {

                if ( Utils.isNotEmptyOrNull(subject) ) {
                    Conversation item = new Conversation(id,subject,createdAt,updatedAt,threadId);
                    conversations.add(item);
                }
            } catch ( Exception e1 ){
                e = e1;
            }
        }
        return new ResultConversations(conversations,e);
    }

    private static ResultMessages createMessageList(JSONArray jsonItems){
        List<Message> messages = new ArrayList<>();
        Exception e = null;
        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject jsonItem = null;
            try {
                jsonItem = jsonItems.getJSONObject(i);
            } catch (JSONException e1) {
                e = e1;
            }

            int id = jsonItem.optInt("id", 0);

            Date dateCreatedAt = null;
            try {
                dateCreatedAt = Constants.DATE_JSON_FORMAT.parse(jsonItem.optString("created_at", "2000-01-01T00:00:00.000Z"));
            } catch (ParseException e1) {
                e = e1;
            }
            Date dateUpdateAt = null;
            try {
                dateUpdateAt = Constants.DATE_JSON_FORMAT.parse(jsonItem.optString("updated_at", "2000-01-01T00:00:00.000Z"));
            } catch (ParseException e1) {
                e = e1;
            }
            Log.d(TAG, "createMessageList: " + id + " "+ dateCreatedAt);
            Log.d(TAG, "createMessageList: " + id + " "+ dateUpdateAt);

            String body = jsonItem.optString("body", "");
            String subject = jsonItem.optString("subject", "");
            Date createdAt = dateCreatedAt;
            Date updatedAt = dateUpdateAt;

            int sender_id = jsonItem.optInt("sender_id", 0);
            int sender_type = jsonItem.optInt("sender_type", 0);
            int conversation_id = jsonItem.optInt("conversation_id", 0);
            Boolean draft = jsonItem.optBoolean("draft", false);
            int notified_object_id = jsonItem.optInt("notified_object_id", 0);
            int notified_object_type = jsonItem.optInt("notified_object_type", 0);
            int notification_code = jsonItem.optInt("notification_code", 0);
            String attachment = jsonItem.optJSONObject("attachment").optString("url", "");
            Log.d(TAG, "createMessageList: attachment "+ attachment );
            Boolean global = jsonItem.optBoolean("global", false);

            //expires no se muy bien que hace
            String expires = jsonItem.optString("expires", "");

            try {
                if ( Utils.isNotEmptyOrNull(subject) ) {
                    Message item = new Message(
                            id,body,subject,
                            sender_id,sender_type,conversation_id,
                            draft,
                            updatedAt,createdAt,
                            notified_object_id,notified_object_type,notification_code,
                            attachment,global,expires);
                    messages.add(item);
                }
            } catch ( Exception e1 ){
                e = e1;
            }
        }
        return new ResultMessages(messages,e);

    }

    public static void reply(int conversationId, final String msg, final MessagesCallback callback){
        Log.d(TAG, "reply() called with: " + "conversationId = [" + conversationId + "], msg = [" + msg + "], callback = [" + callback + "]");

        JSONObject jsonRequest = new JSONObject();
        RequestQueue queue = VolleySingleton.getRequestQueue();
        try {
            jsonRequest.put("body",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                Constants.CONVERSATIONS_API_URL + "/" + conversationId + "/messages",
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("--->", "authenticate:" + response.toString());
                        //Utils.dismissProgressDialog(MessagesUtils.pd);
                        Exception error = null;

                        //Log.d(TAG, "onResponse: response" + response.toString());
                        List<Message> messages = new ArrayList<>();
                        boolean Mok = messages.add(new Message(0, msg, ((int) AppSession.getCurrentUser().id), new Date()));
                        Log.d(TAG, "onResponse: mok "+ Mok);

                        callback.onFinished(messages,error);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "getConversationMessages onErrorResponse: ");

                    }
                }
                //MessagesUtils.createReqErrorListener((ConversationsCallback) callback, activity)
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return MessagesUtils.authHeaders();
            }
        };
        queue.add(request);


//        POST a /api/mailboxes/invox/conversations/CONVERSATION_ID/messages
//        con json {"message": {"body": "cuerpo del mensaje 3"}}
//        curl -H "Content-Type: application/json"  -H "Authorization: 8qqRb_KFdp9W2-CNVFKU" -X POST -d '{"message": {"body": "cuerpo del mensaje 3"}}' http://localhost:3000/api/mailboxes/inbox/conversations/1/messages


    }


}
