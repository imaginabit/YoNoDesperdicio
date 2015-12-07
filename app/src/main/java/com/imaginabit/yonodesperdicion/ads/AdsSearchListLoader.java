package com.imaginabit.yonodesperdicion.ads;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fer2015julio on 2/09/15.
 */
public class AdsSearchListLoader extends AsyncTaskLoader<List<Ad>> {
    private static final String LOG_TAG = AdsSearchListLoader.class.getSimpleName();

    private List<Ad> mAds;
    private ContentResolver mContentResolver;
    private Cursor mCursor;
    private String mFilterText;

    public AdsSearchListLoader(Context context, Uri uri, ContentResolver contentResolver, String filterText){
        super(context);
        mContentResolver= contentResolver;
        mFilterText = filterText;
    }


    @Override
    public void deliverResult(List<Ad> ads) {
        if (isReset()){
            if(ads != null){
                mCursor.close();
            }
        }

        List<Ad> oldAdList = mAds;
        if(mAds == null || mAds.size() == 0 ){
            Log.d(LOG_TAG, "++++++++++++++++++++++++ No data returned ");
        }
        mAds = ads;

        if(isStarted()){
            super.deliverResult(ads);
        }

        if(oldAdList != null && oldAdList != ads){
            mCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if(mAds != null){
            deliverResult(mAds);
        }

        if (takeContentChanged()  || mAds == null){
            forceLoad();
        }
    }


    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mCursor != null){
            mCursor.close();
        }

        mAds = null;

    }

    /**
     * Called if the task was canceled before it was completed.  Gives the class a chance
     * to clean up post-cancellation and to properly dispose of the result.
     *
     * @param data The value that was returned by {@link #loadInBackground}, or null
     */
    @Override
    public void onCanceled(List<Ad> data) {
        super.onCanceled(data);
        if (mCursor != null){
            mCursor.close();
        }
    }

    /**
     * Force an asynchronous load. Unlike {@link #startLoading()} this will ignore a previously
     * loaded data set and load a new one.  This simply calls through to the
     * implementation's {@link #onForceLoad()}.  You generally should only call this
     * when the loader is started -- that is, {@link #isStarted()} returns true.
     * <p>
     * <p>Must be called from the process's main thread.
     */
    @Override
    public void forceLoad() {
        super.forceLoad();
    }

    @Override
    public List<Ad> loadInBackground() {
        String[] projection = {BaseColumns._ID,
                AdsContract.AdsColumns.AD_TITLE,
                AdsContract.AdsColumns.AD_USERNAME,
                AdsContract.AdsColumns.AD_WOEID,
                AdsContract.AdsColumns.AD_STATUS,
                AdsContract.AdsColumns.AD_IMAGE_FILENAME,
                AdsContract.AdsColumns.AD_BODY,
                AdsContract.AdsColumns.AD_DATE_CREATED,
                 AdsContract.AdsColumns.AD_COMENTS_ENABLED.toString(),
                 AdsContract.AdsColumns.AD_FAVORITE.toString(),
                String.valueOf(AdsContract.AdsColumns.AD_TYPE),
        };
        List<Ad> entries = new ArrayList<Ad>();

        String selection = AdsContract.AdsColumns.AD_TITLE + " LIKE " + mFilterText + "%' ";

        mCursor= mContentResolver.query(AdsContract.URI_TABLE,projection, selection ,null,null);
        if(mContentResolver!= null){
            if(mCursor.moveToFirst()){
                do {
                    int _id = mCursor.getInt(mCursor.getColumnIndex(BaseColumns._ID));
                    String title = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_TITLE));
                    String username = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_USERNAME));
                    String woeid = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_WOEID));
                    String status = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_STATUS));
                    String image_filename = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_IMAGE_FILENAME));
                    String body = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_BODY));
                    String date_created = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_DATE_CREATED));
                    String comments_enabled = mCursor.getString(
                            mCursor.getColumnIndex(String.valueOf(AdsContract.AdsColumns.AD_COMENTS_ENABLED)));
                } while (mCursor.moveToNext());
            }
        }

        return entries;
    }
}
