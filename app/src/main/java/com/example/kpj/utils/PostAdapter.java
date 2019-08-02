package com.example.kpj.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.PostFilter;
import com.example.kpj.R;
import com.example.kpj.VoteSystemManager;
import com.example.kpj.activities.MainActivity;
import com.example.kpj.fragments.SendToChatDialogFragment;
import com.example.kpj.model.Course;
import com.example.kpj.model.ImagePreview;
import com.example.kpj.model.Post;
import com.example.kpj.model.User;
import com.example.kpj.model.UserPostRelation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> filteredPosts;
    private List<Post>fullPostsList;
    private PostFilter filter;
    private Course course;
    private OnPostClicked onPostClicked;
    private final static String KEY_SEND_POST_TO_CHAT = "A";
    private final static String KEY_SEND_COURSE_TO_CHAT = "B";
    private final ParseUser currentUser;
    private VoteSystemManager voteSystemManager;

    public PostAdapter(Context context, Course course, List<Post> posts, OnPostClicked onPostClicked) {
        this.context = context;
        this.course = course;
        this.onPostClicked = onPostClicked;
        fullPostsList = new ArrayList<>();
        fullPostsList.addAll(posts);
        filteredPosts = posts;
        filteredPosts.addAll(posts);
        currentUser = ParseUser.getCurrentUser();
        filter = new PostFilter((ArrayList<Post>) fullPostsList, this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.post_item, viewGroup, false);
        return new ViewHolder(postView, onPostClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = filteredPosts.get(position);
        bindPostContent(holder, post);
        post.isLiked = false;
        post.isDisliked = false;
        setUpUpVoteListener(holder, post);
        setUpDownVoteListener(holder, post);
        setUpIbSendListener(holder, post);
        setUpIbCommentListener(holder, post);
    }

    private void setUpIbCommentListener(final ViewHolder holder, Post post) {
        holder.ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go into detail activity of associated post
                onPostClicked.onPostClickListener(holder.getAdapterPosition());

                // TODO -- SCROLL TO THE COMMENT SECTION OF A POST
            }
        });
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
                bundle.putParcelable(KEY_SEND_COURSE_TO_CHAT, course);
                dialogBox.setArguments(bundle);
            }
        });
    }

    /** Bind the data base title, body, image info with associated post views
     * @params: ViewHolder, Post
     * @return: void
     */
    private void bindPostContent(@NonNull final ViewHolder holder, Post post) {
        //String that contains all the hashtags
        bindPostUserAssets(holder, post);

        if (post.getTitle() != null) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(post.getTitle());
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }

        if (post.getDescription() != null) {
            holder.tvDescription.setVisibility(View.VISIBLE);
            if (post.getDescription().length() < 250) {
                holder.tvDescription.setText(post.getDescription());
            } else {
                String abridgedText = post.getDescription().substring(0,250) + ". . .";
                holder.tvDescription.setText(abridgedText);
            }
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        if (post.getHasMedia()) {
            holder.ivPostImage.setVisibility(View.VISIBLE);
            ImagePreview image = new ImagePreview(post.getMedia());
            image.loadImage(context, holder.ivPostImage, new RequestOptions().centerCrop());
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }

        try {
            holder.tvDate.setText(post.getSimpleDate());
        } catch (NullPointerException e) {
            // do nothing
        }

        holder.tvUpVotes.setText(String.valueOf(post.getUpVotes()));
        holder.tvDownVotes.setText(String.valueOf(post.getDownVotes()));
        holder.tvCommentCount.setText(String.valueOf(post.getNumComments()));
        holder.tvHashtag1.setText(post.getDisplayHashTags());

    }

    /** Up Vote a post and update parse db
     * @params: ViewHolder, Post
     * @return: void
     */
    private void setUpUpVoteListener(final ViewHolder holder, final Post post) {
        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if there is an existing userPostRelation
                final UserPostRelation.Query userPostRelation = new UserPostRelation.Query();
                userPostRelation.whereEqualTo("user", currentUser);
                userPostRelation.whereEqualTo("post", post);
                userPostRelation.findInBackground(new FindCallback<UserPostRelation>() {
                    @Override
                    public void done(List<UserPostRelation> relation, ParseException e) {
                        if (e == null) {
                            // if there is none the list is empty or of length 0
                            if (relation.isEmpty() || relation.size() == 0) {
                                VoteSystemManager.manageVote(VoteSystemManager.UPVOTE, holder.tvUpVotes, holder.tvDownVotes, currentUser, post);
                                Toast.makeText(context, "upvoted post", Toast.LENGTH_SHORT).show();
                            } else { // there already exists a relation
                                // TODO -- check the state of the realtion
                                UserPostRelation newUserPostRelation = relation.get(0);
                                //If the user previously have liked the post
                                if(newUserPostRelation.getVote() == UserPostRelation.UPVOTE) {
                                    VoteSystemManager.manageVote(VoteSystemManager.UPVOTE, VoteSystemManager.NOVOTE, holder.tvUpVotes, holder.tvDownVotes, post, newUserPostRelation);
                                }

                                //If the user had previously disliked the post
                                else if(newUserPostRelation.getVote() == UserPostRelation.DOWNVOTE) {
                                    VoteSystemManager.manageVote(VoteSystemManager.DOWNVOTE, VoteSystemManager.UPVOTE, holder.tvUpVotes, holder.tvDownVotes, post, newUserPostRelation);
                                }

                                //If the user was neutral
                                else if(newUserPostRelation.getVote() == UserPostRelation.NOVOTE) {
                                    VoteSystemManager.manageVote(VoteSystemManager.NOVOTE, VoteSystemManager.UPVOTE, holder.tvUpVotes, holder.tvDownVotes, post, newUserPostRelation);
                                }
                            }
                        } else {
                            Toast.makeText(context, "could not vote", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                // check if there is an existing userPostRelation
                final UserPostRelation.Query userPostRelation = new UserPostRelation.Query();
                userPostRelation.whereEqualTo("user", currentUser);
                userPostRelation.whereEqualTo("post", post);
                userPostRelation.findInBackground(new FindCallback<UserPostRelation>() {
                    @Override
                    public void done(List<UserPostRelation> relation, ParseException e) {
                        if (e == null) {
                            // if there is none the list is empty or of length 0
                            if (relation.isEmpty() || relation.size() == 0) {
                                VoteSystemManager.manageVote(VoteSystemManager.DOWNVOTE, holder.tvUpVotes, holder.tvDownVotes, currentUser, post);

                                Toast.makeText(context, "downvoted post", Toast.LENGTH_SHORT).show();
                            } else { // there already exists a relation
                                // TODO -- check the state of the realtion
                                UserPostRelation newUserPostRelation = relation.get(0);
                                //If the user previously have liked the post
                                if(newUserPostRelation.getVote() == UserPostRelation.UPVOTE) {
                                    VoteSystemManager.manageVote(VoteSystemManager.UPVOTE, VoteSystemManager.DOWNVOTE, holder.tvUpVotes, holder.tvDownVotes, post, newUserPostRelation);
                                }

                                //If the user had previously disliked the post
                                else if(newUserPostRelation.getVote() == UserPostRelation.DOWNVOTE) {
                                    VoteSystemManager.manageVote(VoteSystemManager.DOWNVOTE, VoteSystemManager.NOVOTE, holder.tvUpVotes, holder.tvDownVotes, post, newUserPostRelation);
                                }

                                //If the user was neutral
                                else if(newUserPostRelation.getVote() == UserPostRelation.NOVOTE) {
                                    VoteSystemManager.manageVote(VoteSystemManager.NOVOTE, VoteSystemManager.DOWNVOTE, holder.tvUpVotes, holder.tvDownVotes, post, newUserPostRelation);
                                }
                            }
                        } else {
                            Toast.makeText(context, "could not vote", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
        return filteredPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivProfile, ivPostImage;
        TextView tvUser, tvDate, tvTitle, tvDescription, tvHashtag1, tvUpVotes,
                tvDownVotes, tvCommentCount;
        ImageButton ibLike, ibDislike, ibComment, ibSend;
        OnPostClicked onPostClicked;

        public ViewHolder(@NonNull View itemView, OnPostClicked onPostClicked) {
            super(itemView);
            initializeViews(itemView);
            this.onPostClicked = onPostClicked;
        }

        private void initializeViews(@NonNull View itemView) {
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvHashtag1 = itemView.findViewById(R.id.tvHashtag);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibDislike = itemView.findViewById(R.id.ibDislike);
            ibComment = itemView.findViewById(R.id.ibComment);
            ibSend = itemView.findViewById(R.id.ibSend);
            tvUpVotes = itemView.findViewById(R.id.tvUpVotes);
            tvDownVotes = itemView.findViewById(R.id.tvDownVotes);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            onPostClicked.onPostClickListener(getAdapterPosition());
        }
    }

    public interface OnPostClicked {
        void onPostClickListener(int position);
    }

    public void filterList(String s) {
        filter.getFilter().filter(s);
    }

    public void setList(List<Post> posts) {
        filteredPosts = posts;
    }

    public void updateFullList(List<Post> posts) {
        fullPostsList.addAll(posts);
        filter.updateFilter(posts);
    }

    public void clearFullList() {
        fullPostsList.clear();
        filter.clearFilter();
    }

}