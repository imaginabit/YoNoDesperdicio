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
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class UsersHelper {
    private static final String TAG = "UsersHelper";

    /**
     * Authenticate the user with passed credentials.
     */
    public static void authenticate(
            final Context context,
            final String userName,
            final String userPassword,
            final UserAccountCallback callback
    ) {
        // Show message
        final ProgressDialog pd = ProgressDialog.show(context, "", context.getString(R.string.user_auth_message));

        try {
            // Json request
            final JSONObject jsonRequest = new JSONObject()
                    .put("username", userName)
                    .put("password", userPassword);

            // Request queue
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    
                    Request.Method.POST,
                    Constants.USERS_SESSIONS_API_URL,
                    jsonRequest,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonResponse) {
                            Log.d(TAG, "onResponse() called with: " + "jsonResponse = [" + jsonResponse + "]");
                            Utils.dismissProgressDialog(pd);
                            Log.i("--->", "authenticate:" + jsonResponse.toString());
                            // Authenticated user
                            UserData user = extractUserData(jsonResponse);
                            user.username = userName;
                            user.password = userPassword;

                            callback.onFinished(user);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse() called with: " + "error = [" + error + "] jsonRequest" + jsonRequest.toString() );
                            Utils.dismissProgressDialog(pd);
                            callback.onError(extractErrorMessage(context, error));
                        }
                    }
            );
            // Add the queue
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Utils.dismissProgressDialog(pd);
            callback.onError(e.getMessage());
        }
    }

    /**
     * Create an user with the passed data
     */
    public static void create(
            final Context context,
            final String name,
            final String username,
            final String password,
            final String email,
            final String city,
            final String province,
            final String zipCode,
            final UserAccountCallback callback
    ) {
        // Show message
        final ProgressDialog pd = ProgressDialog.show(context, "", context.getString(R.string.user_create_message));

        try {
            // Json request
            JSONObject jsonUser = new JSONObject().put("name", name)
                    .put("username", username)
                    .put("password", password)
                    .put("email", email)
                    .put("city", city)
                    .put("province", province)
                    .put("zipcode", zipCode);

            JSONObject jsonRequest = new JSONObject().put("user", jsonUser);

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
                            Log.i("--->", "create:" + jsonResponse.toString());
                            // Created user
                            UserData user = extractUserData(jsonResponse);
                            user.fullname = name;
                            user.username = username;
                            user.password = password;
                            user.email = email;
                            user.city = city;
                            user.province = province;
                            user.zipCode = zipCode;

                            callback.onFinished(user);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.dismissProgressDialog(pd);
                            callback.onError(extractErrorMessage(context, error));
                        }
                    }
            );
            Log.i("--->", jsonRequest.toString());

            // Add the queue
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Utils.dismissProgressDialog(pd);
            callback.onError(e.getMessage());
        }
    }

    /**
     * Extract the error message from VolleyError encapsulation
     */
    private static String extractErrorMessage(Context context, VolleyError error) {
        // Default error message
        String errorMessage = VolleyErrorHelper.getMessage(context, error);
        String message = null;
        Log.d(TAG, "extractErrorMessage: errorMessage" + errorMessage );
        try {
            JSONObject jsonErrorMessage = new JSONObject(errorMessage);
            // {"errors":"Not authenticated"}
            try{
                message = jsonErrorMessage.getString("errors");
                if (Utils.isNotEmptyOrNull(message)) {
                    return message;
                }
                Log.d(TAG, "showErrorsJson: errorStr : "+ message);
            } catch (Exception e){
                e.printStackTrace();
            }
            if(message!= null) {
                // Multiple errors
                if (jsonErrorMessage.has("errors")) {
                    JSONObject jsonErrors = jsonErrorMessage.getJSONObject("errors");
                    JSONArray jsonErrorsList = jsonErrors.names();
                    for (int i = 0; i < jsonErrorsList.length(); i++) {
                        String propertyName = jsonErrorsList.getString(i);
                        JSONArray propertyValues = jsonErrors.getJSONArray(propertyName);
                        if (propertyValues != null && propertyValues.length() > 0) {
                            message = ((i > 0) ? "\n" : "") + propertyName + ": ";
                            for (int j = 0; j < propertyValues.length(); j++) {
                                message += ((j > 0) ? "\n" : "") + propertyValues.getString(j);
                            }
                        }
                    }
                    return message;
                }
                // Single error
                else if (jsonErrorMessage.has("error")) {
                    message = jsonErrorMessage.optString("error", "");
                    if (Utils.isNotEmptyOrNull(message)) {
                        return message;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Ignored
        }
        return errorMessage;
    }

    /**
     * Extract the user data from json response
     */
    private static UserData extractUserData(JSONObject jsonResponse) {
        // Default empty user
        UserData user = new UserData();

        try {
            JSONObject jsonUserData = null;

            // User account creation
            if (jsonResponse.has("user")) {
                // {"user":{"id":37,"created_at":"2015-12-15","username":"adesousab","rating":null,"total_quantity":0,"zipcode":"35001"}}
                jsonUserData = jsonResponse.getJSONObject("user");
                ;
            }
            // Authenticated user (Session)
            else if (jsonResponse.has("session_user")) {
                // {"session_user":{"id":37,"created_at":"2015-12-15","auth_token":"oPA_kwG1K_KsXVs-3RNU","username":"adesousab","rating":null,"email":"antoniodesousabarroso@gmail.com","total_quantity":0}}
                jsonUserData = jsonResponse.getJSONObject("session_user");
            }

            if (jsonUserData != null) {
                // Extract from response
                long id = jsonUserData.optLong("id", 0L);
                String created = jsonUserData.optString("created_at", "");
                String authToken = jsonUserData.optString("auth_token", "");
                String username = jsonUserData.optString("username", "");
                String email = jsonUserData.optString("email", "");
                String zipCode = jsonUserData.optString("zipcode", "");
                float rating = (float) jsonUserData.optDouble("rating", 0.0d);
                int totalQuantity = jsonUserData.optInt("total_quantity", 0);

                // Populate
                user.id = id;
                user.created = created;
                user.authToken = authToken;
                user.username = username;
                user.email = email;
                user.zipCode = zipCode;
                user.rating = rating;
                user.totalQuantity = totalQuantity;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    // Callbacks

    public interface UserAccountCallback {
        public void onFinished(UserData user);

        public void onError(String errorMessage);
    }
}
