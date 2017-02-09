package com.imaginabit.yonodesperdicion.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.graphics.BitmapCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.activities.MainActivity;
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
                            List<Message> ms = (ArrayList<Message>) cs.getMessages();

                            Log.d(TAG, "ms size  "+ ms.size() + " body last msg " + ms.get(ms.size()-1).getBody() );
                            Log.d(TAG, "ms size  msg string "+  ms.get(ms.size()-1).toString() );
                            Message lastMs = ms.get(ms.size()-1);
                            showNotification(lastMs.getBody(), lastMs.getSubject());
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


    private void showNotification(String notificationBody, String title){
//        Log.d(TAG, "onMessageReceived: notificationText: lenght " + notificationBody  );
//        Log.d(TAG, "onMessageReceived: notificationText: body:  " + notificationText[0]  );
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_stat_brick_notif)
                .setLargeIcon(largeicon)
                .setContentTitle("Yo No Desperdicio - " + title)
                .setContentText( notificationBody );

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

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
        int mId = 1234;
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
