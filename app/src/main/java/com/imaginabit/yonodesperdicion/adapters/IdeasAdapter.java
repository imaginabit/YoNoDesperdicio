package com.imaginabit.yonodesperdicion.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.imaginabit.yonodesperdicion.models.Idea;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by fer2015julio on 19/11/15.
 */
public class IdeasAdapter extends RecyclerView.Adapter<IdeasAdapter.ViewHolder> {
    private static final String TAG = IdeasAdapter.class.getSimpleName();

    private List<Idea> ideasList = new ArrayList<>();
    private Double weightTotal;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView title;
        private TextView category;
        private TextView body;
        private TextView intro;
        private ImageView image;

        public ViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.articulo);
            title = (TextView) view.findViewById(R.id.idea_title);
            category = (TextView) view.findViewById(R.id.idea_category);
            body = (TextView) view.findViewById(R.id.idea_body);
//            intro = (TextView) view.findViewById(R.id.idea_introduction);
            image = (ImageView) view.findViewById(R.id.idea_image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public IdeasAdapter(List<Idea> ideasList,Context c) {
        this.ideasList = ideasList;
        this.context = c;
    }

    public IdeasAdapter(List<Idea> ideasList,Context c, Double weight){
        this.ideasList = ideasList;
        this.context = c;
        this.weightTotal = weight;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public IdeasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_info_ideas, parent, false);

        // Set the view's size, margins, paddings and layout parameters

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Idea idea = ideasList.get(position);
        if( weightTotal != null && position!=0)
            idea = ideasList.get(position-1);

        if (weightTotal != null && position== 0) {

            //print kg with decimal up comma
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols( Locale.getDefault()  );
            otherSymbols.setDecimalSeparator('\'');
            otherSymbols.setGroupingSeparator(',');
            DecimalFormat df = new DecimalFormat("#.##", otherSymbols);

            df.setRoundingMode(RoundingMode.CEILING);
            String sWeight = df.format(weightTotal);

            String strWeight = sWeight + " Kg";

            holder.cardView.setVisibility(View.VISIBLE);
            holder.body.setText("Son los kilos de comida que hemos evitado que acaben en la basura");
            holder.title.setText( strWeight );
            holder.image.setVisibility(View.GONE);
            holder.category.setVisibility(View.GONE);
//            holder.intro.setVisibility(View.GONE);
        } else {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            holder.title.setText(idea.getTitle());
            holder.body.setText(idea.getBody());
            holder.category.setText(idea.getCategory());
//            holder.intro.setText(idea.getIntroduction());


            //get image from website
            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            String imageUri = Constants.HOME_URL + idea.getImageUrl();

            Log.i(TAG, "onBindViewHolder:" + imageUri);


            ImageSize targetSize = new ImageSize(300, 200); // result Bitmap will be fit to this size
            imageLoader.displayImage(imageUri, holder.image);

            final Idea finalIdea = idea;
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = Constants.IDEA_URL + finalIdea.getId();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });

            //Log.d(TAG, "onBindViewHolder: position "+ position + " total size "+ ideasList.size());

            //if last item add space
//            if (ideasList.size()==position+1){
//                Log.d(TAG, "onBindViewHolder: last position");
//                holder.cardView.setUseCompatPadding(true);
////                holder.cardView.setCardBackgroundColor(R.color.accent);
//            }

        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (ideasList != null) {
            return ideasList.size();
        }
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
