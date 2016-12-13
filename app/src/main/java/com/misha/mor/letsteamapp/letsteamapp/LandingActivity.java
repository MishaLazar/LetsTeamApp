package com.misha.mor.letsteamapp.letsteamapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    SharedPreferences sharedPreferences;
    String sUsername;
    String sEmail;
    String uid;
    String sUserEmail;
    String sPassword;
    ProgressBar pBar;
    RelativeLayout mainLayout;
    Button btnRegister;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Toast.makeText(LandingActivity.this,
                R.string.profile_info_search,
                Toast.LENGTH_SHORT).show();
        // progress bar start
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.VISIBLE);
        pBar.bringToFront();


        mainLayout = (RelativeLayout) findViewById(R.id.landingLayout);

        dbHandler = FireBaseDBHandler.getFireBaseDBHandlerInstance(LandingActivity.this);
        fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setFdbHandler(dbHandler);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        /*sharedPreferences.edit().clear().commit();*/
        intViews();

        localeEnforce();

        checkIfAlreadyLoggedIn();
    }

    public void intViews(){
        btnLogin = (Button)findViewById(R.id.btnSignUp);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LandingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LandingActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        setButtonState(false);

    }
    @Override
    public void onResume(){
        localeEnforce();
        checkIfAlreadyLoggedIn();
    }


    public void localeEnforce(){
        Configuration config = new Configuration();
        Locale cLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            cLocale = getResources().getConfiguration().getLocales().get(0);
            cLocale = new Locale("en", "US");
            super.onConfigurationChanged(config);
            Locale.setDefault(cLocale);
        } else{
            //noinspection deprecation
            config.locale = Locale.ENGLISH;
            super.onConfigurationChanged(config);
            Locale.setDefault(config.locale);
        }
        LandingActivity.this.getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }


    public void checkIfAlreadyLoggedIn(){


        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        sUserEmail = SharedPreferencesUtil.getUserEmail(sharedPreferences,this); /*sharedPreferences.getString(getString(R.string.userEmail), "") ;*/
        sPassword = SharedPreferencesUtil.getUserPassword(sharedPreferences,this);/*sharedPreferences.getString(getString(R.string.userPassword), "") ;*/



        if((sUserEmail != null && sUserEmail.length()>0)&& (sPassword !=null&& sPassword.length()>0)) { // use a method for authenticate or maybe savelogin == true?
            signInWithEmailAndPassword();
        }else {
            //progress bar finish
            pBar.setVisibility(View.GONE);
            Toast.makeText(LandingActivity.this,
                    R.string.no_profile_info_found,
                    Toast.LENGTH_SHORT).show();
            setButtonState(true);
        }

    }

    public void signInWithEmailAndPassword (){
        /*Toast.makeText(LandingActivity.this, R.string.profile_info_found,
                Toast.LENGTH_SHORT).show();*/
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
                            setButtonState(true);
                        }
                        else{
                            saveInfoToSharedPreferences();
                            intent = new Intent(LandingActivity.this, EventsMenuActivity.class);
                            intent.putExtra("userID",sharedPreferences.getString("userID", ""));
                            intent.putExtra("userName",sharedPreferences.getString(getString(R.string.userName), ""));
                            startActivity(intent);
                            finish();
                        }

                        //progress bar finish
                        pBar.setVisibility(View.GONE);
                    }
                });
    }
    public void accessFireUserInfoAndSetUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            sUsername = user.getDisplayName();
            sEmail = user.getEmail();
            uid = user.getUid();

        }
    }
    public void saveInfoToSharedPreferences(){
        accessFireUserInfoAndSetUserInfo();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.saveLogin), true);
        editor.putString(getString(R.string.userName), sUsername);
        editor.putString(getString(R.string.userEmail),sEmail);
        editor.putString(getString(R.string.userID),uid);
        editor.commit();
    }

    public void setButtonState(boolean isUsable){
        btnLogin.setEnabled(isUsable);
        btnLogin.setEnabled(isUsable);
    }
}