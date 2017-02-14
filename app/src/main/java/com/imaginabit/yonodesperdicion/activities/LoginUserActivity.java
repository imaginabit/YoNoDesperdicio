package com.imaginabit.yonodesperdicion.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.helpers.UsersHelper;
import com.imaginabit.yonodesperdicion.utils.UiUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Authenticate user
 */
public class LoginUserActivity extends AppCompatActivity {
    private static final String TAG = "LoginUserActivity";

    private AppCompatButton buttonIniciar;
    private AppCompatButton buttonCrearCuenta;

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

        buttonIniciar = (AppCompatButton) findViewById( R.id.login_confirm_button );
        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEnteredCredentials()) {
                    authEnteredCredentials();
                }
            }
        });

        buttonCrearCuenta = (AppCompatButton) findViewById(R.id.login_create_button);
        buttonCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateUserActivity();
            }
        });

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

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.login_create_button:
//                startCreateUserActivity();
//                break;
//
//            case R.id.login_confirm_button:
//                if (validateEnteredCredentials()) {
//                    authEnteredCredentials();
//                }
//                break;
//        }
//    }

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
                                                //TODO: Refresh token for push notifications
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

    private void saveUserAvatar(String url){

        final Bitmap bitmap = null;

        //get image from website
        ImageLoader imageLoader; // Get singleton instance
        imageLoader = ImageLoader.getInstance();
        String imageUri = Constants.HOME_URL + url.replace("/original/","/thumb/");

        imageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // Do whatever you want with Bitmap
                String fname = "avatar.jpg";
                File file = new File ( LoginUserActivity.this.getFilesDir(), fname);
                if (file.exists()) file.delete ();

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    if (bitmap!= null) {
                        Log.d(TAG, "saveAvatar: bitmap not null");
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();
                    } else {
                        Log.d(TAG, "saveAvatar: bitmap null");
                    }
                } catch (Exception e) {
                    // FileNotFoundException
                    // IOExeption
                    e.printStackTrace();
                }
            }
        });
    }



}