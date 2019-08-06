package com.example.kpj;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kpj.model.Post;
import com.example.kpj.model.UserPostRelation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class VoteSystemManager {

    public static final int DOWNVOTE = 2;
    public static final int UPVOTE = 1;
    public static final int NOVOTE = 0;

    public static void manageVote(int previousState, int nextState, TextView tvUpVotes, TextView tvDownVotes, Post post, UserPostRelation relation) {
        if(previousState == NOVOTE) {
            if(nextState == UPVOTE) {
                relation.setVote(UserPostRelation.UPVOTE);
                int newCount = post.getUpVotes() +  1;
                tvUpVotes.setText(String.valueOf(newCount));
                post.setUpVotes(newCount);
                post.isLiked = true;
            }

            else if(nextState == DOWNVOTE) {
                relation.setVote(UserPostRelation.DOWNVOTE);
                int newCount = post.getDownVotes() +  1;
                tvDownVotes.setText(String.valueOf(newCount));
                post.setDownVotes(newCount);
                post.isLiked = true;
            }
        }

        else if(previousState == UPVOTE) {
            if(nextState == NOVOTE) {
                //Remove the positive vote
                relation.setVote(UserPostRelation.NOVOTE);
                int newCount = post.getUpVotes() - 1;
                tvUpVotes.setText(String.valueOf(newCount));
                post.setUpVotes(newCount);
                post.isLiked = false;
            }

            else if(nextState == DOWNVOTE) {
                relation.setVote(UserPostRelation.DOWNVOTE);
                int newCount = post.getUpVotes() - 1;
                tvUpVotes.setText(String.valueOf(newCount));
                post.setUpVotes(newCount);
                newCount = post.getDownVotes() + 1;
                tvDownVotes.setText(String.valueOf(newCount));
                post.setDownVotes(newCount);
                post.isLiked = false;
            }
        }

        else if(previousState == DOWNVOTE) {
            if(nextState == UPVOTE) {
                relation.setVote(UserPostRelation.UPVOTE);
                int newCount = post.getUpVotes() + 1;
                tvUpVotes.setText(String.valueOf(newCount));
                post.setUpVotes(newCount);
                newCount = post.getDownVotes() - 1;
                tvDownVotes.setText(String.valueOf(newCount));
                post.setDownVotes(newCount);
                post.isLiked = true;
            }

            else if(nextState == NOVOTE) {
                relation.setVote(UserPostRelation.NOVOTE);
                int newCount = post.getDownVotes() - 1;
                tvDownVotes.setText(String.valueOf(newCount));
                post.setDownVotes(newCount);
                post.isLiked = false;
            }
        }
        relation.saveInBackground();
        post.saveInBackground();
    }

    public static void manageVote(int newState, TextView tvUpVotes, TextView tvDownVotes, ParseUser user, Post post) {
        if(newState == UPVOTE) {
            //create a new relation
            UserPostRelation newUserPostRelation = new UserPostRelation();
            newUserPostRelation.setPost(post);
            newUserPostRelation.setUser(user);
            newUserPostRelation.setVote(UPVOTE);
            newUserPostRelation.saveInBackground();

            //Change UI
            int newCount = post.getUpVotes() + 1;
            tvUpVotes.setText(String.valueOf(newCount));
            post.isLiked = true;
            post.setUpVotes(newCount);
        }

        else if(newState == DOWNVOTE) {
            // create new relation
            UserPostRelation newUserPostRelation = new UserPostRelation();
            newUserPostRelation.setPost(post);
            newUserPostRelation.setUser(user);
            newUserPostRelation.setVote(DOWNVOTE);
            newUserPostRelation.saveInBackground();

            // change UI
            int newCount = post.getDownVotes() + 1;
            tvDownVotes.setText(String.valueOf(newCount));
            post.isLiked = false;
            post.setDownVotes(newCount);

        }
    }


    /** Load like and dislike icons based on user interaction in view holder
     * @params: ViewHolder, Post
     * @return: void
     */
    public static void bindVoteContent(final Post post, final ParseUser user,
                                final ImageButton upVoteImageButton, TextView upVoteText,
                                final ImageButton downVoteImageButton, TextView downVoteText) {

        upVoteImageButton.setVisibility(View.VISIBLE);
        downVoteImageButton.setVisibility(View.VISIBLE);
        upVoteText.setText(String.valueOf(post.getUpVotes()));
        downVoteText.setText(String.valueOf(post.getDownVotes()));

        if (upVoteText.getText().toString().equals("0") && downVoteText.getText().toString().equals("0")) {
            upVoteImageButton.setImageResource(R.drawable.outline_thumb_up_black_18dp);
            downVoteImageButton.setImageResource(R.drawable.outline_thumb_down_black_18dp);
        } else {
            final UserPostRelation.Query userPostRelation = new UserPostRelation.Query();
            userPostRelation.whereEqualTo("user", user);
            userPostRelation.whereEqualTo("post", post);
            userPostRelation.findInBackground(new FindCallback<UserPostRelation>() {
                @Override
                public void done(List<UserPostRelation> relation, ParseException e) {
                    if (e == null) {
                        // if there is none the list is empty or of length 0
                        if (!relation.isEmpty() &&  relation.size() != 0) {
                            // there already exists a relation
                            UserPostRelation newUserPostRelation = relation.get(0);
                            if(newUserPostRelation.getVote() == UserPostRelation.UPVOTE) {
                                // set like to drawable to baseline up vote
                                upVoteImageButton.setImageResource(R.drawable.baseline_thumb_up_black_18dp);
                                // set dislike to drawable neutral down vote
                                downVoteImageButton.setImageResource(R.drawable.outline_thumb_down_black_18dp);
                            } else if(newUserPostRelation.getVote() == UserPostRelation.DOWNVOTE) {
                                // set like to drawable to baseline up vote
                                upVoteImageButton.setImageResource(R.drawable.outline_thumb_up_black_18dp);
                                // set dislike to drawable neutral down vote
                                downVoteImageButton.setImageResource(R.drawable.baseline_thumb_down_black_18dp);
                            } else if(newUserPostRelation.getVote() == UserPostRelation.NOVOTE) {
                                // set like to drawable to baseline up vote
                                upVoteImageButton.setImageResource(R.drawable.outline_thumb_up_black_18dp);
                                // set dislike to drawable neutral down vote
                                downVoteImageButton.setImageResource(R.drawable.outline_thumb_down_black_18dp);
                            }
                        }
                    } else {
                        upVoteImageButton.setImageResource(R.drawable.outline_thumb_up_black_18dp);
                        downVoteImageButton.setImageResource(R.drawable.outline_thumb_down_black_18dp);
                    }
                }
            });
        }

    }

}
