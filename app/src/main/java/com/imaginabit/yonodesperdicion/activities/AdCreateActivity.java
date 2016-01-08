package com.imaginabit.yonodesperdicion.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AdCreateActivity extends NavigationBaseActivity {

    private static final String TAG = "AdCreateActivity";
    ImageView image;
    EditText title;
    EditText weight;
    EditText expiration_date;
    EditText adDescription;
    EditText adZipCode;

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    Bitmap bitmap;
    String selectedImagePath;
    private Intent pictureActionIntent = null;
    private AdCreateCallback mCallback;
    ProgressDialog pd;



    public interface AdCreateCallback {
        public void onFinished();
        public void onError(String errorMessage);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_edit);
        setSupportedActionBar(R.drawable.ic_arrow_back_black);

        //curl -H "Content-Type: application/json"  -H "Authorization: TB1T2pDQuGYExhJQ5vYB" -X POST -d
        // '{"ad": {
        //          "title":"probando desde api",
        //          "body":"un alimento muy rico",
        //          "grams":"120",
        //          "status":"1",
        //          "food_category":"bebidas",
        //          pick_up_date
        // }}'
        // -X POST http://localhost:3000/api/ads

        image =  (ImageView) findViewById(R.id.ad_image);
        image.setVisibility(View.INVISIBLE);
        title = (EditText) findViewById( R.id.title);
        weight = (EditText) findViewById( R.id.weight);
        expiration_date = (EditText) findViewById( R.id.expiration_date);
        adDescription = (EditText) findViewById(R.id.ad_description);
        adZipCode = (EditText) findViewById(R.id.postal_code);


        Button button = (Button) findViewById(R.id.delete_ad);
        button.setVisibility(View.GONE);
        FrameLayout frameImage = (FrameLayout) findViewById(R.id.frame_image);

        frameImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });

        VolleySingleton.init(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ad_edit , menu);
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

//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                pd = ProgressDialog.show(context, "", context.getString(R.string.ad_create_message));
//            }
//        }, 1000);



        RequestQueue queue = VolleySingleton.getRequestQueue();

        JSONObject jsonAd = null;
        JSONObject jsonImage= null;
        try {
            int grams = 0;
            if (Utils.isNotEmptyOrNull( String.valueOf(weight.getText()) )){
                grams = (int) (Float.parseFloat(String.valueOf(weight.getText())) * 1000);
            }

            jsonAd = new JSONObject().put("title", title.getText() )
                    .put("body", adDescription.getText())
                    .put("grams", grams )
                    .put("status", 1)
                    .put("food_category", "bebidas")
                            //.put("province", province) //TODO calcular provincia por cp
                    .put("zipcode", adZipCode.getText() );


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

//                "image":
//                {
//                    "filename": "original_filename.jpeg",
//                        "content_type": "image/jpeg",
//                        "content": "<base64string>"
//                }
                jsonImage = new JSONObject()
                        .put("filename", "image"+ DateFormat.getDateTimeInstance().format(new Date()) )
                        .put("content_type", "image/jpeg")
                        .put("content", "<base64string>");

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
                    //parse response
                    VolleyLog.v("Response:%n %s", response.toString());

                    response.getString("title");
                    //response.getJSONObject("");
                    Log.d(TAG, "onResponse: ");
                } catch (JSONException e){
                    e.printStackTrace();
                    //Utils.dismissProgressDialog(pd);
                    //mCallback.onError(VolleySingleton.extractErrorMessage(context, error));
                }
            }
        };
    }

    private Response.ErrorListener createReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.v("Response:%n %s", error.toString());

                //Utils.dismissProgressDialog(pd);
                //error.printStackTrace();
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                Log.d(TAG, "onErrorResponse: " + error.toString());
                //mCallback.onError(VolleySingleton.extractErrorMessage(context, error));
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

                        startActivityForResult(intent,
                                CAMERA_REQUEST);

                    }
                });
        myAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        bitmap = null;
        selectedImagePath = null;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
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
}
