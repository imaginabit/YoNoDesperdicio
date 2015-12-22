package com.imaginabit.yonodesperdicion.helpers;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.imaginabit.yonodesperdicion.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fernando Ramírez on 22/12/15.
 */
public class VolleySingleton {
    private static VolleySingleton mVolleyS = null;
    //Este objeto es la cola que usará la aplicación
    private static RequestQueue mRequestQueue;



    private VolleySingleton(Context context) {

    }

    public static void init(Context context){
        mRequestQueue = Volley.newRequestQueue(context);

        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
        //mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize));
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }


    public static VolleySingleton getInstance(Context context) {
        if (mVolleyS == null) {
            mVolleyS = new VolleySingleton(context);
        }
        return mVolleyS;
    }

    /**
     * Extract the error message from VolleyError encapsulation
     */
    public static String extractErrorMessage(Context context, VolleyError error) {
        // Default error message
        String errorMessage = VolleyErrorHelper.getMessage(context, error);
        try {
            JSONObject jsonErrorMessage = new JSONObject(errorMessage);
            // Multiple errors
            if (jsonErrorMessage.has("errors")) {
                String message = "";
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
                String message = jsonErrorMessage.optString("error", "");
                if (Utils.isNotEmptyOrNull(message)) {
                    return message;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Ignored
        }
        return errorMessage;
    }






}
