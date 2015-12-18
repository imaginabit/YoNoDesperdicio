package com.imaginabit.yonodesperdicion.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.data.UserData;

public class ProfileActivity extends NavigationBaseActivity {

    UserData mUser;
    TextView userName;
    TextView location;
    TextView weight;
    RatingBar rating;
    LinearLayout userads;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);

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
            /*
            if (mUser.totalQuantity == 0 && mUser.rating == 0){

            }*/

            userads.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate( R.menu.profile , menu);
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
}
