package com.imaginabit.tmpapp.Ad;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Fernando on 2/09/15.
 *
 * Separamos el Content Provider en Provider y Contract
 */
public class AdsContract {
    public interface AdsColumns{
        String AD_ID = "_id";
        String AD_TITLE = "ads_title";
        String AD_BODY = "ads_body";
        String AD_USERNAME = "ads_username";
        String AD_TYPE = "ads_type";
        String AD_WOEID = "ads_woeid";
        String AD_DATE_CREATED = "ads_date_created";
        String AD_IMAGE_FILENAME = "ads_filename";
        String AD_STATUS = "ads_status";
        String AD_COMENTS_ENABLED = "ads_comments_enabled";
        String AD_FAVORITE = "ads_favorite";
    }

    public static final String CONTENT_AUTHORITY = "imaginabit.yonodesperdicio_db.ads.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY );

    private static final String PATH_ADS = "ads";
    public static final Uri URI_TABLE = Uri.parse(BASE_CONTENT_URI.toString() + "/" + PATH_ADS );

    public static final String[] TOP_LEVEL_PATHS = {
            PATH_ADS
    };

    public static class Ads implements AdsColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_ADS).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + ".ads";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + ".ads";

        public static Uri buildAdUri(String AdId){
            return CONTENT_URI.buildUpon().appendEncodedPath(AdId).build();
        }

        public static String getAdId(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}
