package com.misha.mor.letsteamapp.letsteamapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
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

    final int CAMERA_PHOTO = 111;
    final int MY_PERMISSION_WRITE_EXTERNAL = 90;
    final int MY_PERMISSION_LOCATION = 99;
    final int REQUEST_IMAGE_CAPTURE = 1;
    final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    Uri imageToUploadUri;

    Intent intent;
    EditText etxtUser;
    EditText etxtPass;
    EditText etxtEmail;
    Button btnSignUp;
    Button btnCamera;
    ImageView mImageView;


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
    FirebaseAuth.AuthStateListener mAuthListener;
    SharedPreferences sharedPreferences;
    Bitmap reducedSizeBitmap;
    File photoFile;
    Button btnSavePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MyApp.setLocaleEn(RegisterActivity.this);
        setContentView(R.layout.activity_register);

        initViews();
        checkPermission(MY_PERMISSION_LOCATION);

        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

        mImageView = (ImageView)findViewById(R.id.imageView);

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

        btnCamera = (Button)findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(MY_PERMISSION_WRITE_EXTERNAL)) {
                    dispatchTakePictureIntent();
                    //captureCameraImage();
                }
                setPic();
                /*galleryAddPic();*/

            }
        });

        btnSavePic = (Button)findViewById(R.id.btnSavePic);
        btnSavePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeImage(getBitmap(imageToUploadUri.getPath()));

            }
        });


    }

    private boolean checkPermission(int my_permission_code) {
        switch (my_permission_code) {
            case MY_PERMISSION_WRITE_EXTERNAL: {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    /*Log.i(TAG, "No write permission, requesting permission");*/
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_WRITE_EXTERNAL);
                    return false;
                }
            }

            case MY_PERMISSION_LOCATION: {
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                   /* Log.i(TAG, "No location permission, requesting permission");*/
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
        /*String sUsername = etxtUser.getText().toString();
        String sUserUniqueID = etxtPass.getText().toString();
        String sUserEmail = etxtEmail.getText().toString();*/

        newUser = new User(sUsername,uid,sPassword, sUserEmail);
        if(newUser != null){
            newUser.setBitMapUserImage(ImageConverter.BitMapToString(reducedSizeBitmap));
            fdb.registerUser(newUser);
        }


    }

    public void accessFireUserinfoAndSetUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            updateUserBasicProfile();
           /* sUsername = user.getDisplayName();*/
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
        editor.putString(getString(R.string.userProfilePicPath),mCurrentPhotoPath);
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
    private void captureCameraImage() {
        /*Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);*/
        /*File f = new File(Environment.getExternalStorageDirectory(), "POST_IMAGE.jpg");
        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        imageToUploadUri = Uri.fromFile(f);
        startActivityForResult(chooserIntent, CAMERA_PHOTO);*/
    }
    //receive the image after
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try{

                *//*Bundle extras = data.getExtras();*//*
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                *//*mImageView.setImageBitmap(imageBitmap);*//*
                mImageView.setImageBitmap(RoundedImageView.getCroppedBitmap(imageBitmap,40));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            if(photoURI != null){
                Uri selectedImage = photoURI;
                imageToUploadUri = Uri.fromFile(photoFile);
                getContentResolver().notifyChange(selectedImage, null);
                reducedSizeBitmap = getBitmap(imageToUploadUri.getPath());
                /*reducedSizeBitmap = ImageConverter.getResizedBitmap(getBitmap(imageToUploadUri.getPath()),
                        PX_HEIGHT,PX_WIDTH);*/
                if(reducedSizeBitmap != null){
                    mImageView.setImageBitmap(ImageConverter.getRoundedCornerBitmap(reducedSizeBitmap,60));
                   /* storeImage(getBitmap(imageToUploadUri.getPath()));*/
                    /*mImageView.setImageBitmap(RoundedImageView.getCroppedBitmap(reducedSizeBitmap,90));*/
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
        /*File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);*/
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
    //add image taken to gallery dir
   /* private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }*/

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

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

        mImageView.setImageBitmap(bitmap);
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