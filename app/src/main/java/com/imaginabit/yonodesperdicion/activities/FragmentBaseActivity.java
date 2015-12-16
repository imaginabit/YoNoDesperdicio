package com.imaginabit.yonodesperdicion.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;

/**
 * Created by antonio on 02/12/2015.
 */
public class FragmentBaseActivity extends FragmentActivity implements AppCompatCallback {
    // To use getSupportActionBar on a fragment
    private AppCompatDelegate delegate;

    /**
     * Fix the supported action bar using delegate
     */
    public AppCompatDelegate setSupportedActionBar(Bundle savedInstanceState, int conventViewResourceId) {
        // Let's create the delegate, passing the activity at both arguments (Activity, AppCompatCallback)
        delegate = AppCompatDelegate.create(this, this);
        // We need to call the onCreate() of the AppCompatDelegate
        delegate.onCreate(savedInstanceState);
        // We use the delegate to inflate the layout
        delegate.setContentView(conventViewResourceId);
        return delegate;
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
        // Not implemented
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        // Not implemented
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}
