package com.imaginabit.yonodesperdicion.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.App;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.AdsAdapter;
import com.imaginabit.yonodesperdicion.data.AdsContract;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.utils.AdUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavoritesActivity extends NavigationBaseActivity{
    private static final String TAG = "FavoritesActivity";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Ad> mAds;
    private List<Integer> adIds = new ArrayList<>();
    ContentResolver contentResolver;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_favorites);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.appContext = context;
        mAds = new ArrayList<Ad>();
        // Fix action bar and drawer
        Toolbar toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);

        final Activity mainActivity = this;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_ads);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AdsAdapter(context, mAds);
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);


        //Get ads id from database

        contentResolver = getContentResolver();

        String[] projection = new String[]{BaseColumns._ID, AdsContract.FavoritesColumns.FAV_AD_ID};

        Cursor cursor = contentResolver.query(AdsContract.URI_TABLE_FAVORITES, projection, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String ad_strid = cursor.getString(1);
                int ad_id = Integer.parseInt(ad_strid);
                Log.d(TAG, "Cursor recorriendo: " + ad_strid );

                adIds.add(ad_id);
            } while (cursor.moveToNext());
        }

        //Get Ads info
        Log.d(TAG, "onCreate: adIds "+ adIds.toString());

        getAdsFromWeb(adIds);

    }

    private void getAdsFromWeb(List<Integer> ads) {
        Log.d(TAG, "get Ads From Web");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        mAds = new ArrayList<Ad>();

        for(final int id : ads ) {
            if (networkInfo != null && networkInfo.isConnected()) {
                AdUtils.fetchAd(id, new AdUtils.FetchAdCallback() {
                        @Override
                        public void done(Ad ad, User user, Exception e) {
                            if (e == null) {
                                if (ad != null) {
                                    Log.d(TAG, "done: ad = " + ad.toString());

                                    mAds.add(ad);
                                    ((AdsAdapter) adapter).setData(mAds);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.d(TAG, "done: Error al obtener anuncio " + Integer.toString(id));
                            }
                        }
                    }
                );
            } else {
                Toast.makeText(this, "No hay conexion a internet.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.favorites, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Check if user triggered a refresh:
            case R.id.menu_order_distance:
                sortAdsByDistance(mAds);
                return true;
            case R.id.menu_delete_all_favorites:
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(FavoritesActivity.this, R.style.AppTheme_Dialog));
                builder.setTitle(getString(R.string.menu_delete_all_favorites))
                        .setMessage(getString(R.string.delete_all_favorites_message))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                contentResolver.delete(AdsContract.URI_TABLE_FAVORITES, null, null);
                                ((AdsAdapter) adapter).setData(new ArrayList<Ad>());
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
        }
        // User didn't trigger a refresh, let the superclass handle this action
        return super.onOptionsItemSelected(item);
    }

    private void sortAdsByDistance(List<Ad> ads){

        Collections.sort(ads, new Comparator<Ad>() {
            public int compare(Ad o1, Ad o2) {
                if (o1.getLastDistance() == o2.getLastDistance())
                    return 0;
                return o1.getLastDistance() < o2.getLastDistance() ? -1 : 1;
            }
        });

        ((AdsAdapter)adapter).setData(ads);
        adapter.notifyDataSetChanged();
    }




}
