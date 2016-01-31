package com.imaginabit.yonodesperdicion.data;

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
    private static final String DATABASE_NAME = "yonodesperdicio.db";
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;

    interface Tables{
        String ADS = "ads";
        String CONVERSATIONS = "conversations";
        String MESSAGES = "messages";
        String FAVORITES = "favorites";
        String USERS = "users";
    }

    public AdsDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.ADS + " ("
                        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + AdsContract.AdsColumns.AD_TITLE + " TEXT NOT NULL,"
                        + AdsContract.AdsColumns.AD_BODY + " TEXT NOT NULL,"
                        + AdsContract.AdsColumns.AD_USERNAME + " TEXT NOT NULL,"
                        + AdsContract.AdsColumns.AD_DATE_CREATED + " TEXT NOT NULL,"
                        + AdsContract.AdsColumns.AD_IMAGE_FILENAME + " TEXT NOT NULL,"
                        + AdsContract.AdsColumns.AD_STATUS + " Integer NOT NULL,"
                        + AdsContract.AdsColumns.AD_TYPE + " INTEGER NOT NULL,"
                        + AdsContract.AdsColumns.AD_FAVORITE + " BOOL NOT NULL"
                        + AdsContract.AdsColumns.AD_USER_ID + " Integer,"
                        + AdsContract.AdsColumns.AD_LOCATIONS + " Integer,"
                        + AdsContract.AdsColumns.AD_EXPIRATION + " TEXT NOT NULL,"
                        + AdsContract.AdsColumns.AD_WEIGHT_GRAMS + " Integer NOT NULL,"
                        + AdsContract.AdsColumns.AD_POSTAL_CODE + " Integer NOT NULL,"
                        + AdsContract.AdsColumns.AD_CATEGORIA + " Integer NOT NULL,"
                        + ")"
        );

        //crear tabla conversaciones
        /*db.execSQL("CREATE TABLE " + Tables.CONVERSATIONS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ")"
        );*/

        //crear tabla mensajes
//        db.execSQL("CREATE TABLE " + Tables.MESSAGES + " ("
//                        + ")"
//        );

        //crear tabla favoritos ?
        //crear tabla usuarios ?
    }
    

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion;
//        if (version == 1){
//            //add fields
//            version = 2;
//        }

        if (version != DATABASE_VERSION){
            db.execSQL("DROP TABLE IF EXISTS " + Tables.ADS );
            onCreate(db);
        }

    }

    public static void deleteDatabase(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }


}
