package com.imaginabit.yonodesperdicion.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Created by fer2015julio on 29/11/15.
 */
public class UserUtils {
    private static final String TAG = "UserUtils";
    private static Context context;
    private static ProgressDialog pd;
    private static Activity mCurrentActivity;
    private static User mUser;


    public static void fetchUser(final int userId, final FetchUserCallback callback ){
        String TAG = UserUtils.TAG + " fetch User";

        AsyncTask<Void, Void, Void> fetchUsersTask = new AsyncTask<Void, Void, Void>() {
            private String TAG = "fetchUsersTask";

            private JSONObject jObj = null;
            private User mUser = null;
            private Exception e = null;
            int user = userId;

            @Override
            protected Void doInBackground(Void... params) {
                Log.d(TAG, "doInBackground() called with: " + "params = [" + params + "]");
                String json = null;
                try {
                    json = Utils.downloadJsonUrl(Constants.USERS_API_URL + Integer.toString(userId));
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


    public static void getUser(final int userId, final Context context, final FetchUserCallback callback){

        // Show Loading dialog
        //UserUtils.pd = ProgressDialog.show(context, "", context.getString(R.string.loading));

        try{
            JSONObject jsonRequest = new JSONObject();

//           Mailbox_id puede ser: “inbox”, “sent” o “trash”

            RequestQueue queue = VolleySingleton.getRequestQueue();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    Constants.USERS_API_URL +"/" + Integer.toString(userId),
                    jsonRequest,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("--->", "authenticate:" + response.toString());
                            //Utils.dismissProgressDialog(UserUtils.pd);

                            mUser = null;
                            Exception error = null;

//                            {
//                                "user": {
//                                "id": 44,
//                                        "username": "fwrnsoan",
//                                        "zipcode": "35012",
//                                        "total_quantity": 0,
//                                        "rating": null,
//                                        "created_at": "2015-12-17"
//                            }
//                            }

                            if (response.has("user")) {
                                Log.d(TAG, "onResponse: has user ");
                                JSONObject jsonItems = null;
                                try {
                                    //Utils.dismissProgressDialog(UserUtils.pd);
                                    jsonItems = response.getJSONObject("user");

                                    String username = jsonItems.getString("username");
                                    String zip = jsonItems.getString("zipcode");
                                    mUser = new User(userId,username,username,"",zip,0,0);
                                    callback.done(mUser, null);

                                } catch (JSONException e) {
                                    error = e;
                                    callback.done(mUser, error);
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: ERROR");
                            //Utils.dismissProgressDialog(UserUtils.pd);
                        }
                    }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {return AppSession.authHeaders();}
            };
            queue.add(request);

        } catch (Exception e){
//            Utils.dismissProgressDialog(pd);
            e.printStackTrace();
            //callback.onErrror(e.getMessage());
        }
    }



}
