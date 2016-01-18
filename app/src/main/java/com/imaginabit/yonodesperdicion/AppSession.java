package com.imaginabit.yonodesperdicion;

import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.models.Conversation;

/**
 * @author Antonio de Sousa Barroso
 */
public class AppSession {
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


}
