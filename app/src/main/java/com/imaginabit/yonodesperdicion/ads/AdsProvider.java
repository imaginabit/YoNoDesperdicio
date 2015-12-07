package com.imaginabit.yonodesperdicion.ads;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by fer2015julio on 1/09/15.
 */
public class AdsProvider extends ContentProvider {
    private AdsDatabase mOpenHelper;

    private static String TAG = AdsProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher= buildUriMatcher();

    private static final int ADS = 100;
    private static final int ADS_ID = 101;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AdsContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "ads", ADS);
        matcher.addURI(authority, "ads/*", ADS_ID );

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AdsDatabase(getContext());
        return false;
    }



    private void deleteDatabase(){
        mOpenHelper.close();
        AdsDatabase.deleteDatabase(getContext());
        mOpenHelper = new AdsDatabase(getContext());
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ADS:
                return AdsContract.Ads.CONTENT_TYPE;
            case ADS_ID:
                return AdsContract.Ads.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unkown Uri");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(AdsDatabase.Tables.ADS);

        switch (match){
            case ADS:
                // get all ads
                // do nothing
                break;

            case ADS_ID:
                // Get one Ad
                String id = AdsContract.Ads.getAdId(uri);
                queryBuilder.appendWhere(BaseColumns._ID + "=" + id);
                break;

            default:
                throw new IllegalArgumentException("Unkown Uri");
        }

        Cursor cursor = queryBuilder.query(db, projection,selection,selectionArgs, null, null, sortOrder);
        return cursor;
    }

    /**
     * Implement this to handle requests to insert a new row.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(TAG,"insert (uri= "+ uri + ", values= "+ values.toString());

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match){
            case ADS:
                // get all ads
                long recordId = db.insertOrThrow(AdsDatabase.Tables.ADS, null, values);
                return AdsContract.Ads.buildAdUri(String.valueOf(recordId));

            default:
                throw new IllegalArgumentException("Unkown Uri");
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(TAG,"update (uri= "+ uri + ", values= "+ values.toString());

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        String selectionCriteria = selection;
        switch (match){
            case ADS:
                // get all ads
                // do nothing, prevent update all records if not id provide
                break;
            case ADS_ID:
                String id = AdsContract.Ads.getAdId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ") " : "");
                break;

            default:
                throw new IllegalArgumentException("Unkown Uri");
        }

        int updateCount = db.update(AdsDatabase.Tables.ADS, values, selectionCriteria, selectionArgs);
        return updateCount;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v(TAG,"delete (uri= "+ uri );

        if (uri.equals(AdsContract.BASE_CONTENT_URI)){
            deleteDatabase();
            return 0;
        }

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ADS:
                // get all ads
                // do nothing, prevent update all records if not id provide
                break;

            case ADS_ID:
                String id = AdsContract.Ads.getAdId(uri);
                String selectionCriteria = BaseColumns._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ") " : "");
                return db.delete(AdsDatabase.Tables.ADS, selectionCriteria, selectionArgs);

            default:
                throw new IllegalArgumentException("Unkown Uri");
        }
        return 0;
    }
}
