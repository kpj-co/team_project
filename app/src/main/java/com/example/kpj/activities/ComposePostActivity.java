package com.example.kpj.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kpj.CameraLauncher;
import com.example.kpj.GalleryHelper;
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


import java.io.File;
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
    public File photoFile;
    public String imagePath;
    public final static String APP_TAG = "compose post activity";
    private final static int GALLERY_REQUEST_CODE = 100;
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private final static String PREF_NAME = "sharedData";
    private Context context;
    private Course course;
    private GalleryHelper galleryHelper;


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
                onLaunchCamera();
            }
        });
    }

    private void setIBtnAddImageListener() {
        ibAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //grab images from gallery
                pickFromGallery();
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

    private void pickFromGallery() {
        GalleryHelper galleryHelper = new GalleryHelper(ComposePostActivity.this, new GalleryHelper.Callback() {
            @Override
            public void startActivityForResult(Intent intent) {
                ComposePostActivity.this.startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });
        this.galleryHelper = galleryHelper;
        galleryHelper.requestGalleryPermission();
        galleryHelper.pickFromGallery();
    }

    public void onLaunchCamera() {
        CameraLauncher cameraLauncher = new CameraLauncher(ComposePostActivity.this, new CameraLauncher.Callback() {
            @Override
            public void startActivityForResult(Intent intent) {
                ComposePostActivity.this.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        cameraLauncher.requestCameraPermission();
        photoFile = cameraLauncher.onLaunchCamera();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    imagePath = galleryHelper.getImagePath(data);
                    bindImagesToPreview(imagePath);
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

    //Returns an ArrayList of all the Hash Tags given by the user
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
