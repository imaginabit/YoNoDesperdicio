package com.imaginabit.yonodesperdicion.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.activities.AdDetailActivity;
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.utils.OnLoadMoreListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fer2015julio on 19/11/15.
 */
public class AdsAdapter extends RecyclerView.Adapter {

    private static final String TAG = "AdsAdapter";
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private static FragmentManager sFragmentManager;

    private List<Ad> adsList = new ArrayList<Ad>();
    private Context context;
    private Location userLocation;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading;


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
    public AdsAdapter(Context context, List<Ad> adsList, RecyclerView recyclerView) {
        Log.d(TAG, "AdsAdapter() called with: " + "context = [" + context + "], adsList = [" + adsList.size() + "]");

        this.context = context;
        this.adsList = adsList;

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });

        }
    }

    // contructor to use with framents
//    public AdsAdapter(Context context, FragmentManager fragmentManager) {
//        //mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.context = context;
//        sFragmentManager = fragmentManager;
//    }



    // Create new views (invoked by the layout manager)
    @Override
    //public AdsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: hi , parent "+ parent.toString());

        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.ad_mini  , parent, false);

            vh = new AdsViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;


        // Create a new view
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_mini, parent, false);
//        Log.d(TAG, "onCreateViewHolder: inflater");
//        ViewHolder viewHolder = new ViewHolder(view);
//        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: start");


        if ( holder instanceof AdsViewHolder ){
            Ad ad = adsList.get(position);

            ((AdsViewHolder) holder).title.setText(ad.getTitle());
            // holder.status.setText(ad.getStatus());
            ((AdsViewHolder) holder).status.getDrawable().setColorFilter(ContextCompat.getColor(context, ad.getStatusColor()), android.graphics.PorterDuff.Mode.MULTIPLY);
            ((AdsViewHolder) holder).weight.setText(ad.getWeightKgStr());
            ((AdsViewHolder) holder).expiration.setText(ad.getExpirationDateLong());

            int intDistance = ad.getLastDistance();
            Log.d(TAG, "onBindViewHolder: calculateDistance ad.getlastdistance: " + intDistance);

            if ( intDistance>-1 ) {
                String distance = Integer.toString(intDistance);
                ((AdsViewHolder) holder).distance.setText( distance + "Km " );
//            ((AdsViewHolder) holder).title.setText( + );
            } else {
                Log.d(TAG, "onBindViewHolder: distance sin determinar");
                //Disntance undertermined Sin Determinar
                ((AdsViewHolder) holder).distance.setText("");
            }


            //get image from website
            ImageLoader imageLoader; // Get singleton instance
            imageLoader = ImageLoader.getInstance();
            String imageUri = Constants.HOME_URL + ad.getImageUrl();

            Log.i(TAG, "onBindViewHolder:" + imageUri);

//            ImageSize targetSize = new ImageSize(300, 200); // result Bitmap will be fit to this size
            try {
                imageLoader.displayImage(imageUri, ((AdsViewHolder) holder).image );
            } catch ( Exception e){
                ((AdsViewHolder) holder).image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.brick));
            }

            // CardView click listener
            ((AdsViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AdDetailActivity.class);
                    intent.putExtra("ad", (Parcelable) adsList.get(position));
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        } else{
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }


    public void setLoaded() {
        loading = false;
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (adsList != null) {
            return adsList.size();
        }
        return 0;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    /**
     * Set Ads to adapter
     * @param ads Ad list
     */
    public void setData(List<Ad> ads) {
        if (ads != null) {
            adsList.clear();
            for (Ad ad : ads) {
                adsList.add(ad);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return adsList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }


    //
    public static class AdsViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView title;
        private ImageView status;
        private TextView expiration;
        private TextView weight;
        private TextView distance;
        private ImageView image;

        public Ad ad;

        public AdsViewHolder(View view) {
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

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }






}
