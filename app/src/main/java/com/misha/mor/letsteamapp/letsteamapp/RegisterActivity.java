package com.misha.mor.letsteamapp.letsteamapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.UserProfileChangeRequest;


public class RegisterActivity extends AppCompatActivity {

    Intent intent;
    EditText etxtUser;
    EditText etxtPass;
    EditText etxtEmail;
    Button btnSignUp;

    //vars
    String sUsername;
    String sPassword;
    String sUserEmail;
    String uid;
    User newUser;

    //db
    FireBaseDAL fdb; //DAL

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MyApp.setLocaleEn(RegisterActivity.this);
        setContentView(R.layout.activity_register);

        initViews();

        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //initialize data DAL
        fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setContext(RegisterActivity.this);
        mAuth = FirebaseAuth.getInstance();


        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        if(btnSignUp != null){
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sUsername = etxtUser.getText().toString();
                    sUserEmail = etxtEmail.getText().toString();
                    sPassword = etxtPass.getText().toString();

                    if(isUserRegistrationValid()){
                        mAuth.createUserWithEmailAndPassword(sUserEmail,sPassword )
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d("Test", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                        if (!task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, R.string.auth_failed,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            accessFireUserinfoAndSetUserInfo();
                                            intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                        // ...
                                    }
                                });

                    }
                    /*
                    fdb.registerUser(createUser());
                    intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);*/
                }
            });
        }
    }

    public void initViews(){
        etxtUser = (EditText)findViewById(R.id.etxtUserName);

        etxtPass = (EditText)findViewById(R.id.etxtPassword);

        etxtEmail = (EditText)findViewById(R.id.etxtEmail);


        // use method to save the user


    }

    public Boolean isUserRegistrationValid(){
        Validator registerValidatorr = new Validator();

        if(registerValidatorr.isValidName(sUsername) ) {
            if(registerValidatorr.isValidEmail(sUserEmail)){
                if(registerValidatorr.isValidPassword(sPassword)){
                    return true;
                }
                else{
                    Toast.makeText(RegisterActivity.this,R.string.error_incorrect_password,
                            Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(RegisterActivity.this,R.string.error_invalid_email,
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(RegisterActivity.this,R.string.error_field_required,
                    Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public void storeUserInfo(){
        /*String sUsername = etxtUser.getText().toString();
        String sUserUniqueID = etxtPass.getText().toString();
        String sUserEmail = etxtEmail.getText().toString();*/

        newUser = new User(sUsername,uid,sPassword, sUserEmail);
        if(newUser != null){
            fdb.registerUser(newUser);
        }


    }

    public void accessFireUserinfoAndSetUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            updateUserBasicProfile();
            sUsername = user.getDisplayName();
            sUserEmail = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();
            uid = user.getUid();
            storeUserInfo();
            saveInfoToSharedPreferences();
        }
    }

    public void saveInfoToSharedPreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.saveLogin), true);
        editor.putString(getString(R.string.userName), newUser.getsUsername());
        editor.putString(getString(R.string.userEmail),newUser.getsEmail());
        editor.putString(getString(R.string.userPassword),newUser.getsPassword());
        editor.putString(getString(R.string.userID),uid);
        editor.commit();
    }

    private void updateUserBasicProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(sUsername)
                /*.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))*/
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Test", "User profile updated.");
                        }
                    }
                });
    }
}