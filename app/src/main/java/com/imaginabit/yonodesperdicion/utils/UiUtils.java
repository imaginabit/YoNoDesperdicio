package com.imaginabit.yonodesperdicion.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.IBinder;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.imaginabit.yonodesperdicion.R;

/**
 * @author Antonio de Sousa Barroso
 */
public class UiUtils {
    // Softkeyboard

    /**
     * @reference: http://stackoverflow.com/questions/2342620/how-to-hide-keyboard-after-typing-in-edittext-in-android
     */
    public static void hideKeyboard(final Context context, final IBinder windowToken) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideKeyboard(final Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        View currentView = activity.getCurrentFocus();
        if (currentView == null) {
            currentView = new View(activity);
        }

        inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyboard(final Context context, final View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    // Error

    /**
     * Error Shake
     */
    public static void errorShake(final Context context, final View animatedView) {
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        animatedView.startAnimation(shake);
        Utils.vibrate(context, 500);
    }

    // Show Messages

    public static void showMessage(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme_Dialog));

        builder.setTitle(title)
               .setMessage(message)
               .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        if (Utils.isNotEmptyOrNull(title)) {
            final int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
            final View titleDivider = dialog.findViewById(titleDividerId);

            if (titleDivider != null) {
                titleDivider.setVisibility(View.GONE);
            }
        }
    }




}
