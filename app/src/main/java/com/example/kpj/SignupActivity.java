package com.example.kpj;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.model.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.File;

public class SignupActivity extends AppCompatActivity {

    public EditText etNewEmail;
    public EditText etNewUsername;
    public EditText etNewPassword;
    public Button btnNewSignup;
    public TextView tvTakeProfilePic;
    public ImageView ivNewProfilePic;

    public final String APP_TAG = "Signin";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    public File photoFile;

    private String email;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeViews();
        setSignUpButtonListener();
        setCameraListener();
    }

    public void initializeViews() {
        etNewEmail = findViewById(R.id.etNewEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewUsername = findViewById(R.id.etNewUser);
        btnNewSignup = findViewById(R.id.btnNewSignup);
        tvTakeProfilePic = findViewById(R.id.tvTakeProfilePic);
        ivNewProfilePic = findViewById(R.id.ivNewProfilePic);
    }

    public void getStringsFromEditTexts() {
        email = etNewEmail.getText().toString();
        username = etNewUsername.getText().toString();
        password = etNewPassword.getText().toString();
    }

    private void setSignUpButtonListener() {
        btnNewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStringsFromEditTexts();
                signUpUser(email, username, password);
            }
        });
    }

    public void setCameraListener() {
        tvTakeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });
    }

    public void signUpUser(String email, String username, String password) {
        ParseUser user = new ParseUser();
        // send data to parse for new user
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    saveNewProfileAssetsToParse();
                } else {
                    Log.e("SignUpActivity", "Login Failed");
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveNewProfileAssetsToParse() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseFile parseFile = new ParseFile(photoFile);
        user.saveInBackground();
        if (parseFile != null) {
            //send profile photo to parse
            user.put(User.KEY_PROFILE, parseFile);
            //save in background thread
            user.saveInBackground();
        }

        Toast.makeText(SignupActivity.this, "Account created", Toast.LENGTH_LONG).show();
        goToMainActivity();
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);
        // wrap File object into a content provider
        // required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(SignupActivity.this,
                "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Glide.with(SignupActivity.this)
                        .load(photoFile.getAbsoluteFile())
                        //crop photo to size of thing
                        .apply(new RequestOptions().override(200, 200).centerCrop())
                        .into(ivNewProfilePic);
                Toast.makeText(SignupActivity.this, "Profile Picture Set",
                        Toast.LENGTH_LONG).show();
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //send user to home activity
    public void goToMainActivity() {
        Intent in = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(in);
        finish();
    }
}
