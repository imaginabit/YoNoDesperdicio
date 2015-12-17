package com.imaginabit.yonodesperdicion.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.imaginabit.yonodesperdicion.R;

/**
 * User access panel
 */
public class LoginPanelActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_panel_activity);

        // Layout toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);

        AppCompatButton loginPanelGoogleLoginButton = (AppCompatButton) findViewById(R.id.login_panel_google_login_button);
        loginPanelGoogleLoginButton.setVisibility(View.GONE);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Home/Back
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Local

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_panel_create_account_button:
                Intent createUserIntent = new Intent(this, CreateUserActivity.class);
                startActivityForResult(createUserIntent, 0);
                break;

            case R.id.login_panel_login_button:
                Intent loginUserIntent = new Intent(this, LoginUserActivity.class);
                startActivityForResult(loginUserIntent, 1);
                break;

            case R.id.login_panel_google_login_button:
                Intent googleSignInIntent = new Intent(this, GoogleSignInActivity.class);
                startActivityForResult(googleSignInIntent, 2);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    break;

                case 1:
                    break;

                case 2:
                    break;
            }
        }
    }
}