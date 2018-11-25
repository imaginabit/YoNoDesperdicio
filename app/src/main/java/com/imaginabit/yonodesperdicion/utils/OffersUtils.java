package com.imaginabit.yonodesperdicion.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.models.Image;
import com.imaginabit.yonodesperdicion.models.Offer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OffersUtils {
    private static final String TAG = "OffersUtils";


    public static void fetchOffers(final int page, final Activity activity, final FetchOffersCallback callback ){
        final String TAG= OffersUtils.TAG + " fetchOffers";

        AsyncTask<Void, Void, Void> fetchAdsTask = new AsyncTask<Void, Void, Void>() {
            JSONObject jObj = null;
            public List<Offer> offers = null;
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
                    json = Utils.downloadJsonUrl(Constants.OFFERS_API_URL + "/?page=" + String.valueOf(page) );
                } catch (IOException e) {
                    Log.e( TAG , "IOException " + e.toString());
                    this.e = e;
                }

                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                    this.e = e;
                } catch (Throwable t) {
                    Log.e(TAG, "Could not parse malformed JSON: \"" + json + "\"");
                }

                try {
                    if (jObj.has("offers")) {
                        Log.v(TAG,"has offers");
                        JSONArray jsonItems = null;
                        try {
                            jsonItems = jObj.getJSONArray("offers");
                        } catch (JSONException e) {
                            this.e = e;
                            //e2.printStackTrace();
                        }
//                        Log.d(TAG, jObj.toString() );
                        Log.d(TAG,"has Offers " + jsonItems.length());
                        if (jsonItems.length() > 0) {
                            ResultOffers resultOffers;
                            resultOffers = createOffersList(jsonItems);
                            offers = resultOffers.offers;
                            if (resultOffers.e!=null) this.e = resultOffers.e;
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    this.e = e;
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
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
                    callback.done( offers );
                } else {
                    callback.error( e );
                }

            }


        };
        TasksUtils.execute(fetchAdsTask);
    }



    public interface FetchOffersCallback {
        public void done(List<Offer> ads);
        public void error(Exception e);
    }

    private static class ResultOffers {
        public List<Offer> offers;
        public Exception e;

        public ResultOffers(List<Offer> offers, Exception e) {
            this.offers = offers;
            this.e = e;
        }
    }

    private static ResultOffers createOffersList(JSONArray jsonItems){
        Log.d(TAG, "createAdList: ");
        List<Offer> offers = new ArrayList<>();
        Exception e = null;

        Offer item;

        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject jsonItem = null;
            try {
                jsonItem = jsonItems.getJSONObject(i);
            } catch (JSONException e1) {
                e = e1;
            }

            try {
                Offer offer = createOffer(jsonItem);
                if (offer != null) offers.add(offer);
            } catch ( Exception e1 ){
                e = e1;
            }
        }
        Log.d(TAG, "createOffersList: FIN");
        return new ResultOffers(offers,e);
    }

    private static Offer createOffer(JSONObject jsonItem) {
        Log.d(TAG, "createOffer() called with: jsonItem = [" + jsonItem + "]");

        Offer offer = new Offer();

        offer.setId(jsonItem.optInt( "id" ));
        offer.setTitle(jsonItem.optString("title"));
        offer.setAddress(jsonItem.optString("address"));
        offer.setStore(jsonItem.optString("store"));
        offer.setUntil(jsonItem.optString("until"));
        offer.setStatus(jsonItem.optString("status"));

        Image image = new Image();
        image.setLarge(jsonItem.optJSONObject("image").optString("large"));
        image.setMedium(jsonItem.optJSONObject("image").optString("medium"));
        image.setOriginal(jsonItem.optJSONObject("image").optString("original"));

        offer.setImage( image );

        return offer;
    }
}
