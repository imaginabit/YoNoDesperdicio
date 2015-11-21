package com.imaginabit.yonodesperdicion.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.Base;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapter.MoreInfoIdeas;
import com.imaginabit.yonodesperdicion.model.Idea;
import com.imaginabit.yonodesperdicion.util.IdeaUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prueba add new item to recycler view

            }
        });

        //----------------------------------------------------------------------
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_ideas);
        mRecyclerView.setHasFixedSize(true);

        // Initialize Universal Image Loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // This method creates an ArrayList that has three ideas objects saved in ideas
        initializeData();
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
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            IdeaUtils.fetchIdeas(this, new IdeaUtils.FetchIdeasCallback() {
                @Override
                public void done(List<Idea> ideas, Exception e) {
                    if (e == null){
                        Log.e(TAG,"error al obtener las Ideas");
                    } else{
                        mIdeas = ideas;
                        mAdapter.notifyItemInserted(0);
//                        mAdapter.notifyItemInserted(mIdeas.size-mIdeasOldSize);
                        //mAdapter.notifyDataSetChanged( );
                    }
                }
            });
        } else {
            Toast.makeText(MoreInfo.this, "No hay conexion a internet.", Toast.LENGTH_SHORT).show();
        }
    }
}


//
//    private class DownloadIdeasTask extends AsyncTask<String, Void, String> {
//        JSONObject jObj = null;
//
//        @Override
//        protected String doInBackground(String... urls) {
//            // params comes from the execute() call: params[0] is the url.
//            try {
//                return downloadJsonUrl(urls[0]);
//            } catch (IOException e) {
//                return "Unable to retrieve web page. URL may be invalid.";
//            }
//        }
//
//        // onPostExecute displays the results of the AsyncTask.
//        @Override
//        protected void onPostExecute(String result) {
//            // try parse the string to a JSON object
//            try {
//                jObj = new JSONObject(result);
//            } catch (JSONException e) {
//                Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
//            } catch (Throwable t) {
//                Log.e(TAG, "Could not parse malformed JSON: \"" + result + "\"");
//            }
//
//            ideas = new ArrayList<Idea>();
//            try {
//                Log.d(TAG,"Connection Ok");
//                if (jObj.has("ideas")) {
//                    Log.d(TAG,"has Ideas");
//                    JSONArray jsonItems = null;
//                    try {
//                        jsonItems = jObj.getJSONArray("ideas");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    if (jsonItems.length() > 0) {
//                        for (int i = 0; i < jsonItems.length(); i++) {
//                            JSONObject jsonItem = null;
//                            try {
//                                jsonItem = jsonItems.getJSONObject(i);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            // Extract properties
//
//                            // String title,        String id,      String category,
//                            //   String image_url,      String introduction
//                            //long idea_id = jsonItem.optLong("id", 0L);
//                            String idea_id = jsonItem.optString("id", "");
//                            String title = jsonItem.optString("username", "");
//                            String category = jsonItem.optString("category", "");
//                            String image_url = jsonItem.optString("image_url", "");
//                            String introduction = jsonItem.optString("introduction", "");
//
//                            // Validate
//                            if (AppUtils.isNotEmptyOrNull(title) && AppUtils.isNotEmptyOrNull(idea_id)) {
//                                ideas.add(new Idea(title, idea_id, category, image_url, introduction));
//                            }
//                        }
//                        //load the recycleview with data from json
//                        loadIdeasView();
//                    }
//                }
//                //Toast.makeText(MoreInfo.this, result, Toast.LENGTH_SHORT).show();
//                //textView.setText(result);
//            } catch (Exception e) {
//                e.printStackTrace();
////                Log.e(TAG + " JSON Parser", "Error parsing data " + e.toString());
//
//            }
//        }
//
//
//        private String downloadJsonUrl(String myurl) throws IOException {
//            Log.d(TAG, "Start downloadJsonUrl   " + Constants.longline);
//            Log.d(TAG, "myurl  " + myurl);
//
//            InputStream is = null;
//            String json = "";
//
//            try {
//                URL url = new URL(myurl);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//                conn.setReadTimeout(10000 /* milliseconds */);
//                conn.setConnectTimeout(15000 /* milliseconds */);
//                conn.setRequestMethod("GET");
//                conn.setDoInput(true);
//
//                conn.connect();
//                int response = conn.getResponseCode();
//                Log.d(TAG, "The response is: " + response);
//                is = conn.getInputStream();
//
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(
//                        is, "iso-8859-1"), 8);
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "n");
//                }
//                is.close();
//                json = sb.toString();
//            } catch (Exception e) {
//                Log.e("Buffer Error", "Error converting result " + e.toString());
//            }
//
//            return json;
//        }
//    }
//}


