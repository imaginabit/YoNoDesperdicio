package com.imaginabit.yonodesperdicion.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.activity.AdDetail;
import com.imaginabit.yonodesperdicion.model.Ad;
import com.imaginabit.yonodesperdicion.util.AppUtils;
import com.imaginabit.yonodesperdicion.util.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fer2015julio on 19/11/15.
 */
public class AdAdapter extends RecyclerView.Adapter<AdAdapter.ViewHolder> {
    private static String TAG = AdAdapter.class.getSimpleName();
    private List<Ad> ads = new ArrayList<Ad>();
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView title;
        ImageView status;
        TextView expiration;
        TextView weight;
        TextView distance;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: view" + itemView.toString());
            cv = (CardView)itemView.findViewById(R.id.ad_item);
            title = (TextView)itemView.findViewById(R.id.ad_title);
            status = (ImageView)itemView.findViewById(R.id.status_image);
            expiration = (TextView)itemView.findViewById(R.id.ad_expiration);
            distance = (TextView)itemView.findViewById(R.id.ad_distance);
            weight = (TextView)itemView.findViewById(R.id.ad_weight);
            image = (ImageView)itemView.findViewById(R.id.ad_image);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdAdapter(Context context,List<Ad> myDataset) {
        Log.d(TAG, "AdAdapter: Constructor --");
        
        mContext = context;
        this.ads = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        Log.d(TAG, "onCreateViewHolder: hi , parent"+ parent.toString());
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ad_mini , parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Ad ad = ads.get(position);

        holder.title.setText(ad.getTitle());
        //holder.status.setText(ads.get(position).getStatus() );

        holder.status.setImageDrawable(ContextCompat.getDrawable(mContext,ad.getStatusImage()));
        holder.status.getDrawable().setColorFilter(ContextCompat.getColor(mContext, ad.getStatusColor()), android.graphics.PorterDuff.Mode.MULTIPLY);

        holder.weight.setText(ad.getWeightKgStr());
        holder.expiration.setText(ad.getExpirationStrLong());

        //TODO: calcular distancia
        //holder.distance
        // get user location , get ad location in base of zipcode, calculate distance
        double distance = 0; //distance in meters
        try {
            Address adAddress = AppUtils.getGPSfromZip( mContext, ad.getPostalCode() );
            //TODO get user location
            Address userAddress = AppUtils.getGPSfromZip( mContext, ad.getPostalCode() );
            Location adLocation = new Location("Articulo Anuncio");
            adLocation.setLatitude(adAddress.getLatitude());
            adLocation.setLongitude(adAddress.getLongitude() );
            Location userLocation = new Location("User");
            userLocation.setLatitude( userAddress.getLatitude() );
            userLocation.setLongitude( userAddress.getLongitude() );

            distance = adLocation.distanceTo(userLocation);
        } catch (Exception e){
            e.printStackTrace();
        }

        holder.distance.setText( Double.toString(distance) + "m");


        //get image from website
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        String imageUri = Constants.HOME_URL + ad.getImageUrl();
        Log.i(TAG, "onBindViewHolder:" + imageUri);

        ImageSize targetSize = new ImageSize(300, 200); // result Bitmap will be fit to this size
        imageLoader.displayImage(imageUri, holder.image );

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, AdDetail.class);
                intent.putExtra("ad", (android.os.Parcelable) ads.get(position));
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (ads != null) {
            return ads.size();
        } else return 0;

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
