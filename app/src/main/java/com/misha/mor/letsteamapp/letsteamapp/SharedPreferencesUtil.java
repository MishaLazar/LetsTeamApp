package com.misha.mor.letsteamapp.letsteamapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Misha on 12/11/2016.
 */

public class SharedPreferencesUtil {




    public static void saveInfoToSharedPreferences(SharedPreferences sharedPreferences
            , Context context,
            boolean isLogin,String sUsername,String sEmail,String uID){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.saveLogin), isLogin);
        editor.putString(context.getString(R.string.userName), sUsername);
        editor.putString(context.getString(R.string.userEmail),sEmail);
        editor.putString(context.getString(R.string.userID),uID);
        editor.commit();
    }
    public static void saveInfoToSharedPreferences(SharedPreferences sharedPreferences
            , Context context
            ,boolean isLogin,String sUsername,String sEmail,String sPassword,String uID){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.saveLogin), isLogin);
        editor.putString(context.getString(R.string.userName), sUsername);
        editor.putString(context.getString(R.string.userEmail),sEmail);
        editor.putString(context.getString(R.string.userPassword),sPassword);
        editor.putString(context.getString(R.string.userID),uID);
        editor.commit();
    }

    public static void saveInfoToSharedPreferences(SharedPreferences sharedPreferences
            , Context context
            ,boolean isLogin,String sUsername,String sEmail,String uID,String sPassword,String mCurrentPhotoPath){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.saveLogin), isLogin);
        editor.putString(context.getString(R.string.userName), sUsername);
        editor.putString(context.getString(R.string.userEmail),sEmail);
        editor.putString(context.getString(R.string.userPassword),sPassword);
        editor.putString(context.getString(R.string.userID),uID);
        editor.putString(context.getString(R.string.userProfilePicPath),mCurrentPhotoPath);
        editor.commit();
        }
    public static String getLogin(SharedPreferences sharedPreferences
            , Context context){
        String login;
        login = sharedPreferences.getString(context.getString(R.string.saveLogin),"");
        return login;
    }
    public static String getUserName(SharedPreferences sharedPreferences
            , Context context){
        String login;
        login = sharedPreferences.getString(context.getString(R.string.userName),"");
        return login;
    }
    public static String getUserPassword(SharedPreferences sharedPreferences
            , Context context){
        String login;
        login = sharedPreferences.getString(context.getString(R.string.userPassword),"");
        return login;
    }
    public static String getUserEmail(SharedPreferences sharedPreferences
            , Context context){
        String login;
        login = sharedPreferences.getString(context.getString(R.string.userEmail),"");
        return login;
    }
    public static String getUserID(SharedPreferences sharedPreferences
            , Context context){
        String login;
        login = sharedPreferences.getString(context.getString(R.string.userID),"");
        return login;
    }
    public static String getUserProfilePicPath(SharedPreferences sharedPreferences
            , Context context){
        String login;
        login = sharedPreferences.getString(context.getString(R.string.userProfilePicPath),"");
        return login;
    }


}
