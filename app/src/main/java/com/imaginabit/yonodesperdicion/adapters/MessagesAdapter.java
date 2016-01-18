package com.imaginabit.yonodesperdicion.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.models.Message;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando Ramírez on 18/01/16.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private static final String TAG = "MessagesAdapter";

    private List<Message> mMessages = new ArrayList<Message>();
    private User mOtherUser;
    private Context mContext;

    public MessagesAdapter(List<Message> messages) {
        Log.v(TAG, "MessagesAdapter() called with: " + "messages = [" + messages + "]");
        Log.d(TAG, "MessagesAdapter: size " + messages.size());
        mMessages = messages;
    }

    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v(TAG, "onCreateViewHolder() called with: " + "parent = [" + parent + "], viewType = [" + viewType + "]");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_line_me, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        mContext = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MessagesAdapter.ViewHolder holder, int position) {
        Log.v(TAG, "onBindViewHolder() called with: " + "holder = [" + holder + "], position = [" + position + "]");


        holder.userName.setText("");

        int UserID = mMessages.get(position).getSender_id();
        //String username = mMessages.get(position)
        if (UserID == AppSession.getCurrentUser().id ){
            holder.userName.setText(AppSession.getCurrentUser().username);
        } else {
            if (mOtherUser != null && UserID==mOtherUser.getUserId()){
                holder.userName.setText(mOtherUser.getUserName());
            } else {
                UserUtils.getUser(UserID, mContext, new UserUtils.FetchUserCallback() {
                    @Override
                    public void done(User user, Exception e) {
                        if (user!=null) {
                            Log.d(TAG, "done: user "+ user.toString());
                            holder.userName.setText(user.getUserName());
                            mOtherUser = user;
                        }else {
                            if(e!= null) e.printStackTrace();
                        }
                    }
                });
//                UserUtils.fetchUser(UserID, new UserUtils.FetchUserCallback() {
//                    @Override
//                    public void done(User user, Exception e) {//
//                    }
//                });
            }
        }

        holder.chatMessage.setText(mMessages.get(position).getBody().toString());

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

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder() called with: " + "itemView = [" + itemView + "]");

            chatMessage = (TextView) itemView.findViewById(R.id.chat_messages);
            userName = (TextView) itemView.findViewById(R.id.chat_user_name);
        }
    }
}
