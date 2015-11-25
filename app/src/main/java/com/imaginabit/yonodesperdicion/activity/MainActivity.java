package com.imaginabit.yonodesperdicion.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.Base;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapter.AdAdapter;
import com.imaginabit.yonodesperdicion.model.Ad;
import com.imaginabit.yonodesperdicion.util.AdUtils;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.rubengees.introduction.IntroductionBuilder;
import com.rubengees.introduction.entity.Slide;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Base
{
    private final String TAG = this.getClass().getSimpleName();
    private static final String PREFS_NAME = "YoNoDesperdicioPrefs";

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewIdeas;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mAdapterIdeas;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mLayoutManagerIdeas;

    private List<Ad> mAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getApplicationContext();

        //Drawable add_pic = ContextCompat.getDrawable(mContext, R.drawable.ic_add_black);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setImageDrawable(add_pic);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Nuevo Anuncio", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean firstTime = settings.getBoolean("firstTime", true);
        if(firstTime) {
            if (android.os.Build.VERSION.SDK_INT <= 12) {
                new IntroductionBuilder(this).withSlides(generateSlides()).introduceMyself();
                Log.v(TAG," SDK_INT less than 12 ");
            } else {
                Log.v(TAG," SDK_INT greater than 12 ");
                //TODO: use better alternative for last android versions
                new IntroductionBuilder(this).withSlides(generateSlides()).introduceMyself();
            }
        }

        //Initialize Universal Image Loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .threadPoolSize(4)
                .memoryCache(new WeakMemoryCache())
                .imageDownloader(new BaseImageDownloader(mContext,10 * 1000, 30 * 1000))
                .build();
        ImageLoader.getInstance().init(config);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_ads);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        Log.d(TAG, "onCreate: ahora initialize data:");
//        initializeData();

        mAdapter = new AdAdapter(mContext, mAds);
        mRecyclerView.setAdapter(mAdapter);

        //Get Ads
        getAdsFromWeb();

//        mRecyclerViewIdeas = (RecyclerView) findViewById(R.id.recycler_ideas);
//        mRecyclerViewIdeas.setHasFixedSize(true);
//        mLayoutManagerIdeas = new LinearLayoutManager(this);
//        mRecyclerViewIdeas.setLayoutManager(mLayoutManagerIdeas);
//        ArrayList<Idea> ideas = IdeaUtils.sampleData();
//        mAdapterIdeas = new IdeaAdapter(ideas);
//        mRecyclerViewIdeas.setAdapter(mAdapterIdeas);

//        mAdapter.notifyDataSetChanged();
//        mAdapterIdeas.notifyDataSetChanged();

    }

    private void initializeData() {
        Log.d(TAG, "initializeData: start");

        mAds = new ArrayList<>();
        // String title, String id, String category, String image_url, String introduction
        //Ad(String title, String body, String imageUrl, int weightGrams, String expiration, String postalCode, int status, int userId, String userName)

        try {
            mAds.add( new Ad("title", "body", "String imageUrl", 100, "2010-10-23", "3241234", 1, 10, "uaoeu"));
            mAds.add( new Ad("tomate", "asoneuhaoete", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg", 100, "2000-10-15", "28080", 2, 1, "pepito" ) );
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        mAdapter = new AdAdapter(mContext, mAds);
//        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();

        Log.d(TAG, "ideas : " + mAds.size());
    }

    private List<Slide> generateSlides() {
        List<Slide> result = new ArrayList<>();

        result.add(new Slide().withTitle("¡Hola!")
                .withDescription("¿Tienes comida de sobra?\nNo la desperdicies")
                .withColorResource(R.color.primary).withImage(R.drawable.aubergine));
        result.add(new Slide().withTitle("Comparte")
                .withDescription("Ofrece tu comida extra de forma rápida y sencilla")
                .withColorResource(R.color.green_500).withImage(R.drawable.zanahoria));
        result.add(new Slide().withTitle("Busca").withDescription("Localiza los alimentos que necesitas y recógelos")
                .withColorResource(R.color.cyan_500).withImage(R.drawable.bottle));
        result.add(new Slide().withTitle("Conoce").withDescription("Con Yonodesperdicio conocerás a personas como tú")
                .withColorResource(R.color.indigo_500).withImage(R.drawable.apple));
        result.add(new Slide().withTitle("Comienza ahora")
                .withDescription("Forma parte de la red y colabora en la reducción del desperdicio de alimentos")
                .withColorResource(R.color.light_blue_500).withImage(R.drawable.brick));

        //set first_time false and dont show this slides again
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("firstTime", false);
        editor.commit();

        return result;
    }


    private void getAdsFromWeb() {
        Log.d(TAG, "get Ads From Web");
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        final Handler handler = new Handler();

        if (networkInfo != null && networkInfo.isConnected()) {
            AdUtils.fetchAds(this, new AdUtils.FetchAdsCallback() {
                @Override
                public void done(List<Ad> ads, Exception e) {
                    if (e == null) {
                        Log.v(TAG, "---Ads get!");
                        if (ads != null) {
                            mAds = ads;
                            mAdapter = new AdAdapter(mContext, mAds);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            Log.e(TAG, "ideas : " + mAds.size());
                        }

                    } else {
                        Log.e(TAG, "error al obtener los Anuncios");
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





}
