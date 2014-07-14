package max.hej;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by max on 7/14/14.
 */
public class FriendList {
    SharedPreferences credentials;

    LinkedList<String> friends;

    public FriendList(String friendsCSV, SharedPreferences credentials){
        this.credentials = credentials;

        friends = new LinkedList<String>();
        for(String friend : friendsCSV.split(",")){
            if(!friend.equals("")){
                friends.add(friend);
            }
            else{
                String username = credentials.getString("username", "");
                if(!username.equals("")) {
                    friends.add(username);
                }
            }
        }
    }

    public String[] asArray(){
        return friends.toArray(new String[friends.size()]);
    }

    public void addFriend(String newFriend){
        friends.add(newFriend);
        saveFriendsToPreferences();
    }

    public void removeFriend(int position){
        friends.remove(position);
        saveFriendsToPreferences();
    }

    private void saveFriendsToPreferences() {
        String csv = "";
        for (String friend : friends) {
            if (csv.equals("")) { csv += friend;}
            else { csv += "," + friend; }
            this.credentials.edit().putString("friends", csv).commit();
        }
    }
    public void addIfNotFriend(String newFriend){
        if(!friends.contains(newFriend)){
            System.out.println("new friend: " + newFriend);
            addFriend(newFriend);
        }
    }
    public boolean isAFriend(String person){
        return friends.contains(person);
    }

}
