package com.misha.mor.letsteamapp.letsteamapp;

/**
 * Created by mor17_000 on 9/30/2016.
 */
public class User {
    String sUsername;
    String sPassword;
    String sUserUniqueID;
    String sEmail;
    String bitMapUserImage;
    String user_ID;
    String user_creatDate = "";
    String user_deleteDate = "";


    public User(String sUsername, String sUserUniqueID ,String sPassword, String sEmail) {
        this.sUsername = sUsername;
        this.sUserUniqueID = sUserUniqueID;
        this.sPassword = sPassword;
        this.sEmail = sEmail;
        this.user_creatDate = UtilMethods.getDateTimeSimple();
    }

    public String getsPassword() {
        return sPassword;
    }

    public void setsPassword(String sPassword) {
        this.sPassword = sPassword;
    }

    public String getsUsername() {
        return sUsername;
    }

    public void setsUsername(String sUsername) {
        this.sUsername = sUsername;
    }

    public String getsUserUniqueID() {
        return sUserUniqueID;
    }

    public void setsUserUniqueID(String sUserUniqueID) {
        this.sUserUniqueID = sUserUniqueID;
    }

    public String getsEmail() {
        return sEmail;
    }

    public void setsEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
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

    public String getBitMapUserImage() {
        return bitMapUserImage;
    }

    public void setBitMapUserImage(String bitMapUserImage) {
        this.bitMapUserImage = bitMapUserImage;
    }
}