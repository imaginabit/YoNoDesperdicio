package com.imaginabit.yonodesperdicion.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.imaginabit.yonodesperdicion.App;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.SearchForLocationTask;
import com.imaginabit.yonodesperdicion.adapters.AdsAdapter;
import com.imaginabit.yonodesperdicion.helpers.VolleyErrorHelper;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
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

    public static void fetchAdsVolley(User user, final Activity activity, final FetchAdsCallback cb){
        Log.d(TAG, "fetchAdsVolley() called with: " + "user = [" + user + "], activity = [" + activity + "], cb = [" + cb + "]");

        //ProgressDialog pd = new ProgressDialog(activity);

        JSONObject jsonRequest = new JSONObject();
//        RequestQueue queue = VolleySingleton.getRequestQueue();
        AppSession.requestQueue = VolleySingleton.getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                Constants.USER_ADS_API_URL + Integer.toString(user.getUserId()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse() called with: " + "response = [" + response.toString(2) + "]");
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: error");
                        }
                        List<Ad> ads = new ArrayList<>();
//                        Exception error = null;
                        if (response.has("ads")) {
                            JSONArray jsonItems = new JSONArray();
                            try {
                                jsonItems = response.getJSONArray("ads");
                                Log.d(TAG,"User has Ads " + jsonItems.length());
                                if (jsonItems.length() > 0) {
                                    ResultAds resultAds;
                                    resultAds = createAdList(jsonItems);
                                    ads = resultAds.ads;
                                    cb.done(ads,null);
                                }

                            } catch (JSONException e) {
                                cb.done(null,e);
                                e.printStackTrace();
                            }
                        } else Log.d(TAG, "doInBackground: User without ads");
                        Log.d(TAG, "onResponse: Fin");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse() called with: " + "error = [" + error + "]");
                        String errorMessage = VolleyErrorHelper.getMessage(activity, error);
                        String errorDialogMsg = Utils.showErrorsJson(errorMessage, activity);
                    }
                }
        );
        AppSession.requestQueue.add(request);
    }

    public static void fetchAds(final User u,final Activity activity, final FetchAdsCallback callback ){
        final String TAG= AdUtils.TAG + "fetchAds filter user";
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
                    json = Utils.downloadJsonUrl(Constants.USER_ADS_API_URL + u.getUserId());
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
        Log.d(TAG, "createAdList: ");
        List<Ad> ads = new ArrayList<>();
        Exception e = null;

        Ad item;

        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject jsonItem = null;
            try {
                jsonItem = jsonItems.getJSONObject(i);
            } catch (JSONException e1) {
                e = e1;
            }

            try {
                Ad ad = createAd(jsonItem);
                if (ad != null) ads.add(ad);
            } catch ( Exception e1 ){
                e = e1;
            }
        }
        Log.d(TAG, "createAdList: FIN");
        return new ResultAds(ads,e);
    }

    private static Ad createAd(JSONObject jsondata){
        try {
            Log.d(TAG, "createAd() called with: " + "jsondata = [" + jsondata.toString(2) + "]");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Ad ad;

        String categoria;
        String zipcode;
        int ad_id;
        String title;
        String category;
        String image_url;
        String body;
        int status;
        int grams;
        int user_id;
        String pick_up_date;

        ad_id = jsondata.optInt("id", 0);
        title = jsondata.optString("title", "");
        category = jsondata.optString("food_category", "");
        image_url = jsondata.optString("image", "");
        body = jsondata.optString("body", "");
        status = jsondata.optInt("status", 0);
        grams = jsondata.optInt("grams", 0);
        user_id = jsondata.optInt("user_id", 0);
        pick_up_date = jsondata.optString("pick_up_date", "");
        zipcode = jsondata.optString("zipcode", "");
        categoria = jsondata.optString("food_category", "");

        Log.d(TAG, "createAd: pick_up_date: " + pick_up_date);

        try {
            //status 3 => producto entregado
            //TODO: if status == 3 pero es de l usuario mostrar
            if ( Utils.isNotEmptyOrNull(title)
                    && status != 3
                    && ( pick_up_date=="null" ||
                                !Utils.isExpired(Constants.DATE_JSON_SORT_FORMAT.parse(pick_up_date) ) )
                    ) {
                //Ad(String title, String body, String imageUrl, int weightGrams, Date expiration, int postalCode, Status status, int userId, String userName)
                ad = new Ad(ad_id,title,body,image_url,grams,pick_up_date,zipcode,status,user_id,"Usuario");
                //ad.setLocation(calculateLocation(ad));
//                ad.setLastDistance(calculateDistance(ad));
                ad.setCategoria(categoria);

                return ad;
            }else {
                return null;
            }
        } catch ( Exception e1 ){
            e1.printStackTrace();
            return null;
        }
    }
    public static Location calculateLocation(final Ad ad) {
        return calculateLocation(ad, null, 0);
    }

    public static Location calculateLocation(final Ad ad, final AdsAdapter adapter, final int pos){
        Log.d(TAG, "calculateLocation() called with: " + "ad = [" + ad.getTitle() + "]");
        final Location adLocation = new Location(ad.getTitle());
        //final Address adAddress = Utils.getGPSfromZip( App.appContext , ad.getPostalCode());


        final String searchgps = ad.getPostalCodeString() + " "+ ad.getProvince();
        Log.d(TAG, "getGPSfromZipGmaps : calculateLocation: "+ searchgps + " " + ad.getPostalCode());
        Utils.getGPSfromZipGmaps(searchgps, new SearchForLocationTask.SearchForLocationTaskEventListener() {
            @Override
            public void onFinish(LatLng result) {
                Log.d(TAG, "onFinish: getGPSfromZipGmaps, String: " + searchgps);
                Log.d(TAG, "calculateLocation searchforlocationTask onFinish() called with: " + "ad = [" + ad.getTitle() + "]" + "result = [" + result + "]" );
                try {


                    adLocation.setLatitude(result.latitude);
                    adLocation.setLongitude(result.longitude);
                    ad.setLocation(adLocation);

                    ad.setLastDistance(calculateDistance(ad));
                    //tell de adapter to refersh view
                    if (adapter != null) {
                        adapter.notifyItemChanged(pos);
                    }
                } catch (Exception e ){
                    e.printStackTrace();
                }
            }
        });

        return null;
    }


    /**
     * Calculate last distance from ad and the user in Kilometers and set it in Ad
     * @param ad
     * @return distance in kilometer
     */
    public static int calculateDistance(final Ad ad){
        Log.d(TAG, "calculateDistance() called with: " + "ad = [" + ad.getTitle() + "]");
        Location userLocation;
        try {
            Log.d(TAG, "calculateDistance: location: " + AppSession.lastLocation.getLatitude() + ", " + AppSession.lastLocation.getLongitude());
            Log.d(TAG, "calculateDistance: location: " + AppSession.lastLocation.toString());
        } catch (Exception e ){
            Log.d(TAG, "calculateDistance: no hay lastlocation");
            e.printStackTrace();
        }

        if (AppSession.lastLocation != null  && ad.getLocation()!=null) {
            userLocation = AppSession.lastLocation;
            Log.d(TAG, "calculateDistance: " + ad.getTitle() + " lat:" + userLocation.getLatitude() + " lng: " + userLocation.getLongitude());

            float d = (ad.getLocation().distanceTo(userLocation)) / 1000;//kilometers
            int distance = Math.round(d);
            ad.setLastDistance(distance);
            Log.d(TAG, "calculateDistance: New distance: " + distance);
            return distance;
        } else {
            if (AppSession.lastLocation == null) {
                Log.d(TAG, "calculateDistance: no hay last location del gps");
                //tendria que usar el el gps  o el  cp del usuario si hay sesion
                if(AppSession.getCurrentUser()!=null) {
                    Utils.getGPSfromZipGmaps(AppSession.getCurrentUser().zipCode,
                            new SearchForLocationTask.SearchForLocationTaskEventListener() {
                                @Override
                                public void onFinish(LatLng result) {
                                    Log.d(TAG, "calculateDistance searchforlocationTask onFinish() called with: " + "result = [" + result + "]");
                                    Location l = new Location("user");
                                    l.setLatitude(result.latitude);
                                    l.setLongitude(result.longitude);
                                    AppSession.lastLocation = l;
                                }
                            });
                }
            }
            if(ad.getLocation()== null){
                Log.d(TAG, "calculateDistance: no hay ad location ");

            }
//            userLocation = new Location("User");
//            userLocation.setLatitude(adAddress.getLatitude());
//            userLocation.setLongitude(adAddress.getLongitude());
//                    userLocation.setLatitude(userAddress.getLatitude());
//                    userLocation.setLongitude(userAddress.getLongitude());
        }


        //Address userAddress = Utils.getGPSfromZip(App.appContext, Integer.parseInt( AppSession.getCurrentUser().zipCode ) );

        return -1;
    }

    public static void fetchAds(final int page, final Activity activity, final FetchAdsCallback callback ){
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
                    json = Utils.downloadJsonUrl(Constants.ADS_API_URL + "/?page=" + String.valueOf(page) );
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
                    try {
                        pd.dismiss();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
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
        void done(List<Ad> ads, Exception e);
    }

    public interface FetchAdCallback {
        void done(Ad ad, User user, Exception e);
    }

    public static void fetchAd(final int adId, final FetchAdCallback callback ){
        final String TAG= AdUtils.TAG + " fetchAds";
        AsyncTask<Void, Void, Void> fetchAdTask = new AsyncTask<Void, Void, Void>() {
            JSONObject jObj = null;
            JSONObject jsonData = null;
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
                    try {
                        jsonData = jObj.getJSONObject("ad");
                        ad = createAd(jsonData);
                    } catch (JSONException e) {
                        this.e = e;
                        //e2.printStackTrace();
                    }
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
                    int userId = userJson.optInt("id") ;
                    String userName = userJson.optString("username","");
                    String zip = userJson.optString("zipcode", "");
                    int total_quantity = userJson.optInt("total_quantity", 0);
                    float rating = (float) userJson.optDouble("rating",0.0);
                    String avatar = userJson.optString("image", "");

                    user = new User(userId,userName,userName ,"",zip,total_quantity, rating, avatar );
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

    public static void fetchCategories(final categoriesCallback callback){

        AppSession.requestQueue = VolleySingleton.getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                Constants.CATEGORIES_URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse() called with: " + "response = [" + response.toString(2) + "]");
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: error");
                        }
                        List<String> categories = new ArrayList<>();

                        if (response.has("categories")) {
                            JSONArray jsonItems = new JSONArray();
                            try {
                                jsonItems = response.getJSONArray("categories");
                                Log.d(TAG,"User has categories " + jsonItems.length());
                                if (jsonItems.length() > 0) {
                                    Log.d(TAG, "onResponse: items "+ jsonItems.toString());

                                    for (int i = 0; i < jsonItems.length(); i++) {
                                        String item = jsonItems.getString(i);
                                        Log.d(TAG, "onResponse: "+ item);
                                        categories.add(item);

                                    }
                                    callback.done(categories);
                                }
                            } catch (JSONException e) {
                                callback.error(e);
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse() called with: " + "error = [" + error + "]");
                        String errorMessage = VolleyErrorHelper.getMessage(App.appContext, error);
//                        String errorDialogMsg = Utils.showErrorsJson(errorMessage, App.appContext);
                    }
                }
        );
        AppSession.requestQueue.add(request);
    }


    public interface categoriesCallback {
        void done(List<String> categories);
        void error(Exception e);
    }

}
