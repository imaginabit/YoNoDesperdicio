package com.imaginabit.yonodesperdicion.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
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

                    /*
                     this is a nonsese.....
                    if(mConversationList!=null) {
                        Log.d(TAG, "onFinished: CONVERSATION NOT NULL");
                        Utils.dismissProgressDialog(pd);
                        //copy oy mConversations w/o messages
                        List<Conversation> mconversationsLight = new ArrayList<Conversation>();
                        for (int i=0;i<mConversationList.size();i++){
                            Conversation c = mConversationList.get(i);
                            if (c!=null) {
                                c.setMessages(null);
                                mconversationsLight.add(i, c);
                            }
                        }
                        //compare conversation form api with loaded in app
                        if (mconversationsLight == conversations) {
                            Log.d(TAG, "onFinished: no hay cambios");
                        } else {
                            Log.d(TAG, "onFinished: Cambios");
                            Log.d(TAG, "onFinished: mConversationList size:" + mconversationsLight.size() );
                            Log.d(TAG, "onFinished: conversations: size" + conversations.size() );
//                            Log.d(TAG, "onFinished: mConversationList:" + mConversationList.get(0).toString()  );
//                            Log.d(TAG, "onFinished: conversations:" + conversations.get(0).toString()  );
                            Log.d(TAG, "onFinished: mConversationList:" + mconversationsLight.get(mConversationList.size()-1).toString()  );
                            Log.d(TAG, "onFinished: conversations:" + conversations.get(conversations.size()-1).toString()  );

                            Log.d(TAG, "onFinished: compare "+ (conversations.get(1) == mconversationsLight.get(1)) );
                            Log.d(TAG, "onFinished: compare "+ conversations.get(1).toString() );
                            Log.d(TAG, "onFinished: compare "+ mconversationsLight.get(1).toString() );

                            if ((mconversationsLight.containsAll(conversations)) &&
                                    (mconversationsLight.size() == conversations.size())){
                                //TODO: si son iguales ??
                                // no hace falta cargar los mensajes pero esto da igual no? ya los ha cargado
                                //genial no recuerdo por que estaba prubando todo este rollo ya
                                Log.d(TAG, "onFinished: TRUE");
                            }

                            Log.d(TAG, "onFinished: containsAll" + mconversationsLight.equals(conversations));
                            mconversationsLight.removeAll(conversations);
//                            Log.d(TAG, "onFinished: mConversationList size after:" + mconversationsLight.size());
//                            Log.d(TAG, "onFinished: mConversationList string:" + mconversationsLight.toString() );

                            //Log.d(TAG, "onFinished: conversations:" + conversations.toString());
                        }
                    } else {
                        Log.d(TAG, "onFinished: CONVERSATION NULL --------------------");
                    }
                    */

                    adapter = new ConversationsAdapter(context, conversations);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.v(TAG, "Conversacionesl : " + conversations.size());
                    Log.v(TAG, "Conversaciones getItemCount : " + adapter.getItemCount());
                    mConversationList = conversations;

                    Date d = new Date();
                    Log.v(TAG, "getConversaitonMessages time: " + Constants.DATE_JSON_FORMAT.format(d.getTime()));

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



}
