package max.hej;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class RespondToHej extends Activity {
    Intent loginScreen;
    private max.hej.Message message;
    Toast mToast;
    String username;
    String password;
    SharedPreferences credentials;
    Handler handler;
    FriendList friends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_to_hej);
        credentials = getSharedPreferences(MyActivity.PREFS_NAME, 0);
        loginScreen = new Intent(this, MyActivity.class);
        username = credentials.getString("username", "not set");
        password = credentials.getString("password", "not set");
        friends = new FriendList(credentials.getString("friends",username+",Max"), credentials);

        handler = new Handler(){
            public void handleMessage(android.os.Message msg){
                Bundle bundle = msg.getData();
                String string = bundle.getString("0");
            }
        };

        //get notificationmanager context
        NotificationManager mNM = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        Bundle extras = getIntent().getExtras();
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
                    Communicator comm = new Communicator(message, handler);
                    comm.execute();
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), "Sent Hej to: " + msgFrom, Toast.LENGTH_SHORT);
                        mToast.show();

                    } else {
                        mToast.setText("Sent Hej to: " + msgFrom);
                        mToast.show();
                    }
                }

            }
            startActivity(loginScreen);
        }

    }
    public void onResume(Bundle savedInstance){
        this.onCreate(savedInstance);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.respond_to_hej, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
