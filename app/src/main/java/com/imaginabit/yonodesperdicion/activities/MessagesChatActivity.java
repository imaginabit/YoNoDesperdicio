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
    private List<Message> mMessages = new ArrayList<Message>();
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

        updateScreen();//set adpater
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
        if(pushed==false && Utils.isNotEmptyOrNull(msg)) {
            pushed=true;//avoid accidental double tapping

            MessagesUtils.reply(mConversation.getId(), msg, new MessagesUtils.MessagesCallback() {
                @Override
                public void onFinished(List<Message> messages, Exception e) {
                    Log.d(TAG, "pushedSendMessageButton_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "]");

                    //refresh adapter content
                    MessagesAdapter ma = (MessagesAdapter) adapter;
                    Message oMsg = messages.get(messages.size() - 1);
                    if (oMsg != null && adapter!= null) {
                        //adapter.add(oMsg);// ????
                        mMessages.add(oMsg);
                        ma.add(oMsg);

                        Log.d(TAG, "pushedSendMessageButton_onFinished: message size" + mMessages.size());

                        chatInput.setText("");
                        //chatInput.clearFocus();

                        recyclerView.smoothScrollToPosition(mMessages.size() - 1);
                        pushed = false;
                    } else {
                        Log.d(TAG, "pushedSendMessageButton_onFinished: message null?");
                        Log.d(TAG, "pushedSendMessageButtonon_Finished: messages: " + messages.toString());
                    }
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
                mConversation = (Conversation) data.get(0);
                mMessages = mConversation.getMessages();

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
        Log.d(TAG, "updateScreen: ");
        getSupportActionBar().setTitle(mConversation.getSubject());

        if (adapter== null){
            adapter = new MessagesAdapter(mMessages);
        } else {
            //add new messages only
            if (((MessagesAdapter)adapter).getItemCount()>0) {
                Log.d(TAG, "updateScreen: MessageAdapter count"+ ((MessagesAdapter)adapter).getItemCount() );
                for (int i = ((MessagesAdapter) adapter).getItemCount() - 1; i < mMessages.size(); i++) {//
                    ((MessagesAdapter) adapter).add(mMessages.get(i));
                }
            } else {
                adapter = new MessagesAdapter(mMessages);
            }
        }
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        Log.d(TAG, "getMessages: scrollstate" + recyclerView.getScrollState());

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
