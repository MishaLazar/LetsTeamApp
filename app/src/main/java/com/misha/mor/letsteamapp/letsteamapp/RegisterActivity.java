package com.misha.mor.letsteamapp.letsteamapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void btnSignUpClick(View v){
        EditText etxtUser = (EditText)findViewById(R.id.etxtUsername);
        String sUsername = etxtUser.getText().toString();
        EditText etxtPass = (EditText)findViewById(R.id.etxtPassword);
        String sPassword = etxtPass.getText().toString();
        EditText etxtEmail = (EditText)findViewById(R.id.etxtEmail);
        String sEmail = etxtPass.getText().toString();

        // use method to save the user

        intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}