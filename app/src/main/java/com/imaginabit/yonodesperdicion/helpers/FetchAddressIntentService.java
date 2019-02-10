package com.imaginabit.yonodesperdicion.helpers;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service to get localization of the device
 * Created by Fernando Ramírez on 27/01/16.
 */
public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddress IS";
    private ResultReceiver mReceiver;

    /**
     * Fix the FetchAddressIntentService has no zero argument constructor
     */
    public FetchAddressIntentService() { super("yonodesp");  }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        String errorMessage = "";
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (location == null) {
            errorMessage = getString(R.string.no_location_data_provided);
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            return;
        }

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        } catch (NullPointerException nullExeption){
            errorMessage = getString(R.string.invalid_location);
            Log.e(TAG, "onHandleIntent: "+ errorMessage );
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));

            Log.d(TAG, "onHandleIntent: addressFragments " + addressFragments.toString());
            //String msg = TextUtils.join(System.getProperty("line.separator"),addressFragments);
            String msg = TextUtils.join("",addressFragments);
            Log.d(TAG, "onHandleIntent: addressFragments msg "+ msg );
            deliverResultToReceiver(Constants.SUCCESS_RESULT, msg);

        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.RESULT_DATA_KEY, message);
            Log.d(TAG, "deliverResultToReceiver: message : " + message);
            Log.d(TAG, "deliverResultToReceiver: bundle : " + bundle.toString());
            if (bundle!=null){
                Log.d(TAG, "deliverResultToReceiver: bundle not null");
            }
            mReceiver.send(resultCode, bundle);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
