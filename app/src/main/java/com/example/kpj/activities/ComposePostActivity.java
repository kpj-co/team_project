package com.example.kpj.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kpj.R;
import com.example.kpj.model.Course;
import com.example.kpj.model.Message;
import com.example.kpj.model.Post;
import com.example.kpj.model.PostHashtagRelation;
import com.example.kpj.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.parceler.Parcels;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.parse.Parse.getApplicationContext;


public class ComposePostActivity extends AppCompatActivity {

    EditText etComposePostTitle;
    EditText etComposeBody;
    EditText etHashtags;

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

    public String photoFileName = "cameraPhoto.jpg";
    public File photoFile;
    public String imagePath;
    public final static String APP_TAG = "compose post activity";

    private final static int GALLERY_REQUEST_CODE = 100;
    private final static int PICK_FROM_GALLERY = 3;
    private final static int CAMERA_PERMISSION_CODE = 1;
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private final static String PREF_NAME = "sharedData";
    private Context context;
    private Course course;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_post);
        initializeViews();
        initializeVariables();
        //if a user wants to post a message as a post, this method will do the job
        preparePostFromComment();
    }

    private void initializeVariables() {
        context = ComposePostActivity.this;
        imagePath = "";
        String courseName = getCurrentCourseName();
        final Course.Query courseQuery = new Course.Query();
        courseQuery.whereEqualTo("name", courseName);
        // query for current course
        courseQuery.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> selectedCourse, ParseException e) {
                course = selectedCourse.get(0);
            }
        });
    }

    private void initializeViews() {
        etComposePostTitle = findViewById(R.id.etComposePostTitle);
        etComposeBody = findViewById(R.id.etComposeBody);
        etHashtags = findViewById(R.id.etTags);
        tvComposeUsername = findViewById(R.id.tvComposeUsername);
        ivComposeProfile = findViewById(R.id.ivComposeProfile);
        bindUserProfileToView();
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

    // TODO - implement the following
    private void setIBtnPdfListener() {
        //grab pdf into image files
    }

    private void setIBtnCameraListener() {
        ibCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });
    }

    private void setIBtnAddImageListener() {
        //grab images from gallery
        ibAddImage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                requestGalleryPermission();
            }
        });
    }


    public void setBtnLaunchListener() {
        bLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost();
                ComposePostActivity.this.finish();
            }
        });
    }


    private String getCurrentCourseName() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return settings.getString("courseName", "");
    }

    private void bindUserProfileToView() {
        //get image and username
        ParseUser user = ParseUser.getCurrentUser();
        String username = user.getUsername();
        ParseFile profile = user.getParseFile(User.KEY_PROFILE);
        tvComposeUsername.setText(username);

        if (profile != null) {
            Glide.with(ComposePostActivity.this)
                    .load(profile.getUrl())
                    .apply(new RequestOptions().centerCrop())
                    .into(ivComposeProfile);
        }
    }

    private void requestCameraPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ComposePostActivity.this, new String[]
                {Manifest.permission.CAMERA, Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_CODE);
            } else {
                onLaunchCamera();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestGalleryPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ComposePostActivity.this, new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PICK_FROM_GALLERY);
            } else {
                pickFromGallery();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                } else {
                    Toast.makeText(context, "no permission from gallery", Toast.LENGTH_SHORT).show();
                    requestGalleryPermission();
                }
                break;
            case CAMERA_PERMISSION_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onLaunchCamera();
                } else {
                    Toast.makeText(context, "no permission from camera", Toast.LENGTH_SHORT).show();
                    requestGalleryPermission();
                }
        }
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

    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);
        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(context,
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
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }
        // TODO -- ASK IVAN WHY THE MATH.RANDOM Work
        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName + Math.random());
        return file;
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
                    imagePath = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView
                    bindImagesToPreview(imagePath);
                    // Save the image path locally
                    break;
                case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    // Set the Image in ImageView
                    bindImagesToPreview(photoFile);
                    Toast.makeText(context, "Camera photo set", Toast.LENGTH_LONG).show();
                    break;
            }
    }

    private void bindImagesToPreview(File photo) {
        Glide.with(context)
                .load(photo)
                //TODO - change center crop to resize and fit parent container
                .apply(new RequestOptions().centerCrop())
                .into(ivComposeImage);
    }

    private void bindImagesToPreview(String photoPath) {
        Glide.with(context)
                .load(photoPath)
                //TODO - change center crop to resize and fit parent container
                .apply(new RequestOptions().centerCrop())
                .into(ivComposeImage);
    }

    private void savePost() {
        // Create new post instance
        Post newPost = new Post();
        // Grab text content from compose
        String newTitle = etComposePostTitle.getText().toString();
        String newBody = etComposeBody.getText().toString();
        ArrayList<String> hashtags = returnHashtags(etHashtags.getText().toString());
        // Set user of post
        newPost.setUser(ParseUser.getCurrentUser());
        // Set content of post
        if (newTitle.length() != 0) { newPost.setTitle(newTitle); }
        if (newBody.length() != 0) { newPost.setDescription(newBody); }
        if (imagePath.length() != 0) {
            File imageFile = new File(imagePath);
            if (imageFile != null) {
                ParseFile imageParseFile = new ParseFile(imageFile);
                newPost.setMedia(imageParseFile);
            }
        }
        if (photoFile != null) {
            ParseFile imageParseFile = new ParseFile(photoFile);
            newPost.setMedia(imageParseFile);
        }
        // Save associated course
        if (course != null) {
            newPost.setCourse(course);
        } else {
            Toast.makeText(context, "could not find course associated", Toast.LENGTH_SHORT).show();
        }

        //Add the relationship post-hashtag to the database for each hashYWtag
        for(String hashtag : hashtags) {
            PostHashtagRelation postHashtagRelation = new PostHashtagRelation();
            postHashtagRelation.setPost(newPost);
            postHashtagRelation.setHashtag(hashtag);
            postHashtagRelation.saveInBackground();
        }

        // Setup vote count
        newPost.setUpVotes(0);
        newPost.setDownVotes(0);
        // Save post in background thread
        newPost.saveInBackground();
        Toast.makeText(ComposePostActivity.this, "Save successful", Toast.LENGTH_LONG).show();
        goToMainActivity();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(ComposePostActivity.this, MainActivity.class);
        startActivity(intent);
    }

    //if a user wants to post a message as a post, this method will do the job
    public void preparePostFromComment() {
        Message message = getIntent().getParcelableExtra("message");
        if (message != null) {
            etComposeBody.setText(message.getDescription());
        }
    }

    //Returns an ArrayList of all the hashtags given by the user
    private ArrayList<String> returnHashtags(String hashtags) {
        //Boolean to know if there is something in the current sequence of characters to be analyzed
        boolean hasContent;
        //Variable to store our "base point", or the # symbol that will start the hashtag
        int basePoint;
        //ArrayList that has the hashtags to be returned
        ArrayList<String> hashtagsList;
        //Initializing the boolean
        hasContent = false;
        //Initializing the basePoint
        basePoint = 0;
        //Initializing the list
        hashtagsList = new ArrayList<>();
        for(int i = 0; i < hashtags.length(); i++) {
            //If it is a #, and we do not have a possible hashtag, change the base point
            if(hashtags.charAt(i) == '#') {
                basePoint = i;
                hasContent = true;
            }
            //If there is a space, it could be a hashtag if it has content
            else if(hashtags.charAt(i) == ' ' && hasContent) {
                hashtagsList.add(hashtags.substring(basePoint + 1, i));

                hasContent = false;
            }

            //The case is slightly different when we are dealing with the last character of the string
            else if(i == hashtags.length() - 1 && hasContent) {
                hashtagsList.add(hashtags.substring(basePoint + 1, i + 1));

                hasContent = false;
            }
        }
        //returning the object
        return hashtagsList;
    }
}
