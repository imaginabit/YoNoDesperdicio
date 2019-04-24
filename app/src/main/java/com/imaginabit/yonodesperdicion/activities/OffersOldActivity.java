package com.imaginabit.yonodesperdicion.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.imaginabit.yonodesperdicion.App;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.OffersAdapter;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.models.Offer;
import com.imaginabit.yonodesperdicion.utils.OffersUtils;
import com.imaginabit.yonodesperdicion.utils.UserUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.imaginabit.yonodesperdicion.utils.PrefsUtils;
import com.imaginabit.yonodesperdicion.utils.UserUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.rubengees.introduction.IntroductionBuilder;
import com.rubengees.introduction.entity.Slide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


//import com.imaginabit.yonodesperdicion.gcm.RegistrationIntentService;

public class OffersOldActivity extends NavigationBaseActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private final String TAG = getClass().getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Offer> offers;

    protected Handler handler;


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GoogleApiClient mGoogleApiClient;

    private boolean mAddressRequested;
    private String mAddressOutput;

    private int page;

    protected static final int LOCATION_REQUEST = 0;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.appContext = context;//for make getGPSfromZip works

        //init Singleton instances
        VolleySingleton.init(this);

        // Put on session
        UserUtils.putUserSessionOn(this);

        offers = new ArrayList<>();

        // Fix action bar and drawer
        Toolbar toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);

        final Activity offersActivity = this;

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: CHECK
                if (Utils.checkLoginAndRedirect( OffersOldActivity.this )) {
                    AppSession.currentOffer = null;
                    Intent intent = new Intent(context, OfferCreateActivity.class);
                    startActivity(intent);
                }
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);


        recyclerView = findViewById(R.id.recycler_ads);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        showIntroSlides();
//        Log.d(TAG, "onCreate: ahora initialize data:");
//        initializeData();

        adapter = new OffersAdapter(context, offers, recyclerView);

        recyclerView.setAdapter(adapter);

        getOffersFromWeb();


        //infinite scroll!
//        handler = new Handler();
//
//        OnLoadMoreListener onLoadMoreListener =  new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                Log.d(TAG, "onLoadMore: called");
//                //add null , so the adapter will check view_type and show progress bar at bottom
//                offers.add(null);
//                adapter.notifyItemInserted(offers.size()-1);
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //remove progress item
//                        offers.remove(offers.size()-1);
//                        adapter.notifyItemRemoved(offers.size());
//                        Log.d(TAG, "run: remove progress bar ");
//
//                        //add items
//                        int start = offers.size();
//                        int end = start + 20;
//
//
//                        Log.d(TAG, "run: Load more Data : current_page+1 " + page+1 );
//                        loadMoreData(page + 1 );
////                        ((AdsAdapter) adapter).setLoaded();
////                        adapter.notifyDataSetChanged();
//                    }
//                },10);
//            }
//        };
//
//        ((AdsAdapter)adapter).setOnLoadMoreListener( onLoadMoreListener  );


        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener()
                {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                        getOffersFromWeb();
                    }
                }
        );
    }

    private void getOffersFromWeb() {
        Log.d(TAG, "getOffersFromWeb: called");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        final Handler handler = new Handler();

        if (networkInfo != null && networkInfo.isConnected()) {
            OffersUtils.fetchOffers(1, this, new OffersUtils.FetchOffersCallback() {
                @Override
                public void done(List<Offer> offers) {
                    Log.d(TAG, "done: OFFERS LOADED " + offers );
                    if (mSwipeRefreshLayout.isRefreshing()){
                        Log.d(TAG, "done: stop refreshing");
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    if (offers != null) {
                        Log.d(TAG, "done: current_page = 1 ");
                        page = 1;

                        OffersOldActivity.this.offers.clear();
                        OffersOldActivity.this.offers.addAll(offers);
                        ((OffersAdapter) adapter).setLoaded();

                        adapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void error(Exception e) {
                    Log.e(TAG, "error al obtener los Anuncios");
                    mSwipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                    //wait 5 secons to try again
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getOffersFromWeb();
                        }
                    }, 5000);
                }
            });
        } else {
            Toast.makeText(this, "No hay conexion a internet.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release session
        AppSession.release();
        // App is not running
        App.setIsAppRunning(false);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Check if user triggered a refresh:
            case R.id.menu_refresh:
                Log.i(TAG, "Refresh menu item selected");

                // Signal SwipeRefreshLayout to start the progress indicator
                mSwipeRefreshLayout.setRefreshing(true);

                // Start the refresh background task.
                // This method calls setRefreshing(false) when it's finished.
                getOffersFromWeb();
                return true;
//            case R.id.menu_order_distance:
//                sortAdsByDistance(offers);
//                return true;
//            case R.id.menu_force_update_gps:
//                mSwipeRefreshLayout.setRefreshing(true);
//                fetchAddressButtonHandler(null);
//                getOffersFromWeb();
//                return true;
        }
        // User didn't trigger a refresh, let the superclass handle this action
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public void onStart() {
        //mGoogleApiClient.connect();
        getOffersFromWeb();
        checkGoogleApiClient();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "gps onConnected() called with: " + "bundle = [" + bundle + "]");
        // Gets the best and most recent location currently available,
        // which may be null in rare cases when a location is not available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onConnected: No hay permisos para usar esto");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        AppSession.lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);





        //check for location every 5 seconds if there is not any
//        if (AppSession.lastLocation == null) {
//            Log.d(TAG, "gps onConnected: LOCATION NULL - NO AppSession.lastLocation ");
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    fetchAddressButtonHandler(null);
//                }
//            }, Constants.MINUTE * 5  );
//        } else {
//            Log.d(TAG, "onConnected: LOCATION GET !");
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "gps onConnectionSuspended() called with: " + "i = [" + i + "]");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "gps onConnectionFailed() called with: " + "connectionResult = [" + connectionResult + "]");
    }



    /**
     * Create an instance of GoogleAPIClient.
     */
    private void checkGoogleApiClient() {
        if (mGoogleApiClient == null) {
            Log.d(TAG, "onCreate: google api client");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (mGoogleApiClient == null) {
            Log.d(TAG, "gps checkGoogleApiClient: mGoogleApiClient: " + null);
        }

        if (!mGoogleApiClient.isConnected()) {

            try {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //ask for permission
                    Log.d(TAG, "checkGoogleApiClient: no hay permisos para ver la ubicacion del telefono");
                    ActivityCompat.requestPermissions(OffersOldActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_REQUEST);
                    //if dont get de connection anyway them set location by ZIPCODE
                    if ((AppSession.getCurrentUser() != null) && (AppSession.getCurrentUser().zipCode != null)){
                        Address address = Utils.getGPSfromZip(context, Integer.parseInt(AppSession.getCurrentUser().zipCode));
                        AppSession.lastLocation = new Location("");
                        AppSession.lastLocation.setLatitude(address.getLatitude());
                        AppSession.lastLocation.setLongitude(address.getLongitude());
                    }
                } else {

                    //if dont get conection get location from phone
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    /*
                    //check http://stackoverflow.com/questions/4745670/android-unable-to-get-the-gps-location-on-the-emulator
                    //get form network provider
                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.i(TAG, "OnLocation Changed triggered");
                            Toast msg = Toast.makeText(MainActivity.this, "Lon: " + Double.toString(location.getLongitude()) + " Lat: " + Double.toString(location.getLatitude()), Toast.LENGTH_SHORT);
                            msg.show();
                        }
                        public void onStatusChanged(String provider, int status, Bundle extras) {}

                        public void onProviderEnabled(String provider) {}

                        public void onProviderDisabled(String provider) {}
                    };

                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
                    */


                    if (location!=null){
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();

                        AppSession.lastLocation = new Location("");
                        AppSession.lastLocation.setLatitude(latitude);
                        AppSession.lastLocation.setLongitude(longitude);
                    }
                }


            }catch (Exception e ){
                e.printStackTrace();
            }

            mGoogleApiClient.connect();
        } else {
            //google me obliga a poner esto
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "checkGoogleApiClient: no hay permisos para ver la ubicacion del telefono");
                ActivityCompat.requestPermissions(OffersOldActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST);

                return;
            } else {
                AppSession.lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
        }



    }

    public void onRequestPermissionsResult(int requestCode, String[] permisions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }





    private void showToast(String string) {
        Toast.makeText(OffersOldActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    private void sortAdsByDistance(List<Ad> ads){

        Collections.sort(ads, new Comparator<Ad>() {
            public int compare(Ad o1, Ad o2) {
                if (o1.getLastDistance() == o2.getLastDistance())
                    return 0;
                return o1.getLastDistance() < o2.getLastDistance() ? -1 : 1;
            }
        });

        adapter.notifyDataSetChanged();
    }

//    private void removeExpiredAds(){
//        Log.d(TAG, "removeExpiredAds: ads size before: " + offers.size());
//
//        for (int i=0;i<offers.size();i++){
//            Date adExpiration= offers.get(i).getExpiration();
//            Date today =new Date();
//
//            if (adExpiration!=null  ){
//                Date adExpirationPlus1 = null;
//                Calendar c = Calendar.getInstance();
//                c.setTime(adExpiration);
//                c.add(Calendar.DAY_OF_MONTH, 1);
//                adExpirationPlus1 = c.getTime();
//
//                if (today.after(adExpirationPlus1)) {
//                    String strAdExpiration = Constants.DATE_LOCAL_FORMAT.format(adExpiration);
//                    String strAdExpirationPlus = Constants.DATE_LOCAL_FORMAT.format(adExpirationPlus1);
//                    Log.d(TAG, "removeExpiredAds: strAdExpiration " + offers.get(i).getTitle() + " " + strAdExpiration + " " + strAdExpirationPlus);
//
//                    offers.remove(i);
//                }
//            }
//        }
//        Log.d(TAG, "removeExpiredAds: ads size after: " + offers.size());
//
////        adapter.notifyDataSetChanged();
//    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void removeDeliveredAds(List<Ad> ads){
        //if status is delivered remove from list

        //do nothing


//        for(Iterator<Ad> i = ads.iterator(); i.hasNext(); ) {
//            Ad item = i.next();
//            if (item.getStatus() == Ad.Status.DELIVERED ) {
//                i.remove();
//                Log.d(TAG, "done: removido rollito");
//            }
//        }
    }

    @Override
    protected void onResume() {
        getOffersFromWeb();
        super.onResume();
    }

    private void showIntroSlides() {
        String valid_until = "20/2/2019";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",  new Locale("es","ES") );
        Date strDate = null;
        try {
            strDate = sdf.parse(valid_until);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // show if is First time or if is before date "valid_until"
        if (
                PrefsUtils.getBoolean(this, PrefsUtils.KEY_FIRST_TIME_OFFERS, true)
                || new Date().before(strDate)
        ) {
            // TODO: use better alternative for last android versions
                new IntroductionBuilder(this).withSlides(generateSlides())
                .introduceMyself();
        }

    }
    private List<Slide> generateSlides() {
        List<Slide> slides = new ArrayList<>();

        slides.add(new Slide().withTitle("Nueva sección ¡Ofertas!")
                .withDescription("¿Tu tienda ha rebajado el precio de algún alimento para evitar tirarlo antes de que caduque?")
                .withColorResource(R.color.primary).withImage(R.drawable.bottle));

        slides.add(new Slide().withTitle("Comparte")
                .withDescription("¿Quieres compartir esta oferta con la red Yonodesperdicio?")
                .withColorResource(R.color.green_500).withImage(R.drawable.zanahoria));

        slides.add(new Slide().withTitle("Recuerda")
                .withDescription("Solo descuentos por próxima caducidad")
                .withColorResource(R.color.indigo_500).withImage(R.drawable.apple));

        // First time set to false
        PrefsUtils.commit(this, PrefsUtils.KEY_FIRST_TIME_OFFERS, false);

        return slides;
    }



}
