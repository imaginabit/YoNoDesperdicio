package com.imaginabit.yonodesperdicion.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.imaginabit.yonodesperdicion.models.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by fer2015julio on 29/11/15.
 */
public class UserUtils {
    private static final String TAG = "UserUtils";

    public static void fetchUser(final int userId, final FetchUserCallback callback ){
        String TAG = UserUtils.TAG + " fetch User";

        AsyncTask<Void, Void, Void> fetchUsersTask = new AsyncTask<Void, Void, Void>() {
            private JSONObject jObj = null;
            private String TAG = "IdeaUtils DownloadIdeasTask";
            private User mUser = null;
            private Exception e = null;
            int user = userId;

            @Override
            protected Void doInBackground(Void... params) {
                String json = null;
                try {
                    json = Utils.downloadJsonUrl( Constants.USERS_API_URL + Integer.toString(userId) );
                } catch (IOException e) {
                    this.e = e;
                }
                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);
                } catch (JSONException e) {
                    Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
                } catch (Throwable t) {
                    Log.e(TAG, "Could not parse malformed JSON: \"" + json + "\"");
                }

                if (jObj.has("session_user")) {
                    JSONObject user = null;
                    try {
                        user = jObj.getJSONObject("session_user");

                        String username = user.optString("username");
                        String email = user.optString("email");
                        Double rating = user.optDouble("rating");
                        int total_quanty = user.optInt("total_quantity");
                        user.optString("created_at");

                        mUser = new User(userId,username,total_quanty,rating);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }

                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                if (e == null) {
                    callback.done(mUser, null);
                } else {
                    callback.done(null, e);
                }
            }
        };
        TasksUtils.execute(fetchUsersTask);
    }

    public interface FetchUserCallback {
        public void done(User user, Exception e);

    }


}
