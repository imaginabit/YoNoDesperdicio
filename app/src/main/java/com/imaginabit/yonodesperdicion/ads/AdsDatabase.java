package com.imaginabit.yonodesperdicion.ads;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Fernando on 1/09/15.
 * Guarda los datos de los anuncios recientes y los que marquemos como favoritos, o enviemos mensajes
 * Store data form recent ads , favorites
 */
public class AdsDatabase extends SQLiteOpenHelper {
    private static final String TAG = AdsDatabase.class.getSimpleName();

    private static final String DATABASE_NAME = "ads_database";
    private static final int DATABASE_VERSION = 2;

    private Context context;

    interface Tables{
        String ADS = "ads";
    }

    public AdsDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
/*
        CREATE TABLE ads (
            _id INTEGER PRIMARY KEY AUTOINCREMENT,
            ads_title TEXT NOT NULL,
            ads_body TEXT NOT NULL,
            ads_username TEXT NOT NULL,
            ads_date_created TEXT NOT NULL,
            ads_filename TEXT NOT NULL,
            ads_status Integer NOT NULL,

            0 INTEGER NOT NULL,

            ads_woeid TEXT NOT NULL,

            true BOOL NOT NULL,
            false BOOL NOT NULL,

                at android.database.sqlite.SQLiteConnection.nativePrepareStatement(Native Method)


        while compiling: CREATE TABLE ads (
        _id INTEGER PRIMARY KEY AUTOINCREMENT,
        ads_title TEXT NOT NULL,ads_body TEXT NOT NULL,
        ads_username TEXT NOT NULL,ads_date_created TEXT NOT NULL,
        ads_filename TEXT NOT NULL,ads_status Integer NOT NULL,
        ads_type INTEGER NOT NULL,
        ads_woeid TEXT NOT NULL,
        ads_comments_enabled BOOL NOT NULL,
        ads_favorite BOOL NOT NULL,
            at android.database.sqlite.SQLiteConnection.nativePrepareStatement(Native Method)
*/
        db.execSQL(
                    "CREATE TABLE " + Tables.ADS + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                        + AdsContract.AdsColumns.AD_TITLE + " TEXT NOT NULL,"
                                                        + AdsContract.AdsColumns.AD_BODY + " TEXT NOT NULL,"
                                                        + AdsContract.AdsColumns.AD_USERNAME + " TEXT NOT NULL,"
                                                        + AdsContract.AdsColumns.AD_DATE_CREATED + " TEXT NOT NULL,"
                                                        + AdsContract.AdsColumns.AD_IMAGE_FILENAME + " TEXT NOT NULL,"
                                                        + AdsContract.AdsColumns.AD_STATUS + " Integer NOT NULL,"
                                                        + AdsContract.AdsColumns.AD_TYPE + " INTEGER NOT NULL,"
                                                        + AdsContract.AdsColumns.AD_WOEID + " TEXT NOT NULL,"
                                                        + AdsContract.AdsColumns.AD_COMENTS_ENABLED + " BOOL NOT NULL,"
                                                        + AdsContract.AdsColumns.AD_FAVORITE + " BOOL NOT NULL" + ")"
                  );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion;
        if (version == 1){
            // Add fields
            version = 2;
        }

        if (version != DATABASE_VERSION){
            db.execSQL("DROP TABLE IF EXISTS " + Tables.ADS );
            onCreate(db);
        }
    }

    public static void deleteDatabase(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }




}
