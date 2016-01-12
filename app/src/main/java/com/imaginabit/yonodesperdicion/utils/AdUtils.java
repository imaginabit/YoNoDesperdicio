package com.imaginabit.yonodesperdicion.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fer2015julio on 24/11/15.
 */
public class AdUtils {
    private static final String TAG = "AdUtils";

    public static void fetchAds(final User u,final Activity activity, final FetchAdsCallback callback ){
        final String TAG= AdUtils.TAG + " fetchAds filter user";
        AsyncTask<Void, Void, Void> fetchAdsTask = new AsyncTask<Void, Void, Void>() {
            JSONObject jObj = null;
            public List<Ad> ads = null;
            private Exception e = null;
            ProgressDialog pd = new ProgressDialog(activity);

            @Override
            protected void onPreExecute() {
                pd.setTitle("Cargando");
                pd.setMessage("Recibiendo datos...");
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                String json = null;
                try {
                    json = Utils.downloadJsonUrl(Constants.USER_ADS_API_URS + u.getUserId());
                } catch (IOException e) {
                    Log.e( TAG , "IOExeption " + e.toString());
                    this.e = e;
                }

                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);
                } catch (JSONException e) {
                    Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
                    this.e = e;
                } catch (Throwable t) {
                    Log.e(TAG, "Could not parse malformed JSON: \"" + json + "\"");
                }

                try {
                    if (jObj.has("ads")) {
                        Log.v(TAG,"has ads");
                        JSONArray jsonItems = null;
                        try {
                            jsonItems = jObj.getJSONArray("ads");
                        } catch (JSONException e) {
                            this.e = e;
                            //e2.printStackTrace();
                        }
//                        Log.d(TAG, jObj.toString() );
                        Log.d(TAG,"User has Ads " + jsonItems.length());
                        if (jsonItems.length() > 0) {
                            ResultAds resultAds;
                            resultAds = createAdList(jsonItems);
                            ads = resultAds.ads;
                            if (resultAds.e!=null) this.e = resultAds.e;
                        }
                    } else Log.d(TAG, "doInBackground: User without ads");
                } catch (Exception e) {
                    //e.printStackTrace();
                    this.e = e;
                    Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
                }
                Log.d(TAG, "doInBackground: ads:" + ads.size());
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                if (e == null) {
                    callback.done(ads, null);
                } else {
                    callback.done(null, e);
                }
            }
        };
        TasksUtils.execute(fetchAdsTask);
    }

    private static class ResultAds {
        public List<Ad> ads;
        public Exception e;

        public ResultAds(List<Ad> ads, Exception e) {
            this.ads = ads;
            this.e = e;
        }
    }

    private static ResultAds createAdList(JSONArray jsonItems){
        List<Ad> ads = new ArrayList<>();
        Exception e = null;
        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject jsonItem = null;
            try {
                jsonItem = jsonItems.getJSONObject(i);
            } catch (JSONException e1) {
                e = e1;
            }

            // title, id, category, image_url, introduction
            //long idea_id = jsonItem.optLong("id", 0L);
            int ad_id = jsonItem.optInt("id", 0);
            String title = jsonItem.optString("title", "");
            String category = jsonItem.optString("food_category", "");
            String image_url = jsonItem.optString("image", "");
            String body = jsonItem.optString("body", "");
            int status = jsonItem.optInt("status", 0);
            int grams = jsonItem.optInt("grams", 0);
            int user_id = jsonItem.optInt("user_id", 0);
            String pick_up_date =jsonItem.optString("pick_up_date", "");
            String zipcode =jsonItem.optString("zipcode", "");

            Log.v(TAG, "add ad " + jsonItem.toString()  );
            Log.v(TAG, "add Ad id:" + ad_id + " title:" + title + " cat:" + category + " image:" + image_url + " ");
            Log.v(TAG, "add Ad id:" + ad_id + " intro: " + zipcode.toString());

            try {
                //status 3 => producto entregado
                if ( Utils.isNotEmptyOrNull(title) && status != 3 ) {
                    //Ad(String title, String body, String imageUrl, int weightGrams, Date expiration, int postalCode, Status status, int userId, String userName)
                    Ad item = new Ad(ad_id,title,body,image_url,grams,pick_up_date,zipcode,status,user_id,"Usuario");

                    ads.add(item);
                }
            } catch ( Exception e1 ){
                //e.printStackTrace();
                e = e1;
            }
            //Log.d(TAG,"added " + ads.size() );
        }
        return new ResultAds(ads,e);

    }

    public static void fetchAds(final Activity activity, final FetchAdsCallback callback ){
        final String TAG= AdUtils.TAG + " fetchAds";
        AsyncTask<Void, Void, Void> fetchAdsTask = new AsyncTask<Void, Void, Void>() {
            JSONObject jObj = null;
            public List<Ad> ads = null;
            private Exception e = null;
            ProgressDialog pd = new ProgressDialog(activity);

            @Override
            protected void onPreExecute() {
                pd.setTitle("Cargando");
                pd.setMessage("Recibiendo datos...");
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                String json = null;
                try {
                    json = Utils.downloadJsonUrl(Constants.ADS_API_URL);
                } catch (IOException e) {
                    Log.e( TAG , "IOExeption " + e.toString());
                    this.e = e;
                }

                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);
                } catch (JSONException e) {
                    Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
                    this.e = e;
                } catch (Throwable t) {
                    Log.e(TAG, "Could not parse malformed JSON: \"" + json + "\"");
                }

                try {
                    if (jObj.has("ads")) {
                        Log.v(TAG,"has ads");
                        JSONArray jsonItems = null;
                        try {
                            jsonItems = jObj.getJSONArray("ads");
                        } catch (JSONException e) {
                            this.e = e;
                            //e2.printStackTrace();
                        }
//                        Log.d(TAG, jObj.toString() );
                        Log.d(TAG,"has Ads " + jsonItems.length());
                        if (jsonItems.length() > 0) {
                            ResultAds resultAds;
                            resultAds = createAdList(jsonItems);
                            ads = resultAds.ads;
                            if (resultAds.e!=null) this.e = resultAds.e;
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    this.e = e;
                    Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

                if (e == null) {
                    callback.done(ads, null);
                } else {
                    callback.done(null, e);
                }

            }


        };
        TasksUtils.execute(fetchAdsTask);
    }




    public interface FetchAdsCallback {
        public void done(List<Ad> ads, Exception e);
    }

    public interface FetchAdCallback {
        public void done(Ad ad,User user, Exception e);
    }


    public static void fetchAd(final int adId, final FetchAdCallback callback ){
        final String TAG= AdUtils.TAG + " fetchAds";
        AsyncTask<Void, Void, Void> fetchAdTask = new AsyncTask<Void, Void, Void>() {
            JSONObject jObj = null;
            private Ad ad = null;
            private User user = null;
            private Exception e = null;


            @Override
            protected Void doInBackground(Void... params) {
                String json = null;
                try {
                    json = Utils.downloadJsonUrl(Constants.ADS_API_URL+  "/" + adId);
                } catch (IOException e) {
                    Log.e( TAG , "------ ERROR ----- IOExeption " + e.toString());
                    this.e = e;
                }

                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);
                } catch (JSONException e) {
                    Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
                    this.e = e;
                } catch (Throwable t) {
                    Log.e(TAG, "Could not parse malformed JSON: \"" + json + "\"");
                }

                if (jObj.has("ad")){
                    //
                }
                if (jObj.has("users")){
                    JSONArray users = null;
                    JSONObject userJson = null;
                    try {
                        users = jObj.getJSONArray("users");
                    } catch (JSONException e1) {
                        this.e = e1;
                        //e1.printStackTrace();
                    }
                    try {
                        userJson = users.getJSONObject(0);
                    } catch (JSONException e1) {
                        this.e = e1;
                        //e1.printStackTrace();
                    }
//                    "id": 5,
//                    "username": "anita2",
//                    "zipcode": "28903",
//                    "total_quantity": 500,
//                    "rating": 3,
                    int userId = userJson.optInt("id") ;
                    String userName = userJson.optString("username","");
                    String zip = userJson.optString("zipcode", "");
                    int total_quantity = userJson.optInt("total_quantity", 0);
                    float rating = (float) userJson.optDouble("rating",0.0);

                    user = new User(userId,userName,userName ,"",zip,total_quantity, rating );

                }
                if (jObj.has("comments")){
                    //implementar cargar comentarios
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (e == null) {
                    callback.done(ad, user, null);
                } else {
                    callback.done(null, null, e);
                }
                super.onPostExecute(aVoid);
            }
        };
        TasksUtils.execute(fetchAdTask);
    }








}
