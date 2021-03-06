package com.imaginabit.yonodesperdicion.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.data.AdsContract;
import com.imaginabit.yonodesperdicion.helpers.VolleyErrorHelper;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.utils.AdUtils;
import com.imaginabit.yonodesperdicion.utils.PrefsUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

//import com.imaginabit.yonodesperdicion.views.RoundedImageView;

public class AdDetailActivity extends NavigationBaseActivity implements Observer, OnMapReadyCallback {
    private static final String TAG = "AdDetailActivity";
    private static final int AD_EDIT_REQUEST = 1;

    static final int AD_EDIT_OK = 2;
    static final int AD_EDIT_DELETE = 3;

    private Ad mAd;
    private boolean isFavorite;
    private boolean isBooked;
    private boolean isDelivered;
    private TextView statusView;
    private ImageView statusImageView;

    private SharedPreferences.Editor prefsEdit = PrefsUtils.getSharedPreferencesEditor(context);
    private ContentResolver contentResolver;
    private ContentValues valuesFavorite;
    private ContentValues valuesConversation;


    private String[] projection;
    private String selectionClause;
    private String[] selectionArgs;
    private String emptyWhere = "";
    private String[] emptyArgs = {};

    private GoogleMap mMap;
    private MapView mMapView;

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_content);
        contentResolver = getContentResolver();
        valuesFavorite = new ContentValues();

        // Retrieve args
        final Bundle data = getIntent().getExtras();
        Log.d(TAG, "onCreate: data : " + data.toString());
        final Ad ad = data.getParcelable("ad");
        Log.d(TAG, "onCreate: ad: " + ad);
        isFavorite = false;

        if (ad == null) {
            Toast.makeText(this, "No se ha pasado el argumento", Toast.LENGTH_LONG).show();
        } else {
            mAd = ad;
            observe(mAd);

            projection = new String[]{AdsContract.FavoritesColumns.FAV_AD_ID};
            selectionClause = AdsContract.FavoritesColumns.FAV_AD_ID + " = ?";
            selectionArgs = new String[]{Integer.toString(mAd.getId())};

            Cursor returned = contentResolver.query(AdsContract.URI_TABLE_FAVORITES, projection, selectionClause, selectionArgs, "");
            Log.d(TAG, "onCreate: returned cursor " + returned);
            Log.d(TAG, "onCreate: returned cursor " + returned.getCount());

            if (returned.getCount() > 0) {
                isFavorite = true;
            }
            isBooked = (mAd.getStatus() == Ad.Status.BOOKED);
            isDelivered = (mAd.getStatus() == Ad.Status.DELIVERED);

            Log.d(TAG, "onCreate: mAd = " + mAd.getId());
            // Fix action bar and drawer
            Toolbar toolbar = setSupportedActionBar();
            //toolbar.setTitle(ad.getTitle());
            setDrawerLayout(toolbar);
            getSupportActionBar().setTitle(ad.getTitle());
            VolleySingleton.init(this);

            // Content
            TextView bodyView = findViewById(R.id.ad_body);
            bodyView.setText(ad.getBody());


            statusView = findViewById(R.id.ad_status);
            statusView.setText(ad.getStatusStr());
            statusView.setTextColor(ContextCompat.getColor(context, ad.getStatusColor()));


            statusImageView = findViewById(R.id.ad_image_status);
            statusImageView.setImageDrawable(ContextCompat.getDrawable(context, ad.getStatusImage()));
//            statusImageView.getDrawable().setColorFilter(ContextCompat.getColor(context, ad.getStatusColor()), android.graphics.PorterDuff.Mode.MULTIPLY);

            TextView expirationText = findViewById(R.id.ad_expiration);
            expirationText.setText(ad.getExpirationDateLong());

            TextView weightText = findViewById(R.id.ad_weight);
            weightText.setText(ad.getWeightKgStr());

            final ImageView image = findViewById(R.id.backdrop);

            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            final String imageUri = Constants.HOME_URL + ad.getImageUrl();

            ImageSize targetSize = new ImageSize(300, 200); // result Bitmap will be fit to this size
            imageLoader.displayImage(imageUri, image);


            TextView categoriaText = findViewById(R.id.ad_category);
            categoriaText.setText(ad.getCategoria());


            Log.d(TAG, "onCreate: " + ad.getStatusStr() + ad.getStatusColor());

            if (ad.getStatusStr() == "entregado") {
                TableRow row = findViewById(R.id.row_status);
                row.setVisibility(View.GONE);
            }
            if (Utils.isEmptyOrNull(ad.getExpirationDateLong())) {
                TableRow row = findViewById(R.id.row_expiration);
                row.setVisibility(View.GONE);
            }
            if (Utils.isEmptyOrNull(ad.getWeightKgStr())) {
                TableRow row = findViewById(R.id.row_weight);
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

            mMapView = findViewById(R.id.mapview);
            mMapView.onCreate(savedInstanceState);

            // Gets to GoogleMap from the MapView and does initialization stuff
            try {
                mMapView.getMapAsync(this);

                //mMap.setMyLocationEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }


            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            try {
                MapsInitializer.initialize(AdDetailActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
//            } catch (GooglePlayServicesNotAvailableException eMap) {
//                eMap.printStackTrace();
            }

            //actualy geting user info in ads api
            Log.d(TAG, "onCreate: Ad id :" + ad.getId());

            AdUtils.fetchAd(ad.getId(), new AdUtils.FetchAdCallback() {
                @Override
                public void done(final Ad ad, final User user, Exception e) {
                    Log.d(TAG, "done() called with: " + "ad = [" + ad + "], user = [" + user + "], e = [" + e + "]");
                    if (ad != null) {
                        mAd = ad;
                        TextView userName = findViewById(R.id.user_name);
                        userName.setText(user.getUserName());
                        TextView userLocation = findViewById(R.id.user_location);
                        userLocation.setText(user.getZipCode());

                        RatingBar userRatting = findViewById(R.id.user_ratting);
                        userRatting.setRating(user.getRatting());

                        TextView userWeight = findViewById(R.id.user_weight);
                        userWeight.setText(Utils.gramsToKgStr(user.getGrams()));

                        //load avatar
                        if (!user.getAvatar().equals(Constants.DEFAULT_USER_AVATAR)) {
                            Log.d(TAG, "done: User Avatar: " + user.getAvatar());
                            Log.d(TAG, "done: Default Avatar: " + Constants.DEFAULT_USER_AVATAR);
//                            RoundedImageView userAvatar = (RoundedImageView) findViewById(R.id.user_avatar);
                            ImageView userAvatar = findViewById(R.id.user_avatar);
                            ImageLoader imageLoaderAvatar = ImageLoader.getInstance(); // Get singleton instance
                            imageLoaderAvatar.displayImage(Constants.HOME_URL + user.getAvatar(), userAvatar);
                        }

                        CardView cardView = findViewById(R.id.perfil_mini);
                        cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent itntPerfil = new Intent(context, ProfileActivity.class);
                                itntPerfil.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                itntPerfil.putExtra("ad", ad);
                                itntPerfil.putExtra("user", user);
                                startActivity(itntPerfil);
                                //Toast.makeText(AdDetailActivity.this, "Usuario "+ user.getUserId(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        observe(mAd);
                        mAd.setLocation(AdUtils.calculateLocation(mAd));

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

            FloatingActionButton fab = findViewById(R.id.fab);

            if (userIsOwner(ad)) {
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

    private boolean userIsOwner(Ad ad) {
        return AppSession.getCurrentUser() != null && AppSession.getCurrentUser().id == ad.getUserId();
    }

    private void clickMessage(final Ad ad) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (Utils.checkLoginAndRedirect(AdDetailActivity.this)) {
            Log.v(TAG, "onClick: is logged!");

            //check if there is a conversation created for this ad
            final String converForAd = "conversationForAd" + ad.getId();
            int converId = prefs.getInt(converForAd, 0);
            String converTitle = ad.getTitle();

            Log.d(TAG, "clickMessage: converId " + converId);

            //this if take old method conversation id saved in preferencies
            if (converId == 0) {
                Log.d(TAG, "clickMessage: convertId = 0");
                //create a new conversation, new message for this ad and go to it
                /*
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
                            int converId = conversation.getId();
                            //Toast.makeText(AdDetailActivity.this, "" + conversation.getId(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "------------------------onFinished: converforad " + converForAd + " cid " + converId);

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
                */

                Conversation conversation = null;
                Uri conversationUri = null;

                //search if this ad is in conversations table
//                projection = new String[]{AdsContract.ConversationsColumns.CONVERSATION_ID};
                projection = new String[]{};
                selectionClause = AdsContract.ConversationsColumns.CONVERSATION_AD_ID + " = ?";
                selectionArgs = new String[]{Integer.toString(mAd.getId())};
                Cursor returnConversation = contentResolver.query(AdsContract.URI_TABLE_CONVERSATIONS, projection, selectionClause, selectionArgs, "");
                //if is in database take the existing conversation
                if (returnConversation.moveToFirst()) {
                    int paso = 0;
                    do {
                        int id = returnConversation.getInt(0);
                        int webId = returnConversation.getInt(1);
                        int adId = returnConversation.getInt(2);
                        int userId = returnConversation.getInt(3);
                        String title = returnConversation.getString(5);
                        Log.d(TAG, "Cursor recorriendo: CONVERSATION_WEB_ID 1: " + returnConversation.getString(1));
                        Log.d(TAG, "Cursor recorriendo: CONVERSATION_AD_ID 2: " + returnConversation.getString(2));
                        Log.d(TAG, "Cursor recorriendo: CONVERSATION_USER 3: " + returnConversation.getString(3));
                        Log.d(TAG, "Cursor recorriendo: CONVERSATION_STATUS 4: " + returnConversation.getString(4));
                        Log.d(TAG, "Cursor recorriendo: CONVERSATION_TITLE 5: " + returnConversation.getString(5));

                        paso++;
                        if (paso > 1) title = title + " " + paso;

                        conversation = new Conversation(webId, title);

                        Log.d(TAG, "clickMessage: paso " + paso);
                        conversationUri = AdsContract.Conversations.buildConversationUri(String.valueOf(id));
                    } while (returnConversation.moveToNext());
                } else {
                    //search if there is other conversation in database with the same name
                    projection = new String[]{};
                    selectionClause = AdsContract.ConversationsColumns.CONVERSATION_TITLE + " = ?";
                    selectionArgs = new String[]{mAd.getTitle()};
                    Cursor returnConversationByTitle = contentResolver.query(AdsContract.URI_TABLE_CONVERSATIONS, projection, selectionClause, selectionArgs, "");
                    if (returnConversationByTitle.moveToFirst()) {
                        do {

                            int id = returnConversationByTitle.getInt(0);
                            int webId = returnConversationByTitle.getInt(1);
                            int adId = returnConversationByTitle.getInt(2);
                            int userId = returnConversationByTitle.getInt(3);
                            String title = returnConversationByTitle.getString(5);
                            conversation = new Conversation(webId, title);
                            conversationUri = AdsContract.Conversations.buildConversationUri(String.valueOf(id));
                        } while (returnConversationByTitle.moveToNext());
                    }
                    //if there is no conversation create a new one
                    if (conversation == null) {
                        //db create new record in conversation table
                        valuesConversation = new ContentValues();
                        conversation = new Conversation(0, ad.getTitle());
                        //save conversation in database
                        valuesConversation.put(AdsContract.ConversationsColumns.CONVERSATION_USER, ad.getUserId());
                        valuesConversation.put(AdsContract.ConversationsColumns.CONVERSATION_AD_ID, ad.getId());
                        valuesConversation.put(AdsContract.ConversationsColumns.CONVERSATION_TITLE, ad.getTitle());
                        conversationUri = contentResolver.insert(AdsContract.URI_TABLE_CONVERSATIONS, valuesConversation);
                    }
                    Log.d(TAG, "clickMessage: Record Id returned is " + conversationUri.toString());
                }

                //prefsEdit.putInt(converForAd, conversation.getId());
                //prefsEdit.commit();

                Intent intent = new Intent(context, MessagesChatActivity.class);
//                intent.putExtra("conversationId", conversation.getId());
                intent.putExtra("conversationUri", conversationUri);
                intent.putExtra("adName", ad.getTitle());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppSession.currentConversation = conversation;
                context.startActivity(intent);

            } else {
                Conversation conversation = new Conversation(converId, converTitle);

                Intent intent = new Intent(context, MessagesChatActivity.class);
                intent.putExtra("conversationId", converId);
                AppSession.currentConversation = conversation;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        }
    }

    /**
     * When click on edit Ad, send all data to add edit on
     * @param ad
     */
    private void clickEdidAd(Ad ad) {
        Log.d(TAG, "clickEdidAd() called with: " + "ad = [" + ad + "]");

        Intent intent = new Intent(context, AdCreateActivity.class);
        intent.putExtra("ad", ad);
        startActivityForResult( intent, AD_EDIT_REQUEST);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);

        if (userIsOwner(mAd)) {
            getMenuInflater().inflate(R.menu.ad_owner, menu);
        } else {
            getMenuInflater().inflate(R.menu.ad, menu);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            if (mAd != null) {
                Log.d(TAG, "onOptionsItemSelected: mAd " + mAd.getId());
                if (!isFavorite) {
                    valuesFavorite.put(AdsContract.FavoritesColumns.FAV_AD_ID, mAd.getId());
                    Uri returned = contentResolver.insert(AdsContract.URI_TABLE_FAVORITES, valuesFavorite);
                    Log.d(TAG, "onOptionsItemSelected: Record Id returned is " + returned.toString());
                    isFavorite = true;

                    Toast.makeText(AdDetailActivity.this, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onOptionsItemSelected: is favorited and going to remove it ");
//                    String where = AdsContract.FavoritesColumns.FAV_AD_ID + " = ?";

                    Uri favorited_ad = Uri.parse(AdsContract.URI_TABLE_FAVORITES + "/ad/" + mAd.getId());
                    Log.d(TAG, "onOptionsItemSelected: favorited ad id uri= " + favorited_ad);

                    int deleteReturned = contentResolver.delete(favorited_ad, emptyWhere, emptyArgs);

                    if (deleteReturned > 0) {
                        Toast.makeText(AdDetailActivity.this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                        isFavorite = false;
                    }
                }
            }
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_booked) {
            if (isBooked) {
                isBooked = false;
                Toast.makeText(AdDetailActivity.this, "Quitada la reserva", Toast.LENGTH_SHORT).show();
            } else {
                isBooked = true;
                isDelivered = false;//they can be both
                statusView.setText("reservado");
                statusView.setTextColor(ContextCompat.getColor(context, R.color.ad_reservado));
                statusImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_booked));
                Toast.makeText(AdDetailActivity.this, "Marcado como reservado", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_deliver) {
            if (isDelivered) {
                isDelivered = false;
            } else {
                isDelivered = true;
                isBooked = false;//they can be both
                statusView.setText("entregado");
                statusView.setTextColor(ContextCompat.getColor(context, R.color.ad_reservado));
                statusImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_booked));
                Toast.makeText(AdDetailActivity.this, "Marcado como entregado", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_share_button) {
            Log.d(TAG, "onOptionsItemSelected: Compratir");
            startActivity( Intent.createChooser(createShareIntent(),"Compartir") ); //createChooser() need to show directshare icons!
        }



        if (isBooked == false && isDelivered == false && id != R.id.action_share_button  ) {
            Log.d(TAG, "onOptionsItemSelected: avalaible ");
            statusImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_available));
            statusView.setTextColor(ContextCompat.getColor(context, R.color.ad_disponible));
            statusView.setText("disponible");
            try {
                sendStatus(Ad.Status.AVAILABLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //send status
        try {
            if (isBooked)
                sendStatus(Ad.Status.BOOKED);
            if (isDelivered)
                sendStatus(Ad.Status.DELIVERED);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }


    private void sendStatus(Ad.Status status) throws JSONException {
        RequestQueue queue = VolleySingleton.getRequestQueue();
        JSONObject jsonAd = new JSONObject();
        jsonAd.put("status", "1");
        if (status == Ad.Status.BOOKED) {
            jsonAd.put("status", "2");
        }
        if (status == Ad.Status.DELIVERED) {
            jsonAd.put("status", "3");
        }
        JSONObject jsonRequest = new JSONObject().put("ad", jsonAd);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                Constants.ADS_API_URL + "/" + mAd.getId(),
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse() called with: " + "response = [" + response + "]");
                        VolleyLog.v("Response:%n %s", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse() called with: " + "error = [" + error + "]");
                        String errorMessage;
                        String errorDialogMsg;
                        errorMessage = VolleyErrorHelper.getMessage(AdDetailActivity.this, error);
                        errorDialogMsg = Utils.showErrorsJson(errorMessage, AdDetailActivity.this);
                        Log.d(TAG, "onErrorResponse: error message:" + errorMessage);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map headers = new HashMap();
                String token = AppSession.getCurrentUser().authToken;
                headers.put("Authorization", token);
                Log.d(TAG, "getHeaders: authToken " + token);

                headers.put("Content-Type", "application/json; charset=utf-8");

                return headers;
            }
        };
        queue.add(request);
        Log.d(TAG, "sendDataRequest: request: " + request.toString());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu() called with: " + "menu = [" + menu + "]");

        int favPosition = 0;


        if (isFavorite) {
            menu.getItem(favPosition).setIcon(R.drawable.ic_favorite_white);
//            add icon on runtime
            MenuItem mi = menu.add("Favorito");
//            mi.setIcon(R.drawable.zanahoria);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
                Log.d(TAG, "onPrepareOptionsMenu: mi id : " + mi.getItemId());
            }
//            isFavorite = false;
        } else {
            menu.getItem(favPosition).setIcon(R.drawable.ic_favorite_border);
//            isFavorite = true;
        }

        if (mAd != null && userIsOwner(mAd)) {

            if (isBooked) {
                Log.d(TAG, "onPrepareOptionsMenu: is booked");
                menu.findItem(R.id.action_booked).setIcon(R.drawable.ic_local_offer_black);
            } else {
                Log.d(TAG, "onPrepareOptionsMenu: is not booked");
                menu.findItem(R.id.action_booked).setIcon(R.drawable.ic_local_offer);
            }

            if (isDelivered) {
                Log.d(TAG, "onPrepareOptionsMenu: is delivered");
                menu.findItem(R.id.action_deliver).setIcon(R.drawable.ic_sentiment_very_satisfied_black);
            } else {
                Log.d(TAG, "onPrepareOptionsMenu: is not delivered");
                menu.findItem(R.id.action_deliver).setIcon(R.drawable.ic_sentiment_very_satisfied);
            }
        }

        return super.onPrepareOptionsMenu(menu);

    }


    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void observe(Observable o) {
        Log.d(TAG, "observe() called with: " + "o = [" + o + "]");
        o.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, "update() called with: " + "observable = [" + observable + "], data = [" + data + "]");
        Location location = ((Ad) observable).getLocation();

        zoomLocation((Ad) observable);
    }

    /**
     * Center the map on ad location
     * @param ad
     */
    private void zoomLocation(Ad ad) {
        Location adLocation = ad.getLocation();

        if (adLocation != null && mMap != null) {
            // Updates the location and zoom of the MapView
            LatLng latLng = new LatLng(adLocation.getLatitude(), adLocation.getLongitude());
            //Log.d(TAG, "zoomLocation: latlng: " + latLng.toString() );
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
            mMap.animateCamera(cameraUpdate);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //DO WHATEVER YOU WANT WITH GOOGLEMAP
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            googleMap.setMyLocationEnabled(true);
            //googleMap.setTrafficEnabled(true);
            //googleMap.setIndoorEnabled(true);
            //googleMap.setBuildingsEnabled(true);
            //googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            zoomLocation(mAd);
        }

        mMap = googleMap;
    }

    //create and return share intent
    private Intent createShareIntent(){
        Log.d(TAG, "createShareIntent: ");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Yo no desperdicio " + mAd.getTitle() + " " +Constants.HOME_URL + "ad/"+ mAd.getId() );
        return shareIntent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == AD_EDIT_DELETE ){
            Intent itnt= new Intent(context, MainActivity.class);
            itnt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(itnt);
            finish();
        }

    }
}
