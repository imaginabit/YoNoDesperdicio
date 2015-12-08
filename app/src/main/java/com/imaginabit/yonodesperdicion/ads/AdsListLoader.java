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
public class AdsListLoader extends AsyncTaskLoader<List<Ad>> {
    private static final String LOG_TAG = AdsListLoader.class.getSimpleName();

    private List<Ad> adsList;
    private ContentResolver contentResolver;
    private Cursor cursor;

    public AdsListLoader(Context context, Uri uri, ContentResolver contentResolver){
        super(context);
        this.contentResolver = contentResolver;
    }

    /**
     * Sends the result of the load to the registered listener. Should only be called by subclasses.
     * <p>
     * Must be called from the process's main thread.
     *
     * @param adsList the result of the load
     */
    @Override
    public void deliverResult(List<Ad> adsList) {
        if (isReset()) {
            if (adsList != null) {
                cursor.close();
            }
        }

        if (adsList == null || adsList.size() == 0 ){
            Log.d(LOG_TAG, "--- No data returned ---");
        }

        this.adsList = adsList;

        if (isStarted()) {
            super.deliverResult(adsList);
        }

        if (adsList != null){
            cursor.close();
        }
    }

    /**
     * Subclasses must implement this to take care of loading their data,
     * as per {@link #startLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #startLoading()}.
     */
    @Override
    protected void onStartLoading() {
        if(adsList != null){
            deliverResult(adsList);
        }

        if (takeContentChanged()  || adsList == null){
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

        if (cursor != null){
            cursor.close();
        }

        adsList = null;
    }

    /**
     * Called if the task was canceled before it was completed.  Gives the class a chance
     * to clean up post-cancellation and to properly dispose of the result.
     */
    @Override
    public void onCanceled(List<Ad> data) {
        super.onCanceled(data);
        if (cursor != null){
            cursor.close();
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

        cursor = contentResolver.query(AdsContract.URI_TABLE,projection, null, null, null);
        if (contentResolver != null) {
            if (cursor.moveToFirst()) {
                do {
                    int _id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
                    String title = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_TITLE));
                    String username = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_USERNAME));
                    String woeid = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_WOEID));
                    String status = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_STATUS));
                    String imageFilename = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_IMAGE_FILENAME));
                    String body = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_BODY));
                    String dateCreated = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_DATE_CREATED));
                    String commentsEnabled = cursor.getString(cursor.getColumnIndex(String.valueOf(AdsContract.AdsColumns.AD_COMENTS_ENABLED)));
                }
                while (cursor.moveToNext());
            }
        }

        return entries;
    }
}
