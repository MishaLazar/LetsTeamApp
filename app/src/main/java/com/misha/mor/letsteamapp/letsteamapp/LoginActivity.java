package com.misha.mor.letsteamapp.letsteamapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void btnLoginClick(View v){
        EditText etxtUser = (EditText)findViewById(R.id.etxtUsername);
        String sUsername = etxtUser.getText().toString();
        EditText etxtPass = (EditText)findViewById(R.id.etxtPassword);
        String sPassword = etxtPass.getText().toString();


        if(true) { // use a method for authenticate
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.edit().putBoolean("saveLogin", true);
            sharedPreferences.edit().putString("username",sUsername);
            sharedPreferences.edit().putString("password",sPassword);

            intent = new Intent(this, EventsMenuActivity.class);
            startActivity(intent);
        }
    }
}