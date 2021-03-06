package com.imaginabit.yonodesperdicion.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.helpers.VolleyErrorHelper;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Offer;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OfferCreateActivity extends NavigationBaseActivity
        implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "OfferCreateActivity";

    ImageView image;
    ImageView imageEditable;
    EditText store;
    EditText title;
    EditText address;
    EditText until;
    EditText description;
    Button delete;

    Offer offer;

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    protected static final int STORAGE_PERMISSION_RC = 3;

    Bitmap bitmap;
    String selectedImagePath;

    ProgressDialog pd;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    File capturedPhoto;

    private boolean isEditing = false;

    public interface CreatedCallback {
        void onFinished();
        void onError(String errorMessage);
    }

    private CreatedCallback createdCallback;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_create);

        setSupportedActionBar(R.drawable.ic_arrow_back_black);
        context = getApplicationContext();

        image = findViewById(R.id.ad_image);
        image.setVisibility(View.INVISIBLE);

        imageEditable = findViewById(R.id.ad_image_editable);
        imageEditable.setVisibility(View.INVISIBLE);

        title = findViewById( R.id.title);
        description = findViewById(R.id.ad_description);
        store = findViewById(R.id.store);
        address = findViewById(R.id.offer_address);

        delete = findViewById(R.id.delete);

        VolleySingleton.init(this);

        FrameLayout frameImage = findViewById(R.id.frame_image);
        frameImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: cliked");
                addImageFromGalley();
            }
        });

        // Retrieve args
        if (AppSession.currentOffer  != null ){
            offer = AppSession.currentOffer;
            isEditing = true;
            Log.d(TAG, "onCreate: editing offer " + offer.getUserID() );

            title.setText(offer.getTitle());
            description.setText(offer.getDescription());
            store.setText(offer.getStore());
            address.setText(offer.getAddress());

            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            final String imageUri = offer.getImage().getMedium();

            ImageSize targetSize = new ImageSize(300, 200); // result Bitmap will be fit to this size
            imageLoader.displayImage(imageUri, image);
            image.setVisibility(View.VISIBLE);

            delete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder( OfferCreateActivity.this ,R.style.yndDialog );

                            builder.setMessage(getString(R.string.are_you_sure))
                                    .setCancelable(false)
                                    .setMessage(getString(R.string.delete_offer_msg))
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            deleteOffer();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
            );


        } else {
            delete.setVisibility(View.GONE);
        }


    }

    private void addImageFromGalley() {
        Log.d(TAG, "addImageFromGalley() called");


        Intent pictureActionIntent = null;

        //permision check android 6
        int permissionCheck = ContextCompat.checkSelfPermission(OfferCreateActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            pictureActionIntent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pictureActionIntent, STORAGE_PERMISSION_RC);
        } else {
            //Muestra el dialogo de pedir permisos
            ActivityCompat.requestPermissions(OfferCreateActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_RC);
            //luego va a OnRequestPermissionResult

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ad_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_done) {
            //Toast.makeText(AdCreateActivity.this, "guardar", Toast.LENGTH_SHORT).show();
            sendData();
            return true;
        }
        if (id == android.R.id.home ) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendData() {
        RequestQueue queue = VolleySingleton.getRequestQueue();

        JSONObject jsonOffer = new JSONObject();
        JSONObject jsonImage= null;


        try {
            if (Utils.isNotEmptyOrNull( title.getText().toString() )) jsonOffer.put("title", title.getText() );
            if (Utils.isNotEmptyOrNull( store.getText().toString() )) jsonOffer.put("store", store.getText() );
            if (Utils.isNotEmptyOrNull( address.getText().toString())) jsonOffer.put("address", address.getText() );
            if (Utils.isNotEmptyOrNull( description.getText().toString())) jsonOffer.put("description", description.getText() );

            Calendar calendar = Calendar.getInstance();
            System.out.println(calendar.getTime());// print today's date
            calendar.add(Calendar.DATE, 2);
            //hoy + 2 dias o 48 horas
            jsonOffer.put("until", Constants.DATE_JSON_FORMAT.format(calendar.getTime()));

            if (isEditing) jsonOffer.put("id",offer.getId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ( bitmap != null ){
            float aspectRatio = bitmap.getWidth() /
                    (float) bitmap.getHeight();
            int width = 500;
            int height = Math.round(width / aspectRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, width, height, false);

            //Convent Bitmap in jpeg base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            //json image format
            // "image":{"filename": "original_filename.jpeg","content_type": "image/jpeg","content": "<base64string>"}

            try {
                jsonImage = new JSONObject()
                        .put("filename", "u" + AppSession.getCurrentUser().id + "offerimage" + sdf.format(new Date()))
                        .put("content_type", "image/jpeg")
                        .put("content", encodedImage);

                jsonOffer.put("image", jsonImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            JSONObject jsonRequest = new JSONObject().put("offer", jsonOffer);
            JsonObjectRequest request;
//        if (isEditing)
//            request = sendDataEditAd(jsonRequest);
//        else
//            Log.d(TAG, "sendData: jsonReuest" + jsonRequest );
            if ( isEditing ){
                request = sendDataEdit(jsonRequest);
            }else {
                request = sendDataNew(jsonRequest);
            }
            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JsonObjectRequest sendDataNew(JSONObject jsonRequest){
        Log.d(TAG, "sendDataNewAd() called with: " + "jsonRequest = [" + jsonRequest + "]");
        return sendDataRequest(jsonRequest, Request.Method.POST, Constants.OFFERS_API_URL);
    }
    private JsonObjectRequest sendDataEdit(JSONObject jsonRequest){
        Log.d(TAG, "sendDataEdit() called with: jsonRequest = [" + jsonRequest + "]");

        return sendDataRequest(jsonRequest, Request.Method.PUT, Constants.OFFERS_API_URL + "/" + offer.getId() ) ;
    }

    private JsonObjectRequest sendDataRequest(JSONObject jsonRequest, int method, String url){
        Log.d(TAG, "sendDataRequest() called with: " + "jsonRequest = [" + jsonRequest + "], method = [" + method + "], url = [" + url + "]");

        JsonObjectRequest request = new JsonObjectRequest(method, url,
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                        try {
                        VolleyLog.v("Response:%n %s", response.toString());
//                        JSONObject offer = response.getJSONObject("offer");
                        //response is an offer plain
                        JSONObject offer = response;

                        String title = offer.getString("title");
//                        String id = offer.getString("id");

                        Toast.makeText(OfferCreateActivity.this, "Oferta creada " +  title , Toast.LENGTH_SHORT).show();

                        setResult( OfferDetailActivity.OFFER_EDIT_OK);
                        finish();

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "ErrorListener_onErrorResponse() called with: " + "error = [" + error + "]");
                        String errorMessage;
                        String errorDialogMsg;
                        errorMessage = VolleyErrorHelper.getMessage(context, error);
                        errorDialogMsg = Utils.showErrorsJson(errorMessage, OfferCreateActivity.this);
                        Toast.makeText(OfferCreateActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onErrorResponse: error message:" + errorMessage);
                    }
                }
                ){
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
        Log.d(TAG, "sendDataRequest: request: " + request.toString());
        return request;
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private Bitmap reziseBitMap(Bitmap bitmap) {
        Log.d(TAG, "reziseBitMap() called with: " + "bitmap = [" + bitmap + "]");
        final int maxSize = 400;
        return Utils.reziseBitMap(bitmap, maxSize);
    }


    // On activity result:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        bitmap = null;
        selectedImagePath = null;

        if (resultCode == RESULT_OK && requestCode == STORAGE_PERMISSION_RC) {
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
                bitmap = reziseBitMap(bitmap);
                // preview image
                image.setImageBitmap(bitmap);
                image.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(getApplicationContext(), "Cancelado",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void deleteOffer(){
        Log.d(TAG, "deleteOffer: called!" + offer );

        RequestQueue queue = VolleySingleton.getRequestQueue();

        JSONObject jsonRequest = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, Constants.OFFERS_API_URL + "/" + offer.getId(),

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d( TAG, "deleteOffer success");
                        Toast.makeText(OfferCreateActivity.this, "Oferta borrada", Toast.LENGTH_SHORT).show();

                        setResult( OfferDetailActivity.OFFER_EDIT_DELETE);
                        finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d( TAG, "deleteOffer error"  + error.getMessage() );
                        Toast.makeText(OfferCreateActivity.this, "hubo un error al borrar", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OfferCreateActivity.this,OffersOldActivity.class);
                        startActivity(intent);
                    }
                }) {
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
    }




}
