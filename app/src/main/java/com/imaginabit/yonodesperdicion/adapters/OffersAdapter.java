package com.imaginabit.yonodesperdicion.adapters;

import android.content.Context;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.models.Offer;
import com.imaginabit.yonodesperdicion.utils.OnLoadMoreListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter {

    private static final String TAG = "OffersAdapter";
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private static FragmentManager sFragmentManager;

    private List<Offer> offersList = new ArrayList<Offer>();
    private Context context;
    private Location userLocation;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading;

    public OffersAdapter(Context context, List<Offer> offersList, RecyclerView recyclerView){
        Log.d(TAG, "OffersAdapter() called");
//                with: context = [" + context + "], offersList = [" + offersList + "], recyclerView = [" + recyclerView + "]");

        this.context = context;
        this.offersList = offersList;

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


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private CardView cardView;
        private TextView title;
        private ImageView status;
        private TextView expiration;
        private TextView store;
        private TextView distance;
        private ImageView image;

        public ViewHolder(View view) {
            super(view);

            Log.d(TAG, "ViewHolder: view" + view.toString());

            cardView = view.findViewById(R.id.ad_item);
            title = view.findViewById(R.id.ad_title);
            status = view.findViewById(R.id.status_image);
            expiration = view.findViewById(R.id.ad_expiration);
            distance = view.findViewById(R.id.ad_distance);
            store = view.findViewById(R.id.offer_store);
            image = view.findViewById(R.id.ad_image);
            description = view.findViewById(R.id.offer_description);

            Log.d(TAG, "ViewHolder: ");
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: hi , parent "+ parent.toString());

        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.offer_mini , parent, false);

            vh = new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new AdsAdapter.ProgressViewHolder(v);
        }
        return vh;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: start");


        if ( holder instanceof ViewHolder ){
            final Offer offer = offersList.get(position);

            ((ViewHolder) holder).title.setText( offer.getTitle() );
            ((ViewHolder) holder).store.setText( offer.getStore() );
            ((ViewHolder) holder).description.setText( offer.getDescription() );
            // holder.status.setText(ad.getStatus());
//            ((ViewHolder) holder).status.getDrawable().setColorFilter(ContextCompat.getColor(context, ad.getStatusColor()), android.graphics.PorterDuff.Mode.MULTIPLY);
            ((ViewHolder) holder).expiration.setText( offer.getExpirationDateLong() );
            ((ViewHolder) holder).status.setVisibility( View.GONE );


//            int intDistance = ad.getLastDistance();
//            Log.d(TAG, "onBindViewHolder: calculateDistance ad.getlastdistance: " + intDistance);

            //get image from website
            ImageLoader imageLoader; // Get singleton instance
            imageLoader = ImageLoader.getInstance();
            String imageUri = offer.getImage().getLarge();

            Log.i(TAG, "onBindViewHolder:" + imageUri);

            try {
                imageLoader.displayImage(imageUri, ((ViewHolder) holder).image );
            } catch ( Exception e){
                ((ViewHolder) holder).image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.brick));
            }

            // CardView click listener
            ((ViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "oferta: " + offer, Toast.LENGTH_LONG).show();

//                    Intent intent = new Intent(context, AdDetailActivity.class);
//                    intent.putExtra("offer", (Serializable) offersList.get(position));
//                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
                }
            });

        } else{
            ((AdsAdapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }



    @Override
    public int getItemCount() {
        if (offersList != null) {
            return offersList.size();
        }
        return 0;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setData(List<Offer> offers) {
        if (offers != null) {
            offersList.clear();
            // why not this?  offersList = offers;

            for (Offer offer : offers) {
                offersList.add(offer);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return offersList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setLoaded() {
        loading = false;
    }

}
