package com.imaginabit.yonodesperdicion.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.model.Idea;
import com.imaginabit.yonodesperdicion.util.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by fer2015julio on 19/11/15.
 */
public class MoreInfoIdeas extends RecyclerView.Adapter<MoreInfoIdeas.ViewHolder> {
    private String TAG = MoreInfoIdeas.class.getSimpleName();
    private List<Idea> ideas;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView category;
        TextView body;
        TextView intro;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.articulo);
            title = (TextView)itemView.findViewById(R.id.idea_title);
            category = (TextView)itemView.findViewById(R.id.idea_category);
            body = (TextView)itemView.findViewById(R.id.idea_body);
            intro = (TextView)itemView.findViewById(R.id.idea_introduction);
            image = (ImageView)itemView.findViewById(R.id.idea_image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MoreInfoIdeas(List<Idea> myDataset) {

        this.ideas = myDataset;

        // Create global configuration and initialize ImageLoader with this config

    }

    // Create new views (invoked by the layout manager)
    @Override
    public MoreInfoIdeas.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.more_info_ideas , parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText( ideas.get(position).getTitle() );
        holder.body.setText( ideas.get(position).getBody() );
        holder.category.setText( ideas.get(position).getCategory() );
        holder.intro.setText(ideas.get(position).getIntroduction());

        //get image from website
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        String imageUri = Constants.HOME_URL + ideas.get(position).getImageUrl();
        Log.i(TAG, "onBindViewHolder:" + imageUri );
        imageLoader.displayImage(imageUri, holder.image);

//        imageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                // Do whatever you want with Bitmap
//            }
//        });

        //holder.image.setImageResource(R.drawable.food);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ideas.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
