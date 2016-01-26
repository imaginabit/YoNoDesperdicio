package com.imaginabit.yonodesperdicion.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.utils.ProvinciasCP;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AdCreateActivity extends NavigationBaseActivity {

    private static final String TAG = "AdCreateActivity";
    ImageView image;
    ImageView imageEditable;
    EditText title;
    EditText weight;
    EditText expiration_date;
    EditText adDescription;
    EditText adZipCode;
    Ad ad;

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    protected static final int STORAGE_PERMISSION_RC = 3;
    Bitmap bitmap;
    String selectedImagePath;
    private Intent pictureActionIntent = null;
    private AdCreateCallback mCallback;
    ProgressDialog pd;
    Activity thisAdCreateActivity;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    File capturedPhoto;

    private boolean isEditing= false;

    public interface AdCreateCallback {
        public void onFinished();
        public void onError(String errorMessage);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ad_edit);
        setSupportedActionBar(R.drawable.ic_arrow_back_black);

        context = getApplicationContext();

        image =  (ImageView) findViewById(R.id.ad_image);
        image.setVisibility(View.INVISIBLE);

        imageEditable =  (ImageView) findViewById(R.id.ad_image_editable);
        imageEditable.setVisibility(View.INVISIBLE);

        title = (EditText) findViewById( R.id.title);
        weight = (EditText) findViewById( R.id.weight);
        expiration_date = (EditText) findViewById( R.id.expiration_date);
        adDescription = (EditText) findViewById(R.id.ad_description);
        adZipCode = (EditText) findViewById(R.id.postal_code);


        Button btnDeleteAd = (Button) findViewById(R.id.delete_ad);
        btnDeleteAd.setVisibility(View.GONE);

        FrameLayout frameImage = (FrameLayout) findViewById(R.id.frame_image);
        frameImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });

        VolleySingleton.init(this);

        thisAdCreateActivity = this;

        // Retrieve args
        Bundle data = getIntent().getExtras();
        if (data != null) {
            Log.d(TAG, "onCreate: data " + data.toString());
            ad = (Ad) data.getParcelable("ad");
        }
        if (ad != null) {
            isEditing= true;
            getSupportActionBar().setTitle("Editar " + ad.getTitle());
            btnDeleteAd.setVisibility(View.VISIBLE);

            imageEditable.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.brick));
            imageEditable.setVisibility(View.VISIBLE);

            //rellenar campos
            title.setText(ad.getTitle());
            weight.setText(ad.getWeightKgStr());
            expiration_date.setText( ad.getExpirationDate() );
            adZipCode.setText( Integer.toString(ad.getPostalCode()) );
            adDescription.setText(ad.getBody());

            Log.d(TAG, "onCreate: image:"+ ad.getImageUrl());

            ImageLoader imageLoader; // Get singleton instance
            imageLoader = ImageLoader.getInstance();
            String imageUri = Constants.HOME_URL + ad.getImageUrl();

            //me da a mi que ha esto no le esta haciendo ningun caso
            ImageSize targetSize = new ImageSize(300, 200); // result Bitmap will be fit to this size
            try {
                imageLoader.displayImage(imageUri, imageEditable );
                //imageEditable.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } catch ( Exception e){
                e.printStackTrace();
                //imageEditable.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.brick));
            }

            //borrar anuncio

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
            sendAdData();
            return true;
        }
        if (id == android.R.id.home ) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendAdData() {
        RequestQueue queue = VolleySingleton.getRequestQueue();

        JSONObject jsonAd = null;
        JSONObject jsonImage= null;
        ProvinciasCP.init();

        try {
            int grams = 0;
            if (Utils.isNotEmptyOrNull( String.valueOf(weight.getText()) )){
                grams = (int) (Float.parseFloat(String.valueOf(weight.getText())) * 1000);
            }

            String provincia = "";
            String zipCode = adZipCode.getText().toString();
            if(Utils.isNotEmptyOrNull( zipCode ) ){
                int codigoProvincia = Integer.parseInt(zipCode.substring(0, 2));
                try {
                    provincia = ProvinciasCP.mProvincias.get(codigoProvincia-1).mProvincia;
                    Log.d(TAG, "sendAdData: c provincia"+ provincia);
                }catch (Exception e){
                    Log.d(TAG, "sendAdData: Error al sacar la provincia del codigo postal: " + codigoProvincia);
                }
            }

            //String pickUpDate= sdf.format(expiration_date.getText());

            jsonAd = new JSONObject().put("title", title.getText() )
                    .put("body", adDescription.getText())
                    .put("grams", grams )
                    .put("status", 1)
                    .put("food_category", "bebidas")
                    .put("province", provincia)
                    .put("zipcode", adZipCode.getText() )
                    .put("pick_up_date", expiration_date.getText());

            if (isEditing){
                //TODO: edit ad
                // no funciona por ahora
                //jsonAd.put("id");
            }


            if (bitmap != null) {

                //scale image max 400px width
                float aspectRatio = bitmap.getWidth() /
                        (float) bitmap.getHeight();
                int width = 400;
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

                jsonImage = new JSONObject()
                        .put("filename", "u"+ AppSession.getCurrentUser().id + "image"+ sdf.format(new Date()) )
                        .put("content_type", "image/jpeg")
                        .put("content", encodedImage);

                jsonAd.put("image",jsonImage);
            }

            JSONObject jsonRequest = new JSONObject().put("ad", jsonAd);

            Log.d(TAG, " ---- sendAdData: jsonRequest :"+ jsonRequest.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.ADS_API_URL,
                    jsonRequest,
                    createResponseSuccessListener(), createReqErrorListener() ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map headers = new HashMap();
                    String token = AppSession.getCurrentUser().authToken;
                    headers.put("Authorization", token);
                    Log.d(TAG, "getHeaders: authToken " + token);

                    headers.put("Content-Type", "application/json; charset=utf-8");


                    return headers;
                }
//
//                @Override
//                public String getBodyContentType() {
//                    //return super.getBodyContentType();
//                    return "application/json; charset=utf-8";
//                }
//
//                @Override
//                public byte[] getBody()
//                {
//                    String body = "some text";
//                    try
//                    {
//                        return body.getBytes(getParamsEncoding());
//                    }
//                    catch (UnsupportedEncodingException uee)
//                    {
//                        throw new RuntimeException("Encoding not supported: "
//                                + getParamsEncoding(), uee);
//                    }
//                }

            };
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private Response.Listener<JSONObject> createResponseSuccessListener(){
        return new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString());

                    //response.getString("title");
                    Log.d(TAG, "onResponse: " + response.toString());

                    JSONObject ad = response.getJSONObject("ad");
                    String title = ad.getString("title");
                    String id = ad.getString("id");

                    Toast.makeText(AdCreateActivity.this, "Anuncio creado", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: title " + title + " " + id);
                    thisAdCreateActivity.finish();
                } catch (JSONException e){
                    e.printStackTrace();
                    //Utils.dismissProgressDialog(pd);
                }
            }
        };
    }

    private Response.ErrorListener createReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage;
                String errorDialogMsg;
                errorMessage = VolleyErrorHelper.getMessage(context, error);
                errorDialogMsg = Utils.showErrorsJson(errorMessage, AdCreateActivity.this);
                //Toast.makeText(AdCreateActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse: error message:" + errorMessage);
            }
        };
    }


    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this ,R.style.yndDialog );

        myAlertDialog.setTitle("Foto");
        myAlertDialog.setMessage("¿Desde donde quieres obtener la foto?");

        myAlertDialog.setPositiveButton("Fotos",
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

        myAlertDialog.setNegativeButton("Camara",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(f));

                        int permissionCheck = ContextCompat.checkSelfPermission(AdCreateActivity.this,
                                Manifest.permission.CAMERA);

                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            startActivityForResult(intent, CAMERA_REQUEST);
                        } else {
                            ActivityCompat.requestPermissions(AdCreateActivity.this, new String[]{Manifest.permission.CAMERA},
                                    CAMERA_REQUEST);
                            //Toast.makeText(AdCreateActivity.this, "Cant use camera", Toast.LENGTH_SHORT).show();

                        }


                        // Here, thisActivity is the current activity
//                        if (ContextCompat.checkSelfPermission(AdCreateActivity.this,
//                                Manifest.permission.CAMERA)
//                                != PackageManager.PERMISSION_GRANTED) {
//
//                            // Should we show an explanation?
//                            if (ActivityCompat.shouldShowRequestPermissionRationale(AdCreateActivity.this,
//                                    Manifest.permission.CAMERA)) {
//
//                                // Show an expanation to the user *asynchronously* -- don't block
//                                // this thread waiting for the user's response! After the user
//                                // sees the explanation, try again to request the permission.
//
//                            } else {
//
//                                // No explanation needed, we can request the permission.
//
//                                ActivityCompat.requestPermissions(AdCreateActivity.this,
//                                        new String[]{Manifest.permission.CAMERA},
//                                        CAMERA_REQUEST);
//
//                                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                                // app-defined int constant. The callback method gets the
//                                // result of the request.
//                            }
//                        }

                    }
                });
        myAlertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(f));
                    startActivityForResult(intent, CAMERA_REQUEST);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case STORAGE_PERMISSION_RC:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    File f = new File(Environment.getExternalStorageDirectory()
                            .toString());
                    setPhoto(f);
                } else {
                    Toast.makeText(this, "No permission to read external storage.", Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        bitmap = null;
        selectedImagePath = null;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
//            Toast.makeText(this, "Image saved to:\n" +
//                    data.getExtras().get("data"), Toast.LENGTH_LONG).show();


            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());

            Log.d(TAG, "onActivityResult: file: " + f.toString());
            if(f.exists()) {
                Log.d(TAG, "onActivityResult: Existe!");
                if (f.canRead() ){
                    Log.d(TAG, "onActivityResult: file: can read" );
                }else{
                    int permissionCheck = ContextCompat.checkSelfPermission(AdCreateActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(AdCreateActivity.this, "Puedo leer ", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onActivityResult: tengo permisos para leer external storage");
                        //startActivityForResult(intent, CAMERA_REQUEST);
                    } else {
                        ActivityCompat.requestPermissions(AdCreateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_RC);
                        //Toast.makeText(AdCreateActivity.this, "Cant use camera", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onActivityResult: no tengo permisos para leer external storage");
                        return;
                    }
                }

                if (f.isFile()) {
                    Log.d(TAG, "onActivityResult: is file: " + f.getName());

                }
                if (f.isDirectory()){
                    Log.d(TAG, "onActivityResult: file: " + f.listFiles().toString());
                }
                
            }else {
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

//                if (selectedImagePath != null) {
//                    txt_image_path.setText(selectedImagePath);
//                }

                bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                // preview image
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);

                image.setImageBitmap(bitmap);
                image.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(getApplicationContext(), "Cancelado",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void setPhoto(File f){
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

            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
