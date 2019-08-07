package com.example.kpj.activities;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.kpj.model.ImagePreview;
import com.example.kpj.model.Message;
import com.example.kpj.model.Post;
import com.example.kpj.model.PostHashtagRelation;
import com.example.kpj.model.PostImageRelation;
import com.example.kpj.model.User;
import com.example.kpj.utils.HashtagSanitizer;
import com.example.kpj.utils.ImagePreviewAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComposePostActivity extends AppCompatActivity {

    EditText etComposePostTitle, etComposeBody, etHashtags;
    TextView tvComposeTitleLabel, tvComposeUsername, tvComposeBodyLabel;
    ImageView ivComposeProfile;
    ImageButton ibAddPdf, ibCamera, ibExitCompose, ibAddImage;
    RecyclerView rvImagePreview;
    Button bLaunch;

    public final static String APP_TAG = "compose post activity";
    private final static int GALLERY_REQUEST_CODE = 100;
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private final static String PREF_NAME = "sharedData";
    private Context context;
    private Course course;
    private GalleryHelper galleryHelper;
    private HashtagSanitizer hashtagSanitizer;
    private File photoFile;
    private String imagePath;
    private List<ImagePreview> mImages;
    private ImagePreviewAdapter imagePreviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_post);
        initializeVariables();
        initializeViews();
        //if a user wants to post a message as a post, this method will do the job
        preparePostFromComment();
    }

    private void initializeVariables() {
        context = ComposePostActivity.this;
        hashtagSanitizer = new HashtagSanitizer();
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
        rvImagePreview = findViewById(R.id.rvDetailImagePreview);
        this.mImages = new ArrayList<>();
        setUpImagePreview();
        rvImagePreview.setVisibility(View.GONE);
    }

    private void setUpImagePreview() {
        imagePreviewAdapter = new ImagePreviewAdapter(context, mImages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        rvImagePreview.setLayoutManager(linearLayoutManager);
        rvImagePreview.setAdapter(imagePreviewAdapter);
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
                rvImagePreview.setVisibility(View.VISIBLE);
                onLaunchCamera();
            }
        });
    }

    private void setIBtnAddImageListener() {
        ibAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //grab images from gallery
                rvImagePreview.setVisibility(View.VISIBLE);
                onLaunchGallery();
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

    private void onLaunchGallery() {
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
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    imagePath = galleryHelper.getImagePath(data);
                    // create a new image preview and inset it in the recycler view
                    ImagePreview newImage = new ImagePreview(imagePath);
                    notifyAdapterItemInserted(newImage);
                    break;
                case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    // Set the Image in ImageView
                    ImagePreview image = new ImagePreview(photoFile);
                    notifyAdapterItemInserted(image);
                    Toast.makeText(context, "Camera photo set", Toast.LENGTH_LONG).show();
                    break;
            }
    }

    private void notifyAdapterItemInserted(ImagePreview newImage) {
        mImages.add(newImage);
        imagePreviewAdapter.notifyItemInserted(mImages.size() - 1);
        rvImagePreview.scrollToPosition(mImages.size() - 1);
    }

    private void savePost() {
        // Create new post instance
        final Post newPost = new Post();
        // Grab text content from compose
        String newTitle = etComposePostTitle.getText().toString();
        String newBody = etComposeBody.getText().toString();
        final ArrayList<String> hashtags = (ArrayList<String>) hashtagSanitizer.returnHashtags(etHashtags.getText().toString());
        // Set user of post
        newPost.setUser(ParseUser.getCurrentUser());
        // Set content of post
        if (newTitle.length() != 0) { newPost.setTitle(newTitle); }
        if (newBody.length() != 0) { newPost.setDescription(newBody); }

        // Save associated course
        if (course != null) {
            newPost.setCourse(course);
        } else {
            Toast.makeText(context, "could not find course associated", Toast.LENGTH_SHORT).show();
        }

        // Setup vote count
        newPost.setUpVotes(0);
        newPost.setDownVotes(0);
        // Setup comment count
        newPost.setCommentCount(0);

        // Save post in background thread
        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    savePhotos();
                    saveHashtags();
                    Toast.makeText(ComposePostActivity.this, "Save successful", Toast.LENGTH_LONG).show();
                    goToMainActivity();
                } else {
                    Toast.makeText(ComposePostActivity.this, "ERROR: could not save post", Toast.LENGTH_LONG).show();
                    goToMainActivity();
                }
            }

            // Save media via PostImage relation
            private void savePhotos() {
                if (mImages.size() != 0) {
                    for (ImagePreview image : mImages) {
                        PostImageRelation postImageRelation = new PostImageRelation();
                        postImageRelation.setPost(newPost);
                        postImageRelation.setImage(image.getParseFile());
                        postImageRelation.saveInBackground();
                    }
                    newPost.setHasMedia(true);
                    // save first photo of set
                    newPost.setMedia(mImages.get(0).getParseFile());
                    newPost.saveInBackground();
                } else {
                    newPost.setHasMedia(false);
                    newPost.saveInBackground();
                }
            }

            // Add the relationship post-hashtag to the database for each hash tag
            private void saveHashtags() {
                if (hashtags.size() != 0 ){
                    for(String hashtag : hashtags) {
                        PostHashtagRelation postHashtagRelation = new PostHashtagRelation();
                        postHashtagRelation.setPost(newPost);
                        postHashtagRelation.setHashtag(hashtag);
                        postHashtagRelation.saveInBackground();
                    }
                }
            }

        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(ComposePostActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //if a user wants to post a message as a post, this method will do the job
    public void preparePostFromComment() {
        Message message = getIntent().getParcelableExtra("message");
        if (message != null) {
            etComposeBody.setText(message.getDescription());
        }
    }
}
