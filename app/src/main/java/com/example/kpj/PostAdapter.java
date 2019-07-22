package com.example.kpj;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.model.Post;
import com.example.kpj.model.User;
import com.parse.ParseFile;

import java.io.File;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> mPosts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.post_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(postView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = mPosts.get(position);

        //populate the user associated views
        holder.tvUser.setText(post.getUser().getUsername());
        ParseFile profile = post.getUser().getParseFile(User.KEY_PROFILE);
        if (profile != null) {
            Glide.with(context)
                    .load(profile.getUrl())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.ivProfile);
        }

        //populate post title, body
        if (post.getTitle() != null) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(post.getTitle());
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }

        if (post.getDescription() != null) {
            holder.tvDiscription.setVisibility(View.VISIBLE);
            holder.tvDiscription.setText(post.getDescription());
        } else {
            holder.tvDiscription.setVisibility(View.GONE);
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

        //populate post likes, dislikes
        holder.tvUpVotes.setText(String.valueOf(post.getUpVotes()));
        holder.tvDownVotes.setText(String.valueOf(post.getDownVotes()));
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
        public TextView tvDiscription;
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
            tvDiscription = itemView.findViewById(R.id.tvDescription);
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
