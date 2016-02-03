package com.imaginabit.yonodesperdicion.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.activities.AdDetailActivity;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fer2015julio on 19/11/15.
 */
public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.ViewHolder> {

    private static final String TAG = "AdsAdapter";
    private static FragmentManager sFragmentManager;

    private List<Ad> adsList = new ArrayList<Ad>();
    private Context context;
    private Location userLocation;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView title;
        private ImageView status;
        private TextView expiration;
        private TextView weight;
        private TextView distance;
        private ImageView image;

        public ViewHolder(View view) {
            super(view);

            Log.d(TAG, "ViewHolder: view" + view.toString());

            cardView = (CardView) view.findViewById(R.id.ad_item);
            title = (TextView) view.findViewById(R.id.ad_title);
            status = (ImageView) view.findViewById(R.id.status_image);
            expiration = (TextView) view.findViewById(R.id.ad_expiration);
            distance = (TextView) view.findViewById(R.id.ad_distance);
            weight = (TextView) view.findViewById(R.id.ad_weight);
            image = (ImageView) view.findViewById(R.id.ad_image);

            Log.d(TAG, "ViewHolder: ");
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdsAdapter(Context context, List<Ad> adsList) {
        Log.d(TAG, "AdsAdapter() called with: " + "context = [" + context + "], adsList = [" + adsList.size() + "]");

        this.context = context;
        this.adsList = adsList;
    }

    // contructor to use with framents
    public AdsAdapter(Context context, FragmentManager fragmentManager) {
        //mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        sFragmentManager = fragmentManager;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: hi , parent "+ parent.toString());

        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_mini, parent, false);
        Log.d(TAG, "onCreateViewHolder: inflater");

        // set the view's size, margins, paddings and layout parameters
        
        Double dis = 0.0;
        

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: start");
        Ad ad = adsList.get(position);


        holder.title.setText(ad.getTitle());
        // holder.status.setText(ad.getStatus());
        holder.status.getDrawable().setColorFilter(ContextCompat.getColor(context, ad.getStatusColor()), android.graphics.PorterDuff.Mode.MULTIPLY);
        holder.weight.setText(ad.getWeightKgStr());
        holder.expiration.setText(ad.getExpirationDateLong());

        if (ad.getLastDistance()>0) {
            String distance = Integer.toString(ad.getLastDistance());
            holder.distance.setText( distance + "Km " );
//            holder.title.setText( + );
        } else {
            holder.distance.setText("Sin Determinar");
        }


        //get image from website
        ImageLoader imageLoader; // Get singleton instance
        imageLoader = ImageLoader.getInstance();
        String imageUri = Constants.HOME_URL + ad.getImageUrl();

        Log.i(TAG, "onBindViewHolder:" + imageUri);

        ImageSize targetSize = new ImageSize(300, 200); // result Bitmap will be fit to this size
        try {
            imageLoader.displayImage(imageUri, holder.image );
        } catch ( Exception e){
            holder.image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.brick));
        }

        // CardView click listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdDetailActivity.class);
                intent.putExtra("ad", (Parcelable) adsList.get(position));
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (adsList != null) {
            return adsList.size();
        }
        return 0;
    }

    /**
     * Set Ads to adapter
     * @param ads Ad list
     */
    public void setData(List<Ad> ads) {
        adsList.clear();

        if (ads != null) {
            for (Ad ad : ads) {
                adsList.add(ad);
            }
        }
    }


}
