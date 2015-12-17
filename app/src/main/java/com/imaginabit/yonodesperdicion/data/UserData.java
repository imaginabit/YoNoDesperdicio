package com.imaginabit.yonodesperdicion.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.imaginabit.yonodesperdicion.utils.PrefsUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;

/**
 * Represent the user data in memory
 */
public class UserData {
    // Prefs keys
    private static final String PREFS_KEY_ID = "user_id";
    private static final String PREFS_KEY_CREATED = "user_created";
    private static final String PREFS_KEY_AUTH_TOKEN = "user_auth_token";
    private static final String PREFS_KEY_FULLNAME = "user_fullname";
    private static final String PREFS_KEY_USERNAME = "user_username";
    private static final String PREFS_KEY_EMAIL = "user_email";
    private static final String PREFS_KEY_PASSWORD = "user_password";
    private static final String PREFS_KEY_CITY = "user_city";
    private static final String PREFS_KEY_PROVINCE = "user_province";
    private static final String PREFS_KEY_ZIP_CODE = "user_zip_code";
    private static final String PREFS_KEY_RATING = "user_rating";
    private static final String PREFS_KEY_TOTAL_QUANTITY = "user_total_quantity";

    public long id = 0L;
    public String created = "";
    public String authToken = "";
    public String fullname = "";
    public String username = "";
    public String email = "";
    public String password = "";
    public String city = "";
    public String province = "";
    public String zipCode = "";
    public float rating = 0.0f;
    public int totalQuantity = 0;

    // Constructors

    public UserData() {
        // Default
    }

    public UserData(
                       long id,
                       String created,
                       String authToken,
                       String fullname,
                       String username,
                       String email,
                       String password,
                       String city,
                       String province,
                       String zipCode,
                       float rating,
                       int totalQuantity
                   ) {
        this.id = id;
        this.created = created;
        this.authToken = authToken;
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.city = city;
        this.province = province;
        this.zipCode = zipCode;
        this.rating = rating;
        this.totalQuantity = totalQuantity;
    }

    /**
     * Commit the user to preferences
     */
    public boolean prefsCommit(Context context) {
        SharedPreferences.Editor ed = PrefsUtils.getSharedPreferencesEditor(context);

        ed.putLong(PREFS_KEY_ID, id);
        ed.putString(PREFS_KEY_CREATED, created);
        ed.putString(PREFS_KEY_AUTH_TOKEN, authToken);
        ed.putString(PREFS_KEY_FULLNAME, fullname);
        ed.putString(PREFS_KEY_USERNAME, username);
        ed.putString(PREFS_KEY_EMAIL, email);
        ed.putString(PREFS_KEY_PASSWORD, password);
        ed.putString(PREFS_KEY_CITY, city);
        ed.putString(PREFS_KEY_PROVINCE, province);
        ed.putString(PREFS_KEY_ZIP_CODE, zipCode);
        ed.putFloat(PREFS_KEY_RATING, rating);
        ed.putInt(PREFS_KEY_TOTAL_QUANTITY, totalQuantity);

        return ed.commit();
    }

    /**
     * Fetch from preferences
     */
    public static UserData prefsFetch(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        long id = sp.getLong(PREFS_KEY_ID, 0L);
        String created = sp.getString(PREFS_KEY_CREATED, "");
        String authToken = sp.getString(PREFS_KEY_AUTH_TOKEN, "");
        String username = sp.getString(PREFS_KEY_USERNAME, "");
        if (id == 0L || Utils.isEmptyOrNull(created) || Utils.isEmptyOrNull(authToken) || Utils.isEmptyOrNull(username)) {
            // If there is no token or username defined, the user is not valid
            return null;
        }

        String fullname = sp.getString(PREFS_KEY_FULLNAME, "");
        String email = sp.getString(PREFS_KEY_EMAIL, "");
        String password = sp.getString(PREFS_KEY_PASSWORD, "");
        String city = sp.getString(PREFS_KEY_CITY, "");
        String province = sp.getString(PREFS_KEY_PROVINCE, "");
        String zipCode = sp.getString(PREFS_KEY_ZIP_CODE, "");
        float rating = sp.getFloat(PREFS_KEY_RATING, 0.0f);
        int totalQuantity = sp.getInt(PREFS_KEY_TOTAL_QUANTITY, 0);

        UserData user = new UserData(
                                        id,
                                        created,
                                        authToken,
                                        fullname,
                                        username,
                                        email,
                                        password,
                                        city,
                                        province,
                                        zipCode,
                                        rating,
                                        totalQuantity
                                    );

        return user;
    }

    /**
     * Remove user from preferences
     */
    public boolean prefsRemove(Context context) {
        SharedPreferences.Editor ed = PrefsUtils.getSharedPreferencesEditor(context);

        ed.remove(PREFS_KEY_ID);
        ed.remove(PREFS_KEY_CREATED);
        ed.remove(PREFS_KEY_AUTH_TOKEN);
        ed.remove(PREFS_KEY_FULLNAME);
        ed.remove(PREFS_KEY_USERNAME);
        ed.remove(PREFS_KEY_EMAIL);
        ed.remove(PREFS_KEY_PASSWORD);
        ed.remove(PREFS_KEY_CITY);
        ed.remove(PREFS_KEY_PROVINCE);
        ed.remove(PREFS_KEY_ZIP_CODE);
        ed.remove(PREFS_KEY_RATING);
        ed.remove(PREFS_KEY_TOTAL_QUANTITY);

        return ed.commit();
    }
}
