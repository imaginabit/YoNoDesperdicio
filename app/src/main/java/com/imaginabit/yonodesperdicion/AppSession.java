package com.imaginabit.yonodesperdicion;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.imaginabit.yonodesperdicion.activities.MainActivity;
import com.imaginabit.yonodesperdicion.data.AdsDatabase;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.gcm.MyFirebaseInstanceService;
import com.imaginabit.yonodesperdicion.helpers.UsersHelper;
import com.imaginabit.yonodesperdicion.helpers.VolleyErrorHelper;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.utils.MessagesUtils;
import com.imaginabit.yonodesperdicion.utils.UiUtils;
import com.imaginabit.yonodesperdicion.utils.UserUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * AppSession Control and store session Status
 */
public class AppSession {

    private static final String TAG = "AppSession";
    private static UserData user;

    public static Conversation currentConversation;
    public static User currentOtherUser;
    //public static Ad currentAd;
    public static Location lastLocation;
    public static RequestQueue requestQueue;
    //public List<Ad> userAds;

    // User
    public static void setCurrentUser(UserData user) {
        AppSession.user = user;
        UserUtils.saveUserAvatar(user.avatar);
    }

    public static UserData getCurrentUser() {
        return AppSession.user;
    }

    // Release session data
    public static synchronized void release() {
        UserUtils.deleteUserAvatar();
        AppSession.user = null;
        AppSession.currentConversation = null;
        AppSession.currentOtherUser = null;
        //AppSession.currentAd = null;
        AppSession.lastLocation = null;
        AppSession.lastLocation = null;
        //AppSession.userAds = null;
    }

    public static void logoff(Activity activity){
        if (AppSession.getCurrentUser()!=null) {
            AppSession.user.prefsRemove(activity);
            AdsDatabase.deleteDatabase(activity);
            release();
        }
    }


    /**
     *  AuthHeaders
     * @return Map with headers for auth with volley requests
     */
    public static Map authHeaders(){
        Map headers = new HashMap();
        if (getCurrentUser()!=null) {
            String token = getCurrentUser().authToken;
            headers.put("Authorization", token);
            Log.d(TAG, "getHeaders: authToken " + token);
            headers.put("Content-Type", "application/json; charset=utf-8");
        }  else {
            MessagesUtils.mCurrentActivity.finish();
            Utils.checkLoginAndRedirect(MessagesUtils.mCurrentActivity);
        }
        return headers;
    }

    /**
     * Restart the app
     */
    public static void restart(Activity activity) {
        //final ProgressDialog pd = ProgressDialog.show(this, "a", "b");

        // Restart Intent
        Intent restartIntent = new Intent(activity, MainActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                activity,
                0,
                restartIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );


        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Release session
        AppSession.release();

        // App is not running
        App.setIsAppRunning(false);


        // i dont get why wait for this
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);

        activity.startActivity(restartIntent);

        activity.finish();
    }

    public static void checkAuthCredentials(final Activity activity){

        RequestQueue queue = VolleySingleton.getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                Constants.USERS_API_URL + "/" + AppSession.getCurrentUser().id,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //do nothing
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "onErrorResponse() called with: " + "error = [" + error + "]");
                        String errorMessage = VolleyErrorHelper.getMessage(App.appContext, error);
                        Log.d(TAG, "onErrorResponse: error message:" + errorMessage);
                        if (error.networkResponse != null && error.networkResponse.statusCode==401){
                            relogin(activity);
                        } else {
                            String errorDialogMsg = Utils.showErrorsJson(errorMessage, activity);
                        }
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppSession.authHeaders();
            }
        };
        queue.add(request);
    }

    public static void relogin(final Activity activity){
        //Request new login
        UserData user = AppSession.getCurrentUser();
        String userName = AppSession.getCurrentUser().username;
        String userPassword = AppSession.getCurrentUser().password;

        UsersHelper.authenticate(
                activity,
                userName,
                userPassword,
                new UsersHelper.UserAccountCallback() {
                    @Override
                    public void onFinished(UserData user) {
                        if (user == null || user.id == 0L) {
                            Log.d(TAG, "onFinished: User null!");
                            // @TODO error
                        } else {
                            user.prefsRemove(activity);
                            user.prefsCommit(activity);
                            AppSession.restart(activity);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        UiUtils.showMessage(activity, "Entra en tu cuenta", errorMessage);
                    }
                }
        );
    }

    public static boolean isSessionOn(){
        return user != null;
    }
}
