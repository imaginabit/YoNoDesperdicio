package com.imaginabit.yonodesperdicion.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.ConversationsAdapter;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.models.Message;
import com.imaginabit.yonodesperdicion.utils.MessagesUtils;

import java.util.Date;
import java.util.List;

public class MessagesActivity extends NavigationBaseActivity {
    private final String TAG = getClass().getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Conversation> mConversationList;

    //private List<Ad> mAds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        // Put on session
        UserData user = UserData.prefsFetch(this);
        if (user != null) {
            AppSession.setCurrentUser(user);
        }

        // Fix action bar and drawer
        Toolbar toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_conversations);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ConversationsAdapter(context, mConversationList);
        recyclerView.setAdapter(adapter);

        VolleySingleton.init(this);
        MessagesUtils.getConversations(MessagesActivity.this, new MessagesUtils.ConversationsCallback() {
            @Override
            public void onFinished(final List<Conversation> conversations, Exception e) {
                Log.v(TAG, "onFinished: finishesd");
                if (conversations != null) {
//                    mConversations = conversations
                    adapter = new ConversationsAdapter(context, conversations);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Conversacionesl : " + conversations.size());
                    Log.d(TAG, "Conversaciones getItemCount : " + adapter.getItemCount());
                    //Toast.makeText(MessagesActivity.this, conversations.get(0).getSubject(), Toast.LENGTH_SHORT).show();

                    Log.v(TAG, "createConversationList: getMessages start");
                    Date d = new Date();
                    Log.v(TAG, "getConversaitonMessages time: "+ Constants.DATE_JSON_FORMAT.format(d.getTime()) );

                    MessagesUtils.getConversationMessages(conversations, new MessagesUtils.MessagesCallback() {
                        @Override
                        public void onFinished(List<Message> messages, Exception e) {
                            Log.d(TAG, "onFinished: messages.size " + messages.size());
                            Log.d(TAG, "onFinished: messages.tostring " + messages.toString());

                            String lastmsgsubject = messages.get(messages.size()-1).getSubject();
                            Log.d(TAG, "onFinished: subject last msg" + lastmsgsubject);

                            //adapter.getItemId(x)
                            Log.d(TAG, "onFinished: conversations: " + conversations.toString());
                            Log.d(TAG, "onFinished: conversations: " + conversations.get(conversations.size()-1).toString());

                            adapter = new ConversationsAdapter(context, conversations);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(adapter.getItemCount()-1);
                            //recyclerView.scrollToPosition(messages.size()-1);



                            Date d = new Date();
                            Log.v(TAG, "getConversaitonMessages onFinished: "+ Constants.DATE_JSON_FORMAT.format(d.getTime()) );
                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    });
                }
            }
            @Override
            public void onError (String errorMessage){
                Log.d(TAG, "onError: error");
            }
        }, MessagesActivity.this);


    }

}
