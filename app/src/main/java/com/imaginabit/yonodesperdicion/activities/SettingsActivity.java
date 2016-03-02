package com.imaginabit.yonodesperdicion.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.BuildConfig;
import com.imaginabit.yonodesperdicion.R;

public class SettingsActivity extends NavigationBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settigns);

        Toolbar toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);

        TextView tv = (TextView) findViewById(R.id.version);

        tv.setText("version " + BuildConfig.VERSION_NAME );

        // Back button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
