package com.imaginabit.yonodesperdicion.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.OperationCanceledException;
import android.util.Log;

import com.imaginabit.yonodesperdicion.models.Ad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fer2015julio on 2/09/15.
 *
 */
public class AdsListLoader extends AsyncTaskLoader<List<Ad>> {
    private static final String LOG_TAG = AdsListLoader.class.getSimpleName();

    private List<Ad> mAds;
    private ContentResolver mContentResolver;
    private Cursor mCursor;

    public AdsListLoader(Context context, Uri uri, ContentResolver contentResolver) {
        super(context);
        mContentResolver = contentResolver;
    }

    /**
     * Sends the result of the load to the registered listener. Should only be called by subclasses.
     * <p>
     * Must be called from the process's main thread.
     *
     * @param ads the result of the load
     */
    @Override
    public void deliverResult(List<Ad> ads) {
        if (isReset()) {
            if (ads != null) {
                mCursor.close();
            }
        }

        List<Ad> oldAdList = mAds;
        if (mAds == null || mAds.size() == 0) {
            Log.d(LOG_TAG, "++++++++++++++++++++++++ No data returned ");
        }
        mAds = ads;

        if (isStarted()) {
            super.deliverResult(ads);
        }

        if (oldAdList != null && oldAdList != ads) {
            mCursor.close();
        }
    }

    /**
     * Subclasses must implement this to take care of loading their data,
     * as per {@link #startLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #startLoading()}.
     */
    @Override
    protected void onStartLoading() {
        if (mAds != null) {
            deliverResult(mAds);
        }

        if (takeContentChanged() || mAds == null) {
            forceLoad();
        }
    }

    /**
     * Subclasses must implement this to take care of stopping their loader,
     * as per {@link #stopLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #stopLoading()}.
     * This will always be called from the process's main thread.
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    /**
     * Subclasses must implement this to take care of resetting their loader,
     * as per {@link #reset()}.  This is not called by clients directly,
     * but as a result of a call to {@link #reset()}.
     * This will always be called from the process's main thread.
     */
    @Override
    protected void onReset() {
        onStopLoading();
        if (mCursor != null) {
            mCursor.close();
        }

        mAds = null;

    }

    /**
     * Called if the task was canceled before it was completed.  Gives the class a chance
     * to clean up post-cancellation and to properly dispose of the result.
     *
     * @param data The value that was returned by {@link #loadInBackground}, or null
     *             if the task threw {@link OperationCanceledException}.
     */
    @Override
    public void onCanceled(List<Ad> data) {
        super.onCanceled(data);
        if (mCursor != null) {
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
        String[] projection = {
                BaseColumns._ID,
                AdsContract.AdsColumns.AD_TITLE,
                AdsContract.AdsColumns.AD_USERNAME,
                AdsContract.AdsColumns.AD_STATUS,
                AdsContract.AdsColumns.AD_IMAGE_FILENAME,
                AdsContract.AdsColumns.AD_BODY,
                AdsContract.AdsColumns.AD_DATE_CREATED,
                AdsContract.AdsColumns.AD_EXPIRATION,
                String.valueOf(AdsContract.AdsColumns.AD_FAVORITE),
                String.valueOf(AdsContract.AdsColumns.AD_TYPE),
                String.valueOf(AdsContract.AdsColumns.AD_USER_ID),
                AdsContract.AdsColumns.AD_LOCATIONS,
                AdsContract.AdsColumns.AD_LAST_DISTANCE,
                AdsContract.AdsColumns.AD_WEIGHT_GRAMS,
                AdsContract.AdsColumns.AD_POSTAL_CODE,
                AdsContract.AdsColumns.AD_CATEGORIA,
        };
        List<Ad> entries = new ArrayList<Ad>();

        mCursor = mContentResolver.query(AdsContract.URI_TABLE, projection, null, null, null);
        if (mContentResolver != null) {
            if (mCursor.moveToFirst()) {
                do {
                    int _id = mCursor.getInt(mCursor.getColumnIndex(BaseColumns._ID));
                    String title = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_TITLE));
                    String username = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_USERNAME));
                    String status = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_STATUS));
                    String image_filename = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_IMAGE_FILENAME));
                    String body = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_BODY));
                    String date_created = mCursor.getString(
                            mCursor.getColumnIndex(AdsContract.AdsColumns.AD_DATE_CREATED));

                    //last added:
                    String ad_expiration = mCursor.getString(
                            mCursor.getColumnIndex( AdsContract.AdsColumns.AD_EXPIRATION ));
                    String ad_favorite = mCursor.getString(
                            mCursor.getColumnIndex( String.valueOf(AdsContract.AdsColumns.AD_FAVORITE) ));
                    String ad_type = mCursor.getString(
                            mCursor.getColumnIndex(String.valueOf(AdsContract.AdsColumns.AD_TYPE)));
                    String ad_user_id = mCursor.getString(
                            mCursor.getColumnIndex( String.valueOf(AdsContract.AdsColumns.AD_USER_ID) ));
                    String ad_locations = mCursor.getString(
                            mCursor.getColumnIndex( AdsContract.AdsColumns.AD_LOCATIONS ));
                    String ad_last_distance = mCursor.getString(
                            mCursor.getColumnIndex( AdsContract.AdsColumns.AD_LAST_DISTANCE ));
                    String ad_weight_grams = mCursor.getString(
                            mCursor.getColumnIndex( AdsContract.AdsColumns.AD_WEIGHT_GRAMS ));
                    String ad_postal_code = mCursor.getString(
                            mCursor.getColumnIndex( AdsContract.AdsColumns.AD_POSTAL_CODE ));
                    String ad_categoria = mCursor.getString(
                            mCursor.getColumnIndex( AdsContract.AdsColumns.AD_CATEGORIA ));

                } while (mCursor.moveToNext());
            }
        }

        return entries;
    }
}

