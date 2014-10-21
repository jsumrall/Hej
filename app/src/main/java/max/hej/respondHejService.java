package max.hej;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class respondHejService extends IntentService {

    private max.hej.Message message;
    String username;
    String password;
    SharedPreferences credentials;
    Handler handler;
    FriendList friends;


    public respondHejService() {
        super("respondHejService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Here");
        if (intent != null) {
            System.out.println("Now Here");
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();

            NotificationManager mNM = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            credentials = getSharedPreferences(MyActivity.PREFS_NAME, 0);
            username = credentials.getString("username", "not set");
            password = credentials.getString("password", "not set");
            friends = new FriendList(credentials.getString("friends",username+",Max"), credentials);

            handler = new Handler(){
                public void handleMessage(android.os.Message msg){
                    Bundle bundle = msg.getData();
                    String string = bundle.getString("0");
                }
            };


            if(extras != null) {
                if (extras.containsKey("sender")) {
                    String msgFrom = extras.getString("sender");
                    if (!msgFrom.equals("null")) {
                        friends.addIfNotFriend(msgFrom);//add the person to friends list if they are not a friend.
                    }
                }
                if (extras.containsKey("respondTo")) {
                    String msgFrom = extras.getString("respondTo");
                    if (!msgFrom.equals("null")) {
                        //Cancel the notification
                        mNM.cancel(msgFrom.hashCode());//i made the id the hash
                        message = new max.hej.Message.Builder()
                                .username(username)
                                .password(password)
                                .intent(max.hej.Message.SEND_HEJ)
                                .target(msgFrom)
                                .build();
                        Communicator comm = new Communicator(message, this.handler);
                        comm.execute();

                    }

                }
            }
        }
    }
}
