package com.example.kpj.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.R;
import com.example.kpj.model.Comment;
import com.example.kpj.model.Post;
import com.example.kpj.model.User;
import com.parse.ParseFile;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context context;
    Post post;
    List<Comment> mComments;

    public CommentAdapter(Context context, Post post, List<Comment> comments) {
        this.context = context;
        this.post = post;
        this.mComments = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View commentView = inflater.inflate(R.layout.comment_item, viewGroup, false);
        return new ViewHolder(commentView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        final Comment comment = mComments.get(position);
        bindPostContent(holder, comment);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    private void bindPostContent(@NonNull CommentAdapter.ViewHolder holder, Comment comment) {
        if (comment.getUser().getParseFile(User.KEY_PROFILE) != null) {
            holder.ivCommentProfilePic.setVisibility(View.VISIBLE);
            ParseFile photoFile = comment.getUser().getParseFile(User.KEY_PROFILE);
            Glide.with(context)
                    .load(photoFile.getUrl())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.ivCommentProfilePic);
        } else {
            holder.ivCommentProfilePic.setColorFilter(Color.DKGRAY);
        }
        holder.tvCommentUsername.setText(comment.getUser().getUsername());
        holder.tvCommentBody.setText(comment.getDescription());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCommentProfilePic;
        TextView tvCommentUsername, tvCommentBody;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCommentProfilePic = itemView.findViewById(R.id.ivCommentProfilePic);
            tvCommentUsername = itemView.findViewById(R.id.tvCommentUsername);
            tvCommentBody = itemView.findViewById(R.id.tvCommentBody);
        }
    }


}
