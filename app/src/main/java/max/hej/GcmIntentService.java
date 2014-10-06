package max.hej;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import static android.os.Build.VERSION.SDK_INT;

public class GcmIntentService extends IntentService {
    public int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    String TAG = "HejApp";


    public GcmIntentService() {
        super("GcmIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.

                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification(extras.get("sender").toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        Uri hejsound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.hejsound);
        long[] notifyVibrate = {0, 200, 100, 200};
        Intent hejIntent = new Intent(this, MyActivity.class);
        hejIntent.putExtra("sender", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                hejIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NOTIFICATION_ID = msg.hashCode();

        if (SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {

            NotificationManager mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("Hej")
                            .setTicker("Hej from " + msg + "!")
                            .setVibrate(notifyVibrate)
                            .setSound(hejsound)
                            .setLights(0xFFFF8B00, 200, 200)
                            .setContentText("Hej from " + msg + "!");


            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());


        } else { //Sander Edition
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            System.out.println("what the fuck");
            int icon = R.drawable.ic_launcher;
            CharSequence text = "Hej from " + msg + "!";
            CharSequence contentTitle = "Hej";
            CharSequence contentText = "Hej from " + msg + "!";
            long when = System.currentTimeMillis();

            Notification notification = new Notification(icon,text,when);

            notification.vibrate = notifyVibrate;
            notification.sound = hejsound;
            notification.ledARGB = 0xFFFF8B00;
            notification.ledOffMS = 200;
            notification.ledOnMS = 200;

            notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);

            notificationManager.notify(NOTIFICATION_ID, notification);


        }
    }
}