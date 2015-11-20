package com.imaginabit.yonodesperdicion.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.Base;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapter.MoreInfoIdeas;
import com.imaginabit.yonodesperdicion.model.Idea;
import com.imaginabit.yonodesperdicion.util.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoreInfo extends Base {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Idea> ideas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getApplicationContext();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //----------------------------------------------------------------------
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_ideas);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // This method creates an ArrayList that has three ideas objects saved in ideas
        initializeData();
        getIdeasFromWeb();

        // Initialize Universal Image Loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        // specify an adapter (see also next example)
        mAdapter = new MoreInfoIdeas(ideas);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void initializeData(){
        ideas = new ArrayList<>();

        // String title,        String id,      String category,
        //   String image_url,      String introduction
        ideas.add(new Idea("Sopa de aprovechamiento de verduras", "10","recetas", "/system/ideas/images/000/000/010/original/sopa_aprovechamiento_verduras.jpg", "intro"));
        ideas.add(new Idea("Hojaldre relleno de mandarinas y nata", "10","recetas", "propias/d_brick_original.png", "intro"));
        ideas.add(new Idea("Una idea", "10","recetas", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg?1443097172", "intro"));

    }

    private void getIdeasFromWeb(){
        TextView textview = new TextView(mContext) ;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadIdeasTask().execute(Constants.ADS_API_URL);
        } else {
            Toast.makeText(MoreInfo.this, "No hay conexion a internet.", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadIdeasTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(DownloadIdeasTask.this, result, Toast.LENGTH_SHORT).show();
            //textView.setText(result);
        }
    }

    private String downloadUrl(String myurl) throws IOException {
        return ""
    }
}
