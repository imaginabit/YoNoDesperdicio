package com.imaginabit.yonodesperdicion.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.data.AdsContract;
import com.imaginabit.yonodesperdicion.helpers.VolleyErrorHelper;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.models.Offer;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.utils.MessagesUtils;
import com.imaginabit.yonodesperdicion.utils.PrefsUtils;
import com.imaginabit.yonodesperdicion.utils.UserUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

//import com.imaginabit.yonodesperdicion.views.RoundedImageView;

public class OfferDetailActivity extends NavigationBaseActivity implements Observer, OnMapReadyCallback {
    private static final String TAG = "AdDetailActivity";
    private Offer myOffer;
    private boolean isFavorite;
    private boolean isBooked;
    private boolean isDelivered;
    private TextView statusView;
    private ImageView statusImageView;

    private SharedPreferences.Editor prefsEdit = PrefsUtils.getSharedPreferencesEditor(context);
    private ContentResolver contentResolver;
    private ContentValues valuesFavorite;
    private ContentValues valuesConversation;


    private String[] projection;
    private String selectionClause;
    private String[] selectionArgs;
    private String emptyWhere = "";
    private String[] emptyArgs = {};

    private GoogleMap mMap;
    private MapView mMapView;

    private ShareActionProvider mShareActionProvider;

    static final int OFFER_EDIT_REQUEST = 1;  // The request code
    static final int OFFER_EDIT_OK = 2;  // The request code
    static final int OFFER_EDIT_DELETE = 3;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ad_content);


        contentResolver = getContentResolver();
        valuesFavorite = new ContentValues();

        Offer offer = null;
        if (AppSession.currentOffer != null) {
            offer = AppSession.currentOffer;
        } else {
            this.finish();
        }


        Log.d(TAG, "onCreate: Offer: " + offer);
        isFavorite = false;

        if (offer == null) {
            Toast.makeText(this, "No se ha pasado el argumento", Toast.LENGTH_LONG).show();
        } else {
            myOffer = offer;

            Log.d(TAG, "onCreate: myOffer = " + myOffer.getId());
            // Fix action bar and drawer

            Toolbar toolbar = setSupportedActionBar();
            toolbar.setTitle(offer.getTitle());
            setDrawerLayout(toolbar);
            getSupportActionBar().setTitle(offer.getTitle());

            setSupportedActionBar(R.drawable.ic_arrow_back_black);

            VolleySingleton.init(this);

            // Content
            TextView bodyView = findViewById(R.id.ad_body);
            bodyView.setText(offer.getDescription());
            TextView expirationText = findViewById(R.id.ad_expiration);
            expirationText.setText(offer.getExpirationDateLong());

//            TextView weightText = findViewById(R.id.ad_weight);
//            weightText.setVisibility(View.GONE);

            final ImageView image = findViewById(R.id.backdrop);

            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            final String imageUri = offer.getImage().getMedium();

            ImageSize targetSize = new ImageSize(300, 200); // result Bitmap will be fit to this size
            imageLoader.displayImage(imageUri, image);

            if (Utils.isEmptyOrNull(offer.getExpirationDateLong())) {
                TableRow row = findViewById(R.id.row_expiration);
                row.setVisibility(View.GONE);
            }

            // Remove  rows unused in offers
            TableRow row_weight = findViewById(R.id.row_weight);
            TableRow row_cat = findViewById(R.id.row_categoria);
            TableRow row_status = findViewById(R.id.row_status);
            row_weight.setVisibility(View.GONE);
            row_cat.setVisibility(View.GONE);
            row_status.setVisibility(View.GONE);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (image.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
                        //                    image.setAdjustViewBounds(false);
                        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        Log.d(TAG, "onClick: setAdjustViewBounds false");
                    } else {
                        //                    image.setAdjustViewBounds(true);
                        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Log.d(TAG, "onClick: setAdjustViewBounds true");
                    }
                }
            });

            mMapView = findViewById(R.id.mapview);
            mMapView.onCreate(savedInstanceState);

            // Gets to GoogleMap from the MapView and does initialization stuff
            try {
                mMapView.getMapAsync(this);

                //mMap.setMyLocationEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }


            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            try {
                MapsInitializer.initialize(OfferDetailActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
//            } catch (GooglePlayServicesNotAvailableException eMap) {
//                eMap.printStackTrace();
            }


            //load user data form api with offer.getUserId()
            //Need save current activity for auth
            MessagesUtils.mCurrentActivity = this;

            if ( offer.getUserID() != null  ){
                UserUtils.getUser(offer.getUserID(), this, new UserUtils.FetchUserCallback() {
                    @Override
                    public void done(User user, Exception e) {
                        if (user != null) {
                            TextView userName = findViewById(R.id.user_name);
                            userName.setText(user.getName());
                            TextView userLocation = findViewById(R.id.user_location);
                            userLocation.setText(user.getUserName());
                            RatingBar userRatting = findViewById(R.id.user_ratting);
                            userRatting.setRating(user.getRatting());
                        }

                    }
                });
            }




            FloatingActionButton fab = findViewById(R.id.fab);


            if (userIsOwner(offer)) {
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mode_edit_white));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickEdit();
//                        Toast.makeText(AdDetailActivity.this, "No disponible", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                fab.hide();
            }
        }


    }

    private boolean userIsOwner(Offer offer) {
        Log.d(TAG, "userIsOwner() called with: offer = [" + offer + "]");
        Boolean isOwner = AppSession.getCurrentUser() != null && AppSession.getCurrentUser().id == offer.getUserID();
        Log.d(TAG, "userIsOwner: Current User: " + AppSession.getCurrentUser().id + " offer user " + offer.getUserID() );
        Log.d(TAG, "userIsOwner: is? " + isOwner );
        return  isOwner;
    }



    /**
     * When click on edit Ad, send all data to add edit on
     */
    private void clickEdit() {

        AppSession.currentOffer = myOffer;
        Intent intent = new Intent(context, OfferCreateActivity.class);
        startActivityForResult( intent, OFFER_EDIT_REQUEST);
//        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == OFFER_EDIT_DELETE ){
            Intent itnt= new Intent(context, OffersOldActivity.class );
            itnt.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT );
            startActivity(itnt);
            finish();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.offer, menu);
        return super.onCreateOptionsMenu(menu);

//        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            if (myOffer != null) {
                Log.d(TAG, "onOptionsItemSelected: myOffer " + myOffer.getId());
                if (!isFavorite) {
                    valuesFavorite.put(AdsContract.FavoritesColumns.FAV_AD_ID, myOffer.getId());
                    Uri returned = contentResolver.insert(AdsContract.URI_TABLE_FAVORITES, valuesFavorite);
                    Log.d(TAG, "onOptionsItemSelected: Record Id returned is " + returned.toString());
                    isFavorite = true;

//                    Toast.makeText(OfferDetailActivity.this, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onOptionsItemSelected: is favorited and going to remove it ");
//                    String where = AdsContract.FavoritesColumns.FAV_AD_ID + " = ?";

                    Uri favorited_ad = Uri.parse(AdsContract.URI_TABLE_FAVORITES + "/ad/" + myOffer.getId());
                    Log.d(TAG, "onOptionsItemSelected: favorited ad id uri= " + favorited_ad);

                    int deleteReturned = contentResolver.delete(favorited_ad, emptyWhere, emptyArgs);

                    if (deleteReturned > 0) {
                        Toast.makeText(OfferDetailActivity.this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                        isFavorite = false;
                    }
                }
            }
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_booked) {
            if (isBooked) {
                isBooked = false;
                Toast.makeText(OfferDetailActivity.this, "Quitada la reserva", Toast.LENGTH_SHORT).show();
            } else {
                isBooked = true;
                isDelivered = false;//they can be both
                statusView.setText("reservado");
                statusView.setTextColor(ContextCompat.getColor(context, R.color.ad_reservado));
                statusImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_booked));
                Toast.makeText(OfferDetailActivity.this, "Marcado como reservado", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_deliver) {
            if (isDelivered) {
                isDelivered = false;
            } else {
                isDelivered = true;
                isBooked = false;//they can be both
                statusView.setText("entregado");
                statusView.setTextColor(ContextCompat.getColor(context, R.color.ad_reservado));
                statusImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_booked));
                Toast.makeText(OfferDetailActivity.this, "Marcado como entregado", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_share_button) {
            Log.d(TAG, "onOptionsItemSelected: Compartir");
            startActivity( Intent.createChooser(createShareIntent(),"Compartir") ); //createChooser() need to show directshare icons!
        } else if (id == android.R.id.home ) {
            AppSession.currentOffer = null;
            finish();
            return true;
        }

        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }


    private void sendStatus(Ad.Status status) throws JSONException {
        RequestQueue queue = VolleySingleton.getRequestQueue();
        JSONObject jsonAd = new JSONObject();
        jsonAd.put("status", "1");
        if (status == Ad.Status.BOOKED) {
            jsonAd.put("status", "2");
        }
        if (status == Ad.Status.DELIVERED) {
            jsonAd.put("status", "3");
        }
        JSONObject jsonRequest = new JSONObject().put("ad", jsonAd);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                Constants.ADS_API_URL + "/" + myOffer.getId(),
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse() called with: " + "response = [" + response + "]");
                        VolleyLog.v("Response:%n %s", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse() called with: " + "error = [" + error + "]");
                        String errorMessage;
                        String errorDialogMsg;
                        errorMessage = VolleyErrorHelper.getMessage(OfferDetailActivity.this, error);
                        errorDialogMsg = Utils.showErrorsJson(errorMessage, OfferDetailActivity.this);
                        Log.d(TAG, "onErrorResponse: error message:" + errorMessage);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map headers = new HashMap();
                String token = AppSession.getCurrentUser().authToken;
                headers.put("Authorization", token);
                Log.d(TAG, "getHeaders: authToken " + token);

                headers.put("Content-Type", "application/json; charset=utf-8");

                return headers;
            }
        };
        queue.add(request);
        Log.d(TAG, "sendDataRequest: request: " + request.toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady() called with: " + "googleMap = [" + googleMap + "]");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            //        Utils.getGPSfromZip(this, )
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocationName(myOffer.getAddress(), 1);
                Address location = addressList.get(0);
                zoomLocation(new LatLng(location.getLatitude(), location.getLongitude()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap = googleMap;
        }
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void observe(Observable o) {
        Log.d(TAG, "observe() called with: " + "o = [" + o + "]");
        o.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, "update() called with: " + "observable = [" + observable + "], data = [" + data + "]");
        Location location = ((Ad) observable).getLocation();

//        zoomLocation((Ad) observable);
    }

    /**
     * Center the map on ad location
     * @param location
     */
    private void zoomLocation(LatLng location) {
        if (location != null && mMap != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 13);
            mMap.animateCamera(cameraUpdate);
        }
    }

    //create and return share intent
    private Intent createShareIntent(){
        Log.d(TAG, "createShareIntent: ");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Yo no desperdicio " + myOffer.getTitle() + " " +Constants.HOME_URL + "offers/"+ myOffer.getId() );
        return shareIntent;
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder( this ,R.style.yndDialog );
        OfferDetailActivity.this.finish();
    }



}
