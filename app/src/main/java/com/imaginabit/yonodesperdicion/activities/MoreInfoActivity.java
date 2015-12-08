package com.imaginabit.yonodesperdicion.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.IdeasAdapter;
import com.imaginabit.yonodesperdicion.models.Idea;
import com.imaginabit.yonodesperdicion.utils.IdeaUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;

import java.util.List;

public class MoreInfoActivity extends NavigationBaseActivity {
    private final String TAG = getClass().getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Idea> ideasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        Toolbar toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);

        //----------------------------------------------------------------------

        recyclerView = (RecyclerView) findViewById(R.id.recycler_ideas);
        recyclerView.setHasFixedSize(true);

        // Initialize Universal Image Loader
        //ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
//                .threadPoolSize(4)
//                .memoryCache(new WeakMemoryCache())
//                .imageDownloader(new BaseImageDownloader(mContext,10 * 1000, 30 * 1000))
//                .build();
//        ImageLoader.getInstance().init(config);

        // Use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // This method creates an ArrayList that has three ideas objects saved in ideas
        //mIdeas = initializeData();
        adapter = new IdeasAdapter(ideasList);
        recyclerView.setAdapter(adapter);

        getIdeasFromWeb();
    }

    private void getIdeasFromWeb() {
        Log.d(TAG, "get Ideas From Web");
        // Check if network link is available
        if (Utils.isActiveNetworkConnection(this)) {
            IdeaUtils.fetchIdeas(this, new IdeaUtils.FetchIdeasCallback() {
                @Override
                public void done(List<Idea> ideas, Exception e) {
                    if (e == null) {
                        Log.e(TAG, "obtenidas las Ideas!");
                        if (ideas != null) {
                            ideasList = ideas;

                            adapter = new IdeasAdapter(ideasList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            Log.e(TAG, "ideas : " + ideasList.size());
                        }
                    } else {
                        Log.e(TAG, "error al obtener las Ideas");
                        e.printStackTrace();

                        final Handler handler = new Handler();
                        // Wait 5 secons to try again
                        handler.postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        getIdeasFromWeb();
                                    }
                                },
                                5000
                        );
                    }
                }
            });
        }
        else {
            Toast.makeText(MoreInfoActivity.this, "No hay conexion a internet.", Toast.LENGTH_SHORT).show();
        }
    }
}
