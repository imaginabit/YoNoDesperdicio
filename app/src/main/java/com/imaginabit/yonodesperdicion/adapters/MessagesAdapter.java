package com.imaginabit.yonodesperdicion.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.models.Message;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.views.RoundedImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Fernando Ramírez on 18/01/16.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private static final String TAG = "MessagesAdapter";

    private List<Message> mMessages = new ArrayList<Message>();
    private User mOtherUser;
    private Context mContext;
    private Bitmap mAvatar;

    private static final int MYSELF=0;
    private static final int OTHER=1;


    public MessagesAdapter( List<Message> messages, User user, Bitmap image ) {
        Log.v(TAG, "MessagesAdapter() called with: " + "messages = [" + messages + "]");
        Log.d(TAG, "MessagesAdapter: size " + messages.size());
        mMessages = messages;
        mOtherUser = user;
        mAvatar = image;
    }



    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v(TAG, "onCreateViewHolder() called with: " + "parent = [" + parent + "], viewType = [" + viewType + "]");
        View view;

        if ( viewType == MYSELF ) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_line_me, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_line_other, parent, false);
        }

        ViewHolder viewHolder = new ViewHolder(view);
        mContext = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.v(TAG, "onBindViewHolder() called with: " + "holder = [" + holder + "], position = [" + position + "]");

        holder.userName.setText("");

        int UserID = mMessages.get(position).getSender_id();
        //String username = mMessages.get(position)
        if (UserID == AppSession.getCurrentUser().id ){
            holder.userName.setText(AppSession.getCurrentUser().username);
        } else {
            if (mOtherUser != null && UserID==mOtherUser.getUserId()){
                holder.userName.setText(mOtherUser.getUserName());
                if (mAvatar != null)
                    holder.avatar.setImageDrawable( new BitmapDrawable(Resources.getSystem(),mAvatar));
            } else {
                Log.d(TAG, "onBindViewHolder: no other user data");
                /*
                UserUtils.getUser(UserID, mContext, new UserUtils.FetchUserCallback() {
                    @Override
                    public void done(User user, Exception e) {
                        if (user!=null){
                            Log.d(TAG, "done: user "+ user.toString());
                            holder.userName.setText(user.getUserName());
                            mOtherUser = user;


                            //get image from website
                            ImageLoader imageLoader; // Get singleton instance
                            imageLoader = ImageLoader.getInstance();
                            String imageUri = Constants.HOME_URL + user.getAvatar();
                            Log.i(TAG, "avatar url : " + imageUri);
                            try {
                                Log.d(TAG, "done: loaded avatar");
                                imageLoader.displayImage(imageUri, holder.avatar );
                            } catch ( Exception eUser){
                                eUser.printStackTrace();
                                holder.avatar.setImageDrawable(ContextCompat.getDrawable(App.appContext, R.drawable.brick));
                            }

                        } else {
                            Log.d(TAG, "done: Other User null");
                            if(e!= null) e.printStackTrace();
                            holder.userName.setText("");
                        }
                    }
                });
                */
            }
        }
        holder.chatMessage.setText(mMessages.get(position).getBody().toString());

        long now = System.currentTimeMillis();
        Date msgCreated = mMessages.get(position).getCreated_at();
        if ( msgCreated != null ) {
            Log.d(TAG, "onBindViewHolder: msgCreated" + msgCreated.toString());
            String d = (String) DateUtils.getRelativeTimeSpanString(msgCreated.getTime(), now, DateUtils.HOUR_IN_MILLIS);
            Log.d(TAG, "onBindViewHolder: msgCreated relative: " + d);

            holder.time.setText(d);
        } else {
            holder.time.setText("");
            holder.time.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (mMessages != null){
            return mMessages.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "MAViewHolder";

        private TextView chatMessage;
        private TextView userName;
        private TextView time;
        private RoundedImageView avatar;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder() called with: " + "itemView = [" + itemView + "]");

            chatMessage = (TextView) itemView.findViewById(R.id.chat_messages);
            userName = (TextView) itemView.findViewById(R.id.chat_user_name);
            time = (TextView) itemView.findViewById(R.id.chat_time);
            avatar = (RoundedImageView) itemView.findViewById(R.id.chat_avatar);
        }
    }

    public void add(Message msg){
        mMessages.add(msg);
        notifyDataSetChanged();
    }

    public void add(String msg){
        Message oMsg = new Message(0, msg, ((int) AppSession.getCurrentUser().id), new Date());
        mMessages.add(oMsg);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int UserID = mMessages.get(position).getSender_id();

        if (UserID == AppSession.getCurrentUser().id){
            return MYSELF;
        }
        return OTHER;
    }


}
