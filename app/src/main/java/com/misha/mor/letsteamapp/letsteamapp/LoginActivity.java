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
import android.widget.Button;
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

    Button btnlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MyApp.onCreate(this, "en");
        setContentView(R.layout.activity_login);

        initViews();
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        checkLoginAfterRegster();
        mAuth = FirebaseAuth.getInstance();

       /* //register innerReceiver for Broadcast
        innerReceiver = new InnerReceiver(LoginActivity.this);
        receiver = registerReceiver(innerReceiver, new IntentFilter(getString(R.string.BROADCAST_ACTION_LOGIN)));
*/
        //get\create singleton db reference
       /* fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setContext(LoginActivity.this);*/
        //updateViews();
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
        btnlogin = (Button)findViewById(R.id.btnSignUp);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sUserEmail = etxtUser.getText().toString();
                sPassword = etxtPass.getText().toString();


                if((sUserEmail !=null && sUserEmail.length()>0) && sPassword!=null){

                    signInWithEmailAndPassword();


                }
            }
        });

    }

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
                            finish();
                        }

                    }
                });
    }

    public void accessFireUserinfoAndSetUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            /*sUsername = user.getDisplayName();*/
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
        editor.putString(getString(R.string.userPassword),sPassword);
        editor.putString(getString(R.string.userID),uid);
        editor.commit();
    }

    private void updateViews() {
        recreate();
    }

}