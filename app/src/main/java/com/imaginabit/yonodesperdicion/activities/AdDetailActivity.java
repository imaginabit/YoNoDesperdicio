package com.imaginabit.yonodesperdicion.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.data.AdsContract;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.models.Message;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.utils.AdUtils;
import com.imaginabit.yonodesperdicion.utils.MessagesUtils;
import com.imaginabit.yonodesperdicion.utils.PrefsUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

public class AdDetailActivity extends NavigationBaseActivity {
    String TAG = "AdDetailActivity";
    Ad mAd;
    boolean isFavorite;

    SharedPreferences.Editor prefsEdit = PrefsUtils.getSharedPreferencesEditor(context);
    ContentResolver contentResolver;
    ContentValues values;

    String[] projection;
    String selectionClause;
    String[] selectionArgs;
    String emptyWhere = "";
    String[] emptyArgs = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_content);
        contentResolver = getContentResolver();
        values = new ContentValues();

        // Retrieve args
        Bundle data = getIntent().getExtras();
        final Ad ad = (Ad) data.getParcelable("ad");
        isFavorite = false;

        if (ad == null) {
            Toast.makeText(this, "No se ha pasado el argumento", Toast.LENGTH_LONG).show();
        } else {
            mAd = ad;

            projection = new String[]{AdsContract.FavoritesColumns.FAV_AD_ID};
            selectionClause = AdsContract.FavoritesColumns.FAV_AD_ID + " = ?";
            selectionArgs = new String[]{Integer.toString(mAd.getId())};

            Cursor returned = contentResolver.query(AdsContract.URI_TABLE_FAVORITES,projection, selectionClause, selectionArgs, "");
            Log.d(TAG, "onCreate: returned cursor " + returned);
            Log.d(TAG, "onCreate: returned cursor " + returned.getCount());

            if (returned.getCount()>0) {
                isFavorite = true;
            }

            Log.d(TAG, "onCreate: mAd = " +mAd.getId() );
            // Fix action bar and drawer
            Toolbar toolbar = setSupportedActionBar();
            //toolbar.setTitle(ad.getTitle());
            setDrawerLayout(toolbar);
            getSupportActionBar().setTitle(ad.getTitle());
            VolleySingleton.init(this);

            // Content
            TextView bodyView = (TextView) findViewById(R.id.ad_body);
            bodyView.setText(ad.getBody());

            TextView statusView = (TextView) findViewById(R.id.ad_status);
            statusView.setText(ad.getStatusStr());
            statusView.setTextColor(ContextCompat.getColor(context, ad.getStatusColor()));

            ImageView statusImageView = (ImageView) findViewById(R.id.ad_image_status);
            statusImageView.setImageDrawable(ContextCompat.getDrawable(context, ad.getStatusImage()));
//            statusImageView.getDrawable().setColorFilter(ContextCompat.getColor(context, ad.getStatusColor()), android.graphics.PorterDuff.Mode.MULTIPLY);

            TextView expirationText = (TextView) findViewById(R.id.ad_expiration);
            expirationText.setText(ad.getExpirationDateLong());

            TextView weightText = (TextView) findViewById(R.id.ad_weight);
            weightText.setText(ad.getWeightKgStr());

            final ImageView image = (ImageView) findViewById(R.id.backdrop);

            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            String imageUri = Constants.HOME_URL + ad.getImageUrl();

            ImageSize targetSize = new ImageSize(300, 200); // result Bitmap will be fit to this size
            imageLoader.displayImage(imageUri, image);

            TextView categoriaText = (TextView) findViewById(R.id.ad_category);
            categoriaText.setText(ad.getCategoria());


            Log.d(TAG, "onCreate: " + ad.getStatusStr() + ad.getStatusColor());

            if ( ad.getStatusStr()=="entregado" ) {
                TableRow row = (TableRow) findViewById(R.id.row_status);
                row.setVisibility(View.GONE);
            }
            if (Utils.isEmptyOrNull(ad.getExpirationDateLong()) ){
                TableRow row = (TableRow) findViewById(R.id.row_expiration);
                row.setVisibility(View.GONE);
            }
            if ( Utils.isEmptyOrNull(ad.getWeightKgStr()) ){
                TableRow row = (TableRow) findViewById(R.id.row_weight);
                row.setVisibility(View.GONE);
            }

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (image.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
                        //                    image.setAdjustViewBounds(false);
                        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        Log.d(TAG, "onClick: setAdjustViewBounds false");
                    } else {
                        //                    image.setAdjustViewBounds(true);
                        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Log.d(TAG, "onClick: setAdjustViewBounds true");
                    }
                }
            });


            //actualy geting user info in ads api
            Log.d(TAG, "onCreate: Ad id :" + ad.getId());

            AdUtils.fetchAd(ad.getId(), new AdUtils.FetchAdCallback() {
                @Override
                public void done(Ad ad, User user, Exception e) {
                    Log.d(TAG, "done() called with: " + "ad = [" + ad + "], user = [" + user + "], e = [" + e + "]");
                    if (ad != null) {
                        mAd = ad;
                        TextView userName = (TextView) findViewById(R.id.user_name);
                        userName.setText(user.getUserName());
                        TextView userLocation = (TextView) findViewById(R.id.user_location);
                        userLocation.setText(user.getZipCode());

                        RatingBar userRatting = (RatingBar) findViewById(R.id.user_ratting);
                        userRatting.setRating(user.getRatting());

                        TextView userWeight = (TextView) findViewById(R.id.user_weight);
                        userWeight.setText(Utils.gramsToKgStr(user.getGrams()));
                        Log.d(TAG, "done: mad: " + mAd.getId());
                    } else {
                        Log.d(TAG, "AdUtils.fetchAd_done return null ad");
                    }
                }
            });



            //load user data form api with ad.getUserId()

            //        TextView userName = (TextView) findViewById(R.id.user_name);
            //        userName.setText(ad.getUserName());
            //        TextView userLocation = (TextView) findViewById(R.id.user_location);
            //        userLocation.setText(ad.getUserName());
            //
            //        TextView userRatting= (TextView) findViewById(R.id.user_ratting);
            //        userRatting.setText(ad.getUserName());
            //
            //        RatingBar userWeight = (RatingBar) findViewById(R.id.user_weight);
            //        userWeight.setRating();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            if (AppSession.getCurrentUser()!=null && AppSession.getCurrentUser().id == ad.getUserId()){
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mode_edit_white));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickEdidAd(ad);
//                        Toast.makeText(AdDetailActivity.this, "No disponible", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickMessage(ad);
                    }
                });
            }
        }
    }

    private void clickMessage(final Ad ad){
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (Utils.checkLoginAndRedirect(AdDetailActivity.this)) {
            Log.v(TAG, "onClick: is logged!");

            //check if there is a conversation created for this ad
            final String converForAd = "conversationForAd" + ad.getId();
            int converId = prefs.getInt(converForAd, 0);
            String converTitle = ad.getTitle();

            Log.d(TAG, "clickMessage: converId "+ converId );

            if(converId == 0) {
                Log.d(TAG, "clickMessage: convertId = 0");
                //create a new conversation, new message for this ad and go to it
                MessagesUtils.createConversation(converTitle, ad.getUserId(), new MessagesUtils.MessagesCallback() {
                    @Override
                    public void onFinished(List<Message> messages, Exception e) {
                        Log.d(TAG, "clickMessage_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "]");
                        //do nothing
                    }

                    @Override
                    public void onFinished(List<Message> messages, Exception e, ArrayList data) {
                        Log.d(TAG, "clickMessage_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "], data = [" + data + "]");
                        if (data != null && data.size() > 0) {
                            Conversation conversation = ((Conversation) data.get(0));
                            int converId= conversation.getId();
                            //Toast.makeText(AdDetailActivity.this, "" + conversation.getId(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "------------------------onFinished: converforad "+ converForAd + " cid " + converId );

                            prefsEdit.putInt(converForAd, conversation.getId());
                            prefsEdit.commit();
                            Intent intent = new Intent(context, MessagesChatActivity.class);
                            intent.putExtra("conversationId", conversation.getId());
                            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                            AppSession.currentConversation = conversation;
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.d(TAG, "clickMessage_onError() called with: " + "errorMessage = [" + errorMessage + "]");
                        //if (errorMessage=="{\"errors\":\"Not authenticated\"}");
                        //TODO: onError
                    }
                });
            } else {
                Conversation conversation = new Conversation(converId, converTitle);

                Intent intent = new Intent(context, MessagesChatActivity.class);
                intent.putExtra("conversationId", converId );
                AppSession.currentConversation = conversation;
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        }
    }

    /**
     * When click on edit Ad, send all data to add edit on
     * @param ad
     */
    private void clickEdidAd(Ad ad){
        Log.d(TAG, "clickEdidAd() called with: " + "ad = [" + ad + "]");

        Intent intent = new Intent(context, AdCreateActivity.class);
        intent.putExtra("ad", (Parcelable) ad);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_favorite ) {
            if (mAd != null) {
                Log.d(TAG, "onOptionsItemSelected: mAd " + mAd.getId());
                if (!isFavorite){
                    values.put(AdsContract.FavoritesColumns.FAV_AD_ID, mAd.getId());
                    Uri returned = contentResolver.insert(AdsContract.URI_TABLE_FAVORITES, values);
                    Log.d(TAG, "onOptionsItemSelected: Record Id returned is " + returned.toString());
                    isFavorite = true;

                    Toast.makeText(AdDetailActivity.this, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onOptionsItemSelected: is favorited and going to remove it ");
//                    String where = AdsContract.FavoritesColumns.FAV_AD_ID + " = ?";

                    Uri favorited_ad = Uri.parse(AdsContract.URI_TABLE_FAVORITES +"/ad/" + mAd.getId());
                    Log.d(TAG, "onOptionsItemSelected: favorited ad id uri= " + favorited_ad );

                    int deleteReturned = contentResolver.delete(favorited_ad, emptyWhere, emptyArgs);

                    if (deleteReturned>0) {
                        Toast.makeText(AdDetailActivity.this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                        isFavorite = false;
                    }
                }
                invalidateOptionsMenu();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu() called with: " + "menu = [" + menu + "]");

        if(isFavorite){
            menu.getItem(0).setIcon(R.drawable.ic_favorite_white);
//            add icon on runtime
            MenuItem mi = menu.add("Favorito");
//            mi.setIcon(R.drawable.zanahoria);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
//            isFavorite = false;
            }
        else{
            menu.getItem(0).setIcon(R.drawable.ic_favorite_border);
//            isFavorite = true;
        }

        return super.onPrepareOptionsMenu(menu);

    }
}
