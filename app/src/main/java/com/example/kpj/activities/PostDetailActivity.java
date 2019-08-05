package com.example.kpj.activities;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.example.kpj.fragments.SendToChatDialogFragment;
import com.example.kpj.model.Comment;
import com.example.kpj.model.ImagePreview;
import com.example.kpj.model.Post;
import com.example.kpj.model.PostHashtagRelation;
import com.example.kpj.model.PostImageRelation;
import com.example.kpj.model.User;
import com.example.kpj.utils.CommentAdapter;
import com.example.kpj.utils.ImagePreviewAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;
import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private Post post;
    private List postHashtags;
    private List<Comment> mComments;
    private List<ImagePreview> mImages;
    private CommentAdapter commentAdapter;
    private ImagePreviewAdapter imagePreviewAdapter;

    Context context;
    ImageView ivDetailProfilePic, ivDetailComment;
    TextView tvDetailUsername, tvDetailRelativeTime, tvDetailTitle, tvDetailDescription,
            tvDetailHashTags, tvDetailUpVotes, tvDetailDownVotes, tvDetailCommentCount;
    ImageButton ibDetailLike, ibDetailDislike, ibDetailSend, ibAddComment;
    RecyclerView rvComments, rvDetailImagePreview;
    EditText etWriteComment;

    private final static String KEY_SEND_POST_TO_CHAT = "A";
    private final static String KEY_SEND_COURSE_TO_CHAT = "B";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail2);
        //get post
        initializeVariables();
        initializeViews();
        bindPostDetailContent(post);
        setOnClickListeners();
        setUpImages();
        setUpComments();
        queryImages();
        queryComments();
    }

    private void queryImages() {
        if (post.getHasMedia()) {
            PostImageRelation.Query query = new PostImageRelation.Query();
            query.whereEqualTo("post", post);
            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<PostImageRelation>() {
                @Override
                public void done(List<PostImageRelation> relations, ParseException e) {
                    if (e == null && relations.size() != 0) {
                        for (PostImageRelation relation : relations) {
                            mImages.add(new ImagePreview(relation.getImage()));
                            imagePreviewAdapter.notifyItemInserted(mImages.size() - 1);
                        }
                    } else {
                        Toast.makeText(context, "Error loading images", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            rvDetailImagePreview.setVisibility(View.GONE);
        }
    }

    private void queryComments() {
        final Comment.Query query = new Comment.Query();
        query.include("user");
        query.whereEqualTo("post", post);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e == null) {
                    for (Comment comment : comments) {
                        mComments.add(comment);
                        commentAdapter.notifyItemInserted(mComments.size() - 1);
                    }
                } else {
                    Toast.makeText(context, "Error loading comments", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    mComments.add(newComment);
                    commentAdapter.notifyItemInserted(mComments.size() - 1);
                    // increase comment count
                    post.setCommentCount(post.getNumComments() + 1);
                    post.saveInBackground();
                    // clear the edit text
                    etWriteComment.setText("");
                    Toast.makeText(context, "Commented", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibDetailSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogBox = new SendToChatDialogFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                dialogBox.show(fragmentManager, "post detail");
                Bundle bundle = new Bundle();
                bundle.putParcelable(KEY_SEND_POST_TO_CHAT, post);
                bundle.putParcelable(KEY_SEND_COURSE_TO_CHAT, post.getCourse());
                dialogBox.setArguments(bundle);
            }
        });
    }

    private void initializeVariables() {
        try {
            Bundle bundle = getIntent().getExtras();
            this.post = bundle.getParcelable("post");
            this.postHashtags = bundle.getStringArrayList("postHashTags");
        } catch (Exception e) {
            // do nothing
        }
        this.context = this;
        this.mComments = new ArrayList<>();
        this.mImages = new ArrayList<>();
    }

    private void queryHashTags(final Post post) {
        final PostHashtagRelation.Query query = new PostHashtagRelation.Query();
        query.whereEqualTo("post", post);
        query.findInBackground(new FindCallback<PostHashtagRelation>() {
            @Override
            public void done(List<PostHashtagRelation> relations, ParseException e) {
                for (PostHashtagRelation relation : relations) {
                    postHashtags.add(relation.getHashtag());
                }
                post.setHashtags((ArrayList) postHashtags);
                tvDetailHashTags.setText(post.getDisplayHashTags());
            }
        });
    }

    private void initializeViews() {
        ivDetailProfilePic = findViewById(R.id.ivDetailProfilePic);
        tvDetailUsername = findViewById(R.id.tvDetailUsername);
        tvDetailRelativeTime = findViewById(R.id.tvDetailTime);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailHashTags = findViewById(R.id.tvDetailHashTags);
        ibDetailLike = findViewById(R.id.ibDetailLike);
        tvDetailUpVotes = findViewById(R.id.tvDetailUpVotes);
        ibDetailDislike = findViewById(R.id.ibDetailDislike);
        tvDetailDownVotes = findViewById(R.id.tvDetailDownVotes);
        ivDetailComment = findViewById(R.id.ivDetailComment);
        tvDetailCommentCount = findViewById(R.id.tvDetailCommentCount);
        ibDetailSend = findViewById(R.id.ibDetailSend);
        rvComments = findViewById(R.id.rvComments);
        rvDetailImagePreview = findViewById(R.id.rvDetailImagePreview);
        etWriteComment = findViewById(R.id.etWriteComment);
        ibAddComment = findViewById(R.id.ibAddComment);
    }

    private void setUpComments() {
        commentAdapter = new CommentAdapter(context, post, mComments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(commentAdapter);
    }

    private void setUpImages() {
        imagePreviewAdapter = new ImagePreviewAdapter(context, mImages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        rvDetailImagePreview.setLayoutManager(linearLayoutManager);
        rvDetailImagePreview.setAdapter(imagePreviewAdapter);
    }

    private void bindPostDetailContent(Post post) {
        try {
            tvDetailUsername.setText((post.getUser().fetchIfNeeded()).getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseFile profile = post.getUser().getParseFile(User.KEY_PROFILE);
        if (profile != null) {
            Glide.with(context)
                    .load(profile.getUrl())
                    .apply(new RequestOptions().centerCrop())
                    .into(ivDetailProfilePic);
        } else {
            ivDetailProfilePic.setColorFilter(Color.DKGRAY);
        }

        if (post.getTitle() != null) {
            tvDetailTitle.setVisibility(View.VISIBLE);
            tvDetailTitle.setText(post.getTitle());
        } else {
            tvDetailTitle.setVisibility(View.INVISIBLE);
        }

        try {
            tvDetailRelativeTime.setText(post.getSimpleDate());
        } catch (NullPointerException e) {
            // do nothing
        }

        if (post.getDescription() != null) {
            tvDetailDescription.setVisibility(View.VISIBLE);
            tvDetailDescription.setText(post.getDescription());
        } else {
            tvDetailDescription.setVisibility(View.INVISIBLE);
        }

        if (postHashtags.size() == 0) {
            queryHashTags(post);
        } else {
            post.setHashtags((ArrayList) postHashtags);
            tvDetailHashTags.setText(post.getDisplayHashTags());
        }

        tvDetailCommentCount.setText(String.valueOf(post.getNumComments()));
        tvDetailUpVotes.setText(String.valueOf(post.getUpVotes()));
        tvDetailDownVotes.setText(String.valueOf(post.getDownVotes()));
    }

}
