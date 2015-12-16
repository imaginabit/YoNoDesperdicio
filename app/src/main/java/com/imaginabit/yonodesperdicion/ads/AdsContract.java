package com.imaginabit.yonodesperdicion.ads;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Fernando on 2/09/15.
 *
 * Separamos el Content Provider en Provider y Contract
 */
public class AdsContract {

    public static class ColumnsIndex {
        public int AD_ID_INDEX;
        public int AD_TITLE_INDEX;
        public int AD_BODY_INDEX;
        public int AD_USERNAME_INDEX;
        public int AD_TYPE_INDEX;
        public int AD_WOEID_INDEX;
        public int AD_DATE_CREATED_INDEX;
        public int AD_IMAGE_FILENAME_INDEX;
        public int AD_STATUS_INDEX;
        public int AD_COMENTS_ENABLED_INDEX;
        public int AD_FAVORITE_INDEX;

        public ColumnsIndex(Cursor cursor) {
            AD_ID_INDEX = cursor.getColumnIndex(BaseColumns._ID);
            AD_DATE_CREATED_INDEX = cursor.getColumnIndex(AdsColumns.AD_DATE_CREATED);
            AD_TITLE_INDEX = cursor.getColumnIndex(AdsColumns.AD_TITLE);
            AD_BODY_INDEX = cursor.getColumnIndex(AdsColumns.AD_BODY);
            AD_WOEID_INDEX = cursor.getColumnIndex(AdsColumns.AD_WOEID);
            AD_USERNAME_INDEX = cursor.getColumnIndex(AdsColumns.AD_USERNAME);
            AD_TYPE_INDEX = cursor.getColumnIndex(AdsContract.AdsColumns.AD_TYPE);
            AD_IMAGE_FILENAME_INDEX = cursor.getColumnIndex(AdsColumns.AD_IMAGE_FILENAME);
            AD_STATUS_INDEX = cursor.getColumnIndex(AdsColumns.AD_STATUS);
            AD_COMENTS_ENABLED_INDEX = cursor.getColumnIndex(AdsColumns.AD_COMMENTS_ENABLED);
            AD_FAVORITE_INDEX = cursor.getColumnIndex(AdsColumns.AD_FAVORITE);
        }
    }

    public interface AdsColumns {
                                    String AD_ID = "_id";
                                    String AD_TITLE = "ads_title";
                                    String AD_BODY = "ads_body";
                                    String AD_USERNAME = "ads_username";
                                    String AD_TYPE = "ads_type";
                                    String AD_WOEID = "ads_woeid";
                                    String AD_DATE_CREATED = "ads_date_created";
                                    String AD_IMAGE_FILENAME = "ads_filename";
                                    String AD_STATUS = "ads_status";
                                    String AD_COMMENTS_ENABLED = "ads_comments_enabled";
                                    String AD_FAVORITE = "ads_favorite";
                                }

    public static final String CONTENT_AUTHORITY = "imaginabit.yonodesperdicio_db.ads.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY );

    private static final String PATH_ADS = "ads";
    public static final Uri URI_TABLE = Uri.parse(BASE_CONTENT_URI.toString() + "/" + PATH_ADS );

    public static final String[] TOP_LEVEL_PATHS = { PATH_ADS };

    public static class Ads implements AdsColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_ADS).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + ".ads";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + ".ads";

        public static Uri buildAdUri(String adId) {
            return CONTENT_URI.buildUpon().appendEncodedPath(adId).build();
        }

        public static String getAdId(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}
