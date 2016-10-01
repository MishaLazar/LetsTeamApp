package com.misha.mor.letsteamapp.letsteamapp;

/**
 * Created by Misha on 9/10/2016.
 */

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatRoomUser implements ValueEventListener {


    String user_name = "";
    String user_password;
    String user_creatDate = "";
    String user_deleteDate = "";
    String user_diplayName = "";
    String user_email = "";
    String user_ID;
    ArrayList<String> user_FavoritesRooms = new ArrayList<String>(){{add("DefaultValue");}};

    Date date ;

    public  ChatRoomUser(){

    }

    public ChatRoomUser(String user_name, String user_password, String user_diplayName, String user_email) {


        this.user_name = user_name;
        this.user_password = user_password;
        this.user_creatDate = UtilMethods.getTimeStamp();
        this.user_diplayName = user_diplayName;
        this.user_email = user_email;
    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_creatDate() {
        return user_creatDate;
    }

    public void setUser_creatDate(String user_creatDate) {
        this.user_creatDate = user_creatDate;
    }

    public String getUser_deleteDate() {
        return user_deleteDate;
    }

    public void setUser_deleteDate(String user_deleteDate) {
        this.user_deleteDate = user_deleteDate;
    }

    public String getUser_diplayName() {
        return user_diplayName;
    }

    public void setUser_deleDate(String user_deleteDate) {
        this.user_deleteDate = user_deleteDate;
    }

    public String getUser_ID() {
        return user_ID;
    }
    public void setUser_ID(String user_id) {
        this.user_ID = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public ArrayList<String> getUser_FavoritesRooms() {
        return user_FavoritesRooms;
    }

    public void setUser_FavoritesRooms(ArrayList<String> user_FavoritesRooms) {
        this.user_FavoritesRooms = user_FavoritesRooms;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}