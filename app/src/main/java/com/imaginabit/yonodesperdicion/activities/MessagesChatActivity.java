package com.imaginabit.yonodesperdicion.activities;

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
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.MessagesAdapter;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.models.Message;
import com.imaginabit.yonodesperdicion.utils.MessagesUtils;

import java.util.ArrayList;
import java.util.List;


public class MessagesChatActivity extends NavigationBaseActivity {
    private static final String TAG = "MessagesChatActivity";

    Conversation mConversation;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText chatInput;
    private List<Message> mMessages;
    private ImageView mBtnSend;
    private boolean pushed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mConversation = AppSession.currentConversation;

        getSupportActionBar().setTitle(mConversation.getSubject());

        recyclerView = (RecyclerView) findViewById(R.id.list_chat_messages);
        //recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);

        getMessages();
        checkMessages();
        pushed= false;

        chatInput = (EditText) findViewById(R.id.chat_input_text);
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

    private void pushedSendMessageButton(String msg){
        Log.d(TAG, "pushedSendMessageButton() called with: " + "msg = [" + msg + "]");
        if(pushed==false) {
            pushed=true;//avoid accidental double tapping

            MessagesUtils.reply(mConversation.getId(), msg, new MessagesUtils.MessagesCallback() {
                @Override
                public void onFinished(List<Message> messages, Exception e) {
                    Log.d(TAG, "onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "]");

                    //refresh adapter content
                    MessagesAdapter ma = (MessagesAdapter) adapter;
                    Message oMsg = messages.get(messages.size() - 1);
                    if (oMsg != null) {
                        ma.add(oMsg);
                        Log.d(TAG, "onFinished: message size" + mMessages.size());
                        recyclerView.scrollToPosition(mMessages.size() - 1);
                        chatInput.setText("");
                        //chatInput.clearFocus();
                        //recyclerView.smoothScrollToPosition(0);
                        recyclerView.smoothScrollToPosition(mMessages.size() - 1);
                        pushed = false;
                    } else {
                        Log.d(TAG, "onFinished: message null?");
                        Log.d(TAG, "onFinished: messages: " + messages.toString());
                    }
                }

                @Override
                public void onFinished(List<Message> messages, Exception e, ArrayList data) {
                    //do nothing
                }

                @Override
                public void onError(String errorMessage) {
                    Log.d(TAG, "onError() called with: " + "errorMessage = [" + errorMessage + "]");
                }
            });
        }
    }

    private void checkMessages(){
        new Handler().postDelayed(new MessagesActivity.RunnableCheckActive(this) {
            @Override
            public void run() {
                Log.v(TAG, "checkMessages run() called with: " + "");
                NavigationBaseActivity a = (NavigationBaseActivity) mActivity;
                if ( a.isActive() ){
                    Log.d(TAG, "run: active!");
                    getMessages();
                }
                checkMessages();
            }
        }, 60000);
    }

    private void getMessages(){
        Log.v(TAG, "getMessages: from conversation "+ mConversation.getId() );
        mMessages = mConversation.getMessages();
        if( mMessages != null ) {
            Log.d(TAG, "onCreate: Conversation messages " + mMessages.size());
            Log.d(TAG, "onCreate: Conversation messages " + mMessages.get(0).toString());
        }else{
            mMessages = new ArrayList<Message>();
            Log.d(TAG, "onCreate: Conversation messages null" );
        }

        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessagesAdapter(mMessages);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "getMessages: scrollstate" + recyclerView.getScrollState() );


        recyclerView.scrollToPosition(mMessages.size() - 1);
    }



}
