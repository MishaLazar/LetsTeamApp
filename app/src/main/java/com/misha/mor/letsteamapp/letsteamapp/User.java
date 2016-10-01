package com.misha.mor.letsteamapp.letsteamapp;

/**
 * Created by mor17_000 on 9/30/2016.
 */
public class User {
    String sUsername;
    String sPassword;
    String sEmail;

    public User(String sUsername, String sPassword, String sEmail) {
        this.sUsername = sUsername;
        this.sPassword = sPassword;
        this.sEmail = sEmail;
    }

    public String getsUsername() {
        return sUsername;
    }

    public void setsUsername(String sUsername) {
        this.sUsername = sUsername;
    }

    public String getsPassword() {
        return sPassword;
    }

    public void setsPassword(String sPassword) {
        this.sPassword = sPassword;
    }

    public String getsEmail() {
        return sEmail;
    }

    public void setsEmail(String sEmail) {
        this.sEmail = sEmail;
    }
}