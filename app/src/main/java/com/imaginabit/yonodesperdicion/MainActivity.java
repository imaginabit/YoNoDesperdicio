package com.imaginabit.yonodesperdicion;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.rubengees.introduction.IntroductionBuilder;
import com.rubengees.introduction.entity.Option;
import com.rubengees.introduction.entity.Slide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Base
{

    private static final String PREFS_NAME = "YoNoDesperdicioPrefs";

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
            new IntroductionBuilder(this).withSlides(generateSlides()).introduceMyself();
        }
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

}
