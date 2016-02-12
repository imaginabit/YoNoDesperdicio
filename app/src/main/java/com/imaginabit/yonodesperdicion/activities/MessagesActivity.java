package com.imaginabit.yonodesperdicion.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

        mContentValues = new ContentValues();

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
        mConversationList = new ArrayList<Conversation>();

        VolleySingleton.init(this);
        getConversationAppData();
        getConversationsFromApi();
        //checkMessages();
    }

    /**
     * Check for conversation info every 2 minutes
     */
    private void checkMessages(){
        new Handler().postDelayed(new RunnableCheckActive(this) {
            @Override
            public void run() {
                Log.v(TAG, "checkMessages run() called with: " + "");
                MessagesActivity a = (MessagesActivity) mActivity;
                if ( a.isActive() ){
                    Log.d(TAG, "run: active!");
                    getConversationsFromApi();

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

    private void updateAdapter(){
        adapter = new ConversationsAdapter(context, mConversationList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.v(TAG, "Conversaciones getItemCount : " + adapter.getItemCount());
    }

    /**
     * get all conversations on website
     * if they are in database get the other user info
     */
    private void getConversationsFromApi(){
        Log.d(TAG, "getConversationsFromApi() called with: " + "");
        final HashMap<Integer, Conversation> mapDbConversations = new HashMap<>();
        for ( Conversation c : mConversationList ) {
            mapDbConversations.put(c.getId(), c);
        }

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
                    Log.v(TAG, "Conversacionesl : " + conversations.size());

//                    Date d = new Date();
//                    Log.v(TAG, "getConversaitonMessages time: " + Constants.DATE_JSON_FORMAT.format(d.getTime()));

                    for (int i = 0; i < conversations.size(); i++) {
                        Log.d(TAG, "onFinished: for i = " + i );
                        Conversation c = conversations.get(i);
                        Log.d(TAG, "onFinished: current conversation : " + c.toString() );
                        //buscar en la base de datos y crear si no se encuentra
                        try {
                            //load data from database
                            Conversation dbC = mapDbConversations.get(c.getId());
                            Log.d(TAG, "onFinished: coversation database " + dbC.toString() );
                            //get data from database
                            c.setDbId(dbC.getDbId());
                            c.setOtherUserId(dbC.getOtherUserId());
                            mConversationList.set(i,c);

                        } catch (Exception e2 ){
                            e2.printStackTrace();
                            //is not in database
                            //save conversation in database
                            Integer dbId = saveInDb(c);
                            c.setDbId(dbId);
                        }
                    }
                    sortByDate(mConversationList);
                    updateAdapter();

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
//        List<Message> messages = conversation.getConversationsFromApi();
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

    /**
     * get conversation info from internal app database
     */
    private void getConversationAppData(){
        String[] projection = new String[]{};
        String selectionClause = "";
        String[] selectionArgs = new String[]{};
        ContentResolver contentResolver = getContentResolver();
        Cursor returnConversation =  contentResolver.query(AdsContract.URI_TABLE_CONVERSATIONS,projection,selectionClause,selectionArgs,"" );
        //if is in database take the existing conversation
        if (returnConversation.moveToFirst()) {
            int paso = 0;
            do {
                int id = returnConversation.getInt(0);
                int webId = 0;
                webId = returnConversation.getInt(1);
                int adId = returnConversation.getInt(2);
                int userId = returnConversation.getInt(3);
                String title = returnConversation.getString(5);
                Log.d(TAG, "Cursor recorriendo: CONVERSATION_WEB_ID 1: " + returnConversation.getString(1));
                Log.d(TAG, "Cursor recorriendo: CONVERSATION_AD_ID 2: " + returnConversation.getString(2));
                Log.d(TAG, "Cursor recorriendo: CONVERSATION_USER 3: " + returnConversation.getString(3));
                Log.d(TAG, "Cursor recorriendo: CONVERSATION_STATUS 4: " + returnConversation.getString(4));

                paso++;
                Log.d(TAG, "clickMessage: paso " + paso);
                Conversation conversation;

                conversation = new Conversation(webId, title);
                conversation.setDbId(id);
                conversation.setOtherUserId(userId);
                mConversationList.add(conversation);
                updateAdapter();
                //Uri conversationUri = AdsContract.Conversations.buildConversationUri(String.valueOf(id));
            } while (returnConversation.moveToNext());
        }

    }

    private Integer saveInDb(Conversation conversation){
        Log.d(TAG, "saveInDb() called with: " + "conversation = [" + conversation + "]");
        mContentValues = new ContentValues();

        mContentValues.put(AdsContract.ConversationsColumns.CONVERSATION_WEB_ID, conversation.getId());
        if (conversation.getOtherUserId()!=0) {
            mContentValues.put(AdsContract.ConversationsColumns.CONVERSATION_USER, conversation.getOtherUserId());
        }
        mContentValues.put(AdsContract.ConversationsColumns.CONVERSATION_AD_ID, "");
        Uri returned = mContentResolver.insert(AdsContract.URI_TABLE_CONVERSATIONS, mContentValues);
        String returnedId = returned.getLastPathSegment();

        Log.d(TAG, "onFinished: returnedId Save in db with id " + returnedId);
        return Integer.valueOf(returnedId);
    }

    private Integer updateInDb(Conversation conversation){
        Log.d(TAG, "updateInDb() called with: " + "conversation = [" + conversation + "]");
        mContentValues = new ContentValues();
        String where = "";
        String[] args = {};

        Uri uri = AdsContract.Conversations.buildConversationUri(String.valueOf( conversation.getDbId() ));

        mContentValues.put(AdsContract.ConversationsColumns.CONVERSATION_USER, conversation.getOtherUserId());

        Integer count = mContentResolver.update(uri, mContentValues, where, args);
        return count;
    }



}
