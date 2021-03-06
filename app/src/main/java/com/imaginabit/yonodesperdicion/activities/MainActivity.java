package com.imaginabit.yonodesperdicion.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.imaginabit.yonodesperdicion.App;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.AdsAdapter;
import com.imaginabit.yonodesperdicion.helpers.FetchAddressIntentService;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.utils.AdUtils;
import com.imaginabit.yonodesperdicion.utils.OnLoadMoreListener;
import com.imaginabit.yonodesperdicion.utils.PrefsUtils;
import com.imaginabit.yonodesperdicion.utils.ProvinciasCP;
import com.imaginabit.yonodesperdicion.utils.UserUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.rubengees.introduction.IntroductionBuilder;
import com.rubengees.introduction.entity.Slide;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.imaginabit.yonodesperdicion.utils.AdUtils.calculateLocation;

//import android.support.v4.os.ResultReceiver;

//import com.imaginabit.yonodesperdicion.gcm.RegistrationIntentService;

public class MainActivity extends NavigationBaseActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private final String TAG = getClass().getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Ad> mAds;

    protected Handler handler;


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GoogleApiClient mGoogleApiClient;

//    private AddressResultReceiver mResultReceiver;
    private AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());
    private boolean mAddressRequested;
    private String mAddressOutput;

    private int page;
//    private int mPreLast;
//    private int mSrcollY;

    protected static final int LOCATION_REQUEST = 0;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.appContext = context;//for make getGPSfromZip works

        //init Singleton instances
        VolleySingleton.init(this);
        ProvinciasCP.init();
        // Initialize Universal Image Loader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.zanahoria) // resource or drawable
                .showImageOnFail(R.drawable.aubergine) // resource or drawable
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .threadPoolSize(4)
                .memoryCache(new WeakMemoryCache())
                .imageDownloader(new BaseImageDownloader(context, 10 * 1000, 30 * 1000))
                .build();
        ImageLoader.getInstance().init(config);


        // Put on session
        UserUtils.putUserSessionOn(this);

        mAds = new ArrayList<>();

        // Fix action bar and drawer
        Toolbar toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);

        final Activity mainActivity = this;

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.checkLoginAndRedirect(mainActivity)) {
                    Intent intent = new Intent(context, AdCreateActivity.class);
                    startActivity(intent);
                }
            }
        });

        showIntroSlides();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_ads);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        Log.d(TAG, "onCreate: ahora initialize data:");
//        initializeData();

        adapter = new AdsAdapter(context, mAds, recyclerView);
        recyclerView.setAdapter(adapter);

//        if (mAds.isEmpty()){
//            recyclerView.setVisibility(View.GONE);
//            // empty view visible
//        } else {
//            recyclerView.setVisibility(View.VISIBLE);
//            // empty view visible
//        }


        //comment to fix
//        final EndlessRecyclerOnScrollListener scrollListener = new EndlessRecyclerOnScrollListener((LinearLayoutManager) layoutManager) {
//            @Override
//            public void onLoadMore(int current_page, int current_scroll) {
//                Log.d(TAG, "onLoadMore() called with: " + "current_page = [" + current_page + "], current_scroll = [" + current_scroll + "]");
//                mSrcollY = current_scroll;
//                Log.d(TAG, "onLoadMore: mSrcoll nau: " + mSrcollY);
//                //page 1 is the same as page 0
//                loadMoreData(current_page+1);
//            }
//        };


        //Get Ads
        getAdsFromWeb();

        //infinite scroll!
        handler = new Handler();

        OnLoadMoreListener onLoadMoreListener =  new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore: called");
                //add null , so the adapter will check view_type and show progress bar at bottom
                mAds.add(null);
                adapter.notifyItemInserted(mAds.size()-1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //remove progress item
                        mAds.remove(mAds.size()-1);
                        adapter.notifyItemRemoved(mAds.size());
                        Log.d(TAG, "run: remove progress bar ");

                        //add items
                        int start = mAds.size();
                        int end = start + 20;


                        Log.d(TAG, "run: Load more Data : current_page+1 " + page+1 );
                        loadMoreData(page + 1 );
//                        ((AdsAdapter) adapter).setLoaded();
//                        adapter.notifyDataSetChanged();
                    }
                },10);
            }
        };

        ((AdsAdapter)adapter).setOnLoadMoreListener( onLoadMoreListener  );


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
                        //scrollListener.setCurrent_page(1);

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        getAdsFromWeb();

                        //layoutManager.scrollToPosition(300);
                        // scrollListener.setCurrent_page(1);
                    }
                }
        );

        //Get Last Location
        // Create an instance of GoogleAPIClient.
        checkGoogleApiClient();

//        if (checkPlayServices()) {
//            // Start IntentService to register this application with GCM.
//            Intent intent = new Intent(this, RegistrationIntentService.class);
//            startService(intent);
//        }


        SharedPreferences sharedPref =  this.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_fbt), "");
        editor.commit();

        String mToken = FirebaseInstanceId.getInstance().getToken();

        if (AppSession.getCurrentUser()!= null ) {
            saveToken(mToken);
            Log.d(TAG, "onCreate: token : " + mToken);
        } else {
            saveToken("");
        }

    }

    private void showIntroSlides() {
        // First time?
        if (PrefsUtils.getBoolean(this, PrefsUtils.KEY_FIRST_TIME, true)) {
            if (Build.VERSION.SDK_INT <= 12) {
                Log.v(TAG, "--- SDK_INT <= 12 ---");
            } else {
                Log.v(TAG, "--- SDK_INT > 12 ---");
            }

            // TODO: use better alternative for last android versions
            new IntroductionBuilder(this).withSlides(generateSlides())
                    .introduceMyself();
        }
    }

    /*
    comprueba que el token esta guardado en las preferencias
    */


    private void saveToken(String token){
        Log.d(TAG, "saveToken: called");
        SharedPreferences sharedPref =  this.getPreferences(Context.MODE_PRIVATE);

        String defaultValue_fbt = "";
        String saved_fbt = sharedPref.getString(getString(R.string.preference_fbt), defaultValue_fbt);
        Log.d(TAG, "saveToken: token " + token);
        Log.d(TAG, "saveToken: saved_fbt " + saved_fbt);
        if ( ! saved_fbt.equals(token) ) {
            Log.d(TAG, "saveToken: not equal, sending");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.preference_fbt), token);
            editor.commit();

            //envia el token al server
            UserUtils.sendTokenToServer( AppSession.getCurrentUser(), token);

        }
    }


    private void loadMoreData(final int current_page) {
        Log.d(TAG, "loadMoreData() called with: " + "current_page = [" + current_page + "]");
        AdUtils.fetchAds(current_page, this, new AdUtils.FetchAdsCallback() {
            @Override
            public void done(List<Ad> ads, Exception e) {
                if (e == null) {
                    if (ads != null) {

                        Log.d(TAG, "loadMoreData_done() called with: " + "ads = [" + ads + "], e = [" + e + "]");
//                        removeDeliveredAds(ads);
                        mAds.addAll(ads);
                        page = current_page;
                        ((AdsAdapter) adapter).setLoaded();
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "done: current scroll " + recyclerView.getScrollY());

//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                            recyclerView.setScrollY(mSrcollY);
//                        }
                        //recyclerView.scrollToPosition((current_page*10)+1);
//                        recyclerView.scrollTo(recyclerView.getScrollX(), mSrcollY);
                        //recyclerView.scrollToPosition(mSrcollY);

                        Log.d(TAG, "done: LoadMore current scroll after scrollto " + recyclerView.getScrollY());
                        Log.d(TAG, "LoadMore Anuncios general : " + mAds.size());
                    } else {
                        Log.d(TAG, "done: current scroll, ads==null " );
                    }
                } else {
                    Log.e(TAG, "error al obtener los Anuncios");
                    e.printStackTrace();
                }
            }
        });

    }

    private void initializeData() {
        Log.d(TAG, "initializeData: start");

        mAds.clear();
        // String title, String id, String category, String image_url, String introduction
        //Ad(String title, String body, String imageUrl, int weightGrams, String expiration, String postalCode, int status, int userId, String userName)

        try {
            mAds.add(new Ad("title", "body", "String imageUrl", 100, "2010-10-23", "3241234", 1, 10, "uaoeu"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
            mAds.add(new Ad("title", "body", "String imageUrl", 100, "2010-10-23", "3241234", 1, 10, "uaoeu"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
            mAds.add(new Ad("title", "body", "String imageUrl", 100, "2010-10-23", "3241234", 1, 10, "uaoeu"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
            mAds.add(new Ad("title", "body", "String imageUrl", 100, "2010-10-23", "3241234", 1, 10, "uaoeu"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
            mAds.add(new Ad("title", "body", "String imageUrl", 100, "2010-10-23", "3241234", 1, 10, "uaoeu"));
            mAds.add(new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "ideas : " + mAds.size());
    }

    /**
     * Generates first time slides
     */
    private List<Slide> generateSlides() {
        List<Slide> slides = new ArrayList<>();

        slides.add(new Slide().withTitle("¡Hola!")
                .withDescription("¿Tienes comida de sobra?\nNo la desperdicies")
                .withColorResource(R.color.primary).withImage(R.drawable.aubergine));

        slides.add(new Slide().withTitle("Comparte")
                .withDescription("Ofrece tu comida extra de forma rápida y sencilla")
                .withColorResource(R.color.green_500).withImage(R.drawable.zanahoria));

        slides.add(new Slide().withTitle("Busca")
                .withDescription("Localiza los alimentos que necesitas y recógelos")
                .withColorResource(R.color.cyan_500).withImage(R.drawable.bottle));

        slides.add(new Slide().withTitle("Conoce")
                .withDescription("Con Yonodesperdicio conocerás a personas como tú")
                .withColorResource(R.color.indigo_500).withImage(R.drawable.apple));

        slides.add(new Slide().withTitle("Comienza ahora")
                .withDescription("Forma parte de la red y colabora en la reducción del desperdicio de alimentos")
                .withColorResource(R.color.light_blue_500).withImage(R.drawable.brick));

        // First time set to false
        PrefsUtils.commit(this, PrefsUtils.KEY_FIRST_TIME, false);

        return slides;
    }


    private void getAdsFromWeb() {
        Log.d(TAG, "get Ads From Web");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        final Handler handler = new Handler();

        if (networkInfo != null && networkInfo.isConnected()) {
            AdUtils.fetchAds(1,this, new AdUtils.FetchAdsCallback() {
                @Override
                public void done(List<Ad> ads, Exception e) {
                    if (e == null) {
                        Log.v(TAG, "---Ads get!");

                        if (mSwipeRefreshLayout.isRefreshing()){
                            Log.d(TAG, "done: stop refreshing");
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        if (ads != null) {
                            Log.d(TAG, "done: current_page = 1 ");
                            page = 1;
//                            removeDeliveredAds(ads);

                            for (int i = 0; i < ads.size(); i++) {
                                Ad ad = ads.get(i);
                                ad.setLocation(calculateLocation(ad, (AdsAdapter) adapter,i));
                            }

                            mAds.clear();
                            mAds.addAll(ads);
                            ((AdsAdapter)adapter).setLoaded();

                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Anuncios general : " + mAds.size());
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    adapter.notifyDataSetChanged();
//                                }
//                            }, 1000);
                        }
                    } else {
                        Log.e(TAG, "error al obtener los Anuncios");
                        mSwipeRefreshLayout.setRefreshing(false);
                        e.printStackTrace();
                        //wait 5 secons to try again
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getAdsFromWeb();
                            }
                        }, 5000);
                    }
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
                getAdsFromWeb();
                return true;
            case R.id.menu_order_distance:
                sortAdsByDistance(mAds);
                return true;
            case R.id.menu_force_update_gps:
                mSwipeRefreshLayout.setRefreshing(true);
                fetchAddressButtonHandler(null);
                getAdsFromWeb();
                return true;
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

        // Determine whether a Geocoder is available.
        if (!Geocoder.isPresent()) {
            startIntentService();
            Toast.makeText(this, R.string.no_geocoder_available,
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (mAddressRequested) {
            Log.d(TAG, "onConnected: maddrest requested");
            startIntentService();
        }

        //check for location every 5 seconds if there is not any
        if (AppSession.lastLocation == null) {
            Log.d(TAG, "gps onConnected: LOCATION NULL - NO AppSession.lastLocation ");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchAddressButtonHandler(null);
                }
            }, Constants.MINUTE * 5  );
        } else {
            Log.d(TAG, "onConnected: LOCATION GET !");
        }
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
     * Start service for get gps data and address geocoder
     */
    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, AppSession.lastLocation);
        startService(intent);
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
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
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
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
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

    private void fetchAddressButtonHandler(View view) {
        String[] permis = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};

        ActivityCompat.requestPermissions(this,permis, Constants.PERMISSION_REQUEST_ACCESS_COARSE_LOCATION);

        checkGoogleApiClient();

        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        if (mGoogleApiClient.isConnected() && AppSession.lastLocation != null) {
            Log.d(TAG, "gps fetchAddressButtonHandler: start intent service");
            startIntentService();
        } else {
            if(!mGoogleApiClient.isConnected()) {
                Log.d(TAG, "gps fetchAddressButtonHandler: not connected!");
                checkGoogleApiClient();
//                mGoogleApiClient.connect();
            }
            if (AppSession.lastLocation== null) {
                Log.d(TAG, "gps fetchAddressButtonHandler: null lastLocation");
                mGoogleApiClient.disconnect();
//                mGoogleApiClient.connect();
                checkGoogleApiClient();
            }
        }

        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;

    }

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        private static final String TAG = "AddressResultReceiver";

        public AddressResultReceiver(Handler handler) {
            super(handler);
//            Log.d(TAG, "AddressResultReceiver() called with: " + "handler = [" + handler + "]");
        }


        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d(TAG, "onReceiveResult() called with: " + "resultCode = [" + resultCode + "], resultData = [" + resultData + "]");

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            Log.d(TAG, "onReceiveResult: Result: " + resultCode);

//            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
//                showToast(mAddressOutput);
                Log.d(TAG, "onReceiveResult: "+ getString(R.string.address_found) +" "+ mAddressOutput);
            }

        }
    }

    private void showToast(String string) {
        Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
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
//        Log.d(TAG, "removeExpiredAds: ads size before: " + mAds.size());
//
//        for (int i=0;i<mAds.size();i++){
//            Date adExpiration= mAds.get(i).getExpiration();
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
//                    Log.d(TAG, "removeExpiredAds: strAdExpiration " + mAds.get(i).getTitle() + " " + strAdExpiration + " " + strAdExpirationPlus);
//
//                    mAds.remove(i);
//                }
//            }
//        }
//        Log.d(TAG, "removeExpiredAds: ads size after: " + mAds.size());
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

//    private void removeDeliveredAds(List<Ad> ads){
//        //if status is delivered remove from list
//
//        //do nothing
//
//
////        for(Iterator<Ad> i = ads.iterator(); i.hasNext(); ) {
////            Ad item = i.next();
////            if (item.getStatus() == Ad.Status.DELIVERED ) {
////                i.remove();
////                Log.d(TAG, "done: removido rollito");
////            }
////        }
//    }

}
