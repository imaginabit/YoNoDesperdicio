package com.imaginabit.yonodesperdicion.gcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by fer on 4/12/16.
 */

public class MyFirebaseInstanceService extends FirebaseInstanceIdService {

    private static final String TAG = "FireBaseInstanceServ";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);

        super.onTokenRefresh();
    }

    private void sendRegistrationToServer(String refreshedToken) {
        //Enviar el token al servidor via api
        Log.d(TAG, "sendRegistrationToServer: send " + refreshedToken);
    }
}
