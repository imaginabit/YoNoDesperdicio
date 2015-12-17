package com.imaginabit.yonodesperdicion.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.helpers.UsersHelper;
import com.imaginabit.yonodesperdicion.utils.UiUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;

/**
 * Create user
 */
public class CreateUserActivity extends AppCompatActivity implements View.OnClickListener {
    // Total number of pages
    private static final int MAX_FORM_PAGES = 3;

    // Page 1
    private LinearLayout layoutPage1;

    private TextInputLayout userFullnameWrapper;
    private EditText userFullnameEdit;

    private TextInputLayout userNameWrapper;
    private EditText userNameEdit;

    private TextInputLayout userEmailWrapper;
    private EditText userEmailEdit;

    // Page 2
    private LinearLayout layoutPage2;

    private TextInputLayout userPasswordWrapper;
    private EditText userPasswordEdit;

    private TextInputLayout userRepeatPasswordWrapper;
    private EditText userRepeatPasswordEdit;

    // Page 3
    private LinearLayout layoutPage3;

    private TextInputLayout userCityWrapper;
    private EditText userCityEdit;

    private TextInputLayout userProvinceWrapper;
    private EditText userProvinceEdit;

    private TextInputLayout userZipCodeWrapper;
    private EditText userZipCodeEdit;

    private AppCompatButton nextButton;
    private AppCompatButton confirmButton;
    private TextView legalInfo;

    // Current form index
    private int currentFormIndex = 0;

    private Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user_activity);

        // Page 1 properties
        layoutPage1 = (LinearLayout) findViewById(R.id.create_user_page_1);
        userFullnameEdit = (EditText) findViewById(R.id.create_user_user_fullname);
        userNameEdit = (EditText) findViewById(R.id.create_user_username);
        userEmailEdit = (EditText) findViewById(R.id.create_user_user_email);

        // Page 2 properties
        layoutPage2 = (LinearLayout) findViewById(R.id.create_user_page_2);
        userPasswordEdit = (EditText) findViewById(R.id.create_user_user_password);
        userRepeatPasswordEdit = (EditText) findViewById(R.id.create_user_user_repeat_password);

        // Page 2 properties
        layoutPage3 = (LinearLayout) findViewById(R.id.create_user_page_3);
        userCityEdit = (EditText) findViewById(R.id.create_user_user_city);
        userProvinceEdit = (EditText) findViewById(R.id.create_user_user_province);
        userZipCodeEdit = (EditText) findViewById(R.id.create_user_user_zip_code);

        // Wrappers
        userFullnameWrapper = (TextInputLayout) findViewById(R.id.create_user_fullname_wrapper);
        userNameWrapper = (TextInputLayout) findViewById(R.id.create_user_username_wrapper);
        userEmailWrapper = (TextInputLayout) findViewById(R.id.create_user_user_email_wrapper);
        userPasswordWrapper = (TextInputLayout) findViewById(R.id.create_user_user_password_wrapper);
        userRepeatPasswordWrapper = (TextInputLayout) findViewById(R.id.create_user_user_repeat_password_wrapper);
        userCityWrapper = (TextInputLayout) findViewById(R.id.create_user_user_city_wrapper);
        userProvinceWrapper = (TextInputLayout) findViewById(R.id.create_user_user_province_wrapper);
        userZipCodeWrapper = (TextInputLayout) findViewById(R.id.create_user_user_zip_code_wrapper);

        // Buttons
        nextButton = (AppCompatButton) findViewById(R.id.create_user_next_button);
        confirmButton = (AppCompatButton) findViewById(R.id.create_user_confirm_button);

        legalInfo = (TextView) findViewById(R.id.link_informacion_legal);

        // Layout toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        legalInfo.isClickable();
        legalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://yonodesperdicio.org/page/legal?locale=es"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

        // TEST
/*
        userFullnameEdit.setText("Antonio de Sousa");
        userNameEdit.setText("adesousa");
        userEmailEdit.setText("adesousa@ono.com");
        userPasswordEdit.setText("1234");
        userRepeatPasswordEdit.setText("1234");
        userCityEdit.setText("Ciudad");
        userProvinceEdit.setText("Provincia");
        userZipCodeEdit.setText("35001");
*/
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // First time?
        if (currentFormIndex == 0) {
            // First form
            currentFormIndex = 1;
            // Update the current UI form
            updateCurrentUI();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Home/Back
        if (id == android.R.id.home) {
            if (currentFormIndex > 1) {
                currentFormIndex--;
                // Update current UI form
                updateCurrentUI();
            }
            else {
                setResult(RESULT_CANCELED);
                hideKeyboard();
                finish();
            }
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

    /**
     * Update user interface
     */
    private void updateCurrentUI() {
        switch (currentFormIndex) {
            case 1: {
                layoutPage1.setVisibility(View.VISIBLE);
                layoutPage2.setVisibility(View.GONE);
                layoutPage3.setVisibility(View.GONE);
                // Focus
                userFullnameEdit.requestFocus();
                UiUtils.showKeyboard(this, userFullnameEdit);
                break;
            }
            case 2: {
                layoutPage1.setVisibility(View.GONE);
                layoutPage2.setVisibility(View.VISIBLE);
                layoutPage3.setVisibility(View.GONE);
                // Button
                nextButton.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.GONE);
                // Focus
                userPasswordEdit.requestFocus();
                UiUtils.showKeyboard(this, userPasswordEdit);
                break;
            }
            case 3: {
                layoutPage1.setVisibility(View.GONE);
                layoutPage2.setVisibility(View.GONE);
                layoutPage3.setVisibility(View.VISIBLE);
                // Button
                nextButton.setVisibility(View.GONE);
                confirmButton.setVisibility(View.VISIBLE);
                // Focus
                userCityEdit.requestFocus();
                UiUtils.showKeyboard(this, userCityEdit);
                break;
            }
        }
    }

    // Polymorphic

    private void startLoginUserActivity() {
        startLoginUserActivity(null, null);
    }

    private void startLoginUserActivity(String username, String password) {
        hideKeyboard();
        Intent loginUserIntent = new Intent(this, LoginUserActivity.class);
        if (username != null && password != null) {
            loginUserIntent.putExtra("username", username);
            loginUserIntent.putExtra("password", password);
        }
        startActivity(loginUserIntent);
    }

    private void hideKeyboard() {
        UiUtils.hideKeyboard(this, userFullnameEdit.getWindowToken());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_user_already_created_button:
                startLoginUserActivity();
                break;

            case R.id.create_user_next_button:
            case R.id.create_user_confirm_button:
                if (currentFormIndex <= MAX_FORM_PAGES) {
                    // Validate current UI form fields
                    if (validateCurrentForm()) {
                        if (currentFormIndex == MAX_FORM_PAGES) {
                            createUserAccount();
                        } else {
                            currentFormIndex++;
                            // Update current UI form
                            updateCurrentUI();
                        }
                    }
                }
                break;
        }
    }

    /**
     * Create user account
     */
    private void createUserAccount() {
        // Extract entered data
        String userFullname = userFullnameEdit.getText().toString().trim();
        final String userName = userNameEdit.getText().toString().trim();
        String userEmail = userEmailEdit.getText().toString().trim();
        final String userPassword = userPasswordEdit.getText().toString().trim();
        String userCity = userCityEdit.getText().toString().trim();
        String userProvince = userProvinceEdit.getText().toString().trim();
        String userZipCode = userZipCodeEdit.getText().toString().trim();

        UsersHelper.create(
                            this,
                            userFullname,
                            userName,
                            userPassword,
                            userEmail,
                            userCity,
                            userProvince,
                            userZipCode,
                            new UsersHelper.UserAccountCallback() {
                                @Override
                                public void onFinished(UserData user) {
                                    if (user == null || user.id == 0L) {
                                        // @TODO error
                                    } else {
                                        startLoginUserActivity(userName, userPassword);
                                        finish();
                                    }
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    UiUtils.showMessage(getActivity(), "Crea tu cuenta", errorMessage);
                                }
                            }
                          );

    }

    /**
     * Validate current UI form
     */
    private boolean validateCurrentForm() {
        switch (currentFormIndex) {
            case 1: {
                // Extract entered data
                String userFullname = userFullnameEdit.getText().toString().trim();
                String userName = userNameEdit.getText().toString().trim();
                String userEmail = userEmailEdit.getText().toString().trim();

                // Validate
                if (Utils.isEmptyOrNull(userFullname)) {
                    UiUtils.errorShake(this, userFullnameWrapper);
                } else if (Utils.isEmptyOrNull(userName)) {
                    UiUtils.errorShake(this, userNameWrapper);
                } else if (Utils.isEmptyOrNull(userEmail) || ! Utils.isValidEmail(userEmail)) {
                    UiUtils.errorShake(this, userEmailWrapper);
                }
                else {
                    return true;
                }

                break;
            }
            case 2: {
                // Extract entered data
                String userPassword = userPasswordEdit.getText().toString().trim();
                String userRepeatPassword = userRepeatPasswordEdit.getText().toString().trim();

                // Validate
                if (Utils.isEmptyOrNull(userPassword)) {
                    UiUtils.errorShake(this, userPasswordWrapper);
                } else if (Utils.isEmptyOrNull(userRepeatPassword)) {
                    UiUtils.errorShake(this, userRepeatPasswordWrapper);
                }
                else if (! userPassword.equals(userRepeatPassword)) {
                    UiUtils.errorShake(this, userPasswordWrapper);
                    UiUtils.errorShake(this, userRepeatPasswordWrapper);
                    Snackbar.make(userPasswordWrapper, "Las contraseñas introducidas no coinciden.", Snackbar.LENGTH_LONG).show();
                }
                else {
                    return true;
                }

                break;
            }
            case 3: {
                // Extract entered data
                String userCity = userCityEdit.getText().toString().trim();
                String userProvince = userProvinceEdit.getText().toString().trim();
                String userZipCode = userZipCodeEdit.getText().toString().trim();

                // Validate
                if (Utils.isEmptyOrNull(userCity)) {
                    UiUtils.errorShake(this, userCityWrapper);
                } else if (Utils.isEmptyOrNull(userProvince)) {
                    UiUtils.errorShake(this, userProvinceWrapper);
                } else if (Utils.isEmptyOrNull(userZipCode)) {
                    UiUtils.errorShake(this, userZipCodeWrapper);
                }
                else {
                    return true;
                }

                break;
            }
        }
        return false;
    }
}