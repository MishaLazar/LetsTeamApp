package com.misha.mor.letsteamapp.letsteamapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LandingActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_landing);

        dbHandler = FireBaseDBHandler.getFireBaseDBHandlerInstance(LandingActivity.this);
        fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setFdbHandler(dbHandler);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
        sUserEmail = sharedPreferences.getString(getString(R.string.userEmail), "") ;
        sPassword = sharedPreferences.getString(getString(R.string.userPassword), "") ;



        if((sUserEmail != null && sUserEmail.length()>0)&& sPassword !=null) { // use a method for authenticate or maybe savelogin == true?
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