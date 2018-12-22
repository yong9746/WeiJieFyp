package com.jby.hanwei.sharePreference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wypan on 2/24/2017.
 */

public class SharedPreferenceManager {



    private static String UserID = "uid";
    private static String IpAddress = "ipAddress";
    private static String UserName = "uName";
    private static String IpDialog = "ipDialog";

    private static SharedPreferences getSharedPreferences(Context context) {
        String SharedPreferenceFileName = "RideDriverPreference";
        return context.getSharedPreferences(SharedPreferenceFileName, Context.MODE_PRIVATE);
    }

    public static void clear(Context context){
        getSharedPreferences(context).edit().clear().apply();
    }

    public static String getUserID(Context context) {
        return getSharedPreferences(context).getString(UserID, "default");
    }

    public static void setUserID(Context context, String userID) {
        getSharedPreferences(context).edit().putString(UserID, userID).apply();
    }

    public static String getIpAddress(Context context) {
        return getSharedPreferences(context).getString(IpAddress, "default");
    }

    public static void setIpAddress(Context context, String ipAddress) {
        getSharedPreferences(context).edit().putString(IpAddress, ipAddress).apply();
    }

    public static String getUserName(Context context) {
        return getSharedPreferences(context).getString(UserName, "default");
    }

    public static void setUserName(Context context, String userName) {
        getSharedPreferences(context).edit().putString(UserName, userName).apply();
    }

    public static String getIpDialog(Context context) {
        return getSharedPreferences(context).getString(IpDialog, "show");
    }

    public static void setIpDialog(Context context, String ipDialog) {
        getSharedPreferences(context).edit().putString(IpDialog, ipDialog).apply();
    }

}
