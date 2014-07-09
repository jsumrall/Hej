package max.hej;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
//import max.hej.MyActivity.R;

public class CreateAccount extends Activity {
    Handler handler;
    public static final String PROPERTY_REG_ID = "registration_id";
    String regid;
    Context context;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "HejApp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        handler = new Handler(){
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                String string = bundle.getString("0");
                accountCreationResults(string);
            }
        };
        context = getApplicationContext();
        regid = getRegistrationId(context);

    }


    public void accountCreationResults(String string){
        String[] results = string.split(",");
        if(results[0].equals("created")){
            //System.out.println("Created Successfully");

            SharedPreferences credentials = getSharedPreferences(MyActivity.PREFS_NAME, 0);
            credentials.edit().putString("username" , results[1]).commit();
            credentials.edit().putString("password", results[2]).commit();

            Intent intent = new Intent(this, HejMenu.class);
            startActivity(intent);
            finish();
        }
        else if(results[0].equals("unavailable")){
            //System.out.println("Username not available");
            Context context = getApplicationContext();
            CharSequence text = "Username is Taken";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if(results[0].equals("network error")){
            //System.out.println("Network Error");
            Context context = getApplicationContext();
            CharSequence text = "Network Error";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else{
            System.out.println("Something went wrong");
            Context context = getApplicationContext();
            CharSequence text = "Network Error" + results[0];
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


    public void tryToCreateAccount(View view){
        String name = ((EditText)findViewById(R.id.editText2)).getText().toString().toUpperCase();
        String password = ((EditText)findViewById(R.id.editText)).getText().toString();
        //System.out.println(name + ", " + password);
        Communicator comm = new Communicator(Communicator.requestType.NEWUSER,name, password, regid, handler);
        comm.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_account, menu);
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



    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MyActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}
