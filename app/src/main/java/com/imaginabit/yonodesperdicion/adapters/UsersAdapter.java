package com.imaginabit.yonodesperdicion.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.models.User;

/**
 * Created by Fernando Ramírez on 15/12/15.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private static final String TAG = UsersAdapter.class.getSimpleName();

    private User mUser;

    public UsersAdapter(User user) {
        mUser = user;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView userName;
        private TextView location;
        private TextView kilos;
        private RatingBar reputacion;
        private ImageView avatar;

        public ViewHolder(View view) {
            super(view);

            userName = (TextView) view.findViewById(R.id.user_name);
            location = (TextView) view.findViewById(R.id.location);
            kilos = (TextView) view.findViewById(R.id.ad_weight);
            reputacion = (RatingBar) view.findViewById(R.id.ad_reputacion);
            avatar = (ImageView) view.findViewById(R.id.avatarpic);
       }
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_profile, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.userName.setText(mUser.getUserName());
        holder.location.setText(mUser.getUserName());
        holder.kilos.setText(mUser.getUserName());
        holder.reputacion.setRating(mUser.getRatting().floatValue());

        // TODO no esta el avatar en la api
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        String imageUri = Constants.HOME_URL + mUser.
        
        
    }

    @Override
    public int getItemCount() {
        if (mUser!= null) return 1;
        else return 0;
    }
}
