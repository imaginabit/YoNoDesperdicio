package com.imaginabit.yonodesperdicion.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Parcelable;
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
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fer2015julio on 19/11/15.
 */
public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.ViewHolder> {

    private static final String TAG = "AdsAdapter";
    //private static final String TAG = AdsAdapter.class.getSimpleName();

    private List<Ad> adsList = new ArrayList<Ad>();
    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView title;
        private ImageView status;
        private TextView expiration;
        private TextView weight;
        private TextView distance;
        private ImageView image;
        private float fDistance;

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
        holder.status.getDrawable().setColorFilter(ContextCompat.getColor(context, ad.getStatusColor()),  android.graphics.PorterDuff.Mode.MULTIPLY);
        holder.weight.setText(ad.getWeightKgStr());
        holder.expiration.setText(ad.getExpirationDateLong());

        //TODO: calcular distancia
        //holder.distance
        // get user location , get ad location in base of zipcode, calculate distance
        if (holder.fDistance == 0.0) {
            Log.d(TAG, "onBindViewHolder: Calculating distance");
            try {
                //Parece que sobrecarga mucho estar calculando esto todo el rato
                Address adAddress = Utils.getGPSfromZip(context, ad.getPostalCode());
                //TODO get user GPS location
                Log.d(TAG, "onBindViewHolder: User zipcode "+ UserData.prefsFetch(context).zipCode );
                Address userAddress = Utils.getGPSfromZip(context, Integer.parseInt( UserData.prefsFetch(context).zipCode ) );
                Location adLocation = new Location("Articulo Anuncio");
                adLocation.setLatitude(adAddress.getLatitude());
                adLocation.setLongitude(adAddress.getLongitude());
                Location userLocation = new Location("User");
                userLocation.setLatitude(userAddress.getLatitude());
                userLocation.setLongitude(userAddress.getLongitude());
                float d = adLocation.distanceTo(userLocation);
                if(d==0.0) {
                    holder.fDistance = 100;
                }else {
                    holder.fDistance = d;
                }
                Log.d(TAG, "onBindViewHolder: fDistance" + holder.fDistance );

//                holder.dblDistance = 0.1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        holder.distance.setText( Float.toString(holder.fDistance) + "m");


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


}
