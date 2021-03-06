package com.imaginabit.yonodesperdicion.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.App;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.utils.Utils;

import java.io.File;


public abstract class NavigationBaseActivity extends AppCompatActivity
                                             implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "NavigationBaseActivity";
    public static Context context;
    private ImageView navUserImage;
    private boolean isAvatarFromLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the context
        context = getApplicationContext();
        App.appContext = context;
        isAvatarFromLocal=false;
    }

    /**
     * Fix the supported action bar (polymorphic)
     */
    public Toolbar setSupportedActionBar() {
        return setSupportedActionBar(0);
    }

    public Toolbar setSupportedActionBar(int navIconRid) {
        Toolbar toolbar = findViewById(R.id.toolbar);
//        Toolbar toolbarCollapsing = (Toolbar) findViewById(R.id.collapsing_toolbar);

        // Set the navigation icon if passed
        if (navIconRid > 0) {
            toolbar.setNavigationIcon(navIconRid);
//            toolbarCollapsing.setNavigationIcon(navIconRid);
        }
        setSupportActionBar(toolbar);
//        setSupportActionBar(toolbarCollapsing);

        return toolbar;
    }

    /**
     * Fix the Drawer Layout to activity
     */
    public DrawerLayout setDrawerLayout(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Current user data
        View headerNavView = navigationView.getHeaderView(0);
        navUserImage = headerNavView.findViewById(R.id.nav_header_user_image);
        TextView navUserFullname = headerNavView.findViewById(R.id.nav_header_user_fullname);

        // User login panel
        LinearLayout navHeaderLayout = headerNavView.findViewById(R.id.nav_header_layout);
        UserData user = AppSession.getCurrentUser();
        if (user == null) {
            navUserFullname.setText("Inicia sesión");
        } else {
            navUserFullname.setText(Utils.isEmptyOrNull(user.fullname) ? user.username : user.fullname);
        }

        // Access to login panel
        navHeaderLayout.setClickable(true);
        navHeaderLayout.setBackgroundResource(R.drawable.selectable_item_background);

        navHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppSession.getCurrentUser() == null) {
                    Intent loginPanelIntent = new Intent(context, LoginPanelActivity.class);
                    startActivity(loginPanelIntent);
                } else {
                    Intent itntPerfil = new Intent(context, ProfileActivity.class);
                    startActivity(itntPerfil);
                }
            }
        });

        setAvatarFromLocal();

        return drawer;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
//        getMenuInflater().inflate(R.menu.activity_main_drawer,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_anuncios) {
//            CharSequence text = "NavigationBaseActivity act!";
//            int duration = Toast.LENGTH_SHORT;
//            Toast toast = Toast.makeText(mContext, text, duration);
//            toast.show();
            Intent itntMain = new Intent(context, MainActivity.class);
            itntMain.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(itntMain);
        }

        else if (id == R.id.nav_offers) {
            Intent itntOffers = new Intent(context, OffersOldActivity.class);
//            itntOffers.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(itntOffers);
        }

        else if (id == R.id.nav_perfil) {
            if (Utils.checkLoginAndRedirect(this)){
                Intent itntPerfil = new Intent(context, ProfileActivity.class);
                startActivity(itntPerfil);
            }
        }
        else if (id == R.id.nav_favoritos) {
            Intent itntFav = new Intent(context, FavoritesActivity.class);
            startActivity(itntFav);
        }
        else if (id == R.id.nav_mensajes) {
            // TODO: quitar pruebas
            // he puesto ver el formulaciro de crear el anuncio aqui como prueba
            Intent itntMsgs = new Intent(context, MessagesActivity.class);
            startActivity(itntMsgs);
        }

        else if (id == R.id.nav_masinfo) {
            Intent itntInfo = new Intent(context, MoreInfoActivity.class);
            itntInfo.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity( itntInfo );

        }
        else if (id == R.id.nav_ajustes) {
            //cargar ajustes
            Intent itntSettings = new Intent(context, SettingsActivity.class);
            startActivity( itntSettings );
        }
        else if (id == R.id.logoff) {
            //closse session
            AppSession.logoff(NavigationBaseActivity.this);
            AppSession.restart(NavigationBaseActivity.this);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        try {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean active = false;

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: called");
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: called");
        super.onStop();
        active = false;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
        active = false;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: Called ");
        if (!isAvatarFromLocal){
            Log.d(TAG, "onResume: is avatar from local true");
            //if avatar is set to brickavatar them load avatar form disk
            setAvatarFromLocal();
        }
        active = true;
        super.onResume();
    }

    public boolean isActive() {
        return active;
    }

    public void setAvatarFromLocal(){
        String path = context.getFilesDir() + "/avatar.jpg";
        File file = new File(path);

        if(file.exists()) {
            if(navUserImage!=null) {
                navUserImage.setImageDrawable(Drawable.createFromPath(path));
                isAvatarFromLocal = true;
                Log.d(TAG, "setAvatarFromLocal: isAvatarFromLocal TRUE");
            }
        }else{
            if(navUserImage!=null) {
                navUserImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.brick_avatar));
            }
            isAvatarFromLocal= false;
        }
    }





}
