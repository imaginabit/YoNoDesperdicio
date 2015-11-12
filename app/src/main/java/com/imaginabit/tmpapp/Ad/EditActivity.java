package com.imaginabit.tmpapp.Ad;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.imaginabit.tmpapp.MainActivity;
import com.imaginabit.tmpapp.R;

//import imaginabit.yonodesperdicio_db.MainActivity;

/**
 * Created by fer2015julio on 4/09/15.
 */
public class EditActivity extends FragmentActivity implements AppCompatCallback {

    private final String LOG_TAG = EditActivity.class.getSimpleName();
    private TextView mTitleTextView, mBodyTextView;  //TODO: para todos los campos!
    private Button mButton;
    private ContentResolver mContentResolver;
    private TextView mFavoriteTextView, mUserNameTextView, mTypeTextView, mWoeidTextView, mDateCreatedTextView, mFilenameTextView, mStatusTextView, mCommentsEnabledTextView;

    //para poder usar getSupportActionBar en un fragment
    private AppCompatDelegate delegate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //let's create the delegate, passing the activity at both arguments (Activity, AppCompatCallback)
        delegate = AppCompatDelegate.create(this, this);

        //we need to call the onCreate() of the AppCompatDelegate
        delegate.onCreate(savedInstanceState);

        //we use the delegate to inflate the layout
        // setContentView(R.layout.add_edit);
        delegate.setContentView(R.layout.add_edit);

        //Finally, let's add the Toolbar (NOOOO)
        //Toolbar toolbar= (Toolbar) findViewById(R.id.);
        //delegate.setSupportActionBar(toolbar);


        //getActionBar().setDisplayHomeAsUpEnabled(true);
        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mTitleTextView = (TextView) findViewById(R.id.adTitle);
        mBodyTextView = (TextView) findViewById(R.id.adBody);

        // waiting to be added to add_edit layout
//        mUserNameTextView = (TextView) findViewById(R.id.adUserName);
//        mTypeTextView = (TextView) findViewById(R.id.adType);
//        mWoeidTextView = (TextView) findViewById(R.id.adWoeid);
//        mDateCreatedTextView = (TextView) findViewById(R.id.adDateCreated);
//        mFilenameTextView = (TextView) findViewById(R.id.adFilename);
//        mStatusTextView = (TextView) findViewById(R.id.adStatus);
//        mCommentsEnabledTextView = (TextView) findViewById(R.id.adCommentsEnabled);
//        mFavoriteTextView = (TextView) findViewById(R.id.adFavorite);

        mContentResolver = EditActivity.this.getContentResolver();

        Intent intent = getIntent();
        final String _id = intent.getStringExtra(AdsContract.AdsColumns.AD_ID);
        String title = intent.getStringExtra(AdsContract.AdsColumns.AD_TITLE);
        String body = intent.getStringExtra(AdsContract.AdsColumns.AD_BODY);

        mTitleTextView.setText(title);
        mBodyTextView.setText(body);

        mButton = (Button) findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ContentValues values = new ContentValues();
                    values.put(AdsContract.AdsColumns.AD_TITLE, mTitleTextView.getText().toString());
                    values.put(AdsContract.AdsColumns.AD_BODY, mBodyTextView.getText().toString());

                    Uri uri = AdsContract.Ads.buildAdUri(_id);
                    int recordsUpdated = mContentResolver.update(uri,values,null,null);
                    Log.d(LOG_TAG, "records updated " + recordsUpdated );
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // si pulsa home vuelve a main actitvity?
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}
