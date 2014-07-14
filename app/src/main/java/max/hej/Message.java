package max.hej;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by max on 7/14/14.
 */
public class Message {
    public static final String NEW_ACCOUNT = "addNewUser";
    public static final String UPDATE_REGID = "updateregid";
    public static final String VALIDATE_USER_NAME = "validateUsername";
    public static final String CHECK_FOR_USERNAME = "checkForUsername";
    public static final String SEND_HEJ = "sendHej";
    private final String username;
    private final String password;
    private final String target;
    private final String intent;
    private final String regid;



    public static final class Builder {
        private  String username = "";
        private  String password = "";
        private  String target = "";
        private  String intent = "";
        private  String regid = "";

        public Builder(){

        }

        public Builder username(String value){
            username = value;
            return this;
        }

        public Builder password(String value){
            password = value;
            return this;
        }
        public Builder target(String value){
            target = value;
            return this;
        }
        public Builder intent(String value){
            intent = value;
            return this;
        }
        public Builder regid(String value){
            regid = value;
            return this;
        }
        public Message build(){
            return new Message(this);
        }
    }

    public Message(Builder builder) {
        username = builder.username;
        password = builder.password;
        target = builder.target;
        intent = builder.intent;
        regid = builder.regid;
    }


    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public String getTarget(){
        return target;
    }
    public String getIntent(){
        return intent;
    }
    public String getRegid(){
        return regid;
    }

    public JSONObject asJSONObject(){
        try {
            JSONObject json = new JSONObject();
            json.put("username", getUsername());
            json.put("password",getPassword());
            json.put("target",getTarget());
            json.put("intent",getIntent());
            json.put("regid",getRegid());
            return json;
        }
        catch(JSONException e){e.printStackTrace();}
        return null;
    }
    public String asJSONString(){
        return asJSONObject().toString();
    }
}
