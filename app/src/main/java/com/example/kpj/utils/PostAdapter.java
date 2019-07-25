package com.example.kpj.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.R;
import com.example.kpj.activities.MainActivity;
import com.example.kpj.fragments.CourseFeedFragment;
import com.example.kpj.fragments.SendToChatDialogFragment;
import com.example.kpj.model.Post;
import com.example.kpj.model.User;
import com.example.kpj.model.UserCourseRelation;
import com.example.kpj.model.UserPostRelation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private Fragment fragment;
    private List<Post> mPosts;
    private final static String KEY_SEND_POST_TO_CHAT = "A";

    public PostAdapter(Context context, Fragment fragment,  List<Post> posts) {
        this.context = context;
        this.fragment = fragment;
        mPosts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
     Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.post_item, viewGroup, false);
        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = mPosts.get(position);
        bindPostContent(holder, post);
        //populate post likes, dislikes
        holder.tvUpVotes.setText(String.valueOf(post.getUpVotes()));
        holder.tvDownVotes.setText(String.valueOf(post.getDownVotes()));
        post.isLiked = false;
        post.isDisliked = false;
        setUpUpVoteListener(holder, post);
        setUpDownVoteListener(holder, post);
        setUpIbSendListener(holder, post);
    }

    private void setUpIbSendListener(final ViewHolder holder, final Post post) {
        holder.ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogBox = new SendToChatDialogFragment();
                dialogBox.show(((MainActivity)context).getSupportFragmentManager(), "tag");
                // send post to dialog fragment via bundle
                Bundle bundle = new Bundle();
                bundle.putParcelable(KEY_SEND_POST_TO_CHAT, post);
                dialogBox.setArguments(bundle);
            }
        });
    }

    /** Bind the data base title, body, image info with associated post views
     * @params: ViewHolder, Post
     * @return: void
     */
    private void bindPostContent(@NonNull ViewHolder holder, Post post) {
        bindPostUserAssets(holder, post);

        if (post.getTitle() != null) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(post.getTitle());
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }

        if (post.getDescription() != null) {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(post.getDescription());
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        if (post.getMedia() != null) {
            holder.ivPostImage.setVisibility(View.VISIBLE);
            ParseFile photoFile = post.getMedia();

            Glide.with(context)
                    .load(photoFile.getUrl())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.ivPostImage);
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }
    }

    /** Up Vote a post and update parse db
     * @params: ViewHolder, Post
     * @return: void
     */
    private void setUpUpVoteListener(final ViewHolder holder, final Post post) {
        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO -- ask ivan
                // check if there is an existing userPostRelation
                final UserPostRelation.Query userPostRelation = new UserPostRelation.Query();
                userPostRelation.whereEqualTo("user", ParseUser.getCurrentUser());
                userPostRelation.whereEqualTo("post", post);
                userPostRelation.findInBackground(new FindCallback<UserPostRelation>() {
                    @Override
                    public void done(List<UserPostRelation> relation, ParseException e) {
                        if (e == null) {
                            // if there is none the list is empty or of length 0
                            if (relation.isEmpty() || relation.size() == 0) {
                                // create new relation
                                UserPostRelation newUserPostRelation = new UserPostRelation();
                                newUserPostRelation.setPost(post);
                                newUserPostRelation.setUser(ParseUser.getCurrentUser());
                                newUserPostRelation.setVote(UserPostRelation.UPVOTE);
                                newUserPostRelation.saveInBackground();
                                // change UI
                                int newCount = post.getUpVotes() + 1;
                                holder.tvUpVotes.setText(String.valueOf(newCount));
                                post.isLiked = true;
                                Toast.makeText(context, "upvoted post", Toast.LENGTH_SHORT).show();
                            } else { // there already exists a relation
                                // TODO -- check the state of the realtion
                            }
                        } else {
                            Toast.makeText(context, "could not vote", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                */

                int newParseCount;
                // Increase UpVote count
                if (!post.isLiked) {
                    // TODO -- change image into dark
                    int newCount = post.getUpVotes() + 1;
                    holder.tvUpVotes.setText(String.valueOf(newCount));
                    post.isLiked = true;
                    newParseCount = newCount;
                    Toast.makeText(context, "upvoted post", Toast.LENGTH_SHORT).show();
                } else { // decrease upvote count
                    // TODO -- change icon into light
                    int newCount = post.getUpVotes() - 1;
                    holder.tvUpVotes.setText(String.valueOf(newCount));
                    post.isLiked = false;
                    newParseCount = newCount;
                    Toast.makeText(context, "undo upvote", Toast.LENGTH_SHORT).show();
                }
                post.setUpVotes(newParseCount);
                post.saveInBackground();
            }
        });
    }

    /** Down Vote a post and update parse db
     * @params: ViewHolder, Post
     * @return: void
     */
    private void setUpDownVoteListener(final ViewHolder holder, final Post post) {
        holder.ibDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newParseCount;
                // Increase down vote count
                if (!post.isDisliked) {
                    // TODO -- CHANGE DISLIKE IMAGE TO DARK COLOR
                    int newCount = post.getDownVotes() + 1;
                    holder.tvDownVotes.setText(String.valueOf(newCount));
                    post.isDisliked = true;
                    newParseCount = newCount;
                    Toast.makeText(context, "downvoted post", Toast.LENGTH_SHORT).show();
                } else { // decrease down vote count
                    // TODO -- CHANGE LIKE IMAGE TO LIGHT COLOR
                    int newCount = post.getDownVotes() - 1;
                    holder.tvDownVotes.setText(String.valueOf(newCount));
                    post.isDisliked = false;
                    newParseCount = newCount;
                    Toast.makeText(context, "undo down vote", Toast.LENGTH_SHORT).show();
                }
                post.setDownVotes(newParseCount);
                post.saveInBackground();
            }
        });

    }

    /** Bind the data base user info with user associated views of a post
     * @params: ViewHolder, Post
     * @return: void
     */
    private void bindPostUserAssets(@NonNull ViewHolder holder, Post post) {
        holder.tvUser.setText(post.getUser().getUsername());
        ParseFile profile = post.getUser().getParseFile(User.KEY_PROFILE);
        if (profile != null) {
            Glide.with(context)
                    .load(profile.getUrl())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.ivProfile);
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfile;
        public TextView tvUser;
        public TextView tvDate;

        public TextView tvTitle;
        public TextView tvDescription;
        public ImageView ivPostImage;

        public TextView tvHashtag1;
        public TextView tvHashtag2;

        public ImageButton ibLike;
        public ImageButton ibDislike;
        public ImageButton ibComment;
        public ImageButton ibSend;

        public TextView tvUpVotes;
        public TextView tvDownVotes;
        public TextView tvCommentCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeViews(itemView);
        }

        private void initializeViews(@NonNull View itemView) {
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvHashtag1 = itemView.findViewById(R.id.tvHashtag1);
            tvHashtag2 = itemView.findViewById(R.id.tvHashtag2);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibDislike = itemView.findViewById(R.id.ibDislike);
            ibComment = itemView.findViewById(R.id.ibComment);
            ibSend = itemView.findViewById(R.id.ibSend);
            tvUpVotes = itemView.findViewById(R.id.tvUpVotes);
            tvDownVotes = itemView.findViewById(R.id.tvDownVotes);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
        }
    }



}
