package com.misha.mor.letsteamapp.letsteamapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class LandingActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        checkIfAlreadyLoggedIn();
    }

    public void btnLoginClick(View v){
        intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void btnRegisterClick(View v){
        intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void checkIfAlreadyLoggedIn(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sUserName = sharedPreferences.getString("username", "") ;
        String sPassword = sharedPreferences.getString("password", "") ;

        if(true) { // use a method for authenticate or maybe savelogin == true?
            intent = new Intent(this, EventsMenuActivity.class);
            startActivity(intent);
        }
    }
}