package com.misha.mor.letsteamapp.letsteamapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LandingActivity extends Activity {

    private Intent intent;
    //db initialize
    FireBaseDBHandler dbHandler;
    FireBaseDAL fdb;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    SharedPreferences sharedPreferences;
    String sUsername;
    String sEmail;
    String uid;
    String sUserEmail;
    String sPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MyApp.onCreate(this, "en");
        setContentView(R.layout.activity_landing);
        dbHandler = FireBaseDBHandler.getFireBaseDBHandlerInstance(LandingActivity.this);
        fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setFdbHandler(dbHandler);
        mAuth = FirebaseAuth.getInstance();
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        intViews();

        localeEnforce();

        checkIfAlreadyLoggedIn();
    }

    public void intViews(){

        /*Button btnLanguage = (Button)findViewById(R.id.btnLanguage);
        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en");
            }
        });*/
        Button btnLogin = (Button)findViewById(R.id.btnSignUp);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LandingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        Button btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LandingActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
    public void localeEnforce(){
        Configuration config = new Configuration();
        config.locale = Locale.ENGLISH;
        super.onConfigurationChanged(config);
        Locale.setDefault(config.locale);
        getBaseContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }


    public void checkIfAlreadyLoggedIn(){

        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        sUserEmail = sharedPreferences.getString(getString(R.string.userEmail), "") ;
        sPassword = sharedPreferences.getString(getString(R.string.userPassword), "") ;



        if((sUserEmail != null && sUserEmail.length()>0)&& (sPassword !=null&& sPassword.length()>0)) { // use a method for authenticate or maybe savelogin == true?
            signInWithEmailAndPassword();
            /*intent = new Intent(this, LoginActivity.class);
            startActivity(intent);*/
        }
    }

    public void signInWithEmailAndPassword (){
        mAuth.signInWithEmailAndPassword(sUserEmail, sPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Test", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Test", "signInWithEmail:failed", task.getException());
                            Toast.makeText(LandingActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            saveInfoToSharedPreferences();
                            intent = new Intent(LandingActivity.this, EventsMenuActivity.class);
                            intent.putExtra("userID",sharedPreferences.getString("userID", ""));
                            intent.putExtra("userName",sharedPreferences.getString(getString(R.string.userName), ""));
                            startActivity(intent);
                            finish();
                        }

                    }
                });
    }
    public void accessFireUserinfoAndSetUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            sUsername = user.getDisplayName();
            sEmail = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();
            uid = user.getUid();

        }
    }
    public void saveInfoToSharedPreferences(){
        accessFireUserinfoAndSetUserInfo();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.saveLogin), true);
        editor.putString(getString(R.string.userName), sUsername);
        editor.putString(getString(R.string.userEmail),sEmail);
        editor.putString(getString(R.string.userID),uid);
        editor.commit();
    }

}