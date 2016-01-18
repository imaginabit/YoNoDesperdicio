package com.imaginabit.yonodesperdicion.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.MessagesAdapter;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.models.Message;

import java.util.List;

public class MessagesChatActivity extends AppCompatActivity {
    private static final String TAG = "MessagesChatActivity";

    Conversation mConversation;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mConversation = AppSession.currentConversation;

        getSupportActionBar().setTitle(mConversation.getSubject());

        recyclerView = (RecyclerView) findViewById(R.id.list_chat_messages);
        //recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);

        List<Message> messages = mConversation.getMessages();
        if( messages != null ) {
            Log.d(TAG, "onCreate: Conversation messages " + messages.size());
            Log.d(TAG, "onCreate: Conversation messages " + messages.get(0).toString());
        }else{
            Log.d(TAG, "onCreate: Conversation messages null" );
        }

        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessagesAdapter(messages);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

}
