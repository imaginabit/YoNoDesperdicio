package com.imaginabit.yonodesperdicion.helpers;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.imaginabit.yonodesperdicion.R;

/**
 * Helper for VolleyException encapsulation
 */
public class VolleyErrorHelper {
    /**
     * Returns appropriate message which is to be displayed to the user
     * against the specified error object.
     */
    public static String getMessage(Context context, VolleyError error){
        if (error instanceof TimeoutError) {
            return context.getResources().getString(R.string.volley_error_timeout);
        }
        else if (isServerError(error)) {
            return handleServerError(context, error);
        }
        else if(isNetworkProblem(error)) {
            return context.getResources().getString(R.string.volley_error_no_internet);
        }
        else {
            return context.getResources().getString(R.string.volley_error_generic);
        }
    }

    /**
     * Handle the Server Error
     */
    private static String handleServerError(Context context, VolleyError error) {
        NetworkResponse response = error.networkResponse;

        if (response != null) {
            switch (response.statusCode){
                case 404:
                case 422:
                case 401:
                    try {
                        return new String(response.data);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    // invalid request
                    return error.getMessage();

                default:
                    return context.getResources().getString(R.string.volley_error_timeout);
            }
        }

        return context.getResources().getString(R.string.volley_error_generic);
    }

    private static boolean isServerError(VolleyError error) {
        return (error instanceof ServerError || error instanceof AuthFailureError);
    }

    private static boolean isNetworkProblem(VolleyError error){
        return (error instanceof NetworkError || error instanceof NoConnectionError);
    }
}
