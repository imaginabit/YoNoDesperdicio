package com.imaginabit.yonodesperdicion.adapters;

import android.content.Context;
import android.content.Intent;
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

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.activities.AdDetailActivity;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fer2015julio on 19/11/15.
 */
public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.ViewHolder> {
    private static final String TAG = AdsAdapter.class.getSimpleName();

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
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdsAdapter(Context context, List<Ad> adsList) {
        Log.d(TAG, "AdsAdapter: Constructor --");

        this.context = context;
        this.adsList = adsList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: hi , parent"+ parent.toString());

        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_mini, parent, false);

        // set the view's size, margins, paddings and layout parameters

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Ad ad = adsList.get(position);

        holder.title.setText(ad.getTitle());
        // holder.status.setText(ad.getStatus());
        holder.status.getDrawable().setColorFilter(ContextCompat.getColor(context, ad.getStatusColor()),  android.graphics.PorterDuff.Mode.MULTIPLY);
        holder.weight.setText(ad.getWeightKgStr());
        holder.expiration.setText(ad.getExpirationDateLong());

        // @TODO: calcular distancia
        // holder.distance

        // @TODO: get image from website
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        String imageUri = Constants.HOME_URL + ad.getImageUrl();

        Log.i(TAG, "onBindViewHolder:" + imageUri);

        ImageSize targetSize = new ImageSize(300, 200); // result Bitmap will be fit to this size
        imageLoader.displayImage(imageUri, holder.image );

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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
