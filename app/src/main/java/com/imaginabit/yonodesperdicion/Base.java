package com.imaginabit.yonodesperdicion;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public abstract class Base extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    public static Context mContext;


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_anuncios) {
//            CharSequence text = "Base act!";
//            int duration = Toast.LENGTH_SHORT;
//            Toast toast = Toast.makeText(mContext, text, duration);
//            toast.show();
            Intent itntMain = new Intent(mContext, MainActivity.class);
            //itntMain.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(itntMain);
        } else if (id == R.id.nav_perfil) {
            Intent itntPerfil = new Intent(mContext, Settigns.class);
            startActivity(itntPerfil);
        } else if (id == R.id.nav_favoritos) {
            Intent itntFav = new Intent(mContext, Settigns.class);
            startActivity(itntFav);
        } else if (id == R.id.nav_mensajes) {
            Intent itntMsgs = new Intent(mContext, Settigns.class);
            startActivity(itntMsgs);
        } else if (id == R.id.nav_masinfo) {
            Intent itntInfo = new Intent(mContext, Settigns.class);
            startActivity( itntInfo );
        } else if (id == R.id.nav_ajustes) {
            //cargar ajustes
            Intent itntSettings = new Intent(mContext, Settigns.class);
            startActivity( itntSettings );

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
