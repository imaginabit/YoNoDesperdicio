package com.imaginabit.yonodesperdicion.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.helpers.UsersHelper;
import com.imaginabit.yonodesperdicion.utils.UiUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;

/**
 * Authenticate user
 */
public class LoginUserActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginUserActivity";

    private TextInputLayout userNameWrapper;
    private EditText userNameEdit;

    private TextInputLayout userPasswordWrapper;
    private EditText userPasswordEdit;

    private Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user_activity);

        // Credentials
        userNameWrapper = (TextInputLayout) findViewById(R.id.login_user_username_wrapper);
        userNameEdit = (EditText) findViewById(R.id.login_user_username);
        userPasswordWrapper = (TextInputLayout) findViewById(R.id.create_user_user_password_wrapper);
        userPasswordEdit = (EditText) findViewById(R.id.login_user_password);

        // Arguments
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String username = extras.getString("username");
            String password = extras.getString("password");
            if (username != null && password != null) {
                userNameEdit.setText(username);
                userPasswordEdit.setText(password);
            }
        }

        // Layout toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Home/Back
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            hideKeyboard();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        hideKeyboard();
        finish();
    }

    // Local

    private void startCreateUserActivity() {
        hideKeyboard();
        Intent createUserIntent = new Intent(this, CreateUserActivity.class);
        startActivity(createUserIntent);
    }

    private void hideKeyboard() {
        UiUtils.hideKeyboard(this, userNameEdit.getWindowToken());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_create_button:
                startCreateUserActivity();
                break;

            case R.id.login_confirm_button:
                if (validateEnteredCredentials()) {
                    authEnteredCredentials();
                }
                break;
        }
    }

    /**
     * Authenticate entered credentials
     */
    private void authEnteredCredentials() {
        // Extract entered data
        String userName = userNameEdit.getText().toString().trim();
        String userPassword = userPasswordEdit.getText().toString().trim();

        UsersHelper.authenticate(
                                    this,
                                    userName,
                                    userPassword,
                                    new UsersHelper.UserAccountCallback() {
                                        @Override
                                        public void onFinished(UserData user) {
                                            if (user == null || user.id == 0L) {
                                                Log.d(TAG, "onFinished: User null!");
                                                // @TODO error
                                            } else {
                                                user.prefsRemove(getActivity());
                                                user.prefsCommit(getActivity());
                                                AppSession.restart(getActivity());
                                            }
                                        }

                                        @Override
                                        public void onError(String errorMessage) {
                                            UiUtils.showMessage(getActivity(), "Entra en tu cuenta", errorMessage);
                                        }
                                    }
                                );
    }

    /**
     * Validate entered credentials
     */
    private boolean validateEnteredCredentials() {
        // Extract entered data
        String userName = userNameEdit.getText().toString().trim();
        String userPassword = userPasswordEdit.getText().toString().trim();

        // Validate
        if (Utils.isEmptyOrNull(userName)) {
            UiUtils.errorShake(this, userNameWrapper);
        } else if (Utils.isEmptyOrNull(userPassword)) {
            UiUtils.errorShake(this, userPasswordWrapper);
        }
        else {
            return true;
        }

        // Default return
        return false;
    }



}