package com.imaginabit.yonodesperdicion.Ad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.activity.MainActivity;
import com.imaginabit.yonodesperdicion.R;


/**
 * Created by fer2015julio on 2/09/15.
 */
public class AdsDialog extends DialogFragment {
    private static final String LOG_TAG = AdsDialog.class.getSimpleName();
    public static final String DIALOG_TYPE = "command";
    public static final String DELETE_RECORD = "deleteRecord";
    public static final String DELETE_DATABASE = "deleteDatabase";
    public static final String CONFIRM_EXIT = "confirmExit";
    private LayoutInflater mLayoutInflater;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mLayoutInflater = getActivity().getLayoutInflater();
        final View view = mLayoutInflater.inflate(R.layout.ad_layout, null);
        String command = getArguments().getString(DIALOG_TYPE);
        if (command.equals(DELETE_RECORD)) {
            final int _id = getArguments().getInt(AdsContract.AdsColumns.AD_ID);
            String title = getArguments().getString(AdsContract.AdsColumns.AD_TITLE);

            TextView popupMesssage = (TextView) view.findViewById(R.id.popup_message);
            popupMesssage.setText("Estas seguro de querer borrrar " + title + " ?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    Uri uri = AdsContract.Ads.buildAdUri(String.valueOf(_id));
                    contentResolver.delete(uri, null, null);
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            });
        } else if (command.equals(DELETE_DATABASE)) {
            TextView popupMesssage = (TextView) view.findViewById(R.id.popup_message);
            popupMesssage.setText("Estas seguro de querer borrrar la base de datos entera ?");
            builder.setView(view).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    Uri uri = AdsContract.URI_TABLE;
                    contentResolver.delete(uri, null, null);
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        } else if(command.equals(CONFIRM_EXIT)){
            // dialogo de confirmar salir
            TextView popupMesssage = (TextView) view.findViewById(R.id.popup_message);
            popupMesssage.setText("Estas seguro de querer salir sin guardar?");
            builder.setView(view).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
        } else {
            Log.d(LOG_TAG, "Invalid command passed as parameter");
        }

        return builder.create();


    }


}
