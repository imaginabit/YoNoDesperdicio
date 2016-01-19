package com.imaginabit.yonodesperdicion;

import android.util.Log;

import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.utils.MessagesUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * AppSession Control and store session Status
 */
public class AppSession {
    private static final String TAG = "AppSession";
    private static UserData user;

    // User

    public static void setCurrentUser(UserData user) {
        AppSession.user = user;
    }

    public static UserData getCurrentUser() {
        return AppSession.user;
    }

    // Release session data
    public static synchronized void release() {
        AppSession.user = null;
    }

    public static Conversation currentConversation;


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
}
