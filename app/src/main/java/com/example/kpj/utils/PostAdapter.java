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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.PostByHashtagFilter;
import com.example.kpj.R;
import com.example.kpj.VoteSystemManager;
import com.example.kpj.activities.MainActivity;
import com.example.kpj.fragments.SendToChatDialogFragment;
import com.example.kpj.model.Course;
import com.example.kpj.model.ImagePreview;
import com.example.kpj.model.Post;
import com.example.kpj.model.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> filteredPosts;
    private List<Post> fullPostsList;
    private PostByHashtagFilter filter;
    private Course course;
    private OnPostClicked onPostClicked;
    private final static String KEY_SEND_POST_TO_CHAT = "A";
    private final static String KEY_SEND_COURSE_TO_CHAT = "B";

    public PostAdapter(Context context, Course course, List<Post> posts, OnPostClicked onPostClicked) {
        this.context = context;
        this.course = course;
        this.onPostClicked = onPostClicked;
        fullPostsList = new ArrayList<>();
        fullPostsList.addAll(posts);
        filteredPosts = posts;
        filteredPosts.addAll(posts);
        filter = new PostByHashtagFilter((ArrayList<Post>) fullPostsList, this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.post_item2, viewGroup, false);
        return new ViewHolder(postView, onPostClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = filteredPosts.get(position);
        bindPostContent(holder, post);
        VoteSystemManager.setUpVoteClickFunctionality(context, post, ParseUser.getCurrentUser(),
                holder.ibLike, holder.tvUpVotes, holder.ibDislike, holder.tvDownVotes);
        VoteSystemManager.setDownVoteClickFunctionality(context, post, ParseUser.getCurrentUser(),
                holder.ibLike, holder.tvUpVotes, holder.ibDislike, holder.tvDownVotes);
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
    private void bindPostContent(@NonNull final ViewHolder holder, final Post post) {
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
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(post.getSimpleDate());
        } catch (NullPointerException e) {
            holder.tvDate.setVisibility(View.GONE);
        }

        if (post.getDisplayHashTags().length() != 0) {
            holder.tvHashtag1.setVisibility(View.VISIBLE);
            holder.tvHashtag1.setText(post.getDisplayHashTags());
        } else {
            holder.tvHashtag1.setVisibility(View.GONE);
        }

        if(post.getPostLink() != null) {
            holder.hideLinkViews(false);
            holder.bindPostLinkAssets((Post) post.getPostLink());
        } else {
            holder.hideLinkViews(true);
        }

        VoteSystemManager.bindVoteContentOnLoad(context, post, ParseUser.getCurrentUser(), holder.ibLike,
                holder.tvUpVotes, holder.ibDislike, holder.tvDownVotes);

        holder.ibComment.setImageResource(R.drawable.outline_comment_black_18dp);
        holder.tvCommentCount.setText(String.valueOf(post.getNumComments()));
    }

    /** Bind the data base user info with user associated views of a post
     * @params: ViewHolder, Post
     * @return: void
     */
    private void bindPostUserAssets(@NonNull ViewHolder holder, Post post) {
        holder.tvUser.setText(post.getUser().getUsername());
        ParseFile profile = post.getUser().getParseFile(User.KEY_PROFILE);
        if (profile != null) {
            ImagePreview profilePreview = new ImagePreview(profile);
            profilePreview.loadImage(context, holder.ivProfile, new RequestOptions().circleCrop());
        }
    }

    @Override
    public int getItemCount() {
        return filteredPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile, ivPostImage, ivLinkIcon;
        TextView tvUser, tvDate, tvTitle, tvDescription, tvHashtag1, tvUpVotes,
                tvDownVotes, tvCommentCount, tvLinkUserName, tvLinkContent;
        ImageButton ibLike, ibDislike, ibComment, ibSend;
        OnPostClicked onPostClicked;
        LinearLayout postContainer, linkContainer;

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
            ivPostImage = itemView.findViewById(R.id.ivLinkImage);
            tvHashtag1 = itemView.findViewById(R.id.tvHashtag);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibDislike = itemView.findViewById(R.id.ibDislike);
            ibComment = itemView.findViewById(R.id.ibComment);
            ibSend = itemView.findViewById(R.id.ibSend);
            tvUpVotes = itemView.findViewById(R.id.tvUpVotes);
            tvDownVotes = itemView.findViewById(R.id.tvDownVotes);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            postContainer = itemView.findViewById(R.id.postContainer);
            setPostClickListener();
            // views for a post link
            ivLinkIcon = itemView.findViewById(R.id.ivItemLinkIcon);
            linkContainer = itemView.findViewById(R.id.linkContainer);
            tvLinkUserName = itemView.findViewById(R.id.tvLinkUserName);
            tvLinkContent = itemView.findViewById(R.id.tvLinkContent);
            hideLinkViews(true);
        }

        public void hideLinkViews(boolean makeHidden) {
            int viewState;
            if (makeHidden) {
                viewState = View.GONE;
            } else {
                viewState = View.VISIBLE;
            }
            ivLinkIcon.setVisibility(viewState);
            linkContainer.setVisibility(viewState);
            tvLinkUserName.setVisibility(viewState);
            tvLinkContent.setVisibility(viewState);
        }

        public void bindPostLinkAssets(Post link) {
            try {
                String linkUserName = "You are referencing post from " +
                        ((Post) link.fetchIfNeeded()).getUser().fetchIfNeeded().getUsername();
                tvLinkUserName.setText(linkUserName);
            } catch (ParseException e) {
                tvLinkUserName.setText("USER NOT FOUND");
                e.printStackTrace();
            }

            try {
                if (((Post) link.fetchIfNeeded()).getTitle() != null) {
                    tvLinkContent.setText(((Post) link.fetchIfNeeded()).getTitle());
                } else if (((Post) link.fetchIfNeeded()).getDescription() != null) {
                    tvLinkContent.setText(((Post) link.fetchIfNeeded()).getDescription());
                } else if (((Post) link.fetchIfNeeded()).getMedia() != null &&
                        ((Post) link.fetchIfNeeded()).getHasMedia()) {
                    tvLinkContent.setText("Post is an Image");
                } else {
                    tvLinkContent.setText(". . .");
                }
            } catch (ParseException e) {
                tvLinkUserName.setText("CONTENT LOADING ERROR");
                e.printStackTrace();
            }
        }

        private void setPostClickListener() {
            postContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPostClicked.onPostClickListener(getAdapterPosition());
                }
            });
        }

    }

    public interface OnPostClicked {
        void onPostClickListener(int position);
    }

    public void filterList(String s) {
        filter.filter(s);
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

    public void setOriginalList() {
        filteredPosts = fullPostsList;
    }

    public int getFullListSize() { return fullPostsList.size();}


}