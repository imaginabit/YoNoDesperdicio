package com.imaginabit.tmpapp.Ad;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.imaginabit.tmpapp.R;

import java.util.List;


/**
 * Created by fer2015julio on 8/09/15.
 */
public class SearchActivity extends FragmentActivity
    implements LoaderManager.LoaderCallbacks<List<Ad>>{

    private static final String LOG_TAG = SearchActivity.class.getSimpleName();

    private AdsCustomAdapter mAdsCustomAdapter;
    private static int LOADER_ID = 2;
    private ContentResolver mContentResolver;
    private List<Ad> adsRetrieved;
    private ListView listView;
    private EditText mSearchEditText;
    private Button mSearchButton;
    private String matchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        listView = (ListView) findViewById(R.id.searchResultList);
        mSearchEditText = (EditText) findViewById(R.id.searchName);
        mSearchButton = (Button) findViewById(R.id.searchButton);
        mContentResolver = getContentResolver();
        mAdsCustomAdapter = new AdsCustomAdapter(SearchActivity.this, getSupportFragmentManager());

        listView.setAdapter(mAdsCustomAdapter);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchText = mSearchEditText.getText().toString();
                getSupportLoaderManager().initLoader(LOADER_ID++, null, SearchActivity.this);
            }
        });
    }



    @Override
    public Loader<List<Ad>> onCreateLoader(int id, Bundle args) {

        return new AdsSearchListLoader(SearchActivity.this, AdsContract.URI_TABLE, this.mContentResolver,matchText);
    }

    @Override
    public void onLoadFinished(Loader<List<Ad>> loader, List<Ad> ads) {
        mAdsCustomAdapter.setData(ads);
        this.adsRetrieved = ads;
    }

    @Override
    public void onLoaderReset(Loader<List<Ad>> loader) {
        mAdsCustomAdapter.setData(null);
    }
}
