package com.imaginabit.yonodesperdicion.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
import com.imaginabit.yonodesperdicion.models.Ad;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.models.Message;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.utils.AdUtils;
import com.imaginabit.yonodesperdicion.utils.MessagesUtils;
import com.imaginabit.yonodesperdicion.utils.UserUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;
import com.imaginabit.yonodesperdicion.views.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

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
    private RoundedImageView avatar;
    private Bitmap avatarBm;

    private Uri mUri;
    private ContentResolver mContentResolver;
    private int adId;
    private int userId;
    private int dbConversationId;
    private String adName;
    private User otherUser;


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

            if (mUri!= null){
                mConversation = AppSession.currentConversation;
                Log.d(TAG, "onCreate: AppSession current conversation: "+AppSession.currentConversation.toString());
            }

            //get data form database
            mContentResolver = getContentResolver();
            //String[] projection = new String[]{BaseColumns._ID, AdsContract.FavoritesColumns.FAV_AD_ID};
            String[] projection = new String[]{};
            Cursor cursor = mContentResolver.query(mUri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    Log.d(TAG, "Cursor recorriendo: " + cursor.getColumnNames().toString());
                    Log.d(TAG, "Cursor recorriendo: id 0: " + cursor.getString(0));
//                    Log.d(TAG, "Cursor recorriendo: CONVERSATION_WEB_ID 1: " + cursor.getString(1) );
//                    Log.d(TAG, "Cursor recorriendo: CONVERSATION_AD_ID 2: " + cursor.getString(2) );
//                    Log.d(TAG, "Cursor recorriendo: CONVERSATION_USER 3: " + cursor.getString(3) );
//                    Log.d(TAG, "Cursor recorriendo: CONVERSATION_STATUS 4: " + cursor.getString(4));
                    mConversation.setDbId( cursor.getInt(0) );
                    mConversation.setOtherUserId( cursor.getInt(3) );
                    mConversation.setAdId( cursor.getInt(2) );
                } while (cursor.moveToNext());
            }

        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mConversation.getSubject());
        if(AppSession.currentOtherUser!= null) {
            getSupportActionBar().setSubtitle(AppSession.currentOtherUser.getUserName());
        }

        recyclerView = (RecyclerView) findViewById(R.id.list_chat_messages);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessagesAdapter(mMessages,otherUser,avatarBm);
        recyclerView.setAdapter(adapter);

//        avatar = (RoundedImageView) findViewById(R.id.chat_avatar);

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
                if (data.size() > 0) {
                    mConversation = (Conversation) data.get(0);
                    mMessages = (ArrayList<Message>) mConversation.getMessages();

                    if (otherUser==null) {
                        //get other user id
                        for (Message m : messages) {
                            Log.d(TAG, "getUserWeb onFinished: recorriendo mensajes ");
                            Log.d(TAG, "getUserWeb onFinished: message : " + m.toString());
                            if (m.getSender_id() != AppSession.getCurrentUser().id) {
                                userId = m.getSender_id();
                                Log.d(TAG, "onFinished: getUserWeb get user from message " + userId);

                                UserUtils.getUser(userId, MessagesChatActivity.this, new UserUtils.FetchUserCallback() {
                                    @Override
                                    public void done(User user, Exception e) {
                                        otherUser = user;
                                        //get image from website
                                        ImageSize targetSize = new ImageSize(300, 300); // result Bitmap will be fit to this size
                                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                                .delayBeforeLoading(0)
                                                .cacheInMemory(true)
                                                .build();
                                        ImageLoader imageLoader; // Get singleton instance
                                        imageLoader = ImageLoader.getInstance();
                                        String imageUri = Constants.HOME_URL + user.getAvatar();
                                        Log.i(TAG, "avatar url : " + imageUri);
                                        imageLoader.loadImage(imageUri, targetSize, options, new SimpleImageLoadingListener() {
                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                // Do whatever you want with Bitmap
                                                avatarBm = loadedImage;
                                                updateScreen();
                                            }
                                        });

                                    }
                                });
                                updateOtherUserInDb(mConversation, userId);
                                break;
                            }
                        }
                    }
                    //if no Ad asigned to conversation
                    if (mConversation.getAdId() == 0 && mMessages.size()>0){
                        if ( mMessages.get(0).getSender_id() == AppSession.getCurrentUser().id){
                            Log.d(TAG, "onFinished: search Ad, first Message from me");
                            if (mConversation.getOtherUserId()!=0) {
                                Log.d(TAG, "onFinished: search Ad, first Message from other");
                                User u = new User(mConversation.getOtherUserId(), "", "", "", "", 0, 0);
                                Log.d(TAG, "get Ads From Web");
                                AdUtils.fetchAdsVolley(u, MessagesChatActivity.this, new AdUtils.FetchAdsCallback() {
                                    @Override
                                    public void done(List<Ad> ads, Exception e) {
                                        Log.d(TAG, "done get ads from other user");
                                        findConversationAd(ads);
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "onFinished: search Ad, first Message from other user");
                            //get user ads
                            User u = new User((int) AppSession.getCurrentUser().id, "", "", "", "", 0, 0);
                            Log.d(TAG, "get Ads From Web");
                            AdUtils.fetchAdsVolley(u, MessagesChatActivity.this, new AdUtils.FetchAdsCallback() {
                                @Override
                                public void done(List<Ad> ads, Exception e) {
                                    Log.d(TAG, "done get ads from current user");
                                    findConversationAd(ads);
                                }
                            });

                        }
                    }

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

    private void updateScreen() {
        Log.d(TAG, "updateScreen: " + ((MessagesAdapter) adapter).getItemCount());
//        TODO: this is wrong, notifyDataSetChanged must update the adapter but no working!
        adapter = new MessagesAdapter(mMessages,otherUser,avatarBm);
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

    private Integer updateOtherUserInDb(final Conversation  conversation, Integer otherUser){
        Log.d(TAG, "getUserWeb updateInDb() called with: " + "conversation = [" + conversation + "]");
        ContentValues contentValues = new ContentValues();
        String where = "";
        String[] args = {};
        conversation.setOtherUserId(otherUser);//change mconversation too

        Uri uri = AdsContract.Conversations.buildConversationUri(String.valueOf( conversation.getDbId() ));

        contentValues.put(AdsContract.ConversationsColumns.CONVERSATION_USER, otherUser);

        Integer count = mContentResolver.update(uri, contentValues, where, args);
        return count;
    }

    private void showDialogChooseConversationAd(final List<Ad> ads){
        Log.d(TAG, "showDialogChooseConversationAd() called with: " + "ads = [" + ads + "]");
        //con que anuncio esta asociado esta conversacion?
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this ,R.style.yndDialog );

        dialog.setTitle("Asociar a un anuncio");
        //yo cant set message and items list
//        dialog.setMessage("Este chat no esta asociado ningun anuncio\n¿Quieres asociarlo ahora?");

//        HashMap<Integer, Ad> mapAdsId = new HashMap<>();
//        HashMap<String, Ad> mapAdsName = new HashMap<>();
        final String[] adsTitles = new String[ads.size()];
        for (int i = 0; i < ads.size(); i++) {
            adsTitles[i] = ads.get(i).getTitle();
        }

        final CharSequence[] options = adsTitles;

        //final CharSequence[] strAds =  ads.toArray(new String[ads.size()]);
        dialog.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Log.d(TAG, "onClick() called with: " + "dialog = [" + dialog + "], item = [" + item + "]");
                String selectedText = adsTitles[item].toString();  //Selected item in listview
                mConversation.setAdId(ads.get(item).getId());
                //todo save in database
                MessagesUtils.updateConversationInDb(mContentResolver, mConversation);
            }
        });

        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialog.create();
        dialog.show();
    }

    private void findConversationAd( List<Ad> ads ){
        if (ads != null) {
            if(ads.size()>1) {
                List<String> adsTitles = new ArrayList<String>();
                int i;
                for ( i = 0; i < ads.size(); i++) {
                    adsTitles.add(ads.get(i).getTitle());
                    if ( ads.get(i).getTitle().equals( mConversation.getSubject()) ){
                        Log.d(TAG, "done set ad: " +
                                " title: " + ads.get(i).getTitle() +
                                " adId: " + ads.get(i).getId() );
                        mConversation.setAdId(ads.get(i).getId());
                        break;
                    }
                }
                //if for is all run w/o get ad id
                if (i== ads.size()) {
                    showDialogChooseConversationAd(ads);
                }
            }
            if (ads.size()==1) mConversation.setAdId( ads.get(0).getId() );

            Log.d(TAG, "anuncios : " + ads.size());
            Log.d(TAG, "done: anuncios : "+ ads.toString() );
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Check if user triggered a refresh:
            case R.id.goto_ad:
                final ProgressDialog pd = new ProgressDialog(MessagesChatActivity.this);
                pd.setTitle("Cargando");
                pd.setMessage("Recibiendo datos...");
                pd.show();

                AdUtils.fetchAd(mConversation.getId(), new AdUtils.FetchAdCallback() {
                    @Override
                    public void done(Ad ad, User user, Exception e) {
                        Intent intent = new Intent(MessagesChatActivity.this, AdDetailActivity.class);
                        intent.putExtra("ad", (Parcelable) ad );
                        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Utils.dismissProgressDialog(pd);
                    }
                });
                return true;
            case R.id.action_booked:
                return true;
            case R.id.action_deliver:
                return true;
        }
        // User didn't trigger a refresh, let the superclass handle this action
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.message_chat, menu);
        return true;
    }
}
