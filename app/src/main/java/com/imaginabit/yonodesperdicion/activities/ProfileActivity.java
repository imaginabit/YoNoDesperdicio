package com.imaginabit.yonodesperdicion.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.AdsAdapter;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.helpers.VolleyErrorHelper;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.utils.AdUtils;
import com.imaginabit.yonodesperdicion.utils.ProvinciasCP;
import com.imaginabit.yonodesperdicion.utils.UserUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.imaginabit.yonodesperdicion.views.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends NavigationBaseActivity {

    private final String TAG = getClass().getSimpleName();

    private UserData mUser;
    private TextView userName;
    private TextView location;
    private TextView weight;
    private RatingBar rating;
    private LinearLayout userads;
    private RoundedImageView avatarView;
    private Drawable avatar;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Ad> mAds;
    private User mUserWeb;
    private NestedScrollView mainscroll;
    private Toolbar toolbar;
    AppBarLayout mAppbarLayout;
    CoordinatorLayout mRootLayout;

    //for get image from camera/gallery
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    protected static final int STORAGE_PERMISSION_RC = 3;
    Bitmap bitmap;
    String selectedImagePath;
    private Intent pictureActionIntent = null;
    File capturedPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);

        mAds = new ArrayList<>();

        mainscroll = (NestedScrollView) findViewById(R.id.main_scroll);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_userads);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        userads = (LinearLayout) findViewById(R.id.user_ads);

        //recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdsAdapter(context, mAds);
        recyclerView.setAdapter(adapter);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (AppSession.getCurrentUser() != null) {
            VolleySingleton.init(context);

            mUser = AppSession.getCurrentUser();

            userName = (TextView) findViewById(R.id.user_name);
            location = (TextView) findViewById(R.id.location);
            weight = (TextView) findViewById(R.id.kilos);
            rating = (RatingBar) findViewById(R.id.ad_reputacion);
            userads = (LinearLayout) findViewById(R.id.user_ads);
            avatarView = (RoundedImageView) findViewById(R.id.avatarpic);

            userName.setText(mUser.username);
            location.setText(mUser.city);

            weight.setText("Entregados " + Integer.toString(mUser.totalQuantity) + " Kg");

            rating.setRating(mUser.rating);
            getUserWeb();

            getAdsFromWeb((int) mUser.id);
        }

        mAppbarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mRootLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        final String collapsedTitle = mUser.username;
        final String expandedTitle = "";
        final CollapsingToolbarLayout mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        mAppbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(TAG, "onOffsetChanged() called with: " + "appBarLayout = [" + appBarLayout + "], verticalOffset = [" + verticalOffset + "]");
                int verticalLimit = (mCollapsingToolbar.getHeight() - 212) * -1;
                if (verticalOffset == 0) {
                    Log.d(TAG, "onOffsetChanged: expanded");
                    toolbar.setTitle("");
                    mCollapsingToolbar.setTitle(expandedTitle);
                    avatarView.setVisibility(View.VISIBLE);
                } else if (!toolbar.getTitle().equals(collapsedTitle)) {
                    Log.d(TAG, "onOffsetChanged: collapsed");
                    toolbar.setTitle(collapsedTitle);
                } else if (verticalOffset < verticalLimit) {
                    toolbar.setTitle(collapsedTitle);
                    avatarView.setVisibility(View.GONE);
                } else if (verticalOffset > verticalLimit) {
                    mCollapsingToolbar.setTitle(expandedTitle);
                    avatarView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.empty:
//                Toast.makeText(ProfileActivity.this, "pulsado ", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onOptionsItemSelected: pulsado menu");
                mainscroll.fullScroll(ScrollView.FOCUS_UP);
                expandToolbar();
                return true;
            case R.id.edit_avatarpic:
                Log.d(TAG, "onOptionsItemSelected: edit avatar pic");
                startSetAvatarDialog();

                return true;
            /*case R.id.edit_name:
                Log.d(TAG, "onOptionsItemSelected: edit name");
                return true;*/
            case R.id.edit_location:
                Log.d(TAG, "onOptionsItemSelected: edit location");
                showDialogChangeLocation();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getAdsFromWeb(final int userId) {
        User u = new User(userId, "", "", "", "", 0, 0);
        Log.d(TAG, "get Ads From Web");

        AdUtils.fetchAdsVolley(u, this, new AdUtils.FetchAdsCallback() {
            @Override
            public void done(List<Ad> ads, Exception e) {
                Log.d(TAG, "done");
                if (ads != null) {
                    mAds = ads;
                    adapter = new AdsAdapter(context, mAds);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
//                    Log.d(TAG, "done: recyclerview height " + recyclerView.getHeight());
//                    Log.d(TAG, "done: layoutManager height " + layoutManager.getHeight());

                    //
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    float adDpInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135, dm);
                    float headDpInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm);
                    int adsTotalHeight = (int) ((int) (ads.size() * adDpInPx) + headDpInPx);
                    Log.d(TAG, "done: Height px :" + adsTotalHeight);
                    userads.setLayoutParams(new LinearLayout.LayoutParams(layoutManager.getWidth(), adsTotalHeight));

                    Log.d(TAG, "anuncios : " + mAds.size());
                }
            }
        });

    }

    private void getUserWeb() {
        Log.d(TAG, "getUserWeb start");
        int userId = (int) mUser.id;
        Log.d(TAG, "getUserWeb: UserId " + userId);

        UserUtils.getUser(userId, ProfileActivity.this, new UserUtils.FetchUserCallback() {
            @Override
            public void done(User user, Exception e) {
                Log.d(TAG, "getUserWeb UserUtils.getUser->done() called with: " + "user = [" + user + "], e = [" + e + "]");
                if (e != null) e.printStackTrace();
                mUserWeb = user;
                String cp = mUserWeb.getZipCode();
                ProvinciasCP.init();
                String provincia = ProvinciasCP.getNameFromCP(cp);
                weight.setText(getString(R.string.entregados) + Utils.gramsToKgStr(mUserWeb.getGrams()));
                rating.setRating(mUserWeb.getRatting());


                //get image from website
                ImageLoader imageLoader; // Get singleton instance
                imageLoader = ImageLoader.getInstance();
                String imageUri = Constants.HOME_URL + mUserWeb.getAvatar();
                ImageSize targetSize = new ImageSize(200, 200); // result Bitmap will be fit to this size

                if (!(imageUri.contains("/propias/"))) {
                    imageLoader.displayImage(imageUri, avatarView);
                }

                location.setText(cp + ", " + provincia);
            }
        });

    }

    /**
     * http://stackoverflow.com/a/30747281/385437
     */
    public void expandToolbar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppbarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.setTopAndBottomOffset(0);

            behavior.onNestedPreScroll(mRootLayout, mAppbarLayout, null, 0, 1, new int[2]);
        }
    }


    private void startSetAvatarDialog() {
        AlertDialog.Builder avatarDialog = new AlertDialog.Builder(this, R.style.yndDialog);

        avatarDialog.setTitle(getString(R.string.Picture));
        avatarDialog.setMessage(getString(R.string.pic_from_where));
        avatarDialog.setIcon(R.drawable.ic_face_white);

        avatarDialog.setPositiveButton(getString(R.string.gallery),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;

                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent,
                                GALLERY_PICTURE);
                    }
                });

        avatarDialog.setNegativeButton(getString(R.string.camera),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(f));

                        int permissionCheck = ContextCompat.checkSelfPermission(ProfileActivity.this,
                                Manifest.permission.CAMERA);

                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            startActivityForResult(intent, CAMERA_REQUEST);
                        } else {
                            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA},
                                    CAMERA_REQUEST);
                            //Toast.makeText(AdCreateActivity.this, "Cant use camera", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        avatarDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = null;
        selectedImagePath = null;
        RoundedImageView image = avatarView;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());

            if (f.exists()) {
                if (f.canRead()) {
                    Log.d(TAG, "onActivityResult: file: can read");
                } else {
                    int permissionCheck = ContextCompat.checkSelfPermission(ProfileActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onActivityResult: tengo permisos para leer external storage");
                    } else {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_RC);
                        Log.d(TAG, "onActivityResult: no tengo permisos para leer external storage");
                        return;
                    }
                }

                if (f.isFile()) {
                    Log.d(TAG, "onActivityResult: is file: " + f.getName());

                }
                if (f.isDirectory()) {
                    Log.d(TAG, "onActivityResult: file: " + f.listFiles().toString());
                }

            } else {
                Log.d(TAG, "onActivityResult: file f no existe");
            }

            setPhoto(f);

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();

                bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                // preview image
                //bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                bitmap = reziseBitMap(bitmap);

                image.setImageBitmap(bitmap);
                image.setVisibility(View.VISIBLE);

                sendAvatarToWeb();
            } else {
                Toast.makeText(getApplicationContext(), "Cancelado",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setPhoto(File f) {
        RoundedImageView image = avatarView;
        for (File temp : f.listFiles()) {
            if (temp.getName().equals("temp.jpg")) {
                f = temp;
                break;
            }
        }
        if (!f.exists()) {
            Toast.makeText(getBaseContext(),
                    "Error while capturing image", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        try {
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            bitmap = reziseBitMap(bitmap);

            int rotate = 0;
            try {
                ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);

            image.setImageBitmap(bitmap);
            image.setVisibility(View.VISIBLE);
            //storeImageTosdCard(bitmap);

            //AND NOW CAN SEND IT TO WEBSITE
            sendAvatarToWeb();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * resize to 100x100~~ respect aspect ratio
     *
     * @param bitmap
     * @return
     */
    private Bitmap reziseBitMap(Bitmap bitmap) {
        Log.d(TAG, "reziseBitMap() called with: " + "bitmap = [" + bitmap + "]");
        final int maxSize = 100;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
        return resizedBitmap;
    }

    private void sendAvatarToWeb() {
        Log.d(TAG, "sendAvatarToWeb: ");
        JSONObject jsonImage = null;

        if (bitmap != null) {
            Log.d(TAG, "sendAvatarToWeb: bitmap existe");
            //Convent Bitmap in jpeg base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            try {
                jsonImage = new JSONObject()
                        .put("filename", "u" + AppSession.getCurrentUser().id + "avatar" + Constants.DATE_JSON_SORT_FORMAT.format(new Date()))
                        .put("content_type", "image/jpeg")
                        .put("content", encodedImage);

                JSONObject jsonUser = new JSONObject();
                jsonUser.put("id", mUser.id);
                jsonUser.put("image", jsonImage);
                Log.d(TAG, "sendAvatarToWeb: jsonuser : " + jsonUser.toString(2));

                sendDataRequest(jsonUser);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private JsonObjectRequest sendDataRequest(JSONObject jsonRequest) {
        Log.d(TAG, "sendDataRequest() called with: " + "jsonRequest = [" + jsonRequest + "]");

        try {
            Log.d(TAG, "sendDataRequest: " + jsonRequest.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = VolleySingleton.getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                Constants.USERS_API_URL + "/" + mUser.id,
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse() called with: " + "response = [" + response + "]");
                        getUserWeb();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse() called with: " + "error = [" + error + "]");
                        Log.d(TAG, "onError: Hubo algun problema al actualizando");
                        String errorMessage = VolleyErrorHelper.getMessage(context, error);
                        String errorDialogMsg = Utils.showErrorsJson(errorMessage, ProfileActivity.this);
                        //Toast.makeText(AdCreateActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onErrorResponse: error message:" + errorMessage);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d(TAG, "getHeaders() called");
                Map headers = new HashMap();
                String token = AppSession.getCurrentUser().authToken;
                headers.put("Authorization", token);
                headers.put("Content-Type", "application/json; charset=utf-8");

                return headers;
            }
        };

        queue.add(request);

        return null;
    }


    private void showDialogChangeLocation() {

        // Creating alert Dialog with one Button
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.yndDialog);

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.change_location));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.enter_postal_code));

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(AppSession.lastLocation.getLatitude(), AppSession.lastLocation.getLongitude(), 1);
            input.setText(addresses.get(0).getPostalCode());
        } catch (IOException e) {
            e.printStackTrace();
        }

        input.setLayoutParams(lp);
        input.setPadding(100, 50, 100, 50);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        input.setWidth(100);
        alertDialog.setView(input);

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_pin_drop_black);

        alertDialog.setPositiveButton(getString(R.string.accept),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        Log.d(TAG, "onClick: text: " + input.getText().toString());
                        sendLocationPostalCode(input.getText().toString());

//                        Toast.makeText(getApplicationContext(),"Password Matched", Toast.LENGTH_SHORT).show();
//                        Intent myIntent1 = new Intent(context, Show.class);
//                        startActivityForResult(myIntent1, 0);
                    }
                });
        alertDialog.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // closed

        // Showing Alert Message
        alertDialog.show();
    }

    private void sendLocationPostalCode(String postalCode) {
        Log.d(TAG, "sendLocationPostalCode() called with: " + "postalCode = [" + postalCode + "]");
        JSONObject json = null;

        if (postalCode != null) {
            Log.d(TAG, "sendAvatarToWeb: bitmap existe");

            try {
                JSONObject jsonUser = new JSONObject();
                //jsonUser.put("id", mUser.id);
                jsonUser.put("zipcode", postalCode);
                Log.d(TAG, "sendLocationPostalCode jsonuser : " + jsonUser.toString(2));

                sendDataRequest(jsonUser);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }






}
