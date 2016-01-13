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
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.helpers.VolleyErrorHelper;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Conversation;

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

/**
 * Created by Fernando Ramírez on 12/01/16.
 */
public class MessagesUtils {
    private static final String TAG = "MessagesUtils";
    private static Context context;
    private static ProgressDialog pd;


    //Users Credentials needed
    //

    public static void getMessages(final Context context,final MessagesCallback callback,Activity activity){
        MessagesUtils.context = context;
        // Show message
        MessagesUtils.pd = ProgressDialog.show(context, "", context.getString(R.string.loading));

        try{
            JSONObject jsonRequest = new JSONObject();

//            Ver conversaciones de un mailbox (estando logueado):
//            /api/mailboxes/MAILBOX_ID/conversations
//            curl -H "Content-Type: application/json"  -H "Authorization: 8qqRb_KFdp9W2-CNVFKU" http://localhost:3000/api/mailboxes/inbox/conversations
//            Mailbox_id puede ser: “inbox”, “sent” o “trash”

            RequestQueue queue = VolleySingleton.getRequestQueue();

//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                    Request.Method.GET,
//                    Constants.MESSAGES_API_URL,
//                    jsonRequest,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject jsonResponse) {
//                            Utils.dismissProgressDialog(pd);
//                            Log.i("--->", "authenticate:" + jsonResponse.toString());
//                            // Authenticated user
////                            UserData user = extractUserData(jsonResponse);
////                            user.username = userName;
////                            user.password = userPassword;
//
////                            callback.onFinished(user);
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//
////                            callback.onError(extractErrorMessage(context, error));
//                        }
//                    }
//            );
//            // Add the queue
//            requestQueue.add(jsonObjectRequest);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    Constants.MESSAGES_API_URL,
                    jsonRequest,
                    MessagesUtils.createResponseSuccessListener(callback), MessagesUtils .createReqErrorListener(callback,activity) ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map headers = new HashMap();
                    String token = AppSession.getCurrentUser().authToken;
                    headers.put("Authorization", token);
                    Log.d(TAG, "getHeaders: authToken " + token);
                    headers.put("Content-Type", "application/json; charset=utf-8");

                    return headers;
                }
//
//                @Override
//                public String getBodyContentType() {
//                    //return super.getBodyContentType();
//                    return "application/json; charset=utf-8";
//                }
//
//                @Override
//                public byte[] getBody()
//                {
//                    String body = "some text";
//                    try
//                    {
//                        return body.getBytes(getParamsEncoding());
//                    }
//                    catch (UnsupportedEncodingException uee)
//                    {
//                        throw new RuntimeException("Encoding not supported: "
//                                + getParamsEncoding(), uee);
//                    }
//                }

            };
            queue.add(request);

        } catch (Exception e){
            Utils.dismissProgressDialog(pd);
            e.printStackTrace();
            //callback.onErrror(e.getMessage());
        }


    }


    public interface MessagesCallback {
        public void onFinished(List<Conversation> conversation, Exception e);

        public void onError(String errorMessage);
    }

    private static Response.Listener<JSONObject> createResponseSuccessListener(final MessagesCallback callback){
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
                        conversations = resultConversations.getConversations();
                        if (resultConversations.e!=null) error = resultConversations.e;
                        Log.d(TAG, "onResponse: conversations size" + conversations.size());

                        callback.onFinished(conversations,error);
                    }
                }
            }
        };
    }
    private static Response.ErrorListener createReqErrorListener(MessagesCallback callback, final Activity activity){
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
            //"id":11,
            // "subject":"prueba",
            // "created_at":"2015-12-29T14:07:27.000Z",
            // "updated_at":"2015-12-29T14:07:27.000Z",
                             //2001-07-04T12:08:56.235-0700
            // "thread_id":0}]}
            //2001-07-04T12:08:56.235-0700            yyyy-MM-dd'T'HH:mm:ss.SSSZ

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


}
