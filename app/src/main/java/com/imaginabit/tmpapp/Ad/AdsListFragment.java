package com.imaginabit.tmpapp.Ad;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

/**
 * Created by fer2015julio on 2/09/15.
 */
public class AdsListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<Ad>>{

    private static final String LOG_TAG = AdsListFragment.class.getSimpleName();
    private AdsCustomAdapter mAdapter;
    private static final int LOADER_ID =1 ;
    private ContentResolver mContentResolver;
    private List<Ad> mAds;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mContentResolver = getActivity().getContentResolver();
        mAdapter = new AdsCustomAdapter(getActivity(), getChildFragmentManager());
        setEmptyText("No Data");

        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(LOADER_ID,null, this);

    }

    @Override
    public Loader<List<Ad>> onCreateLoader(int id, Bundle args) {
        mContentResolver = getActivity().getContentResolver();
        return new AdsListLoader(getActivity(), AdsContract.URI_TABLE, mContentResolver);
    }

    @Override
    public void onLoadFinished(Loader<List<Ad>> loader, List<Ad> data) {
        mAdapter.setData(data);
        mAds = data;
        if (isResumed()){
            setListShown(true);
        } else {
           setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Ad>> loader) {
        mAdapter.setData(null);
    }
}
