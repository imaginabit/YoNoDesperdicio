package com.imaginabit.yonodesperdicion.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
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
    private static final int FAVORITES = 200;
    private static final int FAVORITES_ID = 201;
    private static final int FAVORITES_AD_ID = 202;
    private static final int CONVERSATIONS = 300;
    private static final int CONVERSATIONS_ID = 301;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AdsContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "ads", ADS);
        matcher.addURI(authority, "ads/*", ADS_ID );
        matcher.addURI(authority, "favorites", FAVORITES);
        matcher.addURI(authority, "favorites/ad/*", FAVORITES_AD_ID );// like routes in rails you need this before
        matcher.addURI(authority, "favorites/*", FAVORITES_ID );
        matcher.addURI(authority, "conversations", CONVERSATIONS);
        matcher.addURI(authority, "conversations/*", CONVERSATIONS_ID );


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
            case FAVORITES:
                return AdsContract.Favorites.CONTENT_TYPE;
            case FAVORITES_ID:
                return AdsContract.Favorites.CONTENT_ITEM_TYPE;
            case CONVERSATIONS:
                return AdsContract.Conversations.CONTENT_TYPE;
            case CONVERSATIONS_ID:
                return AdsContract.Conversations.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unkown Uri: "+ uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String id;

        switch (match){
            case ADS:
                queryBuilder.setTables(AdsDatabase.Tables.ADS);
                // get all ads
                // do nothing
                break;
            case ADS_ID:
                // Get one Ad
                queryBuilder.setTables(AdsDatabase.Tables.ADS);
                id = AdsContract.Ads.getAdId(uri);
                queryBuilder.appendWhere(BaseColumns._ID + "=" + id);
                break;

            case FAVORITES:
                // get all
                queryBuilder.setTables(AdsDatabase.Tables.FAVORITES);
                break;
            case FAVORITES_ID:
                // Get one
                queryBuilder.setTables(AdsDatabase.Tables.FAVORITES);
                id = AdsContract.Favorites.getFavoriteId(uri);
                queryBuilder.appendWhere(BaseColumns._ID + "=" + id);
                break;

            case CONVERSATIONS:
                // get all
                queryBuilder.setTables(AdsDatabase.Tables.CONVERSATIONS);
                break;
            case CONVERSATIONS_ID:
                // Get one
                queryBuilder.setTables(AdsDatabase.Tables.CONVERSATIONS);
                id = AdsContract.Conversations.getConversationId(uri);
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
     * As a courtesy, call {@link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after inserting.
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
        Log.v(TAG, "insert (uri= " + uri + ", values= " + values.toString());

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        long recordId;

        switch (match){
            case ADS:
                // get all ads
                recordId = db.insertOrThrow(AdsDatabase.Tables.ADS, null, values);
                return AdsContract.Ads.buildAdUri(String.valueOf(recordId));
            case FAVORITES:
                // get all favorites
                recordId = db.insertOrThrow(AdsDatabase.Tables.FAVORITES, null, values);
                return AdsContract.Favorites.buildFavoriteUri(String.valueOf(recordId));
            case CONVERSATIONS:
                // get all
                recordId = db.insertOrThrow(AdsDatabase.Tables.CONVERSATIONS, null, values);
                return AdsContract.Conversations.buildConversationUri(String.valueOf(recordId));
            default:
                throw new IllegalArgumentException("Unkown Uri");
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(TAG,"update (uri= "+ uri + ", values= "+ values.toString());

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        String id;
        String selectionCriteria = selection;
        int updateCount = 0;
        switch (match){
            case ADS:
                // get all ads
                // do nothing, prevent update all records if not id provide
                break;
            case ADS_ID:
                id = AdsContract.Ads.getAdId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ") " : "");
                updateCount = db.update(AdsDatabase.Tables.ADS, values, selectionCriteria, selectionArgs);
                break;
            case FAVORITES:
                // get all ads
                // do nothing, prevent update all records if not id provide
                break;
            case FAVORITES_ID:
                id = AdsContract.Favorites.getFavoriteId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ") " : "");
                updateCount = db.update(AdsDatabase.Tables.FAVORITES, values, selectionCriteria, selectionArgs);
                break;
            case FAVORITES_AD_ID:
                id = AdsContract.Ads.getAdId(uri);
                selectionCriteria = AdsContract.FavoritesColumns.FAV_AD_ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ") " : "");
                updateCount = db.update(AdsDatabase.Tables.FAVORITES, values, selectionCriteria, selectionArgs);
                break;

            case CONVERSATIONS:
                // do nothing, prevent update all records if not id provide
                break;

            case CONVERSATIONS_ID:
                id = AdsContract.Conversations.getConversationId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ") " : "");
                updateCount = db.update(AdsDatabase.Tables.CONVERSATIONS, values, selectionCriteria, selectionArgs);
                break;


            default:
                throw new IllegalArgumentException("Unkown Uri");
        }

        return updateCount;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v(TAG,"delete (uri= "+ uri );

        if(uri.equals(AdsContract.BASE_CONTENT_URI)){
            deleteDatabase();
            return 0;
        }

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String id;
        String selectionCriteria;
        switch (match){
            case ADS_ID:
                id = AdsContract.Ads.getAdId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ") " : "");
                return db.delete(AdsDatabase.Tables.ADS, selectionCriteria, selectionArgs);

            case FAVORITES:
                return db.delete(AdsDatabase.Tables.FAVORITES,"",selectionArgs);

            case FAVORITES_ID:
                id = AdsContract.Favorites.getFavoriteId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ") " : "");
                return db.delete(AdsDatabase.Tables.FAVORITES, selectionCriteria, selectionArgs);

            case FAVORITES_AD_ID:
                id = AdsContract.Favorites.getAdId(uri);
                selectionCriteria = AdsContract.FavoritesColumns.FAV_AD_ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ") " : "");
                Log.d(TAG, "delete: selectionCriteria "+ selectionCriteria + " args " + selectionArgs );
                Log.d(TAG, "delete: selectionargs " + selectionArgs );
                return db.delete(AdsDatabase.Tables.FAVORITES, selectionCriteria, selectionArgs);

            default:
                throw new IllegalArgumentException("Unkown Uri");
        }
//        return 0;
    }
}
