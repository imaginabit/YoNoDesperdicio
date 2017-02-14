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
import android.widget.Toast;

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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Date;
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
//    private RoundedImageView avatar;
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

            if (mUri!= null && AppSession.currentConversation != null){
                Log.d(TAG, "onCreate: mUri: " + mUri);
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
                    mConversation.setOtherUserId(cursor.getInt(3));
                    mConversation.setAdId(cursor.getInt(2));
                    userId = mConversation.getOtherUserId();
                } while (cursor.moveToNext());
            }

        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( (mConversation==null)? "" : mConversation.getSubject() );
        if(AppSession.currentOtherUser!= null) {
            getSupportActionBar().setSubtitle(AppSession.currentOtherUser.getUserName());
        }

        recyclerView = (RecyclerView) findViewById(R.id.list_chat_messages);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessagesAdapter(mMessages,otherUser,avatarBm);
        recyclerView.setAdapter(adapter);

        getMessages();
        //checkMessages();
        pushed= false;

        chatInput = (EditText) findViewById(R.id.chat_input_text);
        //when selected text input show keybord and hide messages
        //this move the scoll to made last messages visibles
        chatInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    recyclerView.smoothScrollToPosition(mMessages.size() - 1);
                }catch (Exception e){
                    e.printStackTrace();
                }

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
        Log.d(TAG, "pushedSendMessageButton() called with: " + "msg = [" + msg + "]" + " pushed: '" + pushed + "'");
        if(pushed==false && Utils.isNotEmptyOrNull(msg)) {
            pushed=true;//avoid accidental double tapping
            Log.d(TAG, "pushedSendMessageButton: mConversation id "+ mConversation.getId() );

            if (mConversation!= null && mConversation.getId()!=0) {
                //conversation alredy exists and just send new message
                Log.d(TAG, "pushedSendMessageButton: message in old conversation: " + mConversation.getId() );
                MessagesUtils.reply(mConversation.getId(), msg, new MessagesUtils.MessagesCallback() {
                    @Override
                    public void onFinished(List<Message> messages, Exception e) {
                        Log.d(TAG, "pushedSendMessageButton_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "]");
                        //Add the new message
                        Message mensajeUnico = messages.get(0);
                        Date mensajeDate = mensajeUnico.getCreated_at();
                        //show time with timezone diff resolved
                        mensajeDate.setTime(mensajeDate.getTime() + Utils.getTimezoneMillisDiference());
                        Log.d(TAG, "onFinished: time millis " + mensajeDate.getTime() + Utils.getTimezoneMillisDiference() );
                        mMessages.addAll(messages);

                        clearInText();
                        updateScreen();

                        Log.d(TAG, "pushedSendMessageButton_onFinished: message size " + messages.size());
                        Log.d(TAG, "pushedSendMessageButton_onFinished: mMessage size " + mMessages.size());
                    }

                    @Override
                    public void onFinished(List<Message> messages, Exception e, ArrayList data) {
                        Log.d(TAG, "pushedSendMessageButton_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "], data = [" + data + "]");
                        Toast.makeText(MessagesChatActivity.this, "Error enviando mensaje", Toast.LENGTH_SHORT).show();
                        clearInText();
                        //do nothing

                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.d(TAG, "pushedSendMessageButton_onError() called with: " + "errorMessage = [" + errorMessage + "]");
                        Toast.makeText(MessagesChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        clearInText();
                    }
                });
            } else {
                //new conversation
                Log.d(TAG, "pushedSendMessageButton: new conversation: to user: " + userId );
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
                            Message mensajeUnico = messages.get(0);
                            Date mensajeDate = mensajeUnico.getCreated_at();
                            //show time with timezone diff resolved
                            mensajeDate.setTime(mensajeDate.getTime() + Utils.getTimezoneMillisDiference());
                            Log.d(TAG, "onFinished: time millis " + mensajeDate.getTime() + Utils.getTimezoneMillisDiference() );

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
                            clearInText();
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
        } else { // if pushed==true
            Log.d(TAG, "pushedSendMessageButton: ya se habia pulsado el boton");
            //si es igual que el ultimo mensaje lo borra de
            if ( mMessages != null && mMessages.size() > 0 ){
                Log.d(TAG, "pushedSendMessageButton: " + mMessages.get( mMessages.size()-1 ).getBody() );

                if ( mMessages.get( mMessages.size()-1 ).getBody().equals(msg) ){
                    clearInText();
                } else {
                    Log.d(TAG, "pushedSendMessageButton: mensaje diferente");
                }
            }

        }
    }

    //clear input text
    private void clearInText(){
        chatInput.setText("");
        pushed = false;
    }

    private void checkMessages(){
        new Handler().postDelayed(new MessagesActivity.RunnableCheckActive(this) {
            @Override
            public void run() {
                Log.v(TAG, "checkMessages run() called with: " + "");

                NavigationBaseActivity a = (NavigationBaseActivity) mActivity;
                Log.d(TAG, "checkMessages: active:" + a.isActive() );
                if (a.isActive() ) {
                    Log.d(TAG, "run: active!");
                    getMessages();
                }
                checkMessages();
            }
        }, 1 * Constants.MINUTE);
    }

    private void getMessages(){

        MessagesUtils.mCurrentActivity = this;
        List<Conversation> conversations = new ArrayList<>();

        if(mConversation!= null)  conversations.add(mConversation);

        MessagesUtils.getConversationMessages(conversations, new MessagesUtils.MessagesCallback() {
            @Override
            public void onFinished(List<Message> messages, Exception e, ArrayList data) {
                Log.d(TAG, "getMessages_onFinished() called with: " + "messages = [" + messages + "], e = [" + e + "], data = [" + data + "]");
//                Toast.makeText(MessagesChatActivity.this, "Mensajes recibidos", Toast.LENGTH_SHORT).show();
                if (data.size() > 0) {
                    mConversation = (Conversation) data.get(0);
                    mMessages = (ArrayList<Message>) mConversation.getMessages();

                    if (otherUser == null) {
                        //get other user id
                        for (Message m : messages) {
                            Log.d(TAG, "getUserWeb onFinished: recorriendo mensajes ");
                            Log.d(TAG, "getUserWeb onFinished: message : " + m.toString());

                            if ( m.getSender_id() != AppSession.getCurrentUser().id ) {
                                int otherUserId = m.getSender_id();

                                Log.d(TAG, "onFinished: getUserWeb get user from message " + otherUserId);

                                UserUtils.getUser(otherUserId, MessagesChatActivity.this, new UserUtils.FetchUserCallback() {
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
                                updateOtherUserInDb(mConversation, otherUserId);
                                break;
                            }
                        }
                    }
                    // bindAdToConversation();

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
        Log.d(TAG, "updateScreen: " + adapter.getItemCount());
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
        Log.d(TAG, "findConversationAd() called with: " + "\nads = [" + ads + "]");
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
            if (ads.size()==1){
                mConversation.setAdId( ads.get(0).getId() );
                goToAd();
            }

            if (ads.size()==0){
                Toast.makeText(MessagesChatActivity.this, "No se ha encontrado ningun anuncio relacionado", Toast.LENGTH_SHORT).show();
            }

            Log.d(TAG, "anuncios : " + ads.size());
            Log.d(TAG, "done: anuncios : "+ ads.toString() );
        } else {
            Toast.makeText(MessagesChatActivity.this, "No se ha encontrado ningun anuncio relacionado", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Check if user triggered a refresh:
            case R.id.goto_ad:
                goToAd();
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

    private void bindAdToConversation(){
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
                            Log.d(TAG, "done() called with: " + "ads = [" + ads + "], e = [" + e + "]");
                            findConversationAd(ads);
                        }
                    });
                } else {
                    Toast.makeText(MessagesChatActivity.this, "No se puede determinar", Toast.LENGTH_SHORT).show();
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
    }

    private void goToAd(){
        Log.d(TAG, "goToAd called");
        if (mConversation != null && mConversation.getAdId() != 0 ) {
            final ProgressDialog pd = new ProgressDialog(MessagesChatActivity.this);
            pd.setTitle("Cargando");
            pd.setMessage("Recibiendo datos...");
            pd.show();

            AdUtils.fetchAd(mConversation.getAdId(), new AdUtils.FetchAdCallback() {
                @Override
                public void done(Ad ad, User user, Exception e) {
                    Log.d(TAG, "ongoto_ad_done called with: " + "ad = [" + ad + "], user = [" + user + "], e = [" + e + "]");
                    Intent intent = new Intent(MessagesChatActivity.this, AdDetailActivity.class);
                    intent.putExtra("ad", ad);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Utils.dismissProgressDialog(pd);
                }
            });
        } else {
            bindAdToConversation();
            //Toast.makeText(MessagesChatActivity.this, "no encontramos a que anuncio pertenece esta conversacion, escoge la que corresponda", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        getMessages();
        super.onResume();
    }
}
