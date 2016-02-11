package com.imaginabit.yonodesperdicion.activities;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.MessagesAdapter;
import com.imaginabit.yonodesperdicion.data.AdsContract;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.models.Message;
import com.imaginabit.yonodesperdicion.utils.MessagesUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class MessagesChatActivity extends NavigationBaseActivity {
    private static final String TAG = "MessagesChatActivity";

    Conversation mConversation;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText chatInput;
    private ArrayList<Message> mMessages = new ArrayList<Message>();
    private ImageView mBtnSend;
    private boolean pushed;

    private Uri mUri;
    private ContentResolver mContentResolver;
    private int adId;
    private int userId;
    private int dbConversationId;
    private String adName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle data = getIntent().getExtras();
        if (data!=null) {
//            mConversation = new Conversation((Integer) data.get("conversationId"), " ");
            mUri = (Uri) data.get("conversationUri");
            adName = (String) data.get("adName");

            //get data form database
            mContentResolver = getContentResolver();
            //String[] projection = new String[]{BaseColumns._ID, AdsContract.FavoritesColumns.FAV_AD_ID};
            String[] projection = new String[]{};
            Cursor cursor = mContentResolver.query(mUri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                int i =0;
                do {
//                    long id = cursor.getLong(0);
//                    String ad_strid = cursor.getString(1);
//                    int ad_id = Integer.parseInt(ad_strid);
                    Log.d(TAG, "onCreate: paso " +  i);
                    i++;

                    Log.d(TAG, "Cursor recorriendo: " + cursor.getColumnNames().toString());
                    dbConversationId = cursor.getInt(0);
                    Log.d(TAG, "Cursor recorriendo: id 0: " + cursor.getString(0));
                    Log.d(TAG, "Cursor recorriendo: CONVERSATION_WEB_ID 1: " + cursor.getString(1) );
                    adId = cursor.getInt(2);
                    Log.d(TAG, "Cursor recorriendo: CONVERSATION_AD_ID 2: " + cursor.getString(2) );
                    userId = cursor.getInt(3);
                    Log.d(TAG, "Cursor recorriendo: CONVERSATION_USER 3: " + cursor.getString(3) );
                    Log.d(TAG, "Cursor recorriendo: CONVERSATION_STATUS 4: " + cursor.getString(4) );
                } while (cursor.moveToNext());
            }
        }

        if (mUri!= null){
            mConversation = AppSession.currentConversation;
            Log.d(TAG, "onCreate: AppSession current conversation: "+AppSession.currentConversation.toString());
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mConversation.getSubject());

        recyclerView = (RecyclerView) findViewById(R.id.list_chat_messages);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessagesAdapter(mMessages);
        recyclerView.setAdapter(adapter);

        getMessages();
        checkMessages();
        pushed= false;

        chatInput = (EditText) findViewById(R.id.chat_input_text);
        //when selected text input show keybord and hide messages
        //this move the scoll to made last messages visibles
        chatInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(mMessages.size() - 1);
            }
        });
        chatInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String msg = v.getText().toString();
                    pushedSendMessageButton(msg);
                    return true;
                }
                return false;
            }
        });

        mBtnSend = (ImageView) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushedSendMessageButton(chatInput.getText().toString());
            }
        });


    }

    private void pushedSendMessageButton(final String msg){
        Log.d(TAG, "pushedSendMessageButton() called with: " + "msg = [" + msg + "]");
        if(pushed==false && Utils.isNotEmptyOrNull(msg)) {
            pushed=true;//avoid accidental double tapping
            Log.d(TAG, "pushedSendMessageButton: mConversation id "+ mConversation.getId() );

            if (mConversation!= null && mConversation.getId()!=0) {
                //conversation alredy exists and just send new message
                MessagesUtils.reply(mConversation.getId(), msg, new MessagesUtils.MessagesCallback() {
                    @Override
                    public void onFinished(List<Message> messages, Exception e) {
                        Log.d(TAG, "pushedSendMessageButton_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "]");
                        //Add the new message
                        mMessages.addAll(messages);
                        chatInput.setText("");
                        pushed = false;
                        updateScreen();

                        Log.d(TAG, "pushedSendMessageButton_onFinished: message size " + messages.size());
                        Log.d(TAG, "pushedSendMessageButton_onFinished: mMessage size " + mMessages.size());
                    }

                    @Override
                    public void onFinished(List<Message> messages, Exception e, ArrayList data) {
                        Log.d(TAG, "pushedSendMessageButton_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "], data = [" + data + "]");
                        //do nothing
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.d(TAG, "pushedSendMessageButton_onError() called with: " + "errorMessage = [" + errorMessage + "]");
                    }
                });
            } else {
                //new conversation
                MessagesUtils.createConversation(adName, userId, msg, new MessagesUtils.MessagesCallback() {
                    @Override
                    public void onFinished(List<Message> messages, Exception e) {
                        Log.d(TAG, "clickMessage_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "]");
                        //do nothing
                    }

                    @Override
                    public void onFinished(List<Message> messages, Exception e, ArrayList data) {
                        Log.d(TAG, "clickMessage_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "], data = [" + data + "]");
                        if (data != null && data.size() > 0) {
                            Conversation conversation = ((Conversation) data.get(0));
                            int converId = conversation.getId();
                            mConversation.setId(converId);
                            
                            //save new id en database conversation_web_id
                            ContentValues values = new ContentValues();
                            values.put(AdsContract.Conversations.CONVERSATION_WEB_ID, converId);
                            String[] a = {};
                            int count = mContentResolver.update(mUri,values,"", a );
                            Log.d(TAG, "onOptionsItemSelected: Record Id returned is " + count );
                            if (!(count>0)) Log.d(TAG, "onFinished: ____error ! not saved id ");

                            //add message in app, may show it duplicated when getMessages from server
                            //mMessages = new ArrayList<Message>();
                            //mMessages.add(new Message(0,msg, (int) AppSession.getCurrentUser().id,new Date() ));

                            //update messages show in screen
                            chatInput.setText("");
                            pushed = false;
                            getMessages();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.d(TAG, "clickMessage_onError() called with: " + "errorMessage = [" + errorMessage + "]");
                        //if (errorMessage=="{\"errors\":\"Not authenticated\"}");
                        //TODO: onError
                    }
                });

            }

            //save info in database
        }
    }

    private void checkMessages(){
        new Handler().postDelayed(new MessagesActivity.RunnableCheckActive(this) {
            @Override
            public void run() {
                Log.v(TAG, "checkMessages run() called with: " + "");
                NavigationBaseActivity a = (NavigationBaseActivity) mActivity;
                if (a.isActive()) {
                    Log.d(TAG, "run: active!");
                    getMessages();
                }
                checkMessages();
            }
        }, 1 * Constants.MINUTE);
    }

    private void getMessages(){
        Log.v(TAG, "getMessages: from conversation " + mConversation.getId());
//        mMessages = mConversation.getMessages();
        MessagesUtils.mCurrentActivity = this;
        List<Conversation> conversations = new ArrayList<>();
        conversations.add(mConversation);

        MessagesUtils.getConversationMessages(conversations, new MessagesUtils.MessagesCallback() {
            @Override
            public void onFinished(List<Message> messages, Exception e, ArrayList data) {
                Log.d(TAG, "getMessages_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "], data = [" + data + "]");
//                Toast.makeText(MessagesChatActivity.this, "Mensajes recibidos", Toast.LENGTH_SHORT).show();
                if (data.size()>0) {
                    mConversation = (Conversation) data.get(0);
                    mMessages = (ArrayList<Message>) mConversation.getMessages();
                } else {
                    Log.d(TAG, "onFinished: Data menor o igual 0");
                }

                Log.d(TAG, "getMessages_onFinished: message status" + Constants.longline);
                messageStatus();
                updateScreen();
            }

            @Override
            public void onFinished(List<Message> messages, Exception e) {
                Log.d(TAG, "getMessages_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "]");
                //nothing
            }

            @Override
            public void onError(String errorMessage) {
                Log.d(TAG, "getMessages_onError() called with: " + "errorMessage = [" + errorMessage + "]");

            }
        });

        //MessagesUtils.getMessages(mConversation.getId());
        Log.d(TAG, "getMessages: Message Status -----------------------------");
        messageStatus();
    }

    private void updateScreen(){
        Log.d(TAG, "updateScreen: " + ((MessagesAdapter) adapter).getItemCount());
//        TODO: this is wrong, notifyDataSetChanged must update the adapter but no working!
        adapter = new MessagesAdapter(mMessages);
        recyclerView.setAdapter(adapter);

        getSupportActionBar().setTitle(mConversation.getSubject());
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(mMessages.size() - 1);
    }

    
    private void messageStatus(){
        if( mMessages != null ) {
            Log.d(TAG, "onCreate: Conversation messages " + mMessages.size());
            if (mMessages.size()>0)
                Log.d(TAG, "onCreate: Conversation messages " + mMessages.get(0).toString());
        }else{
            mMessages = new ArrayList<Message>();
            Log.d(TAG, "onCreate: Conversation messages null" );
        }
    }

}
