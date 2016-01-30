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
import com.imaginabit.yonodesperdicion.Constants;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.MessagesAdapter;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle data = getIntent().getExtras();
        if (data!=null) {
            mConversation = new Conversation((Integer) data.get("conversationId"), " ");
        }
        mConversation = AppSession.currentConversation;

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

    private void pushedSendMessageButton(String msg){
        Log.d(TAG, "pushedSendMessageButton() called with: " + "msg = [" + msg + "]");
        if(pushed==false && Utils.isNotEmptyOrNull(msg)) {
            pushed=true;//avoid accidental double tapping

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
