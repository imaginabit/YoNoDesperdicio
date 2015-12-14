package com.imaginabit.yonodesperdicion.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.models.Idea;
import com.imaginabit.yonodesperdicion.utils.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fer2015julio on 19/11/15.
 */
public class IdeasAdapter extends RecyclerView.Adapter<IdeasAdapter.ViewHolder> {
    private static final String TAG = IdeasAdapter.class.getSimpleName();

    private List<Idea> ideasList = new ArrayList<>();
    private Double weightTotal;

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
            intro = (TextView) view.findViewById(R.id.idea_introduction);
            image = (ImageView) view.findViewById(R.id.idea_image);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public IdeasAdapter(List<Idea> ideasList){
        this.ideasList = ideasList;
    }

    public IdeasAdapter(List<Idea> ideasList,Double weight){
        this.ideasList = ideasList;
        this.weightTotal = weight;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 1) {
            return 0;
        } else {
            return 1;
        }
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

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Idea idea = ideasList.get(position);
        if( weightTotal != null && position!=0)
            idea = ideasList.get(position-1);

        if (weightTotal != null && position== 0) {

            holder.cardView.setVisibility(View.VISIBLE);
            holder.body.setText("Son los kilos de comida que hemos evitado que acaben en la basura");
            holder.title.setText(weightTotal.toString() + " Kg");
            holder.image.setVisibility(View.GONE);
            holder.category.setVisibility(View.GONE);
            holder.intro.setVisibility(View.GONE);
        } else {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            holder.title.setText(idea.getTitle());
            holder.body.setText(idea.getBody());
            holder.category.setText(idea.getCategory());
            holder.intro.setText(idea.getIntroduction());


            //get image from website
            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            String imageUri = Constants.HOME_URL + idea.getImageUrl();

            Log.i(TAG, "onBindViewHolder:" + imageUri);


            ImageSize targetSize = new ImageSize(300, 200); // result Bitmap will be fit to this size
            imageLoader.displayImage(imageUri, holder.image);
        }


    }

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
