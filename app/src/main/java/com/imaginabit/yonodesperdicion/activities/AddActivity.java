package com.imaginabit.yonodesperdicion.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.ads.AdsContract;
import com.imaginabit.yonodesperdicion.ads.AdsDialog;

/**
 * Created by fer2015julio on 4/09/15.
 */
public class AddActivity extends FragmentBaseActivity {
    private final String LOG_TAG = AddActivity.class.getSimpleName();

    private TextView titleTextView, bodyTextView;  //TODO: para todos los campos!
    private Button mButton;
    private ContentResolver contentResolver;
    private TextView favoriteTextView;
    private TextView userNameTextView;
    private TextView woeidTextView;
    private DatePicker dateCreatedTextView;
    private TextView filenameTextView;
    private Spinner statusTextView;
    private TextView commentsEnabledTextView;
    private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate delegate = setSupportedActionBar(savedInstanceState, R.layout.add_edit);
        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleTextView = (TextView) findViewById(R.id.adTitle);
        bodyTextView = (TextView) findViewById(R.id.adBody);

        // waiting to be added to add_edit layout
        userNameTextView = (TextView) findViewById(R.id.adUserName);
        typeSpinner = (Spinner) findViewById(R.id.adType);

       // En lugar de R.array.ad_types se podria crear: Ad.Type.names() 
       // ver http://stackoverflow.com/questions/13783295/getting-all-names-in-an-enum-as-a-string        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ad_types, android.R.layout.simple_spinner_item);
        typeSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        woeidTextView = (TextView) findViewById(R.id.adWoeid);
        dateCreatedTextView = (DatePicker) findViewById(R.id.adDateCreated);

//        filenameTextView = (TextView) findViewById(R.id.adFilename);

        statusTextView = (Spinner) findViewById(R.id.adStatus);
        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(this, R.array.ad_status, android.R.layout.simple_spinner_item);
        statusTextView.setAdapter(adapterStatus);

        commentsEnabledTextView = (TextView) findViewById(R.id.adCommentsEnabled);
        favoriteTextView = (TextView) findViewById(R.id.adFavorite);
        
        contentResolver = AddActivity.this.getContentResolver();

        mButton = (Button) findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    ContentValues values = new ContentValues();
                    values.put(AdsContract.AdsColumns.AD_TITLE, titleTextView.getText().toString());
                    values.put(AdsContract.AdsColumns.AD_BODY, bodyTextView.getText().toString());

                    Uri returned = contentResolver.insert(AdsContract.URI_TABLE, values);
                    Log.d(LOG_TAG, "record id returned is " + returned.toString());
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } 
                else {
                    Toast.makeText(getApplicationContext(), "Please ensure your have entered some valid data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean isValid(){
        if (someDataEntered()) {
            //validar datos uno por uno
            return true;
        }
        return false;
    }

    private boolean someDataEntered(){
        if (titleTextView.getText().toString().length() == 0 || bodyTextView.getText().toString().length() == 0){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (someDataEntered()){
            AdsDialog dialog = new AdsDialog();
            Bundle args = new Bundle();
            args.putString(AdsDialog.DIALOG_TYPE, AdsDialog.CONFIRM_EXIT );
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "confirm-exit");
        } 
        else {
            super.onBackPressed();
        }
    }
}
