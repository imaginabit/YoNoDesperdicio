package com.imaginabit.yonodesperdicion.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.imaginabit.yonodesperdicion.Constants;

/**
 * Created by Fernando Ramírez on 31/01/16.
 */
public final class AdsContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public AdsContract() {}

    public interface AdsColumns{
        String AD_ID = "_id";
        String AD_TITLE = "ad_title";
        String AD_BODY = "ad_body";
        String AD_USERNAME = "ad_username";
        String AD_TYPE = "ad_type";
        String AD_DATE_CREATED = "ad_date_created";
        String AD_IMAGE_FILENAME = "ad_filename";
        String AD_STATUS = "ad_status";
        String AD_FAVORITE = "ad_favorite";
        String AD_USER_ID = "ad_user_id";
        String AD_LOCATIONS = "ad_location";
        String AD_LAST_DISTANCE = "ad_location";
        String AD_EXPIRATION = "ad_expiration";
        String AD_WEIGHT_GRAMS = "ad_weightgrams";
        String AD_POSTAL_CODE = "ad_postal_code";
        String AD_CATEGORIA = "ad_categoria";
    }

    public static final String CONTENT_AUTHORITY = Constants.PACKAGE_NAME_BASE +".ads.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY );

    private static final String PATH_ADS = "ads";
    public static final Uri URI_TABLE = Uri.parse(BASE_CONTENT_URI.toString() + "/" + PATH_ADS );

    private static final String PATH_FAVORITES = "favorites";
    public static final Uri URI_TABLE_FAVORITES = Uri.parse(BASE_CONTENT_URI.toString() + "/" + PATH_FAVORITES );

    public static final String[] TOP_LEVEL_PATHS = {
            PATH_ADS,
            PATH_FAVORITES
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


    ///---------------Favs


    public interface FavoritesColumns{
        String FAV_ID = "_id";
        String FAV_AD_ID = "ad_id";
    }

    public static class Favorites implements FavoritesColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + ".favorites";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + ".favorites";

        public static Uri buildFavoriteUri(String favId){
            return CONTENT_URI.buildUpon().appendEncodedPath(favId).build();
        }

        public static String getFavoriteId(Uri uri){
            return uri.getPathSegments().get(1);
        }
        public static String getAdId(Uri uri){
            return uri.getPathSegments().get(2);
        }
    }
}

