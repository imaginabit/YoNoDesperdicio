package com.imaginabit.yonodesperdicion.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.graphics.BitmapCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.imaginabit.yonodesperdicion.AppSession;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.activities.MainActivity;
import com.imaginabit.yonodesperdicion.activities.MessagesActivity;
import com.imaginabit.yonodesperdicion.activities.MessagesChatActivity;
import com.imaginabit.yonodesperdicion.data.AdsContract;
import com.imaginabit.yonodesperdicion.models.Conversation;
import com.imaginabit.yonodesperdicion.models.Message;
import com.imaginabit.yonodesperdicion.utils.MessagesUtils;
import com.imaginabit.yonodesperdicion.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private Bitmap largeicon;

    private Conversation mConversation; //conversacion acutal

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + data);
            Log.d(TAG, "onMessageReceived: data message_id : " + data.get("message_id"));
            Log.d(TAG, "onMessageReceived: data author_id : " + data.get("author_id"));
            Log.d(TAG, "onMessageReceived: data conversation : " + data.get("conversation"));


            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                //data in indent extra
            }


            largeicon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.brick_avatar);

            //if is a conversation notification get the info
            String conversation_id = data.get("conversation");
            final String[] notificationText = new String[1];
            String notificationBody = "";
            if (Utils.isNotEmptyOrNull( conversation_id )){
                //get data from conversation
                List<Conversation> conversations = new ArrayList<>();
                Conversation c;
                c = new Conversation(Integer.parseInt(conversation_id),"c");
                conversations.add(c);
                MessagesUtils.getConversationMessagesInbox(conversations, new MessagesUtils.MessagesCallback() {
                    @Override
                    public void onFinished(List<Message> messages, Exception e, ArrayList data) {
                        Log.d(TAG, "MessagesCB onFinished() called with: messages = [" + messages.size() + "], e = [" + e + "], data = [" + data.size() + "]");
                        if (data.size() > 0) {

                            Log.d(TAG, "onFinished: last messages "+ messages.get(messages.size()-1) );
                            Conversation cs  = (Conversation) data.get(0);

                            Log.d(TAG, "onFinished: conversation 0 : " + cs );
                            List<Message> ms = (ArrayList<Message>) cs.getMessages();

                            Log.d(TAG, "ms size  "+ ms.size() + " body last msg " + ms.get(ms.size()-1).getBody() );
                            Log.d(TAG, "ms size  msg string "+  ms.get(ms.size()-1).toString() );
                            Message lastMs = ms.get(ms.size()-1);
                            //update conversation info
                            cs.setSubject(lastMs.getSubject());

                            showNotification(lastMs, cs);
                        }
                    }

                    @Override
                    public void onFinished(List<Message> messages, Exception e) {
                        Log.d(TAG, "MessagesCB onFinished() called with: messages  = [" + messages.size() + "], e = [" + e + "]");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.d(TAG, "MessagesCB onError() called with: errorMessage = [" + errorMessage + "]");
                    }
                });
            }
        }
    }


    /**
     * Crea y muestra la notificacion
     * problems if manifest dont have the property "android:launchMode="singleInstance""
     * @param ms Mensage info
     * @param conversation Conversation info
     */
    private void showNotification(Message ms, Conversation conversation){
        String notificationBody = ms.getBody();
        String title = ms.getSubject();
        int cID =  conversation.getId();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_stat_brick_notif)
                .setLargeIcon(largeicon)
                .setContentTitle( title )
                .setContentText( notificationBody )
                .setAutoCancel(true)
                ;

        //Intent open chat window
        Intent resultIntent = new Intent(this, MessagesActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);



        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        // mId == Conversation ID
        int mId = cID;

        mNotificationManager.notify(mId, mBuilder.build());
    }

    /**
     * Esto deberia ser showNotification cuando se resive de tipo conversacion
     * @param ms mesnaje
     * @param conversation conversacion
     */
    private void showNotificationConversation(Message ms, Conversation conversation){
        String notificationBody = ms.getBody();
        String title = ms.getSubject();
        int cID =  conversation.getId();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_stat_brick_notif)
                .setLargeIcon(largeicon)
                .setContentTitle( title )
                .setContentText( notificationBody )
                .setAutoCancel(true);

        //Intent open chat window
        Intent resultIntent = new Intent(this, MessagesChatActivity.class);

        resultIntent.putExtra("conversationId", conversation.getId());
        Uri conversationUri = AdsContract.Conversations.buildConversationUri(String.valueOf( conversation.getDbId()  ));
        resultIntent.putExtra("conversationUri", conversationUri);

        AppSession.currentConversation = conversation;

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        // mId == Conversation ID
        int mId = cID;

        mNotificationManager.notify(mId, mBuilder.build());
    }
}
