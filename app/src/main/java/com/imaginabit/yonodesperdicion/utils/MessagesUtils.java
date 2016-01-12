package com.imaginabit.yonodesperdicion.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.util.HashMap;
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

    public static void getMessages(final Context context,final MessagesCallback callback){
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
                    MessagesUtils.createResponseSuccessListener(), MessagesUtils.createReqErrorListener() ){
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
        public void onFinished();

        public void onError(String errorMessage);
    }

    private static Response.Listener<JSONObject> createResponseSuccessListener(){
        return new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.i("--->", "authenticate:" + response.toString());
                Utils.dismissProgressDialog(MessagesUtils.pd);
            }
        };
    }
    private static Response.ErrorListener createReqErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error");
                Utils.dismissProgressDialog(MessagesUtils.pd);
                String errorMessage = VolleyErrorHelper.getMessage(context, error);
                Log.d(TAG, "onErrorResponse: error message:" + errorMessage);
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        };
    }

}
