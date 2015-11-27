/*
package com.imaginabit.yonodesperdicion.Ad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.imaginabit.yonodesperdicion.R;

import java.util.List;



*/
/**
 * Created by fer2015julio on 2/09/15.
 *//*

public class AdsCustomAdapter extends ArrayAdapter<Ad> {
    private static FragmentManager sFragmentManager;
    private LayoutInflater mLayoutInflater;

    public AdsCustomAdapter(Context context, FragmentManager fragmentManager) {
        super(context, android.R.layout.simple_expandable_list_item_2);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sFragmentManager = fragmentManager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = mLayoutInflater.inflate(  R.layout.custom_ad, parent, false);

        } else {
            view = convertView;
        }
        final Ad ad = getItem(position);
        final int _id = ad.getId();
        final String title = ad.getTitle();
        final String body = ad.getBody();
        final String username = ad.getUsername();
        final Ad.Type type = ad.getType();
        final int woeid = ad.getWoeid();
        final String date_created = ad.getDate_created();
        final String image_file_name = ad.getImage_file_name();
        final Ad.Status status = ad.getStatus();
        final boolean comments_enabled = ad.isComments_enabled();
        final boolean favorite = ad.isFavorite();

        ((TextView) view.findViewById(R.id.ad_title)).setText(title);
        ((TextView) view.findViewById(R.id.ad_body)).setText(body);

        Button editButton = (Button) view.findViewById(R.id.edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditActivity.class);

                intent.putExtra(AdsContract.AdsColumns.AD_ID, String.valueOf(_id));
                intent.putExtra(AdsContract.AdsColumns.AD_TITLE, title);
                intent.putExtra(AdsContract.AdsColumns.AD_BODY, body);

                getContext().startActivity(intent);

            }
        });

        Button deleteButton = (Button) view.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdsDialog dialog = new AdsDialog();
                Bundle args = new Bundle();
                args.putString(AdsDialog.DIALOG_TYPE, AdsDialog.DELETE_RECORD);
                args.putString(AdsContract.AdsColumns.AD_ID, String.valueOf(_id));

                dialog.setArguments(args);
                dialog.show(sFragmentManager, "delete-record");
            }
        });

        return view;
    }

    public void setData(List<Ad> ads) {
        clear();

        if (ads != null) {
            for (Ad ad : ads) {
                add(ad);
            }
        }
    }
}
*/
