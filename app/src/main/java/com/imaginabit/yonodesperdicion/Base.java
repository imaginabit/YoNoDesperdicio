package com.imaginabit.yonodesperdicion;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.imaginabit.yonodesperdicion.activity.AdCreate;
import com.imaginabit.yonodesperdicion.activity.AdDetail;
import com.imaginabit.yonodesperdicion.activity.MainActivity;
import com.imaginabit.yonodesperdicion.activity.MoreInfo;
import com.imaginabit.yonodesperdicion.activity.Profile;
import com.imaginabit.yonodesperdicion.activity.Settigns;


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
            itntMain.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(itntMain);
        } else if (id == R.id.nav_perfil) {
            Intent itntPerfil = new Intent(mContext, Profile.class);
            startActivity(itntPerfil);

        } else if (id == R.id.nav_favoritos) {
            // TODO: quitar pruebas para que haga lo que de verdad tiene que hacer
            // he puesto ver el detalle del anuncio aqui como prueba
            Intent itntFav = new Intent(mContext, AdDetail.class);
            startActivity(itntFav);
        } else if (id == R.id.nav_mensajes) {
            // TODO: quitar pruebas
            // he puesto ver el formulaciro de crear el anuncio aqui como prueba
            Intent itntMsgs = new Intent(mContext, AdCreate.class);
            startActivity(itntMsgs);

        } else if (id == R.id.nav_masinfo) {
            Intent itntInfo = new Intent(mContext, MoreInfo.class);
            itntInfo.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
