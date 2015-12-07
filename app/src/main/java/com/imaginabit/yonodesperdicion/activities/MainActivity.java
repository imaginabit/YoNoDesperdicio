package com.imaginabit.yonodesperdicion.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.AdsAdapter;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.utils.AdUtils;
import com.imaginabit.yonodesperdicion.utils.PrefsUtils;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.rubengees.introduction.IntroductionBuilder;
import com.rubengees.introduction.entity.Slide;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends NavigationBaseActivity {
    private final String TAG = getClass().getSimpleName();

    private RecyclerView recyclerView;
    //private RecyclerView mRecyclerViewIdeas;
    private RecyclerView.Adapter adapter;
    //private RecyclerView.Adapter mAdapterIdeas;
    private RecyclerView.LayoutManager layoutManager;
    //private RecyclerView.LayoutManager mLayoutManagerIdeas;

    private List<Ad> mAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fix action bar and drawer
        Toolbar toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);

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

        // First time?
        if (PrefsUtils.getBoolean(this, PrefsUtils.KEY_FIRST_TIME, true)) {
            if (android.os.Build.VERSION.SDK_INT <= 12) {
                Log.v(TAG,"--- SDK_INT <= 12 ---");
            } else {
                Log.v(TAG, "--- SDK_INT > 12 ---");
            }

            // TODO: use better alternative for last android versions
            new IntroductionBuilder(this).withSlides(generateSlides())
                                         .introduceMyself();
        }

        // Initialize Universal Image Loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                                                                      .threadPoolSize(4)
                                                                      .memoryCache(new WeakMemoryCache())
                                                                      .imageDownloader(new BaseImageDownloader(context, 10 * 1000, 30 * 1000))
                                                                      .build();
        ImageLoader.getInstance().init(config);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_ads);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        Log.d(TAG, "onCreate: ahora initialize data:");
//        initializeData();

        adapter = new AdsAdapter(context, mAds);
        recyclerView.setAdapter(adapter);

        //Get Ads
        getAdsFromWeb();

//        mRecyclerViewIdeas = (RecyclerView) findViewById(R.id.recycler_ideas);
//        mRecyclerViewIdeas.setHasFixedSize(true);
//        mLayoutManagerIdeas = new LinearLayoutManager(this);
//        mRecyclerViewIdeas.setLayoutManager(mLayoutManagerIdeas);
//        ArrayList<Idea> ideas = IdeaUtils.sampleData();
//        mAdapterIdeas = new IdeasAdapter(ideas);
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

//        mAdapter = new AdsAdapter(mContext, mAds);
//        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();

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
            AdUtils.fetchAds(this, new AdUtils.FetchAdsCallback() {
                @Override
                public void done(List<Ad> ads, Exception e) {
                    if (e == null) {
                        Log.v(TAG, "---Ads get!");
                        if (ads != null) {
                            mAds = ads;
                            adapter = new AdsAdapter(context, mAds);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
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
