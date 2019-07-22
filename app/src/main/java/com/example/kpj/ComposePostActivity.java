package com.example.kpj;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kpj.model.Post;
import com.example.kpj.model.User;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class ComposePostActivity extends AppCompatActivity {

    EditText etComposePostTitle;
    EditText etComposeBody;

    TextView tvComposeTitleLabel;
    TextView tvComposeUsername;
    TextView tvComposeBodyLabel;

    ImageView ivComposeProfile;
    ImageView ivComposeImage;

    ImageButton ibAddPdf;
    ImageButton ibCamera;
    ImageButton ibExitCompose;
    ImageButton ibAddImage;

    Button bLaunch;



    private static final int GALLERY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_post);
        initializeViews();
    }

    private void initializeViews() {
        etComposePostTitle = findViewById(R.id.etComposePostTitle);
        etComposeBody = findViewById(R.id.etComposeBody);

        tvComposeUsername = findViewById(R.id.tvComposeUsername);
        ivComposeProfile = findViewById(R.id.ivComposeProfile);
        setUserProfileToScreen();

        ivComposeImage = findViewById(R.id.ivComposeImage);

        ibExitCompose = findViewById(R.id.ibExitCompose);
        setIBtnExitListener();
        ibAddImage = findViewById(R.id.ibAddImage);
        setIBtnAddImageListener();
        ibCamera = findViewById(R.id.ibCamera);
        setIBtnCameraListener();
        ibAddPdf = findViewById(R.id.ibAddPdf);
        setIBtnPdfListener();

        bLaunch = findViewById(R.id.bLaunch);
        setBtnLaunchListener();
    }

    public void setIBtnExitListener() {
        ibExitCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(ComposePostActivity.this, MainActivity.class);
        startActivity(intent);
    }

    // TODO - implement the following
    private void setIBtnPdfListener() {
        //grab pdf into image files
    }

    private void setIBtnCameraListener() {
        // do if there is time left
    }

    private void setIBtnAddImageListener() {
        //grab images from gallery
        ibAddImage.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  //checkPermissions();
                  pickFromGallery();
              }
          }
      );}

    public void setBtnLaunchListener() {
        bLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost();
                //TODO -- notify adapter
                //TODO -- Send back to feed fragment
            }
        });
    }

    private void setUserProfileToScreen() {
        //get image and usernamme
        ParseUser user = ParseUser.getCurrentUser();
        String username = user.getUsername();
        ParseFile profile = user.getParseFile(User.KEY_PROFILE);
        tvComposeUsername.setText(username);

        if (profile != null) {
            Glide.with(ComposePostActivity.this)
                    .load(profile.getUrl())
                    .apply(new RequestOptions().
                            centerCrop())
                    .into(ivComposeProfile);
        }
    }

    private void savePost() {
        // Create new post instance
        Post newPost = new Post();
        // Grab content from the compose activity
        String newTitle = etComposePostTitle.getText().toString();
        String newBody = etComposeBody.getText().toString();
        // TODO - grab photo/pdf file
        // Set user of post
        newPost.setUser(ParseUser.getCurrentUser());
        // Set content of post
        if (newTitle.length() != 0) {
            newPost.setTitle(newTitle);
        }
        if (newBody.length() != 0) {
            newPost.setDescription(newBody);
        }
        // TODO - send photo/pdf files to parse
        //newPost.setMedia();
        // Setup vote count
        newPost.setUpVotes(0);
        newPost.setDownVotes(0);
        newPost.saveInBackground();
        //goToMainActivity();
        Toast.makeText(ComposePostActivity.this, "Save successful", Toast.LENGTH_LONG).show();
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData return the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
                    Glide.with(ComposePostActivity.this)
                            .load(imgDecodableString)
                            //TODO - change cetner crop
                            .apply(new RequestOptions().centerCrop())
                            .into(ivComposeImage);
                    break;
            }
    }
}
