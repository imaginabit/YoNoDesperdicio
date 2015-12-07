package com.imaginabit.yonodesperdicion.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.utils.Constants;
import com.imaginabit.yonodesperdicion.utils.Utils;

import org.json.JSONObject;

/**
 *
 */
public class UsersHelper {

    /**
     * Authenticate the user with passed credentials.
     */
    public static void authenticate(Context context, String userName, String userPassword) {
        // Show message
        final ProgressDialog pd = ProgressDialog.show(context, "", context.getString(R.string.user_auth_message));

        try {
            // Json request
            JSONObject jsonRequest = new JSONObject().put("username", userName)
                                                     .put("password", userPassword);

            // Request queue
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                                                            Request.Method.POST,
                                                                            Constants.USERS_API_URL,
                                                                            jsonRequest,
                                                                            new Response.Listener<JSONObject>() {
                                                                                @Override
                                                                                public void onResponse(JSONObject jsonResponse) {
                                                                                    Utils.dismissProgressDialog(pd);
                                                                                    Log.i("--->", jsonResponse.toString());
                                                                                }
                                                                            },
                                                                            new Response.ErrorListener() {
                                                                                @Override
                                                                                public void onErrorResponse(VolleyError error) {
                                                                                    Utils.dismissProgressDialog(pd);
                                                                                    Log.i("--->", (error == null || error.getMessage() == null) ? "NULL" : error.getMessage());
                                                                                }
                                                                            }
                                                                      );
            // Add the queue
            requestQueue.add(jsonObjectRequest);
        }
        catch (Exception e) {
            Utils.dismissProgressDialog(pd);
        }
    }
}
