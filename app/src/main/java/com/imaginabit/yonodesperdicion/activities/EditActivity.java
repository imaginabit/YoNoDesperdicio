package com.imaginabit.yonodesperdicion.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.R;

/**
 * Created by fer2015julio on 4/09/15.
 */
public class EditActivity extends FragmentBaseActivity {
    private static final String TAG = EditActivity.class.getSimpleName();

    private Button saveButton;

    private TextView titleTextView;
    private TextView bodyTextView;
    private TextView favoriteTextView;
    private TextView userNameTextView;
    private TextView typeTextView;
    private TextView woeidTextView;

    private TextView dateCreatedTextView;
    private TextView filenameTextView;
    private TextView statusTextView;
    private TextView commentsEnabledTextView;

    // Current Ad
    private String adId;
    private String adTitle;
    private String adBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate delegate = setSupportedActionBar(savedInstanceState, R.layout.add_edit);
        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Args
        Intent intent = getIntent();


        titleTextView = (TextView) findViewById(R.id.adTitle);
        bodyTextView = (TextView) findViewById(R.id.adBody);

        // waiting to be added to add_edit layout
//        userNameTextView = (TextView) findViewById(R.id.adUserName);
//        typeTextView = (TextView) findViewById(R.id.adType);
//        woeidTextView = (TextView) findViewById(R.id.adWoeid);
//        dateCreatedTextView = (TextView) findViewById(R.id.adDateCreated);
//        filenameTextView = (TextView) findViewById(R.id.adFilename);
//        statusTextView = (TextView) findViewById(R.id.adStatus);
//        commentsEnabledTextView = (TextView) findViewById(R.id.adCommentsEnabled);
//        favoriteTextView = (TextView) findViewById(R.id.adFavorite);

        titleTextView.setText(adTitle);
        bodyTextView.setText(adBody);

        // Save button
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateCurrentAd()) {
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * Update current ad with the entered data
     */
    private boolean updateCurrentAd() {
        return false;
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
}
