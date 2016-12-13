package com.misha.mor.letsteamapp.letsteamapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RegisterActivity extends AppCompatActivity {

    final String TAG = "RegisterActivity";

    final int MY_PERMISSION_WRITE_EXTERNAL = 90;
    final int MY_PERMISSION_LOCATION = 99;
    final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    Uri imageToUploadUri;

    Intent intent;
    EditText etxtUser;
    EditText etxtPass;
    EditText etxtEmail;
    Button btnSignUp;
    ImageButton btnImage;


    //vars
    String sUsername;
    String sPassword;
    String sUserEmail;
    String uid;
    User newUser;
    Uri photoURI;

    //db
    FireBaseDAL fdb; //DAL

    FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    Bitmap reducedSizeBitmap;
    File photoFile;
    Button btnSavePic;
    boolean imageStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        checkPermission(MY_PERMISSION_LOCATION);

        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //initialize data DAL
        fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setContext(RegisterActivity.this);
        mAuth = FirebaseAuth.getInstance();



    }

    public void initViews(){
        etxtUser = (EditText)findViewById(R.id.etxtUserName);

        etxtPass = (EditText)findViewById(R.id.etxtPassword);

        etxtEmail = (EditText)findViewById(R.id.etxtEmail);

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
                                            finish();
                                        }
                                        // ...
                                    }
                                });

                    }
                }
            });
        }

        btnImage = (ImageButton)findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(MY_PERMISSION_WRITE_EXTERNAL)) {
                    dispatchTakePictureIntent();
                }
                setPic();

            }
        });

    }

    private boolean checkPermission(int my_permission_code) {
        switch (my_permission_code) {
            case MY_PERMISSION_WRITE_EXTERNAL: {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_WRITE_EXTERNAL);
                    return false;
                }
            }

            case MY_PERMISSION_LOCATION: {
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSION_LOCATION);
                    return false;
                }
            }
        }
        return true;

    }

    public Boolean isUserRegistrationValid(){
        Validator registerValidatorr = new Validator();

        if(registerValidatorr.isValidName(sUsername) ) {
            if(registerValidatorr.isValidEmail(sUserEmail)){
                if(registerValidatorr.isValidPassword(sPassword)){
                    return true;
                }
                else{
                    Toast.makeText(RegisterActivity.this,R.string.error_invalid_password,
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
        newUser = new User(sUsername,uid,sPassword, sUserEmail);
        if(newUser != null && imageStatus){
            newUser.setBitMapUserImage(ImageConverter.BitMapToString(reducedSizeBitmap));

        }else {
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                                                             R.drawable.default_man);
            newUser.setBitMapUserImage(
                    ImageConverter.BitMapToString(
                            icon));
        }


        fdb.registerUser(newUser);


    }

    public void accessFireUserinfoAndSetUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            updateUserBasicProfile();
            sUserEmail = user.getEmail();
            uid = user.getUid();
            storeUserInfo();
            saveInfoToSharedPreferences();
        }
    }

    public void saveInfoToSharedPreferences(){
        /*SharedPreferences.Editor editor = sharedPreferences.edit();*/
        /*editor.putBoolean(getString(R.string.saveLogin), true);
        editor.putString(getString(R.string.userName), newUser.getsUsername());
        editor.putString(getString(R.string.userEmail),newUser.getsEmail());
        editor.putString(getString(R.string.userPassword),sPassword);
        editor.putString(getString(R.string.userID),uid);
        editor.putString(getString(R.string.userProfilePicPath),mCurrentPhotoPath);
        editor.commit();*/
        SharedPreferencesUtil.saveInfoToSharedPreferences(sharedPreferences,RegisterActivity.this,true
                ,sUsername,sUserEmail,uid,sPassword,mCurrentPhotoPath);
    }

    private void updateUserBasicProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(sUsername)
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

    //initiate the external camera app
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
               /* ...*/
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            if(photoURI != null){
                Uri selectedImage = photoURI;
                imageToUploadUri = Uri.fromFile(photoFile);
                getContentResolver().notifyChange(selectedImage, null);
                reducedSizeBitmap = getBitmap(imageToUploadUri.getPath());
                if(reducedSizeBitmap != null){
                    imageStatus = true;
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
                            ImageConverter.getRoundedCornerBitmap(reducedSizeBitmap,60));

                    btnImage.setBackground(bitmapDrawable);

                }else{
                    Toast.makeText(this,"Error while capturing Image",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"Error while capturing Image",Toast.LENGTH_LONG).show();
            }
        }
    }
    //create unique file name to store the image
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = btnImage.getWidth();
        int targetH = btnImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        btnImage.setImageBitmap(bitmap);
    }
    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }


    private void storeImage(Bitmap image) {
        File pictureFile = photoFile;
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }
}