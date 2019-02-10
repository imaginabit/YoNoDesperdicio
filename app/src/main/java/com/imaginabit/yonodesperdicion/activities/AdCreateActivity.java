package com.imaginabit.yonodesperdicion.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.support.v7.widget.AppCompatSpinner;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.utils.AdUtils;
import com.imaginabit.yonodesperdicion.utils.ProvinciasCP;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdCreateActivity extends NavigationBaseActivity
        implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AdCreateActivity";
    ImageView image;
    ImageView imageEditable;
    EditText title;
    EditText weight;
    EditText expiration_date;
    EditText adDescription;
    EditText adZipCode;
    Ad ad;
    String foodCategory;
    String[] mCategories;

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
        void onFinished();
        void onError(String errorMessage);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ad_edit);
        setSupportedActionBar(R.drawable.ic_arrow_back_black);

        context = getApplicationContext();

        image = findViewById(R.id.ad_image);
        image.setVisibility(View.INVISIBLE);

        imageEditable = findViewById(R.id.ad_image_editable);
        imageEditable.setVisibility(View.INVISIBLE);

        title = findViewById( R.id.title);
        weight = findViewById( R.id.weight);
        expiration_date = findViewById( R.id.expiration_date);
        adDescription = findViewById(R.id.ad_description);
        adZipCode = findViewById(R.id.postal_code);

        VolleySingleton.init(this);
        thisAdCreateActivity = this;

        final AppCompatSpinner spinner = findViewById(R.id.input_categoria);
        // Create an ArrayAdapter using the string array and a default spinner layout

        AdUtils.fetchCategories(new AdUtils.categoriesCallback() {
            @Override
            public void done(List<String> categories) {
                Log.d(TAG, "done: categories" + categories.toString());
                mCategories = new String[categories.size()];
                mCategories = categories.toArray(mCategories);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(thisAdCreateActivity, android.R.layout.simple_spinner_item, mCategories);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) thisAdCreateActivity);

            }

            @Override
            public void error(Exception e) {

                Log.e(TAG, "error: loading spinner cat", e );
                e.printStackTrace();

                Resources res = getResources();
                mCategories = new String[res.getStringArray(R.array.food_categories).length];
                mCategories = res.getStringArray(R.array.food_categories);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(thisAdCreateActivity, android.R.layout.simple_spinner_item, mCategories);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) thisAdCreateActivity);

            }
        });


        Button btnDeleteAd = findViewById(R.id.delete_ad);
        btnDeleteAd.setVisibility(View.GONE);

        FrameLayout frameImage = findViewById(R.id.frame_image);
        frameImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageFromGalley();
//                startDialogAddImage();
            }
        });



        // Retrieve args
        Bundle data = getIntent().getExtras();
        if (data != null) {
            Log.d(TAG, "onCreate: data " + data.toString());
            ad = data.getParcelable("ad");
        }
        if (ad != null) {
            isEditing= true;
            getSupportActionBar().setTitle("Editar " + ad.getTitle());
            btnDeleteAd.setVisibility(View.VISIBLE);

            try {
                imageEditable.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.brick));
                imageEditable.setVisibility(View.VISIBLE);
            } catch (Exception e ){
                e.printStackTrace();
            }



            //rellenar campos
            title.setText(ad.getTitle());
            weight.setText(ad.getWeightKgStr());
            expiration_date.setText( ad.getExpirationDate() );
            adZipCode.setText(Integer.toString(ad.getPostalCode()));
            adDescription.setText(ad.getBody());

            Log.d(TAG, "onCreate: image:" + ad.getImageUrl());

            ImageLoader imageLoader; // Get singleton instance
            imageLoader = ImageLoader.getInstance();
            String imageUri = Constants.HOME_URL + ad.getImageUrl();

            //Spinner Categorias get data
            String categoria = ad.getCategoria();
            List<String> foodCategoryStr = Arrays.asList((getResources().getStringArray(R.array.food_categories)));
            int catIndex = foodCategoryStr.indexOf(categoria);

            spinner.setSelection(catIndex);
            Log.d(TAG, "onCreate: cat Index " + catIndex + " categoria  "+ categoria);


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
            btnDeleteAd.setOnClickListener(
                    new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {

                           AlertDialog.Builder builder = new AlertDialog.Builder( AdCreateActivity.this ,R.style.yndDialog );

                           builder.setMessage(getString(R.string.are_you_sure))
                                   .setCancelable(false)
                                   .setMessage(getString(R.string.delete_ad_msg))
                                   .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           deleteAd();
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
        }

        // show calendar widget to select date
        expiration_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    new DatePickerDialog(AdCreateActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
    }

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);// , Locale.ENGLISH

        expiration_date.setText(sdf.format(myCalendar.getTime()));
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

    private void deleteAd(){
        Log.d(TAG, "deleteAd: called!");

        RequestQueue queue = VolleySingleton.getRequestQueue();

        JSONObject jsonRequest = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, Constants.ADS_API_URL + "/" + ad.getId(),
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d( TAG, "deleteAd success");
                        Toast.makeText(AdCreateActivity.this, "Anuncio borrado", Toast.LENGTH_SHORT).show();

                        setResult( AdDetailActivity.AD_EDIT_DELETE );
                        finish();

//                        Intent intent = new Intent(AdCreateActivity.this,MainActivity.class);
//                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d( TAG, "deleteAd error"  + error.getMessage() );
                        //Toast.makeText(AdCreateActivity.this, "hubo un error al borrar", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdCreateActivity.this,MainActivity.class);
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

    private void sendAdData() {
        RequestQueue queue = VolleySingleton.getRequestQueue();

        JSONObject jsonAd = null;
        JSONObject jsonImage= null;
        ProvinciasCP.init();
        int grams;
        grams = 0;
        if (Utils.isNotEmptyOrNull(String.valueOf(weight.getText()))) {
            float f = Float.parseFloat(weight.getText().toString().replaceAll("[^0-9.]", ""));
            grams = (int) (f * 1000);
        }

        String provincia = "";
        String zipCode = adZipCode.getText().toString();
        if (Utils.isNotEmptyOrNull(zipCode)) {
            int codigoProvincia = Integer.parseInt(zipCode.substring(0, 2));
            try {
                provincia = ProvinciasCP.mProvincias.get(codigoProvincia - 1).mProvincia;
                Log.d(TAG, "sendAdData: c provincia" + provincia);
            } catch (Exception e) {
                Log.d(TAG, "sendAdData: Error al sacar la provincia del codigo postal: " + codigoProvincia);
            }
        }

        try {
            jsonAd = new JSONObject();

            if( Utils.isNotEmpty( title.getText().toString() )) jsonAd.put("title", title.getText());
            if( Utils.isNotEmpty( adDescription.getText().toString() ) ) jsonAd.put( "body", adDescription.getText() );
            jsonAd.put( "grams", grams );
//                if( Utils.isNotEmpty( ad.getStatus() ) jsonAd.put( "status", ad.G ); // cant change status
            jsonAd.putOpt("status", 1);
            if( Utils.isNotEmpty( provincia ) ) jsonAd.put( "province", provincia );
            if( Utils.isNotEmpty( adZipCode.getText().toString() ) ) jsonAd.put( "zipcode", adZipCode.getText() );
            if( Utils.isNotEmpty( this.foodCategory ) ) jsonAd.put( "food_category", this.foodCategory );
            if( Utils.isNotEmpty( expiration_date.getText().toString() ) ) jsonAd.put( "pick_up_date", expiration_date.getText() );

            if (isEditing) {
                jsonAd.put("id",ad.getId());
            }

            if (bitmap != null) {
                //scale image max 400px width
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

                jsonImage = new JSONObject()
                        .put("filename", "u" + AppSession.getCurrentUser().id + "image" + sdf.format(new Date()))
                        .put("content_type", "image/jpeg")
                        .put("content", encodedImage);

                jsonAd.put("image", jsonImage);
            }

            JSONObject jsonRequest = new JSONObject().put("ad", jsonAd);

            Log.d(TAG, " ---- sendAdData: jsonRequest :" + jsonRequest.toString());

            JsonObjectRequest request;
            if (isEditing)
                request = sendDataEditAd(jsonRequest);
            else
                request = sendDataNewAd(jsonRequest);

            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JsonObjectRequest sendDataNewAd(JSONObject jsonRequest){
        Log.d(TAG, "sendDataNewAd() called with: " + "jsonRequest = [" + jsonRequest + "]");
        return sendDataRequest(jsonRequest, Request.Method.POST, Constants.ADS_API_URL);
    }
    private JsonObjectRequest sendDataEditAd(JSONObject jsonRequest){
        Log.d(TAG, "sendDataEditAd() called with: " + "jsonRequest = [" + jsonRequest + "]");
        return sendDataRequest(jsonRequest , Request.Method.PUT, Constants.ADS_API_URL+ "/"+ ad.getId() );
    }

    private JsonObjectRequest sendDataRequest(JSONObject jsonRequest, int method, String url){
        Log.d(TAG, "sendDataRequest() called with: " + "jsonRequest = [" + jsonRequest + "], method = [" + method + "], url = [" + url + "]");

        JsonObjectRequest request = new JsonObjectRequest( method , url ,
                jsonRequest,
                createResponseSuccessListener(), 
                createReqErrorListener()) {
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


    private Response.Listener<JSONObject> createResponseSuccessListener(){
        Log.d(TAG, "createResponseSuccessListener: ");
        return new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString());

                    //response.getString("title");
                    Log.d(TAG, "createResponseSuccessListener_onResponse: " + response.toString());

                    JSONObject ad = response.getJSONObject("ad");
                    String title = ad.getString("title");
                    String id = ad.getString("id");

                    if(isEditing) {
                        Toast.makeText(AdCreateActivity.this, "Anuncio editado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdCreateActivity.this,MainActivity.class);
                        startActivity(intent);
                    } else
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
                Log.d(TAG, "ErrorListener_onErrorResponse() called with: " + "error = [" + error + "]");
                String errorMessage;
                String errorDialogMsg;
                errorMessage = VolleyErrorHelper.getMessage(context, error);
                errorDialogMsg = Utils.showErrorsJson(errorMessage, AdCreateActivity.this);
                //Toast.makeText(AdCreateActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse: error message:" + errorMessage);
            }
        };
    }

    private void addImageFromGalley(){
        Log.d(TAG, "addImageFromGalley() called");


        Intent pictureActionIntent = null;

        //permision check android 6
        int permissionCheck = ContextCompat.checkSelfPermission(AdCreateActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            pictureActionIntent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pictureActionIntent, STORAGE_PERMISSION_RC);
        } else {
            //Muestra el dialogo de pedir permisos
            ActivityCompat.requestPermissions(AdCreateActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_RC);
            //luego va a OnRequestPermissionResult

        }

    }




    private void startDialogAddImage() {
        Log.d(TAG, "startDialogAddImage() called");
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this ,R.style.yndDialog );

        myAlertDialog.setTitle(getString(R.string.Picture));
        myAlertDialog.setMessage(getString(R.string.pic_from_where));

        myAlertDialog.setPositiveButton(getString(R.string.gallery),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;

                        //permision check android 6
                        int permissionCheck = ContextCompat.checkSelfPermission(AdCreateActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE);

                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
                        } else {
                            ActivityCompat.requestPermissions(AdCreateActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    GALLERY_PICTURE);
                            //Toast.makeText(AdCreateActivity.this, "Cant use camera", Toast.LENGTH_SHORT).show();
                            //call the dialog again
                            startDialogAddImage();
                        }

                    }
                });

        myAlertDialog.setNegativeButton(getString(R.string.camera),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);

//                        File imagePath = new File(getApplicationContext().getFilesDir(), "images");
//                        File file = new File(imagePath, "temp.jpg");
//                        Uri contentUri = getUriForFile(getApplicationContext(), "com.imaginabit.yonodesperdicion.GenericFileProvider", file);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                        //permision check android 6
                        int permissionCheck = ContextCompat.checkSelfPermission(AdCreateActivity.this,
                                Manifest.permission.CAMERA);

                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                            startActivityForResult(intent, CAMERA_REQUEST);
                        } else {
                            ActivityCompat.requestPermissions(AdCreateActivity.this, new String[]{Manifest.permission.CAMERA},
                                    CAMERA_REQUEST);
                            //Toast.makeText(AdCreateActivity.this, "Cant use camera", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        myAlertDialog.show();
    }


    //esto esta en onRequestPermissionsResult y  startDialogAddImage creo que se usa el de arriba la mayoria de las veces

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult() called with: requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults + "]");

        switch (requestCode) {
            case CAMERA_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

//                    File imagePath = new File(getApplicationContext().getFilesDir(), "images");
//                    File f = new File(imagePath, "temp.jpg");
//                    Uri contentUri = getUriForFile(getApplicationContext(), "com.imaginabit.yonodesperdicion.GenericFileProvider", f);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

//                    Metodo antiguo:
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(f));


                    startActivityForResult(intent, CAMERA_REQUEST);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "onRequestPermissionResult: CAMERA_REQUEST false" );
                }
                return;
            }
            case STORAGE_PERMISSION_RC:{
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    File f = new File(Environment.getExternalStorageDirectory()
                            .toString());
                    setPhoto(f);


                } else {
                    Toast.makeText(this, R.string.No_permission_files, Toast.LENGTH_SHORT).show();
                }
            }
            case Constants.PERMISSION_REQUEST_ACCESS_COARSE_LOCATION:{
                Log.d(TAG, "onRequestPermissionsResult: PERMISSION_REQUEST_ACCESS_COARSE_LOCATION");
            }

        }
    }

    // On activity result:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        bitmap = null;
        selectedImagePath = null;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            Log.d(TAG, "onActivityResult: camera request ");
            Toast.makeText(this, "Image saved to:\n" +
                    data.getExtras().get("data"), Toast.LENGTH_LONG).show();


            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());

            File imagePath = new File(getApplicationContext().getFilesDir(), "images");
            File newFile = new File(imagePath, "temp.jpg");

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

        } else if (resultCode == RESULT_OK && requestCode == STORAGE_PERMISSION_RC) {
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
            bitmap = rotateExif(bitmap, f);
            bitmap = reziseBitMap(bitmap);

            image.setImageBitmap(bitmap);
            image.setVisibility(View.VISIBLE);
            //storeImageTosdCard(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * rotate image to macth the exif info
     * @param bitmap
     * @return
     */
    private Bitmap rotateExif(Bitmap bitmap, File f) {

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
        return bitmap;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected() called with: " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
        this.foodCategory = mCategories[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TAG, "onNothingSelected() called with: " + "parent = [" + parent + "]");

    }

    private Bitmap reziseBitMap(Bitmap bitmap) {
        Log.d(TAG, "reziseBitMap() called with: " + "bitmap = [" + bitmap + "]");
        final int maxSize = 400;
        return Utils.reziseBitMap(bitmap, maxSize);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder( this ,R.style.yndDialog );

        builder.setMessage(getString(R.string.are_you_sure))
                .setCancelable(false)
                .setMessage(getString(R.string.lost_data))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AdCreateActivity.this.finish();
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



