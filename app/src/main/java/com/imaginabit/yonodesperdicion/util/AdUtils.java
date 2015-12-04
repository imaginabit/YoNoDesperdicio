package com.imaginabit.yonodesperdicion.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.imaginabit.yonodesperdicion.model.Ad;

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

    public static void fetchAds(final Activity activity, final FetchAdsCallback callback ){
        final String TAG= AdUtils.TAG + " fetchAds";
        AsyncTask<Void, Void, Void> fetchAdsTask = new AsyncTask<Void, Void, Void>() {
            JSONObject jObj = null;
            public List<Ad> ads = null;
            private Exception e = null;

            @Override
            protected Void doInBackground(Void... params) {
                String json = null;
                try {
                    json = AppUtils.downloadJsonUrl(Constants.ADS_API_URL);
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
                            ads = new ArrayList<>();
                            for (int i = 0; i < jsonItems.length(); i++) {
                                JSONObject jsonItem = null;
                                try {
                                    jsonItem = jsonItems.getJSONObject(i);
                                } catch (JSONException e) {
                                    this.e = e;
                                    //e.printStackTrace();
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
                                    if ( AppUtils.isNotEmptyOrNull(title) ) {
                                        //public Ad(String title, String body, String imageUrl, int weightGrams, Date expiration, int postalCode, Status status, int userId, String userName) {
                                        Ad item = new Ad(title,body,image_url,grams,pick_up_date,zipcode,status,user_id,"Usuario");
                                        ads.add(item);
                                    }
                                } catch ( Exception e ){
                                    e.printStackTrace();
                                }
                                Log.d(TAG,"added " + ads.size() );
                            }
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    this.e = e;
//                Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
//                if (pd != null && pd.isShowing()) {
//                    pd.dismiss();
//                }

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

}
