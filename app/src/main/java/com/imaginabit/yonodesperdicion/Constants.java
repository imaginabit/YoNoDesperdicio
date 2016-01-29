package com.imaginabit.yonodesperdicion;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by fer2015julio on 20/11/15.
 */
public class Constants {

    // URLs
    public static final String HOME_URL = "http://beta.yonodesperdicio.org/";
    public static final String IDEAS_API_URL = HOME_URL + "api/ideas";
    public static final String ADS_API_URL = HOME_URL + "api/ads";


    public static final String USER_ADS_API_URL = ADS_API_URL + "?user_id="; // pe: + "10"
    //http://beta.yonodesperdicio.org/api/ads?user_id=5

    // Create users accounts endpoint
    public static final String USERS_API_URL = HOME_URL + "api/users";

    // Authenticate users endpoint
    public static final String USERS_SESSIONS_API_URL = HOME_URL + "api/sessions";

    public static final String CONVERSATIONS_API_URL = HOME_URL + "api/mailboxes/inbox/conversations";
    public static final String CONVERSATIONS_SENT_API_URL = HOME_URL + "api/mailboxes/sent/conversations";
    public static final String NEW_CONVERSATION_API_URL = HOME_URL + "api/new_message/"; // + RECIPIENT_ID

    public static final String IDEA_URL = HOME_URL + "idea/"; //+ idea id


    public static final String longline = "--------------------------------------------------------";

    public static final String WEIGHT_TOTAL_KG_URL = HOME_URL + "api/total_kg";

    //time costants
    public static int SECOND = 1000;
    public static int MINUTE = 60 * SECOND;
    public static int HOUR = 60 * MINUTE;


    public static Double weightTotal;

    public static java.text.DateFormat DATE_JSON_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

}
