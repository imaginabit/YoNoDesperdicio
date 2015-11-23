package com.imaginabit.yonodesperdicion.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.Base;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapter.MoreInfoIdeas;
import com.imaginabit.yonodesperdicion.model.Idea;
import com.imaginabit.yonodesperdicion.util.IdeaUtils;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.util.ArrayList;
import java.util.List;

public class MoreInfo extends Base {
    private String TAG = this.getClass().getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Idea> mIdeas;


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

        // Initialize Universal Image Loader
        //ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .threadPoolSize(4)
                .memoryCache(new WeakMemoryCache())
                .imageDownloader(new BaseImageDownloader(mContext,10 * 1000, 30 * 1000))
                .build();
        ImageLoader.getInstance().init(config);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // This method creates an ArrayList that has three ideas objects saved in ideas
        //initializeData();
        mAdapter = new MoreInfoIdeas(mIdeas);
        mRecyclerView.setAdapter(mAdapter);

        getIdeasFromWeb();
    }




    private void initializeData() {
        mIdeas = new ArrayList<>();
        // String title, String id, String category, String image_url, String introduction
        mIdeas.add(new Idea("Sopa de aprovechamiento de verduras", "10", "recetas", "/system/ideas/images/000/000/010/original/sopa_aprovechamiento_verduras.jpg", "intro"));
        mIdeas.add(new Idea("Hojaldre relleno de mandarinas y nata", "10", "recetas", "propias/d_brick_original.png", "intro"));
        mIdeas.add(new Idea("Una idea", "10", "recetas", "/system/ideas/images/000/000/001/original/croquetas-pollo.jpg?1443097172", "intro"));
    }

    private void getIdeasFromWeb() {
        Log.d(TAG, "get Ideas From Web");
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            IdeaUtils.fetchIdeas(this, new IdeaUtils.FetchIdeasCallback() {
                @Override
                public void done(List<Idea> ideas, Exception e) {
                    if (e == null){
                        Log.e(TAG,"obtenidas las Ideas!");
                        if ( ideas != null ) {
                            mIdeas = ideas;
                            mAdapter = new MoreInfoIdeas(mIdeas);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
//                        mAdapter.notifyItemInserted(mIdeas.size-mIdeasOldSize);
//                        mAdapter.notifyItemInserted(0);
                            Log.e(TAG, "ideas : " + mIdeas.size() );
                        }

                    } else{
                        Log.e(TAG,"error al obtener las Ideas");
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(MoreInfo.this, "No hay conexion a internet.", Toast.LENGTH_SHORT).show();
        }
    }
}
