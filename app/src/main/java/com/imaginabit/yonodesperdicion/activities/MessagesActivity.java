package com.imaginabit.yonodesperdicion.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.ConversationsAdapter;
import com.imaginabit.yonodesperdicion.data.AdsContract;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.utils.MessagesUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessagesActivity extends NavigationBaseActivity {
    private final String TAG = getClass().getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Conversation> mConversationList;
    private ContentResolver mContentResolver;
    private ContentValues mContentValues;

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

        mContentResolver = getContentResolver();

        VolleySingleton.init(this);
        getMessages();
        checkMessages();
    }

    private void checkMessages(){
        new Handler().postDelayed(new RunnableCheckActive(this) {
            @Override
            public void run() {
                Log.v(TAG, "checkMessages run() called with: " + "");
                MessagesActivity a = (MessagesActivity) mActivity;
                if ( a.isActive() ){
                    Log.d(TAG, "run: active!");
                    getMessages();

                }
                checkMessages();
            }
        }, 2 * 60 * 1000);
    }

    public static class RunnableCheckActive implements Runnable {
        private static final String TAG = "RunnableCheckActive";
        public Activity mActivity;

        public RunnableCheckActive(Activity a) {
            mActivity = a;
        }

        @Override
        public void run() {
            Log.d(TAG, "run() called with: " + "");
        }
    }

    private void getMessages(){
        MessagesUtils.getConversations(MessagesActivity.this, new MessagesUtils.ConversationsCallback() {
            @Override
            public void onFinished(List<Conversation> conversation, Exception e) {
                Log.d(TAG, "onFinished() called with: " + "conversation = [" + conversation + "], e = [" + e + "]");
            }

            @Override
            public void onFinished(final List<Conversation> conversations, Exception e,ProgressDialog pd) {
                Log.v(TAG, "onFinished: finishesd");
                if (conversations != null) {
                    mConversationList = conversations;
                    sortByDate(mConversationList);

                    adapter = new ConversationsAdapter(context, mConversationList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.v(TAG, "Conversacionesl : " + conversations.size());
                    Log.v(TAG, "Conversaciones getItemCount : " + adapter.getItemCount());


//                    Date d = new Date();
//                    Log.v(TAG, "getConversaitonMessages time: " + Constants.DATE_JSON_FORMAT.format(d.getTime()));

                    for (int i = 0; i < conversations.size(); i++) {

                        //buscar en la base de datos y crear si no se encuentra

                        Conversation c = conversations.get(i);
                        //save conversation in database
                        mContentValues.put(AdsContract.ConversationsColumns.CONVERSATION_ID, c.getId() );
                        mContentValues.put(AdsContract.ConversationsColumns.CONVERSATION_USER, c.getOtherUserId());
//                        mContentValues.put(AdsContract.ConversationsColumns.CONVERSATION_STATUS, "");
                        mContentValues.put(AdsContract.ConversationsColumns.CONVERSATION_AD_ID, "" );
                        Uri returned = mContentResolver.insert(AdsContract.URI_TABLE_FAVORITES, mContentValues);

                    }

                    //get messages from all conversations
                    //TAKE too much time to load
                    /*
                    MessagesUtils.getConversationMessagesInbox(conversations, new MessagesUtils.MessagesCallback() {
                        @Override
                        public void onFinished(List<Message> messages, Exception e) {
                            Log.d(TAG, "onFinished: messages.size " + messages.size());
                            Log.d(TAG, "onFinished: messages.tostring " + messages.toString());

                            String lastmsgsubject = messages.get(messages.size() - 1).getSubject();
                            Log.d(TAG, "onFinished: subject last msg" + lastmsgsubject);

                            //adapter.getItemId(x)
                            Log.d(TAG, "onFinished: conversations: " + conversations.toString());
                            Log.d(TAG, "onFinished: conversations: " + conversations.get(conversations.size() - 1).toString());

                            adapter = new ConversationsAdapter(context, conversations);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

//                            Date d = new Date();
//                            Log.v(TAG, "getConversaitonMessages onFinished: " + Constants.DATE_JSON_FORMAT.format(d.getTime()));
                        }

                        @Override
                        public void onFinished(List<Message> messages, Exception e, ArrayList data) {
                            //do nothing
                        }
                        @Override
                        public void onError(String errorMessage) {}
                    });
                    */
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.d(TAG, "onError: error");
            }
        }, MessagesActivity.this);
    }

//    Esto solo se puede hacer si obtengo primero todos los mensajes
//    private int getOtherUser(Conversation conversation){
//        List<Message> messages = conversation.getMessages();
//        for (int j = 0; j < messages.size(); j++) {
//            int userid = messages.get(j).getSender_id();
//            if (userid != AppSession.getCurrentUser().id){
//                return userid;            }
//        }
//        return 0;
//    }

    private void sortByDate(List<Conversation> conversations){

        Collections.sort(conversations, new Comparator<Conversation>() {
            @Override
            public int compare(Conversation lhs, Conversation rhs) {
                if (lhs.getUpdatedAt() == rhs.getUpdatedAt())
                    return 0;
                return lhs.getUpdatedAt().after(rhs.getUpdatedAt()) ? -1 : 1;
            }
        });

        adapter.notifyDataSetChanged();
    }


}
