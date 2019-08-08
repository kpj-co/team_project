package com.example.kpj;

import android.content.Context;
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
    public static final int UPVOTE_LISTENER = 100;
    public static final int DOWNVOTE_LISTENER = 200;

    public static void manageVote(int previousState, int nextState, TextView tvUpVotes, TextView tvDownVotes, Post post, UserPostRelation relation) {
        if(previousState == NOVOTE) {
            if(nextState == UPVOTE) {
                relation.setVote(UserPostRelation.UPVOTE);
                int newCount = post.getUpVotes() +  1;
                if (newCount >= 0) {
                    tvUpVotes.setText(String.valueOf(newCount));
                    post.setUpVotes(newCount);
                }
            }

            else if(nextState == DOWNVOTE) {
                relation.setVote(UserPostRelation.DOWNVOTE);
                int newCount = post.getDownVotes() +  1;
                if (newCount >= 0) {
                    tvDownVotes.setText(String.valueOf(newCount));
                    post.setDownVotes(newCount);
                }
            }
        }

        else if(previousState == UPVOTE) {
            if(nextState == NOVOTE) {
                //Remove the positive vote
                relation.setVote(UserPostRelation.NOVOTE);
                int newCount = post.getUpVotes() - 1;
                if (newCount >= 0) {
                    tvUpVotes.setText(String.valueOf(newCount));
                    post.setUpVotes(newCount);
                }
            }

            else if(nextState == DOWNVOTE) {
                relation.setVote(UserPostRelation.DOWNVOTE);
                int newCount = post.getUpVotes() - 1;
                if (newCount >= 0) {
                    tvUpVotes.setText(String.valueOf(newCount));
                    post.setUpVotes(newCount);
                }
                newCount = post.getDownVotes() + 1;
                if (newCount >= 0) {
                    tvDownVotes.setText(String.valueOf(newCount));
                    post.setDownVotes(newCount);
                }
            }
        }

        else if(previousState == DOWNVOTE) {
            if(nextState == UPVOTE) {
                relation.setVote(UserPostRelation.UPVOTE);
                int newCount = post.getUpVotes() + 1;
                if (newCount >= 0) {
                    tvUpVotes.setText(String.valueOf(newCount));
                    post.setUpVotes(newCount);
                }
                newCount = post.getDownVotes() - 1;
                if (newCount >= 0) {
                    tvDownVotes.setText(String.valueOf(newCount));
                    post.setDownVotes(newCount);
                }
            }

            else if(nextState == NOVOTE) {
                relation.setVote(UserPostRelation.NOVOTE);
                int newCount = post.getDownVotes() - 1;
                if (newCount >= 0) {
                    tvDownVotes.setText(String.valueOf(newCount));
                    post.setDownVotes(newCount);
                }
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
            if (newCount >= 0) {
                tvUpVotes.setText(String.valueOf(newCount));
                post.setUpVotes(newCount);
            }
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
            post.setDownVotes(newCount);

        }
    }


    /** Load like and dislike icons based on user interaction in view holder
     * @params: ViewHolder, Post
     * @return: void
     */
    public static void bindVoteContentOnLoad(final Context context, final Post post, final ParseUser user,
                                             final ImageButton upVoteImageButton, TextView upVoteText,
                                             final ImageButton downVoteImageButton, TextView downVoteText) {

        upVoteImageButton.setVisibility(View.VISIBLE);
        downVoteImageButton.setVisibility(View.VISIBLE);
        upVoteText.setText(String.valueOf(post.getUpVotes()));
        downVoteText.setText(String.valueOf(post.getDownVotes()));

        if (upVoteText.getText().toString().equals("0") && downVoteText.getText().toString().equals("0")) {
            setVoteButtonImages(upVoteImageButton, downVoteImageButton, R.drawable.outline_thumb_up_black_18dp,
                    R.drawable.outline_thumb_down_black_18dp);
        } else {
            final UserPostRelation.Query userPostRelation = new UserPostRelation.Query();
            userPostRelation.whereEqualTo("user", user);
            userPostRelation.whereEqualTo("post", post);
            userPostRelation.findInBackground(new FindCallback<UserPostRelation>() {
                @Override
                public void done(List<UserPostRelation> relation, ParseException e) {
                    try {
                        if (e == null) {
                            if (!relation.isEmpty() &&  relation.size() != 0) {
                                handleNoUserPostRelationOnLoad(upVoteImageButton, downVoteImageButton,
                                        relation.get(0));
                            }
                        } else {
                            setVoteButtonImages(upVoteImageButton, downVoteImageButton,
                                R.drawable.outline_thumb_up_black_18dp, R.drawable.outline_thumb_down_black_18dp);
                        }
                    } catch (Exception exception) {
                        Toast.makeText(context, "Error loading votes", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private static void setVoteButtonImages(ImageButton upVoteImageButton, ImageButton downVoteImageButton, int p, int p2) {
        upVoteImageButton.setImageResource(p);
        downVoteImageButton.setImageResource(p2);
    }

    private static void handleNoUserPostRelationOnLoad(ImageButton upVoteImageButton,
                                                       ImageButton downVoteImageButton,
                                                       UserPostRelation newUserPostRelation) {
        // there already exists a relation
        if(newUserPostRelation.getVote() == UserPostRelation.UPVOTE) {
            // set like to drawable to baseline up vote
            setVoteButtonImages(upVoteImageButton, downVoteImageButton,
                    R.drawable.baseline_thumb_up_black_18dp, R.drawable.outline_thumb_down_black_18dp);
        } else if(newUserPostRelation.getVote() == UserPostRelation.DOWNVOTE) {
            // set like to drawable to baseline up vote
            setVoteButtonImages(upVoteImageButton, downVoteImageButton,
                    R.drawable.outline_thumb_up_black_18dp, R.drawable.baseline_thumb_down_black_18dp);
        } else if(newUserPostRelation.getVote() == UserPostRelation.NOVOTE) {
            // set like to drawable to baseline up vote
            setVoteButtonImages(upVoteImageButton, downVoteImageButton,
                    R.drawable.outline_thumb_up_black_18dp, R.drawable.outline_thumb_down_black_18dp);
        }
    }

    /** Up Vote a post and update parse db
     * @params: ViewHolder, Post
     * @return: void
     */
    public static void setUpVoteClickFunctionality(final Context context, final Post post, final ParseUser user,
                                     final ImageButton ibLike, final TextView tvUpVote,
                                     final ImageButton ibDislike, final TextView tvDownVote) {

        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if there is an existing userPostRelation
                final UserPostRelation.Query userPostRelation = new UserPostRelation.Query();
                userPostRelation.whereEqualTo("user", user);
                userPostRelation.whereEqualTo("post", post);
                userPostRelation.findInBackground(new FindCallback<UserPostRelation>() {
                    @Override
                    public void done(List<UserPostRelation> relation, ParseException e) {
                        try {
                            if (e == null) {
                                if (relation.isEmpty()) {
                                    handleNoUserPostRelationOnClick(VoteSystemManager.UPVOTE_LISTENER,
                                            ibLike, post, tvUpVote, tvDownVote, user);
                                    Toast.makeText(context, "upvoted post", Toast.LENGTH_SHORT).show();
                                } else {
                                    handleExistingUserPostRelationOnClick(VoteSystemManager.UPVOTE_LISTENER,
                                            ibLike, ibDislike, post, relation.get(0), tvUpVote, tvDownVote);
                                }
                            }
                        } catch (Exception exception) {
                            Toast.makeText(context, "could not vote", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /** Up Vote a post and update parse db
     * @params: ViewHolder, Post
     * @return: void
     */
    public static void setDownVoteClickFunctionality(final Context context, final Post post, final ParseUser user,
                                                   final ImageButton ibLike, final TextView tvUpVote,
                                                   final ImageButton ibDislike, final TextView tvDownVote) {
        ibDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if there is an existing userPostRelation
                final UserPostRelation.Query userPostRelation = new UserPostRelation.Query();
                userPostRelation.whereEqualTo("user", user);
                userPostRelation.whereEqualTo("post", post);
                userPostRelation.findInBackground(new FindCallback<UserPostRelation>() {
                    @Override
                    public void done(List<UserPostRelation> relation, ParseException e) {
                        try {
                            if (e == null) {
                                if (relation.isEmpty()) {
                                    handleNoUserPostRelationOnClick(VoteSystemManager.DOWNVOTE_LISTENER,
                                            ibDislike, post, tvUpVote, tvDownVote, user);
                                    Toast.makeText(context, "downvoted post", Toast.LENGTH_SHORT).show();
                                } else {
                                    handleExistingUserPostRelationOnClick(VoteSystemManager.DOWNVOTE_LISTENER,
                                            ibLike, ibDislike, post, relation.get(0), tvUpVote, tvDownVote);
                                }
                            }
                        } catch (Exception exception) {
                            Toast.makeText(context, "could not vote", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private static void handleNoUserPostRelationOnClick(int listenerType, ImageButton button, Post post, TextView tvUpVotes,
                                                        TextView tvDownVotes, ParseUser user) {
        if (listenerType == VoteSystemManager.UPVOTE_LISTENER) {
            // set like to drawable to baseline up vote
            button.setImageResource(R.drawable.baseline_thumb_up_black_18dp);
            // update vote manager
            VoteSystemManager.manageVote(VoteSystemManager.UPVOTE,
                    tvUpVotes, tvDownVotes, user, post);
        } else if (listenerType == VoteSystemManager.DOWNVOTE_LISTENER) {
            // set like to drawable to baseline up vote
            button.setImageResource(R.drawable.baseline_thumb_down_black_18dp);
            // update vote manager
            VoteSystemManager.manageVote(VoteSystemManager.DOWNVOTE,
                    tvUpVotes, tvDownVotes, user, post);
        }

    }

    private static void handleExistingUserPostRelationOnClick(int listenerType, ImageButton ibLike, ImageButton ibDislike,
                                                       Post post, UserPostRelation newUserPostRelation,
                                                       TextView tvUpVotes, TextView tvDownVotes) {
        if (listenerType == VoteSystemManager.UPVOTE_LISTENER) {
            //If the user previously have liked the post
            if (newUserPostRelation.getVote() == UserPostRelation.UPVOTE) {
                // set like to drawable neutral up vote
                ibLike.setImageResource(R.drawable.outline_thumb_up_black_18dp);
                // update vote system manager
                VoteSystemManager.manageVote(VoteSystemManager.UPVOTE,
                        VoteSystemManager.NOVOTE, tvUpVotes, tvDownVotes,
                        post, newUserPostRelation);
            }
            //If the user had previously disliked the post
            else if (newUserPostRelation.getVote() == UserPostRelation.DOWNVOTE) {
                setVoteButtonImages(ibLike, ibDislike, R.drawable.baseline_thumb_up_black_18dp,
                        R.drawable.outline_thumb_down_black_18dp);
                // update vote system manager
                VoteSystemManager.manageVote(VoteSystemManager.DOWNVOTE,
                        VoteSystemManager.UPVOTE, tvUpVotes, tvDownVotes,
                        post, newUserPostRelation);
            }
            //If the user was neutral
            else if (newUserPostRelation.getVote() == UserPostRelation.NOVOTE) {
                // set like to drawable baseline up vote
                ibLike.setImageResource(R.drawable.baseline_thumb_up_black_18dp);
                // update vote system manager
                VoteSystemManager.manageVote(VoteSystemManager.NOVOTE,
                        VoteSystemManager.UPVOTE, tvUpVotes, tvDownVotes,
                        post, newUserPostRelation);
            }

        } else if (listenerType == VoteSystemManager.DOWNVOTE_LISTENER) {
            //If the user previously have liked the post
            if (newUserPostRelation.getVote() == UserPostRelation.UPVOTE) {
                // set dislike to drawable baseline down vote
                ibDislike.setImageResource(R.drawable.baseline_thumb_down_black_18dp);
                // set like to drawable neutral up vote
                ibLike.setImageResource(R.drawable.outline_thumb_up_black_18dp);
                // update vote manager
                VoteSystemManager.manageVote(VoteSystemManager.UPVOTE,
                        VoteSystemManager.DOWNVOTE, tvUpVotes, tvDownVotes,
                        post, newUserPostRelation);
            }
            //If the user had previously disliked the post
            else if (newUserPostRelation.getVote() == UserPostRelation.DOWNVOTE) {
                // set dislike to drawable neutral down vote
                ibDislike.setImageResource(R.drawable.outline_thumb_down_black_18dp);
                // update vote manager
                VoteSystemManager.manageVote(VoteSystemManager.DOWNVOTE,
                        VoteSystemManager.NOVOTE, tvUpVotes,
                        tvDownVotes, post, newUserPostRelation);
            }

            //If the user was neutral
            else if (newUserPostRelation.getVote() == UserPostRelation.NOVOTE) {
                // set dislike to drawable baseline down vote
                ibDislike.setImageResource(R.drawable.baseline_thumb_down_black_18dp);
                // update vote manager
                VoteSystemManager.manageVote(VoteSystemManager.NOVOTE,
                        VoteSystemManager.DOWNVOTE, tvUpVotes, tvDownVotes, post, newUserPostRelation);
            }

        }
    }

}
