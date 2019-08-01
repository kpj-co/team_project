package com.example.kpj.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
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
            view = inflater.inflate(R.layout.message_item, parent, false);
            return new normalMessageViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.message_post_version_item, parent, false);
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
        if(getItemViewType(position) == TYPE_NORMAL) {
            ((normalMessageViewHolder) viewHolder).setDetails(mMessages.get(position));
        } else {
            ((postMessageViewHolder) viewHolder).setDetails(mMessages.get(position));
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
            ivOtherUser = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            ivCurrentUser = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            body = (TextView)itemView.findViewById(R.id.tvBody);
            tvCurrentUserName = (TextView) itemView.findViewById(R.id.tvCurrentUserName);
            tvOtherUserName = (TextView) itemView.findViewById(R.id.tvAnotherUserName);

            itemView.setOnLongClickListener(this);
        }

        private void setDetails(Message message) {
            final boolean isCurrentUser = message.getUsername() != null && message.getUsername().equals(username);

            if (isCurrentUser) {
                ivCurrentUser.setVisibility(View.VISIBLE);
                ivOtherUser.setVisibility(View.GONE);
                body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

                //Change the text view states
                tvCurrentUserName.setVisibility(View.INVISIBLE);
                tvOtherUserName.setVisibility(View.VISIBLE);
                tvOtherUserName.setText(message.getUsername());

                Log.d("ME", username + " is current, the message  " + message.getUsername());
            } else {
                ivOtherUser.setVisibility(View.VISIBLE);
                ivCurrentUser.setVisibility(View.GONE);
                body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

                //Change the text view states
                tvOtherUserName.setVisibility(View.INVISIBLE);
                tvCurrentUserName.setVisibility(View.VISIBLE);
                tvCurrentUserName.setText(message.getUsername());

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
            ivOtherUser = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            ivCurrentUser = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            ivPostImage = (ImageView) itemView.findViewById(R.id.ivPostImage);
            tvCurrentUserName = (TextView) itemView.findViewById(R.id.tvCurrentUserName);
            tvOtherUserName = (TextView) itemView.findViewById(R.id.tvAnotherUserName);
            tvPostTitle = (TextView) itemView.findViewById(R.id.tvPostTitle);
            tvPostDescription = (TextView) itemView.findViewById(R.id.tvPostDescription);
            tvUserOpinion = (TextView) itemView.findViewById(R.id.tvUserOpinion);
            this.onMessageClicked = onMessageClicked;
            itemView.setOnClickListener(this);
        }

        private void setDetails(Message message) {
            final boolean isCurrentUser = message.getUsername() != null && message.getUsername().equals(username);
            Post post = (Post) message.getPost();
            if (isCurrentUser) {
                ivCurrentUser.setVisibility(View.VISIBLE);
                ivOtherUser.setVisibility(View.GONE);

                //Change the text view states
                tvCurrentUserName.setVisibility(View.INVISIBLE);
                tvOtherUserName.setVisibility(View.VISIBLE);
                tvOtherUserName.setText(message.getUsername());

                Log.d("ME", username + " is current, the message  " + message.getUsername());
            } else {
                ivOtherUser.setVisibility(View.VISIBLE);
                ivCurrentUser.setVisibility(View.GONE);

                //Change the text view states
                tvOtherUserName.setVisibility(View.INVISIBLE);
                tvCurrentUserName.setVisibility(View.VISIBLE);
                tvCurrentUserName.setText(message.getUsername());

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

            try {
                tvPostTitle.setText(((Post)post.fetchIfNeeded()).getTitle());
                tvPostDescription.setText(((Post)post.fetchIfNeeded()).getDescription());
                tvUserOpinion.setText(((Message)message.fetchIfNeeded()).getDescription());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (post.getHasMedia()) {
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
