package com.example.kpj.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.R;
import com.example.kpj.RecyclerViewClickListener;
import com.example.kpj.model.ImagePreview;
import com.example.kpj.model.Message;
import com.example.kpj.model.Post;
import com.example.kpj.model.PostImageRelation;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Values for the different types of messages(different views)
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_POST = 2;

    private List<Message> mMessages;
    private Context mContext;
    private String username;
    private OnMessageClicked onMessageClicked;

    private static RecyclerViewClickListener itemListener;

    public MessageAdapter(Context context, String username, List<Message> messages,
                          RecyclerViewClickListener itemListener,
                          MessageAdapter.OnMessageClicked onMessageClicked) {
        mMessages = messages;
        this.username = username;
        mContext = context;
        this.onMessageClicked = onMessageClicked;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if(viewType == TYPE_NORMAL) {
            view = inflater.inflate(R.layout.message_item2, parent, false);
            return new normalMessageViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.messge_post_version_item2, parent, false);
            return new postMessageViewHolder(view, onMessageClicked);
        }
    }

    //TODO: IMPLEMENT THIS METHOD
    @Override
    public int getItemViewType(int position) {
        //If there is no post
        if(mMessages.get(position).getPost() == null) {
            return TYPE_NORMAL;
        } else {
            return TYPE_POST;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        try {
            if(getItemViewType(position) == TYPE_NORMAL) {
                ((normalMessageViewHolder) viewHolder).setDetails(mMessages.get(position));
            } else {
                ((postMessageViewHolder) viewHolder).setDetails(mMessages.get(position));
            }
        } catch (ParseException e) {
            Toast.makeText(mContext, "can not load messages", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class normalMessageViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        ImageView ivOtherUser;
        ImageView ivCurrentUser;
        TextView body;
        TextView tvCurrentUserName;
        TextView tvOtherUserName;

        public normalMessageViewHolder(View itemView) {
            super(itemView);
            ivOtherUser = itemView.findViewById(R.id.ivProfileOther);
            ivCurrentUser = itemView.findViewById(R.id.ivProfileMe);
            tvCurrentUserName = itemView.findViewById(R.id.tvMyUsername);
            tvOtherUserName = itemView.findViewById(R.id.tvAnotherUsername);
            body = itemView.findViewById(R.id.tvBody);
            itemView.setOnLongClickListener(this);
        }

        private void setDetails(Message message) {
            final boolean isCurrentUser = message.getUsername() != null && message.getUsername().equals(username);
            Post post = (Post) message.getPost();
            if (isCurrentUser) {
                ivCurrentUser.setVisibility(View.VISIBLE);
                ivOtherUser.setVisibility(View.GONE);
                tvOtherUserName.setVisibility(View.GONE);
                // set username of current
                tvCurrentUserName.setText(message.getUsername());
                Log.d("ME", username + " is current, the message  " + message.getUsername());
            } else {
                ivOtherUser.setVisibility(View.VISIBLE);
                ivCurrentUser.setVisibility(View.GONE);
                tvCurrentUserName.setVisibility(View.GONE);
                // set username of other
                tvOtherUserName.setText(message.getUsername());
                Log.d("OTHER", username + " is current, the message  " + message.getUsername());
            }

            final ImageView profileView = isCurrentUser ? ivCurrentUser : ivOtherUser;

            if(message.getParseFileUserImage() != null) {
                Glide.with(mContext)
                        .load(message.getParseFileUserImage().getUrl())
                        .apply(new RequestOptions().circleCrop())
                        .into(profileView);
            }

            else {
                try {
                    message.setUserPhoto();
                    message.setUserUsername();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            body.setText(message.getDescription());
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(mContext, "Long click", Toast.LENGTH_LONG).show();
            Log.d("MessageAdapter", "Executed long click");
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());

            //indicate that the click has handled
            return true;
        }
    }



    public class postMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivOtherUser;
        ImageView ivCurrentUser;
        ImageView ivPostImage;
        TextView tvCurrentUserName;
        TextView tvOtherUserName;
        TextView tvPostTitle;
        TextView tvPostDescription;
        TextView tvUserOpinion;
        MessageAdapter.OnMessageClicked onMessageClicked;

        public postMessageViewHolder(@NonNull View itemView, MessageAdapter.OnMessageClicked onMessageClicked) {
            super(itemView);
            ivOtherUser = itemView.findViewById(R.id.ivProfileOther);
            ivCurrentUser = itemView.findViewById(R.id.ivProfileMe);
            tvCurrentUserName = itemView.findViewById(R.id.tvMyUsername);
            tvOtherUserName =  itemView.findViewById(R.id.tvAnotherUsername);
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvPostDescription = itemView.findViewById(R.id.tvPostDescription);
            tvUserOpinion = itemView.findViewById(R.id.tvUserOpinion);
            this.onMessageClicked = onMessageClicked;
            itemView.setOnClickListener(this);
        }

        private void setDetails(Message message) throws ParseException {
            final boolean isCurrentUser = message.getUsername() != null && message.getUsername().equals(username);
            Post post = (Post) message.getPost();
            if (isCurrentUser) {
                ivCurrentUser.setVisibility(View.VISIBLE);
                ivOtherUser.setVisibility(View.GONE);
                tvOtherUserName.setVisibility(View.GONE);
                // set username of current
                tvCurrentUserName.setText(message.getUsername());
                Log.d("ME", username + " is current, the message  " + message.getUsername());
            } else {
                ivOtherUser.setVisibility(View.VISIBLE);
                ivCurrentUser.setVisibility(View.GONE);
                tvCurrentUserName.setVisibility(View.GONE);
                // set username of other
                tvOtherUserName.setText(message.getUsername());
                Log.d("OTHER", username + " is current, the message  " + message.getUsername());
            }

            final ImageView profileView = isCurrentUser ? ivCurrentUser : ivOtherUser;

            if(message.getParseFileUserImage() != null) {
                Glide.with(mContext)
                        .load(message.getParseFileUserImage().getUrl())
                        .apply(new RequestOptions().circleCrop())
                        .into(profileView);
            }

            else {
                try {
                    message.setUserPhoto();
                    message.setUserUsername();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            bindPostLinkText(tvPostTitle, ((Post) post.fetchIfNeeded()).getTitle());
            bindPostLinkText(tvPostDescription, ((Post) post.fetchIfNeeded()).getTitle());
            bindPostLinkText(tvUserOpinion, ((Message)message.fetchIfNeeded()).getDescription());

            if (post.getHasMedia()) {
                ivPostImage.setVisibility(View.VISIBLE);
                PostImageRelation.Query query = new PostImageRelation.Query();
                query.whereEqualTo("post", post);
                query.orderByDescending("createdAt");
                query.findInBackground(new FindCallback<PostImageRelation>() {
                    @Override
                    public void done(List<PostImageRelation> relations, ParseException e) {
                        if (e == null) {
                            ImagePreview image = new ImagePreview((relations.get(0)).getImage());
                            image.loadImage(mContext, ivPostImage);
                        }
                    }
                });
            } else {
                ivPostImage.setVisibility(View.GONE);
            }

        }

        private void bindPostLinkText(TextView textView, String text) {
            try {
                if (text != null) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(text);
                } else {
                    textView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                textView.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "link was clicked", Toast.LENGTH_SHORT).show();
            onMessageClicked.onMessageClicked(getAdapterPosition());
        }
    }


    public interface OnMessageClicked {
        void onMessageClicked(int position);
    }
}
