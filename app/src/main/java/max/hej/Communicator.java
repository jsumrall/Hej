package max.hej;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class Communicator extends AsyncTask<Void, Void, String> {
    protected String URL = "cheapdrink.nl";
    protected int port = 9000;

    protected SSLSocketFactory socketFactory;
    protected SSLSocket socket;
    protected InputStream IN;
    protected BufferedReader BR;
    protected OutputStream OUT;
    public enum requestType {NEWUSER, CHECKFORHEJS, SEND, VALIDATE, FINDUSERNAME,UPDATEGCMID}
    public requestType request;
    protected String username;
    protected String password;
    protected String target;
    protected Handler handler;
    protected PrintWriter PW;

    public Communicator(requestType request, String username, String password, String target, Handler handler){
        this.request = request;
        this.username = username;
        this.password = password;
        this.target = target;
        this.handler = handler;
        this.socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

    }

    public String doInBackground(Void...params){
        switch (this.request) {
            case NEWUSER:
                return createAccount(this.username, this.password, this.target);
            case CHECKFORHEJS:
                return checkForHejs(this.username, this.password);
            case VALIDATE:
                return validateUsername(this.username, this.password, this.target);
            case FINDUSERNAME:
                return checkForUsername(this.target);
            case SEND:
                sendHej(this.username, this.password, this.target);
                break;
            case UPDATEGCMID:
                updateGCMID(this.username, this.password, this.target);
                break;

        }
        try {
            this.socket.close();
        }
        catch(Exception e){e.printStackTrace();}

        return "";

    }

    @Override
    protected void onPostExecute(String results) {
                Message msg = this.handler.obtainMessage();
                Bundle bundle = new Bundle();
                results += "," + this.username + "," + this.password;
                bundle.putString("0",results);
                msg.setData(bundle);
                handler.sendMessage(msg);
    }


    private void connectToServer(){
        try{
            //System.out.println("Trying to connect to Hej server");
            InetAddress serverAddr = InetAddress.getByName(URL);
            this.socket = (SSLSocket) socketFactory.createSocket(serverAddr.getHostAddress(),port);
            final String[] enabledCipherSuites = this.socket.getSupportedCipherSuites();// { "SSL_DH_anon_WITH_RC4_128_MD5" };
            this.socket.setEnabledCipherSuites(enabledCipherSuites);

            //this.socket = new Socket(serverAddr,port);
            this.IN = socket.getInputStream();
            this.BR = new BufferedReader(new InputStreamReader(IN));
            this.OUT = socket.getOutputStream();
            PW = new PrintWriter(socket.getOutputStream(),true);
        }
        catch(Exception e){e.printStackTrace();}
    }

    public String createAccount(String username, String password, String regid){
        if(this.validateUsername(username,password,regid).equals("valid")){
            return "created";
        }
        this.connectToServer();
        try {
            //System.out.println(regid);
            //System.out.println("Sending Write");
            PW.write("addNewUser," + username + "," + password + "," + regid + "\n");
            PW.flush();
            //System.out.println("Waiting for response");
            String response = BR.readLine();
            if(response.equals("New User added: " + username.toUpperCase())){
                //success
                return "created";
            }
            else if(response.equals("Username not available: " + username.toUpperCase())){
                return "unavailable";

            }
            else{
                System.out.println("Received Unknown message " + response);
                return "network error";
            }

        }
        catch(Exception e){e.printStackTrace();}
        return "error";
    }


    public void updateGCMID(String username, String password, String regid){
        connectToServer();
        try{
            PW.write("updateregid," + username + "," + password + "," + regid + "\n");
            PW.flush();
        }
        catch(Exception e){e.printStackTrace();}
    }
    public String validateUsername(String username, String password, String target){
        connectToServer();

        try{
            PW.write("validateUsername," + username + "," + password + "," + target + "\n");
            PW.flush();
            String response = BR.readLine();
            return response; //expecting string "valid" or "invalid"
        }
        catch(Exception e){e.printStackTrace();}
        return "";//nothing or a problem with connection.
    }

    public String checkForUsername(String target){
        connectToServer();
        try{
            PW.write("checkForUsername," + username + "," + password + "," + target + "\n");
            PW.flush();
            String response = BR.readLine();
            return response; //expecting string "valid" or "invalid"
        }
        catch(Exception e){e.printStackTrace();}
        return "";//nothing or a problem with connection.
    }


    public String checkForHejs(String username, String password){
        connectToServer();

        try{
            PW.write("checkForHejs," + username + "," + password + "\n");
            PW.flush();
            String response = BR.readLine();
            return response;
        }
        catch(Exception e){e.printStackTrace();}
        return "";//nothing or a problem with connection.
    }

    public void sendHej(String username, String password, String target){
        connectToServer();
        try{
            PW.write("sendHej," + username + "," + password + "," + target + "\n");
            PW.flush();
        }
        catch(Exception e){e.printStackTrace();}
    }
}
