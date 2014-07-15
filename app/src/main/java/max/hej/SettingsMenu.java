package max.hej;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import max.hej.HejMenu.java.R;

public class SettingsMenu extends Activity {
    Handler handler;
    SharedPreferences credentials;
    String username;
    String password;
    String friendsName = "";
    private max.hej.Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);
        credentials = getSharedPreferences(MyActivity.PREFS_NAME, 0);
        username = credentials.getString("username", "not set");
        password = credentials.getString("password", "not set");
        ((TextView)findViewById(R.id.textView2)).setText("Hej, " + username);

        handler = new Handler(){
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                String string = bundle.getString("0");
                findFriendResults(string);
            }
        };
        //set username string

        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO){
                    tryAddFriend();
                    handled = true;
                }
                return handled;
            }
        });

    }

    public void tryAddFriend(){
        //check if friend exists. If yes, add to friends list (shared prefs), otherwise give an invalid username toast
        this.friendsName = ((EditText)findViewById(R.id.editText)).getText().toString().toUpperCase();
        if (!this.friendsName.equals("")) {
            message = new max.hej.Message.Builder()
                    .username(username)
                    .password(password)
                    .target(friendsName)
                    .intent(max.hej.Message.CHECK_FOR_USERNAME)
                    .build();
            Communicator comm = new Communicator(message, handler);
            comm.execute();
        }
    }

    public void inviteFriendsBtnClicked(View view){
        Intent inviteIntent = new Intent();
        inviteIntent.setAction(Intent.ACTION_SEND);
        inviteIntent.putExtra(Intent.EXTRA_TEXT, "Add me on Hej at: " + username + "\n Get Hej at goo.gl/iHJu4r");
        inviteIntent.setType("text/plain");
        startActivity(Intent.createChooser(inviteIntent, "Choose how to share Hej"));
    }

    public void findFriendResults(String string){
        String[] result = string.split(",");
        if(result[0].equals(Communicator.SUCCESS)){
            MyActivity.friends.addFriend(this.friendsName);
            Context context = getApplicationContext();
            CharSequence text = "Added: " + this.friendsName ;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {
            Context context = getApplicationContext();
            CharSequence text = "Friend not found" ;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

      /*public void logoutBtnClicked(View view){
        //delete shareprefs, return to first activity
        credentials.edit().putString("username", "not set").commit();
        credentials.edit().putString("password", "not set").commit();
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }
*/
}
