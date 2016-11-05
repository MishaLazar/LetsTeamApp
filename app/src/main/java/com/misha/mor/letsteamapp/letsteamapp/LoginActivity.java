package com.misha.mor.letsteamapp.letsteamapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity {

    private Intent intent;

    //FireBaseDAL fdb;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    //InnerReceiver innerReceiver;
    Intent receiver; // broadcast receiver

    User user = null;
    SharedPreferences sharedPreferences;

    EditText etxtUser;
    String sUserEmail;
    EditText etxtPass;
    String sUsername;
    String sPassword;
    String sEmail;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.setLocaleEn(LoginActivity.this);
        setContentView(R.layout.activity_login);

        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        checkLoginAfterRegster();
        mAuth = FirebaseAuth.getInstance();

       /* //register innerReceiver for Broadcast
        innerReceiver = new InnerReceiver(LoginActivity.this);
        receiver = registerReceiver(innerReceiver, new IntentFilter(getString(R.string.BROADCAST_ACTION_LOGIN)));
*/
        //get\create singleton db reference
       /* fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setContext(LoginActivity.this);*/
    }

    private void checkLoginAfterRegster() {
        sUserEmail = sharedPreferences.getString(getString(R.string.userEmail), "") ;
        if(sUserEmail != null && sUserEmail.length() >0){
            etxtUser.setText(sUserEmail);
        }
    }

    private void initViews(){
        etxtUser = (EditText)findViewById(R.id.etxtUserEmail);
        etxtPass = (EditText)findViewById(R.id.etxtPassword);
    }

    public void btnLoginClick(View v){

        sUserEmail = etxtUser.getText().toString();
        sPassword = etxtPass.getText().toString();


        if((sUserEmail !=null && sUserEmail.length()>0) && sPassword!=null){

            signInWithEmailAndPassword();


        }

        /*if(true) { // use a method for authenticate
           *//* SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.edit().putBoolean("saveLogin", true);
            sharedPreferences.edit().putString("username",sUserEmail);
            sharedPreferences.edit().putString("password",sUserUniqueID);


            sharedPreferences.edit().putString("userID","-KT0-9Tdv88UHOQ3h1fF");
*//*

        }*/
    }
    /*class InnerReceiver extends BroadcastReceiver {

        Context context;

        public InnerReceiver() {

        }

        public InnerReceiver(Context context) {
            this.context = context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            //code to do after validation
            Log.e("innerReceiver", "MyReceiver: broadcast received");

        }
    }*/
    //track whenever the user signs in or out
    public void userStateListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TEST", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TEST", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
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
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            saveInfoToSharedPreferences();
                            intent = new Intent(LoginActivity.this, EventsMenuActivity.class);
                            intent.putExtra("userID",sharedPreferences.getString("userID", ""));
                            startActivity(intent);
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
        sharedPreferences.edit().putBoolean(getString(R.string.saveLogin), true);
        sharedPreferences.edit().putString(getString(R.string.userName), sUsername);
        sharedPreferences.edit().putString(getString(R.string.userEmail),sEmail);
        sharedPreferences.edit().putString(getString(R.string.userID),uid);
    }

}