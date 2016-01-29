package com.imaginabit.yonodesperdicion.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.activities.MessagesChatActivity;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.models.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando Ramírez on 13/01/16.
 */
public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {
    private static final String TAG = "ConversationsAdapter";

    private List<Conversation> mConversationList = new ArrayList<Conversation>();
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private CardView cardView;
        private TextView subject;
        private TextView updatedAt;
        private ImageView rattingStatus;
        private TextView lastMessage;
        private LinearLayout messageBox;

        public ViewHolder(View view) {
            super(view);

            Log.v(TAG, "ViewHolder: view" + view.toString());

//            cardView = (CardView) view.findViewById(R.id.ad_item);

            subject = (TextView) view.findViewById(R.id.chat_title);
            updatedAt = (TextView) view.findViewById(R.id.time_last_message);
            rattingStatus = (ImageView) view.findViewById(R.id.ratting_status);
            lastMessage = (TextView) view.findViewById(R.id.last_message);
            messageBox = (LinearLayout) view.findViewById(R.id.message);

        }
    }

    public ConversationsAdapter(Context context, List<Conversation> conversationList) {
        Log.v(TAG, "ConversationsAdapter: Constructor start");
        this.context = context;
        mConversationList = conversationList;
    }

    @Override
    public ConversationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_mini, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConversationsAdapter.ViewHolder holder, int position) {
        Log.v(TAG, "onBindViewHolder: ");
        final Conversation conversation = mConversationList.get(position);

        holder.subject.setText(conversation.getSubject());
        if (conversation.getMessages() != null) {
            Message lastMessage = conversation.getMessages().get(conversation.getMessages().size() - 1);
            String lmBody = lastMessage.getBody();
            if(lmBody.length()>22) {
                lmBody = lmBody.substring(0, 22) + "...";
            }
            holder.lastMessage.setText(lmBody);
        }else {
            holder.lastMessage.setText("");
        }
        holder.rattingStatus.setVisibility(View.INVISIBLE);


        long now = System.currentTimeMillis();
        String d = (String) DateUtils.getRelativeTimeSpanString(conversation.getUpdatedAt().getTime(), now, DateUtils.HOUR_IN_MILLIS);
        holder.updatedAt.setText( d );

        // CardView click listener
        holder.messageBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessagesChatActivity.class);
                intent.putExtra("conversationId", conversation.getId());
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                AppSession.currentConversation = conversation;
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mConversationList!= null){
            return mConversationList.size();
        }
        return 0;
    }
}
