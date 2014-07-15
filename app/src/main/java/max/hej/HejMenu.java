package max.hej;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class HejMenu extends ListActivity
        implements AdapterView.OnItemClickListener {
    SharedPreferences credentials;
    String username;
    String password;
    Thread t;
    Handler handler;
    Handler handler2;
    Intent settings;
    SimpleCursorAdapter mAdapter;
    Cursor mCursor;
    ListView listview;
    String[] FRIEND;
    private max.hej.Message message;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hej_menu);

        listview = getListView();
        settings = new Intent(this, SettingsMenu.class);
        credentials = getSharedPreferences(MyActivity.PREFS_NAME, 0);
        username = credentials.getString("username", "not set");
        password = credentials.getString("password", "not set");
        checkForNewFriendFromNotification();
        FRIEND = MyActivity.friends.asArray();

        handler = new Handler(){
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                String string = bundle.getString("0");
            }
        };

        //mAdapter = new SimpleCursorAdapter();
        //mCursor = this.getContentResolver().query();

        listview.setAdapter(new CustomAdapter(getApplicationContext(), FRIEND));
        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return true;
            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listview,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    //System.out.println("Remove " + position);
                                    MyActivity.friends.removeFriend(position);
                                    refreshFriendList();
                                }
                            }
                        });
        listview.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listview.setOnScrollListener(touchListener.makeScrollListener());

        AppRater.app_launched(this);

    }
    public void onResume(){
        super.onResume();
        checkForNewFriendFromNotification();
        FRIEND = MyActivity.friends.asArray();
        listview.setAdapter(new CustomAdapter(getApplicationContext(), FRIEND));
        listview.setOnItemClickListener(this);

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        message = new max.hej.Message.Builder()
                .username(username)
                .password(password)
                .intent(max.hej.Message.SEND_HEJ)
                .target(FRIEND[position])
                .build();
        Communicator comm = new Communicator(message,handler);
        comm.execute();
        Toast.makeText(getApplicationContext(),"Sent Hej to: "+FRIEND[position],Toast.LENGTH_SHORT).show();
    }


    public void onSettingBtnClick(View view){
        startActivity(settings);


    }

    private void refreshFriendList(){
        FRIEND = MyActivity.friends.asArray();
        listview.setAdapter(new CustomAdapter(getApplicationContext(), FRIEND));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hej_manu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }


    private class CustomAdapter extends ArrayAdapter<String> {
        private int[] colors = {/*Color.rgb(255,153,0), Color.rgb(253,94,83), Color.rgb(196,98,16),*/
                Color.rgb(41, 103, 105), Color.rgb(149, 90, 54), Color.rgb(192, 54, 44),
                Color.rgb(191, 114, 154), Color.rgb(150, 191, 114), Color.rgb(145, 114, 191),
                Color.rgb(191, 126, 114), Color.rgb(127, 114, 191), Color.rgb(114, 175, 191),
                Color.rgb(140, 191, 114), Color.rgb(114, 185, 191), Color.rgb(114, 191, 143)};
        private Random random = new Random();
        String TAG = "HejApp";


        public CustomAdapter(Context context, String[] data) {
            super(context, R.layout.my_custom_layout, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_custom_layout, parent, false);
            }

            // Lookup view for data population
            TextView name = (TextView) convertView.findViewById(R.id.text1);
            // Populate the data into the template view using the data object
            name.setText(FRIEND[position]);
            //tvName.setText(user.name);
            //tvHome.setText(user.hometown);
            // Return the completed view to render on screen
            convertView.setBackgroundColor(colors[random.nextInt(colors.length)]);
            //convertView.setOnTouchListener(this);
            return convertView;
        }
    }
    private void checkForNewFriendFromNotification(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.containsKey("sender")){
                String msgFrom = extras.getString("sender");
                if(!msgFrom.equals("null")) {
                    MyActivity.friends.addIfNotFriend(msgFrom);//add the person to friends list if they are not a friend.
                }
            }

        }
    }

    /*public void checkForHejsResults(String string){
        String[] result = string.split(",");
        Context context = getApplicationContext();
        CharSequence text = result[0].equals("") ? "No Hej 4 u" : "Hej from " + result[0] + "!!" ;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void checkForHejs(View view){
        Communicator comm = new Communicator(Communicator.requestType.CHECKFORHEJS,username,password,"", handler);
        comm.execute();
        }



    public void sendHej(View view){
        Communicator comm = new Communicator(Communicator.requestType.SEND,username,password,"bob",handler2);
        comm.execute();
    }
*/
}
