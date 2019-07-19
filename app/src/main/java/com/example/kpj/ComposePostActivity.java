package com.example.kpj;

import android.content.Intent;
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

        ibExitCompose = findViewById(R.id.ibExitCompose);
        setIBtnExitListener();
        ibAddImage = findViewById(R.id.ibAddImage);
        setIBtnAddImageListener();
        ibCamera  = findViewById(R.id.ibCamera);
        setIBtnCameraListener();
        ibAddPdf  = findViewById(R.id.ibAddPdf);
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
        //grab images from gallary?
    }

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
        // TODO - send photo/pdf files
        // Setup vote count
        newPost.setUpVotes(0);
        newPost.setDownVotes(0);
        newPost.saveInBackground();
        goToMainActivity();
        Toast.makeText(ComposePostActivity.this, "Save successful", Toast.LENGTH_LONG).show();
    }

}
