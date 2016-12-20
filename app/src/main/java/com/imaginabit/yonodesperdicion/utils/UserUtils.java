package com.imaginabit.yonodesperdicion.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imaginabit.yonodesperdicion.App;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.helpers.VolleyErrorHelper;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
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
                    Log.e(TAG + "JSONParse", "Error parsing data " + e.toString());
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
        Log.d(TAG, "getUser() called with: " + "userId = [" + userId + "], context = [" + context + "], callback = [" + callback + "]");
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
                            Log.i(TAG, "---> authenticate:" + response.toString());
                            //Utils.dismissProgressDialog(UserUtils.pd);
                            mUser = null;
                            Exception error = null;

//                            {"session_user": {
//                                "id": 43,
//                                        "username": "fernando",
//                                        "total_quantity": 0,
//                                        "rating": 0,
//                                        "image": "propias/avatar_original.png",
//                                        "zipcode": "35001",
//                                        "city": "Las Palmas De Gran Canaria",
//                                        "province": "Las Palmas",
//                                        "email": "imaginabit@gmail.com",
//                                        "auth_token": "ep8Toibgi-F9E5n5Eozf",
//                                        "created_at": "2015-12-17"

                            if (response.has("user") || response.has("session_user")) {
                                Log.d(TAG, "onResponse: has user ");
                                JSONObject jsonItems = null;
                                try {
                                    //Utils.dismissProgressDialog(UserUtils.pd);
                                    if (response.has("session_user")){
                                        jsonItems = response.getJSONObject("session_user");
                                    }else {
                                        jsonItems = response.getJSONObject("user");
                                    }

                                    String username = jsonItems.getString("username");
                                    String zip = jsonItems.getString("zipcode");
                                    String city = "";
                                    int total_quantity = jsonItems.getInt("total_quantity");

                                    float ratting;
                                    if (jsonItems.getString("rating")!="null"){
                                        Log.d(TAG, "onResponse: ratting : " + jsonItems.getDouble("rating") );
                                        ratting = (float)jsonItems.getDouble("rating");
                                    } else
                                        ratting = 0;

                                    String avatar = jsonItems.getString("image");

                                    if (response.has("session_user")){
                                        Log.d(TAG, "onResponse: yo mismo!");
                                        city = jsonItems.getString("city");
                                        String token = jsonItems.getString("auth_token");
                                        AppSession.getCurrentUser().authToken = token;
                                        mUser = new User(userId,username,username,city,zip,total_quantity,ratting, avatar);
                                    } else {
                                        Log.d(TAG, "onResponse: otro");
                                        mUser = new User(userId,username,username,city,zip,total_quantity,ratting,avatar);
                                    }
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
                            callback.done(null,new Exception(error));
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

    public static void saveUserAvatar(String url) {
        Log.d(TAG, "saveUserAvatar() called with: " + "url = [" + url + "]");

        if (Utils.isNotEmptyOrNull(url)) {

            //get image from website
            ImageLoader imageLoader; // Get singleton instance
            imageLoader = ImageLoader.getInstance();
            String imageUri = Constants.HOME_URL + url.replace("/original/", "/thumb/");

            imageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // Do whatever you want with Bitmap
                    String fname = "avatar.jpg";
                    File file = new File(App.appContext.getFilesDir(), fname);
                    if (file.exists()) file.delete();

                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                        if (loadedImage != null) {
                            Log.d(TAG, "saveAvatar: bitmap not null");
                            loadedImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();
                        } else {
                            Log.d(TAG, "saveAvatar: bitmap null");
                        }
                    } catch (Exception e) {
                        // FileNotFoundException
                        // IOExeption
                        e.printStackTrace();
                    }
                }
            });

        }
    }
    public static void deleteUserAvatar() {
        String fname = "avatar.jpg";
        File file = new File(App.appContext.getFilesDir(), fname);
        if (file.exists()) file.delete();
    }

    public static void sendTokenToServer(UserData mUser, String refreshedToken){
        Log.d(TAG, "sendTokenToServer: called");
        try {
            if ( mUser!= null ) {

                JSONObject jsonUser = new JSONObject();
                jsonUser.put("id", mUser.id);
                jsonUser.put("fcm_registration_token", refreshedToken);

                Log.d(TAG, "sendRegistrationToServer: jsonuser : " + jsonUser.toString(2));
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("user", jsonUser);

                //send data request
                RequestQueue queue = VolleySingleton.getRequestQueue();

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.PUT,
                        Constants.USERS_API_URL + "/" + mUser.id,
                        jsonRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "onResponse() called with: " + "response = [" + response + "]");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "onErrorResponse() called with: " + "error = [" + error + "]");
                                Log.d(TAG, "onError: Hubo algun problema al actualizando");
                                String errorMessage = VolleyErrorHelper.getMessage( App.appContext, error);
                                //String errorDialogMsg = Utils.showErrorsJson(errorMessage, ProfileActivity );
                                Toast.makeText( App.appContext, errorMessage, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onErrorResponse: error message:" + errorMessage);
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Log.d(TAG, "getHeaders() called");
                        Map headers = new HashMap();
                        String token = AppSession.getCurrentUser().authToken;
                        headers.put("Authorization", token);
                        headers.put("Content-Type", "application/json; charset=utf-8");

                        return headers;
                    }
                };

                queue.add(request);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
