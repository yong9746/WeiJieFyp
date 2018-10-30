package dos.suc.workshopday3.preference;

import android.content.Context;
import android.content.SharedPreferences;



public class LoadPreferences {
    String userID;
    public SharedPreferences userPret;

    public LoadPreferences(Context context){
        userPret = context.getSharedPreferences("profile",0);
        userID = userPret.getString("userID",null);
    }

    public String getUserID(){

        return userID;
    }
}
