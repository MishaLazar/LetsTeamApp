package com.misha.mor.letsteamapp.letsteamapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    Intent intent;
    EditText etxtUser;
    EditText etxtPass;
    EditText etxtEmail;
    Button btnSignUp;

    //db
    FireBaseDAL fdb; //DAL




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();


        //initialize data DAL
        fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setContext(RegisterActivity.this);



        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        if(btnSignUp != null){
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    fdb.registerUser(createUser());
                    intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void initViews(){
        etxtUser = (EditText)findViewById(R.id.etxtUsername);

        etxtPass = (EditText)findViewById(R.id.etxtPassword);

        etxtEmail = (EditText)findViewById(R.id.etxtEmail);


        // use method to save the user


    }

    public User createUser(){
        String sUsername = etxtUser.getText().toString();
        String sPassword = etxtPass.getText().toString();
        String sEmail = etxtEmail.getText().toString();

        User newUser = new User(sUsername,sPassword,sEmail);

        return newUser;
    }
}