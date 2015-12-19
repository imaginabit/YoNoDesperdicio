package com.imaginabit.yonodesperdicion.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.AdsAdapter;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.utils.AdUtils;

import java.util.List;

public class ProfileActivity extends NavigationBaseActivity {

    private final String TAG = getClass().getSimpleName();

    private UserData mUser;
    private TextView userName;
    private TextView location;
    private TextView weight;
    private RatingBar rating;
    private LinearLayout userads;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Ad> mAds;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_ads);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AdsAdapter(context, mAds);
        recyclerView.setAdapter(adapter);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (AppSession.getCurrentUser() != null) {
            mUser = AppSession.getCurrentUser();


            userName = (TextView) findViewById(R.id.user_name);
            location = (TextView) findViewById(R.id.location);
            weight = (TextView) findViewById(R.id.kilos);
            rating = (RatingBar) findViewById(R.id.ad_reputacion);
            userads = (LinearLayout) findViewById(R.id.user_ads);

            userName.setText(mUser.username);
            location.setText(mUser.city);

            weight.setText("Entregados " + Integer.toString(mUser.totalQuantity) + " Kg");

            rating.setRating(mUser.rating);

            //userads.setVisibility(View.GONE);
            getAdsFromWeb( (int)mUser.id );

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            Toast.makeText(ProfileActivity.this, "pulsado ", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAdsFromWeb(final int userId) {

        User u = new User(userId,"","","","",0,0);
        Log.d(TAG, "get Ads From Web");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        final Handler handler = new Handler();

        if (networkInfo != null && networkInfo.isConnected()) {
            AdUtils.fetchAds(u, new AdUtils.FetchAdsCallback() {
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
                                getAdsFromWeb(userId);
                            }
                        }, 5000);
                    }
                }
            });
        } else {
            Toast.makeText(this, "No se pudieron descargar los anuncios, no hay conexion a internet.", Toast.LENGTH_SHORT).show();
        }
    }


}
