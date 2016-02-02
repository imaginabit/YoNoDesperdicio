package com.imaginabit.yonodesperdicion.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.AdsAdapter;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.utils.AdUtils;
import com.imaginabit.yonodesperdicion.utils.ProvinciasCP;
import com.imaginabit.yonodesperdicion.utils.UserUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.imaginabit.yonodesperdicion.views.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends NavigationBaseActivity {

    private final String TAG = getClass().getSimpleName();

    private UserData mUser;
    private TextView userName;
    private TextView location;
    private TextView weight;
    private RatingBar rating;
    private LinearLayout userads;
    private RoundedImageView avatarView;
    private Drawable avatar;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Ad> mAds;
    private User mUserWeb;
    private NestedScrollView mainscroll;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);

        mAds = new ArrayList<>();

        mainscroll = (NestedScrollView) findViewById(R.id.main_scroll);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_userads);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        userads = (LinearLayout) findViewById(R.id.user_ads);

        //recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdsAdapter(context, mAds);
        recyclerView.setAdapter(adapter);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (AppSession.getCurrentUser() != null) {
            VolleySingleton.init(context);

            mUser = AppSession.getCurrentUser();

            userName = (TextView) findViewById(R.id.user_name);
            location = (TextView) findViewById(R.id.location);
            weight = (TextView) findViewById(R.id.kilos);
            rating = (RatingBar) findViewById(R.id.ad_reputacion);
            userads = (LinearLayout) findViewById(R.id.user_ads);
            avatarView = (RoundedImageView) findViewById(R.id.avatarpic);

            userName.setText(mUser.username);
            location.setText(mUser.city);

            weight.setText("Entregados " + Integer.toString(mUser.totalQuantity) + " Kg");

            rating.setRating(mUser.rating);
            getUserWeb();

            getAdsFromWeb((int) mUser.id);
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
        switch (id){
            case R.id.empty:
//                Toast.makeText(ProfileActivity.this, "pulsado ", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onOptionsItemSelected: pulsado menu");
                mainscroll.fullScroll(ScrollView.FOCUS_UP);
                expandToolbar();
                return true;
            case R.id.edit_avatarpic:
                Log.d(TAG, "onOptionsItemSelected: edit avatar pic");
                return true;
            case R.id.edit_name:
                Log.d(TAG, "onOptionsItemSelected: edit name");
                return true;
            case R.id.edit_location:
                Log.d(TAG, "onOptionsItemSelected: edit location");
                return true;                        
        }            

        return super.onOptionsItemSelected(item);
    }

    private void getAdsFromWeb(final int userId) {
        User u = new User(userId,"","","","",0,0);
        Log.d(TAG, "get Ads From Web");

        AdUtils.fetchAdsVolley(u, this, new AdUtils.FetchAdsCallback() {
            @Override
            public void done(List<Ad> ads, Exception e) {
                Log.d(TAG, "done");
                if (ads != null) {
                    mAds = ads;
                    adapter = new AdsAdapter(context, mAds);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
//                    Log.d(TAG, "done: recyclerview height " + recyclerView.getHeight());
//                    Log.d(TAG, "done: layoutManager height " + layoutManager.getHeight());

                    //
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    float adDpInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135, dm);
                    float headDpInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm);
                    int adsTotalHeight = (int) ((int) (ads.size() * adDpInPx) + headDpInPx);
                    Log.d(TAG, "done: Height px :" + adsTotalHeight);
                    userads.setLayoutParams(new LinearLayout.LayoutParams(layoutManager.getWidth(), adsTotalHeight));

                    Log.d(TAG, "anuncios : " + mAds.size());
                }
            }
        });

    }

    private void getUserWeb(){
        Log.d(TAG, "getUserWeb start");
        int userId = (int)mUser.id;
        Log.d(TAG, "getUserWeb: UserId " + userId);

        UserUtils.getUser(userId, ProfileActivity.this, new UserUtils.FetchUserCallback() {
            @Override
            public void done(User user, Exception e) {
                Log.d(TAG, "getUserWeb UserUtils.getUser->done() called with: " + "user = [" + user + "], e = [" + e + "]");
                if ( e != null ) e.printStackTrace();
                mUserWeb = user;
                String cp = mUserWeb.getZipCode();
                ProvinciasCP.init();
                String provincia = ProvinciasCP.getNameFromCP(cp);
                weight.setText(getString(R.string.entregados) + Utils.gramsToKgStr(mUserWeb.getGrams()));
                rating.setRating(mUserWeb.getRatting());


                //get image from website
                ImageLoader imageLoader; // Get singleton instance
                imageLoader = ImageLoader.getInstance();
                String imageUri = Constants.HOME_URL + mUserWeb.getAvatar();
                ImageSize targetSize = new ImageSize(200, 200); // result Bitmap will be fit to this size

                if ( !(imageUri.contains("/propias/")) ) {
                    imageLoader.displayImage(imageUri, avatarView);
                }else {
                    avatarView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.brick));
                }


                location.setText(cp + ", " + provincia);
//                location.setVisibility(View.VISIBLE);
            }
        });

    }

    /**
     *  http://stackoverflow.com/a/30747281/385437
     */
    public void expandToolbar(){
        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.appbar);
        CoordinatorLayout rootLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if(behavior!=null) {
            behavior.setTopAndBottomOffset(0);

            behavior.onNestedPreScroll(rootLayout, appbarLayout, null, 0, 1, new int[2]);
        }
    }


}
