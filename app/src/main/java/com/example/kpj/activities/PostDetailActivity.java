package com.example.kpj.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.R;
import com.example.kpj.model.Comment;
import com.example.kpj.model.Post;
import com.example.kpj.model.User;
import com.example.kpj.utils.PostAdapter;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class PostDetailActivity extends AppCompatActivity {

    private Post post;
    Context context;
    ImageView ivDetailProfilePic, ivDetailPostImage, ivDetailComment;
    TextView tvDetailUsername, tvDetailRelativeTime, tvDetailTitle, tvDetailDescription,
            tvDetailHashTags, tvDetailUpVotes, tvDetailDownVotes, tvDetailCommentCount;
    ImageButton ibDetailLike, ibDetailDislike, ibDetailSend, ibAddComment;
    RecyclerView rvComments;
    EditText etWriteComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        //get post
        initializeVariables();
        initializeViews();
        bindPostContent(post);
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        ibAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create new comment
                Comment newComment = new Comment();
                // grab text from edit text to create new comment
                String comment = etWriteComment.getText().toString();
                if (comment != null && comment.length() != 0) {
                    newComment.setDescription(etWriteComment.getText().toString());
                    newComment.setPost(post);
                    newComment.setUser(ParseUser.getCurrentUser());
                    newComment.saveInBackground();
                    // TODO - notify comment adapter
                    // clear the edit text
                    etWriteComment.setText("");
                    Toast.makeText(context, "Commented!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeVariables() {
        this.post = getIntent().getParcelableExtra("post");
        this.context = this;
    }

    private void initializeViews() {
        ivDetailProfilePic = findViewById(R.id.ivDetailProfilePic);
        tvDetailUsername = findViewById(R.id.tvDetailUsername);
        tvDetailRelativeTime = findViewById(R.id.tvDetailRelativeTime);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        ivDetailPostImage = findViewById(R.id.ivDetailPostImage);
        tvDetailHashTags = findViewById(R.id.tvDetailHashTags);
        ibDetailLike = findViewById(R.id.ibDetailLike);
        tvDetailUpVotes = findViewById(R.id.tvDetailUpVotes);
        ibDetailDislike = findViewById(R.id.ibDetailDislike);
        tvDetailDownVotes = findViewById(R.id.tvDetailDownVotes);
        ivDetailComment = findViewById(R.id.ivDetailComment);
        tvDetailCommentCount = findViewById(R.id.tvDetailCommentCount);
        ibDetailSend = findViewById(R.id.ibDetailSend);
        rvComments = findViewById(R.id.rvComments);
        etWriteComment = findViewById(R.id.etWriteComment);
        ibAddComment = findViewById(R.id.ibAddComment);
    }

    private void bindPostContent(Post post) {

        tvDetailUsername.setText(post.getUser().getUsername());
        ParseFile profile = post.getUser().getParseFile(User.KEY_PROFILE);
        if (profile != null) {
            Glide.with(context)
                    .load(profile.getUrl())
                    .apply(new RequestOptions().centerCrop())
                    .into(ivDetailProfilePic);
        }

        if (post.getTitle() != null) {
            tvDetailTitle.setVisibility(View.VISIBLE);
            tvDetailTitle.setText(post.getTitle());
        } else {
            tvDetailTitle.setText(post.getTitle());
        }

        if (post.getDescription() != null) {
            tvDetailDescription.setVisibility(View.VISIBLE);
            tvDetailDescription.setText(post.getDescription());
        } else {
            tvDetailDescription.setVisibility(View.GONE);
        }

        if (post.getMedia() != null) {
            ivDetailPostImage.setVisibility(View.VISIBLE);
            ParseFile photoFile = post.getMedia();

            Glide.with(context)
                    .load(photoFile.getUrl())
                    .into(ivDetailPostImage);
        } else {
            ivDetailPostImage.setVisibility(View.GONE);
        }

        tvDetailUpVotes.setText(String.valueOf(post.getUpVotes()));
        tvDetailDownVotes.setText(String.valueOf(post.getDownVotes()));
    }


}
