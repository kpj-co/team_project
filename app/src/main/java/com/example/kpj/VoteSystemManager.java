package com.example.kpj;

import android.widget.TextView;
import android.widget.Toast;

import com.example.kpj.model.Post;
import com.example.kpj.model.UserPostRelation;
import com.parse.ParseUser;

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
}
