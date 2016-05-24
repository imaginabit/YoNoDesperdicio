package com.imaginabit.yonodesperdicion;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.android.gms.maps.model.LatLng;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Task to lookup the LatLng of an address
 * when geocoder not work you can use google maps
 * https://gist.github.com/selecsosi/6705630
 */
public class SearchForLocationTask extends AsyncTask<String, Void, LatLng> {
    private static final String TAG = SearchForLocationTask.class.getSimpleName();

    Context mContext;

    //Get static reference to requestQueue
    RequestQueue mRequestQueue = VolleySingleton.getRequestQueue();

    String mAddress;

    SearchForLocationTaskEventListener mListener;

    public interface SearchForLocationTaskEventListener {
        public void onFinish(LatLng result);
    }

    public SearchForLocationTask(Context context, SearchForLocationTaskEventListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected LatLng doInBackground(String... params) {
        if(Log.isLoggable(TAG, Log.INFO))Log.i(TAG, "doInBackground");
        mAddress = params[0];
        LatLng returnedLocation = null;
        Geocoder geocoder = new Geocoder(mContext);
        List<Address> addressList = null;
        if(Log.isLoggable(TAG, Log.INFO))Log.i(TAG, "Using geocoder");

        try {
            addressList = geocoder.getFromLocationName(mAddress, 5);
        } catch (IOException e) {
            if(Log.isLoggable(TAG, Log.ERROR))Log.e(TAG, "Error geocoding address", e);
        }

        if( (addressList != null) && (addressList.size() > 0)) {
            returnedLocation = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
        }
        if(returnedLocation == null) {
            if(Log.isLoggable(TAG, Log.INFO))Log.i(TAG, "Using geocoder failed, try using google");
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            try {
                JsonRequest<JSONObject> addressRequest = new JsonRequest<JSONObject>(Request.Method.GET,
                        "http://maps.google.com/maps/api/geocode/json?address=" + URLEncoder.encode(mAddress, "utf-8") + "&ka&sensor=false",
                        "",
                        future,
                        future) {

                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        JSONObject jsonResponse = null;
                        String jsonString = new String(response.data);
                        if(!TextUtils.isEmpty(jsonString)) {
                            try {
                                jsonResponse = new JSONObject(jsonString);
                            } catch(JSONException e) {}
                        }

                        return Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response)) ;
                    };
                };
                future.setRequest(mRequestQueue.add(addressRequest));

                JSONObject response = future.get();

                returnedLocation = Utils.getLatLngFromGoogleJson(response);
            } catch(InterruptedException e) {
                if(Log.isLoggable(TAG, Log.ERROR))Log.e(TAG, "", e);
            } catch (ExecutionException e) {
                if(Log.isLoggable(TAG, Log.ERROR))Log.e(TAG, "", e);
            } catch (UnsupportedEncodingException e) {
                if(Log.isLoggable(TAG, Log.ERROR))Log.e(TAG, "", e);
            }
        }
        return returnedLocation;
    }

    @Override
    protected void onPostExecute(LatLng result) {
        if(Log.isLoggable(TAG, Log.INFO))Log.i(TAG, "onPostExecute");
        super.onPostExecute(result);

        if(mListener != null) {
            mListener.onFinish(result);
        }
    }

}