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

    private List<Ad> adsList;
    private ContentResolver contentResolver;
    private Cursor cursor;
    private String filterText;

    public AdsSearchListLoader(Context context, Uri uri, ContentResolver contentResolver, String filterText){
        super(context);
        this.contentResolver = contentResolver;
        this.filterText = filterText;
    }

    @Override
    public void deliverResult(List<Ad> ads) {
        if (isReset()) {
            if (ads != null) {
                cursor.close();
            }
        }

        List<Ad> oldAdList = this.adsList;
        if (this.adsList == null || this.adsList.size() == 0 ){
            Log.d(LOG_TAG, "++++++++++++++++++++++++ No data returned ++++++++++++++++++++++++");
        }

        this.adsList = ads;

        if (isStarted()) {
            super.deliverResult(ads);
        }

        if (oldAdList != null && oldAdList != ads){
            cursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if(adsList != null){
            deliverResult(adsList);
        }

        if (takeContentChanged()  || adsList == null){
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

        if (cursor != null){
            cursor.close();
        }

        adsList = null;
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

        if (cursor != null) {
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
                                 AdsContract.AdsColumns.AD_COMMENTS_ENABLED,
                                 AdsContract.AdsColumns.AD_FAVORITE,
                                 AdsContract.AdsColumns.AD_TYPE,
                              };

        List<Ad> entriesList = new ArrayList<Ad>();

        String selection = AdsContract.AdsColumns.AD_TITLE + " LIKE " + filterText + "%' ";

        cursor = contentResolver.query(AdsContract.URI_TABLE,projection, selection ,null,null);
        if (cursor != null && cursor.moveToFirst()) {
            // Columns Index
            final AdsContract.ColumnsIndex columnsIndex = new AdsContract.ColumnsIndex(cursor);
            do {
                int id = cursor.getInt(columnsIndex.AD_ID_INDEX);
                String title = cursor.getString(columnsIndex.AD_TITLE_INDEX);
                // @TODO finish
                String username = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_USERNAME));
                String woeid = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_WOEID));
                String status = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_STATUS));
                String imageFilename = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_IMAGE_FILENAME));
                String body = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_BODY));
                String dateCreated = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_DATE_CREATED));
                String commentsEnabled = cursor.getString(cursor.getColumnIndex(AdsContract.AdsColumns.AD_COMMENTS_ENABLED));
            }
            while (cursor.moveToNext());
        }

        return entriesList;
    }
}
