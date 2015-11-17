package com.imaginabit.yonodesperdicion.Ad;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.MainActivity;
import com.imaginabit.yonodesperdicion.R;

//import imaginabit.yonodesperdicio_db.MainActivity;

/**
 * Created by fer2015julio on 4/09/15.
 */
public class AddActivity extends FragmentActivity {

    private final String LOG_TAG = AddActivity.class.getSimpleName();
    private TextView mTitleTextView, mBodyTextView;  //TODO: para todos los campos!
    private Button mButton;
    private ContentResolver mContentResolver;
    private TextView mFavoriteTextView;
    private TextView mUserNameTextView;
    private TextView mWoeidTextView;
    private DatePicker mDateCreatedTextView;
    private TextView mFilenameTextView;
    private Spinner mStatusTextView;
    private TextView mCommentsEnabledTextView;
    private Spinner mTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        mTitleTextView = (TextView) findViewById(R.id.adTitle);
        mBodyTextView = (TextView) findViewById(R.id.adBody);

        // waiting to be added to add_edit layout
        mUserNameTextView = (TextView) findViewById(R.id.adUserName);
        mTypeSpinner = (Spinner) findViewById(R.id.adType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ad_types, android.R.layout.simple_spinner_item);
//      en lugar de R.array.ad_types se podria crear: Ad.Type.names() ver http://stackoverflow.com/questions/13783295/getting-all-names-in-an-enum-as-a-string
        mTypeSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        mWoeidTextView = (TextView) findViewById(R.id.adWoeid);
        mDateCreatedTextView = (DatePicker) findViewById(R.id.adDateCreated);
//        mFilenameTextView = (TextView) findViewById(R.id.adFilename);

        mStatusTextView = (Spinner) findViewById(R.id.adStatus);
        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(this,
                R.array.ad_status, android.R.layout.simple_spinner_item);
        mStatusTextView.setAdapter(adapterStatus);


        mCommentsEnabledTextView = (TextView) findViewById(R.id.adCommentsEnabled);
        mFavoriteTextView = (TextView) findViewById(R.id.adFavorite);


        mContentResolver = AddActivity.this.getContentResolver();

        mButton = (Button) findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    ContentValues values = new ContentValues();
                    values.put(AdsContract.AdsColumns.AD_TITLE, mTitleTextView.getText().toString());
                    values.put(AdsContract.AdsColumns.AD_BODY, mBodyTextView.getText().toString());

                    Uri returned = mContentResolver.insert(AdsContract.URI_TABLE, values);
                    Log.d(LOG_TAG, "record id returned is " + returned.toString());
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please ensure your have entered some valid data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean isValid(){
        if (someDataEntered()){
            //validar datos uno por uno
            return true;
        }
        return false;
    }

    private boolean someDataEntered(){
        if(mTitleTextView.getText().toString().length() == 0 ||
                mBodyTextView.getText().toString().length() == 0){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if(someDataEntered()){
            AdsDialog dialog = new AdsDialog();
            Bundle args = new Bundle();
            args.putString(AdsDialog.DIALOG_TYPE, AdsDialog.CONFIRM_EXIT );
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "confirm-exit");

        }else {
            super.onBackPressed();
        }
    }
}
