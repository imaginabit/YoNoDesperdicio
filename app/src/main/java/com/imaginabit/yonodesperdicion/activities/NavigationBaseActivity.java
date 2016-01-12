package com.imaginabit.yonodesperdicion.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.imaginabit.yonodesperdicion.views.RoundedImageView;


public abstract class NavigationBaseActivity extends AppCompatActivity
                                             implements NavigationView.OnNavigationItemSelectedListener {
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the context
        this.context = getApplicationContext();
    }

    /**
     * Fix the supported action bar (polymorphic)
     */

    public Toolbar setSupportedActionBar() {
        return setSupportedActionBar(0);
    }

    public Toolbar setSupportedActionBar(int navIconRid) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Set the navigation icon if passed
        if (navIconRid > 0) {
            toolbar.setNavigationIcon(navIconRid);
        }
        setSupportActionBar(toolbar);
        return toolbar;
    }

    /**
     * Fix the Drawer Layout to activity
     */
    public DrawerLayout setDrawerLayout(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Current user data
        View headerNavView = navigationView.getHeaderView(0);
        RoundedImageView navUserImage = (RoundedImageView) headerNavView.findViewById(R.id.nav_header_user_image);
        TextView navUserFullname = (TextView) headerNavView.findViewById(R.id.nav_header_user_fullname);

        // User login panel
        LinearLayout navHeaderLayout = (LinearLayout) headerNavView.findViewById(R.id.nav_header_layout);
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
                Intent loginPanelIntent = new Intent(context, LoginPanelActivity.class);
                startActivity(loginPanelIntent);
            }
        });

        return drawer;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        else if (id == R.id.nav_perfil) {
            if (Utils.checkLoginAndRedirect(this)){
                Intent itntPerfil = new Intent(context, ProfileActivity.class);
                startActivity(itntPerfil);
            }

        }
        else if (id == R.id.nav_favoritos) {
            // TODO: quitar pruebas para que haga lo que de verdad tiene que hacer
            // he puesto ver el detalle del anuncio aqui como prueba
//            Intent itntFav = new Intent(context, AdDetailActivity.class);
//            startActivity(itntFav);
        }
        else if (id == R.id.nav_mensajes) {
            // TODO: quitar pruebas
            // he puesto ver el formulaciro de crear el anuncio aqui como prueba
//            Intent itntMsgs = new Intent(context, AdCreateActivity.class);
//            startActivity(itntMsgs);

        }
        else if (id == R.id.nav_masinfo) {
            Intent itntInfo = new Intent(context, MoreInfoActivity.class);
            itntInfo.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity( itntInfo );

        }
        else if (id == R.id.nav_ajustes) {
            //cargar ajustes
//            Intent itntSettings = new Intent(context, SettingsActivity.class);
//            startActivity( itntSettings );

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
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
}
