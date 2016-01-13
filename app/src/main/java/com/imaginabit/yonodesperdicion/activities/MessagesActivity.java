package com.imaginabit.yonodesperdicion.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.data.UserData;
import com.imaginabit.yonodesperdicion.helpers.VolleySingleton;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.utils.MessagesUtils;

import java.util.List;

public class MessagesActivity extends NavigationBaseActivity {
    private final String TAG = getClass().getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

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

        VolleySingleton.init(this);


        MessagesUtils.getMessages(MessagesActivity.this, new MessagesUtils.MessagesCallback() {
            @Override
            public void onFinished(List<Conversation> conversations, Exception e) {
                Log.d(TAG, "onFinished: finishesd");
                if(conversations!=null){
                    //mConversations = conversations
                    //adapter = new
                    //recyclerView.setAdapter(adapter);
                    //adapter.notifyDataSetChanged();
                    Log.d(TAG, "Conversacionesl : " + conversations.size());
                    Toast.makeText(MessagesActivity.this, conversations.get(0).getSubject(), Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.d(TAG, "onError: error");
            }
        });

    }

}
