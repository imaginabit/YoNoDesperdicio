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
    first get inbox conversation,
    them sent conversations ,
    them all conversation messages??
     */
    public static void getConversations(final Context context, final ConversationsCallback callback, final Activity activity){

        Log.d(TAG, "getConversations() called with: " + "context = [" + context.getPackageName() + "], activity = [" + activity.getLocalClassName() + "]");
        final List<Conversation> conversationsFinal;
        getConversationsInbox(context, new ConversationsCallback() {
           @Override
           public void onFinished(List<Conversation> conversation, Exception e) { }

           @Override
           public void onFinished(final List<Conversation> conversations, Exception e, ProgressDialog pd) {
               if (conversations != null) {

                   //we can use data or just conversations anyway this is just called after pass all the conversations
                   getConversationsSent(context, new ConversationsCallback() {
                       @Override
                       public void onFinished(List<Conversation> conversationsSent, Exception e) {
                           Log.d(TAG, "getConversationsSent conversationCallback onFinished() called with: " + "conversations = [" + conversations.size() + ", " + conversationsSent.size() + "], e = [" + e + "]");
                           //add conversations from sent to conversations
                           conversations.addAll(conversationsSent);

                           //getConversationMessagesFull(conversations,callback);
                           if (conversations.size() > 0) {
                               callback.onFinished(conversations, null, MessagesUtils.pd);
                           }
                       }

                       @Override
                       public void onFinished(List<Conversation> conversationsSent, Exception e, ProgressDialog pd) {
                           //normalmente llama a este
                           Log.d(TAG, "getConversationsSent onFinished() called with: " + "conversations = [" + conversations + "], e = [" + e + "], pd = [" + pd + "]");
                           conversations.addAll(conversationsSent);
                           if (conversations.size() > 0) {
                               callback.onFinished(conversations, null, MessagesUtils.pd);
                           }
                       }

                       @Override
                       public void onError(String errorMessage) {
                           Log.d(TAG, "getConversationsSent conversationCallback.onError() called with: " + "errorMessage = [" + errorMessage + "]");
                           //ahora siempre devuelve error por que esta fallando la api
                           // si fala al obtener conversaciones de sent tiene que obtener de inbox de todas formas
                          if (conversations.size() > 0) {
                           callback.onFinished(conversations, null, MessagesUtils.pd);
                          }
                       }
                   }, activity);


               }
           }

           @Override
           public void onError(String errorMessage) {

           }
        }, activity);
    }

    public static void getConversationsSent(final Context c,final ConversationsCallback cb, Activity a){
        Log.d(TAG, "--> getConversationsSent() called with: " + "c = [" + c.getPackageName() + "], cb = [" + cb.getClass().getSimpleName() + "], a = [" + a.getClass().getSimpleName() + "]");
        getConversationsBase(Constants.CONVERSATIONS_SENT_API_URL, c, cb, a);
    }

    public static void getConversationsInbox(final Context c,final ConversationsCallback cb, Activity a){
        Log.d(TAG, "<-- getConversationsInbox() called with: " + "c = [" + c.getPackageName() + "], cb = [" + cb.getClass().getSimpleName() + "], a = [" + a.getClass().getSimpleName() + "]");
        getConversationsBase(Constants.CONVERSATIONS_API_URL, c, cb, a);
    }

    public static void getConversationsBase(String url, final Context context,final ConversationsCallback callback , Activity activity){
        Log.d(TAG, "getConversationsBase() called with: " + "url = [" + url + "], context = [" + context.getPackageName() + "], callback = [" + callback.getClass().getSimpleName() + "], activity = [" + activity.getClass().getSimpleName() + "]");
        MessagesUtils.context = context;
        // Show Loading dialog
        MessagesUtils.pd = ProgressDialog.show(context, "", context.getString(R.string.loading));
        mCurrentActivity = activity;

        try{
            JSONObject jsonRequest = new JSONObject();
//           Mailbox_id puede ser: “inbox”, “sent” o “trash”

            RequestQueue queue = VolleySingleton.getRequestQueue();

            //get Menssages in "inbox"
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    url,
                    jsonRequest,
                    MessagesUtils.createResponseSuccessListener(callback),
                    MessagesUtils.createReqErrorListener(callback, activity)
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {return AppSession.authHeaders();}
            };
            queue.add(request);

        } catch (Exception e){
            Utils.dismissProgressDialog(pd);
            e.printStackTrace();
            //callback.onErrror(e.getMessage());
        }

    }


    /* get messages */
    public static List<Message> getMessages(int conversationId){
        List<Message> messages = null;
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Constants.CONVERSATIONS_API_URL + "/" + conversationId + "/messages", null, future, future){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {  return AppSession.authHeaders(); }
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


    /** Get all the messages <<THIS MAKE A API CALL FOR EVERY CONVERSATION>>
     * @param conversations
     * @param callback
     */
    /*
    get messages from coversation async
     */
    public static void getConversationMessages( final List<Conversation> conversations, final MessagesCallback callback ){
        Activity activity = mCurrentActivity;
        Conversation conversation;
        final ArrayList<Conversation> allConversations = new ArrayList<>();
        for (int i = 0; i < conversations.size(); i++) {

            conversation = conversations.get(i);

            try {
                JSONObject jsonRequest = new JSONObject();
                RequestQueue queue = VolleySingleton.getRequestQueue();

                final Conversation finalConversation = conversation;
                final int finalI = i;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                        Constants.CONVERSATIONS_API_URL + "/" + conversation.getId() + "/messages",
                        jsonRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("--->", "authenticate:" + response.toString());
                                try {
                                    Utils.dismissProgressDialog(MessagesUtils.pd);
                                }catch (Exception e ){
                                    e.printStackTrace();
                                }
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
                                        //add to all conversations
                                        allConversations.add(finalConversation);
                                        //can u call this for every for iteration?
                                        callback.onFinished(messages, error);
                                        //
                                        if (finalI == conversations.size()-1 ){
                                            callback.onFinished(messages,error,allConversations);

                                        }
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
                        return AppSession.authHeaders();
                    }
                };
                queue.add(request);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public interface ConversationsCallback {
        public void onFinished(List<Conversation> conversations, Exception e);
        public void onFinished(List<Conversation> conversations, Exception e,ProgressDialog pd);
        public void onError(String errorMessage);
    }


    /**
     * Basic callback just onFinished
     */
    public interface MessagesCallBackBase{
        public void onFinished(List<Message> messages, Exception e);
        public void onError(String errorMessage);
    }

    /**
     * Basic callback plus onError
     */
    public interface MessagesCallback extends MessagesCallBackBase{
        public void onFinished(List<Message> messages, Exception e, ArrayList data);
    }

    /**
     *  Basic callback plus onError and onfinished with extra argument
     */
//    public interface MessagesCallbackData extends MessagesCallback{
//        public void onFinished(List<Message> messages, Exception e, ArrayList data);
//    }

    private static Response.Listener<JSONObject> createResponseSuccessListener(final ConversationsCallback callback){
        return new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.d("--->", "authenticate:" + response.toString());

                try {
                    Utils.dismissProgressDialog(MessagesUtils.pd);
                }catch (Exception e ){
                    e.printStackTrace();
                }
                List<Conversation> conversations = null;
                Exception error = null;

                if(response.has("conversations")){
                    JSONArray jsonItems = null;
                    try {
                        jsonItems = response.getJSONArray("conversations");
                    } catch (JSONException e) {
                        error = e;
                    }

                    if (jsonItems.length() > 0) {
                        ResultConversations resultConversations;
                        resultConversations = createConversationList(jsonItems);

                        conversations = resultConversations.getConversations();
                        if (resultConversations.e!=null) error = resultConversations.e;
                        Log.d(TAG, "onResponse: conversations size " + conversations.size());

                        callback.onFinished(conversations, error, pd);
                    }
                }
            }
        };
    }
    private static Response.ErrorListener createReqErrorListener(final ConversationsCallback callback, final Activity activity){
        Log.d(TAG, "createReqErrorListener() called with: " + "callback = [" + callback + "], activity = [" + activity + "]");
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "createReqErrorListener onErrorResponse() called with: " + "error = [" + error + "]");

                try {
                    if(MessagesUtils.pd!=null) {
                        Utils.dismissProgressDialog(MessagesUtils.pd);
                    }
                }catch (Exception e ){
                    e.printStackTrace();
                }

                String errorMessage = VolleyErrorHelper.getMessage(context, error);

                //String errorDialogMsg = Utils.showErrorsJson(errorMessage, activity);
                Log.d(TAG, "onErrorResponse: error message:" + errorMessage);
                callback.onError(errorMessage);

            }
        };
    }

    /**
     * Just for no duplicate this in error and finish in getConversations
     * @param conversations
     * @param callback
     */
    private static void getConversationMessagesFull(final List<Conversation> conversations, final ConversationsCallback callback){
        Log.v(TAG, "getConversationMessagesFull() called with: " + "conversations = [" + conversations + "], callback = [" + callback + "]");
        getConversationMessages(conversations, new MessagesCallback() {
            @Override
            public void onFinished(List<Message> messages, Exception e) {
                //eeer this just change conversation inside because conversations is Final
                Log.d(TAG, "getConversationsSent->getConversationMessages->onFinished()");
                //Log.d(TAG, "onFinished:  called with: messages = [" + messages + "], e = [\" + e + \"]");
                //after all the three petition we sent the final callback!
                callback.onFinished(conversations, null, pd);
            }

            @Override
            public void onFinished(List<Message> messages, Exception e, ArrayList data) {
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
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
        Exception error = null;
        for (int i = 0; i < jsonItems.length(); i++) {
            //
            JSONObject jsonItem = null;
            try {
                jsonItem = jsonItems.getJSONObject(i);
            } catch (JSONException e) {
                error = e;
            }
            try {
                Message msg = createMessage(jsonItem);
                messages.add(msg);
            } catch (Exception e) {
                error = e;
                //e1.printStackTrace();
            }
        }
        return new ResultMessages(messages,error);
    }

    /**
     * Create a message object from JSOn returned by api call
     * @param item JSONObject returned by api
     * @return Message
     * @throws Exception any exeption to capture it by callback
     */
    private static Message createMessage(JSONObject item) throws Exception{
        int id = item.optInt("id", 0);

        Date dateCreatedAt = null;
        dateCreatedAt = Constants.DATE_JSON_FORMAT.parse(item.optString("created_at", "2000-01-01T00:00:00.000Z"));

        Date dateUpdateAt = null;
        dateUpdateAt = Constants.DATE_JSON_FORMAT.parse(item.optString("updated_at", "2000-01-01T00:00:00.000Z"));
        Log.d(TAG, "createMessageList: " + id + " " + dateCreatedAt);
        Log.d(TAG, "createMessageList: " + id + " " + dateUpdateAt);

        String body = item.optString("body", "");
        String subject = item.optString("subject", "");
        Date createdAt = dateCreatedAt;
        Date updatedAt = dateUpdateAt;

        int sender_id = item.optInt("sender_id", 0);
        int sender_type = item.optInt("sender_type", 0);
        int conversation_id = item.optInt("conversation_id", 0);
        Boolean draft = item.optBoolean("draft", false);
        int notified_object_id = item.optInt("notified_object_id", 0);
        int notified_object_type = item.optInt("notified_object_type", 0);
        int notification_code = item.optInt("notification_code", 0);
        String attachment = item.optJSONObject("attachment").optString("url", "");
        Log.d(TAG, "createMessageList: attachment " + attachment);
        Boolean global = item.optBoolean("global", false);

        //expires no se muy bien que hace
        String expires = item.optString("expires", "");

        if (Utils.isNotEmptyOrNull(subject)) {
            Message message = new Message(
                    id, body, subject,
                    sender_id, sender_type, conversation_id,
                    draft,
                    updatedAt, createdAt,
                    notified_object_id, notified_object_type, notification_code,
                    attachment, global, expires);
            return message;
        }
        return null;
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
                        try {
                                Utils.dismissProgressDialog(MessagesUtils.pd);
                        }catch (Exception e ){
                            e.printStackTrace();
                        }
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
                return AppSession.authHeaders();
            }
        };
        queue.add(request);


//        POST a /api/mailboxes/invox/conversations/CONVERSATION_ID/messages
//        con json {"message": {"body": "cuerpo del mensaje 3"}}
//        curl -H "Content-Type: application/json"  -H "Authorization: 8qqRb_KFdp9W2-CNVFKU" -X POST -d '{"message": {"body": "cuerpo del mensaje 3"}}' http://localhost:3000/api/mailboxes/inbox/conversations/1/messages

    }

    /**
     * Create a new conversation
     *
     * Nuevo mensaje:
        POST a /api/new_message/RECIPIENT_ID
            con json {"message": {"subject": "test2", "body": "cuerpo del mensaje 3"}}
            curl -H "Content-Type: application/json"  -H "Authorization: 8qqRb_KFdp9W2-CNVFKU" -X POST -d '{"message": {"subject": "test2", "body": "cuerpo del mensaje 3"}}' http://localhost:3000/api/new_message/2
     *
     *  @param title Conversation title or subject
     * @param sendTo user
     * @param callback callback with message created response
     */
    public static void createConversation(final String title, int sendTo, final MessagesCallback callback){
        Log.d(TAG, "createConversation() called with: " + "title = [" + title + "], sendTo = [" + sendTo + "], callback = [" + callback + "]");

        final String msg = "Nuevo mensaje";

        JSONObject jsonRequest = new JSONObject();
        RequestQueue queue = VolleySingleton.getRequestQueue();
        try {
            jsonRequest.put("subject",title);
            jsonRequest.put("body",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                Constants.NEW_CONVERSATION_API_URL + sendTo ,
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("--->", "authenticate:" + response.toString());
                        try {
                            Utils.dismissProgressDialog(MessagesUtils.pd);
                        }catch (Exception e ){
                            e.printStackTrace();
                        }
                        Exception error = null;
                        ArrayList data = new ArrayList();
                        List<Message> messages = new ArrayList<>();

                        //get conversation id
                        try {
                            int conversationId;
                            String strConversationId = response.getString("conversation_id");
                            if (Utils.isNotEmptyOrNull(strConversationId) && "null" != strConversationId) {
                                Log.d(TAG, "onResponse: conversation id " + strConversationId);
                                conversationId = Integer.parseInt(strConversationId);
                            } else {
                                conversationId = 0;
                            }
                            Conversation c = new Conversation(conversationId,title);
                            data.add(c);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            Message message = createMessage(response);
                            messages.add(message);
                        } catch (Exception e) {
                            error = e;
                            //e.printStackTrace();
                        }
                        //boolean Mok = messages.add(new Message(0, msg, ((int) AppSession.getCurrentUser().id), new Date()));
                        //Log.d(TAG, "onResponse: mok "+ Mok);

                        callback.onFinished(messages,error, data);
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
                return AppSession.authHeaders();
            }
        };
        queue.add(request);


    }


}
