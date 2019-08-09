package com.example.kpj.utils;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.R;
import com.example.kpj.RecyclerViewClickListener;
import com.example.kpj.model.ImagePreview;
import com.example.kpj.model.Message;
import com.example.kpj.model.Post;
import com.example.kpj.model.User;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

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
            view = inflater.inflate(R.layout.messge_link_version_item, parent, false);
            return new linkMessageViewHolder(view, onMessageClicked);
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
                ((linkMessageViewHolder) viewHolder).setDetails(mMessages.get(position));
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
        ImageView ivOtherUser, ivCurrentUser;
        TextView body, tvCurrentUserName, tvOtherUserName;

        public normalMessageViewHolder(View itemView) {
            super(itemView);
            ivOtherUser = itemView.findViewById(R.id.ivProfileOther);
            ivCurrentUser = itemView.findViewById(R.id.ivProfileMe);
            tvCurrentUserName = itemView.findViewById(R.id.tvMyUsername);
            tvOtherUserName = itemView.findViewById(R.id.tvAnotherUsername);
            body = itemView.findViewById(R.id.tvBody);
            body.setOnLongClickListener(this);
        }

        private void setDetails(Message message) {
            String senderName;
            ParseFile profilePic = null;
            try {
                senderName = message.getUsername();
                profilePic = message.getUser().fetchIfNeeded().getParseFile(User.KEY_PROFILE);
            } catch (ParseException e) {
                senderName = "USER NOT FOUND";
            }

            boolean isCurrentUser = senderName != null &&
                    senderName.equals(ParseUser.getCurrentUser().getUsername());
            if (isCurrentUser) {
                ivOtherUser.setVisibility(View.INVISIBLE);
                tvOtherUserName.setVisibility(View.INVISIBLE);
                // set sender name to current user
                ivCurrentUser.setVisibility(View.VISIBLE);
                tvCurrentUserName.setText(senderName);
            } else {
                ivCurrentUser.setVisibility(View.INVISIBLE);
                tvCurrentUserName.setVisibility(View.INVISIBLE);
                // set username of other
                ivOtherUser.setVisibility(View.VISIBLE);
                tvOtherUserName.setText(senderName);
            }

            ImageView profileView = isCurrentUser ? ivCurrentUser : ivOtherUser;

            if(profilePic != null) {
                ImagePreview profileImg = new ImagePreview(profilePic);
                profileImg.loadImage(mContext, profileView, new RequestOptions().circleCrop());
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

    /***
     * View Holder for a message with link to a post
     */
    public class linkMessageViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        ImageView ivOtherUser, ivCurrentUser, ivPostImage;
        TextView tvCurrentUserName, tvOtherUserName, tvPostTitle, tvPostDescription, tvUserOpinion;
        LinearLayout postLinkContainer;
        MessageAdapter.OnMessageClicked onMessageClicked;

        public linkMessageViewHolder(@NonNull View itemView, MessageAdapter.OnMessageClicked onMessageClicked) {
            super(itemView);
            ivOtherUser = itemView.findViewById(R.id.ivProfileOther);
            ivCurrentUser = itemView.findViewById(R.id.ivProfileMe);
            tvCurrentUserName = itemView.findViewById(R.id.tvMyUsername);
            tvOtherUserName =  itemView.findViewById(R.id.tvAnotherUsername);
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvPostDescription = itemView.findViewById(R.id.tvPostDescription);
            tvUserOpinion = itemView.findViewById(R.id.tvUserOpinion);
            postLinkContainer = itemView.findViewById(R.id.postLinkContainer);
            this.onMessageClicked = onMessageClicked;
            setPostLinkListener();
        }

        private void setPostLinkListener() {
            tvUserOpinion.setOnLongClickListener(this);
            postLinkContainer.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(mContext, "Long click", Toast.LENGTH_LONG).show();
            Log.d("MessageAdapter", "Executed long click");
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());

            //indicate that the click has handled
            return true;
        }

        private void setDetails(Message message) throws ParseException {
            String senderName;
            Post post = (Post) message.getPost();
            ParseFile profilePic = null;
            try {
                senderName = message.getUsername();
                profilePic = message.getUser().fetchIfNeeded().getParseFile(User.KEY_PROFILE);
            } catch (ParseException e) {
                senderName = "USER NOT FOUND";
            }

            boolean isCurrentUser = senderName != null &&
                    senderName.equals(ParseUser.getCurrentUser().getUsername());
            if (isCurrentUser) {
                ivOtherUser.setVisibility(View.INVISIBLE);
                tvOtherUserName.setVisibility(View.INVISIBLE);
                // set sender name to current user
                ivCurrentUser.setVisibility(View.VISIBLE);
                tvCurrentUserName.setText(ParseUser.getCurrentUser().getUsername());
            } else {
                ivCurrentUser.setVisibility(View.INVISIBLE);
                tvCurrentUserName.setVisibility(View.INVISIBLE);
                // set username of other
                ivOtherUser.setVisibility(View.VISIBLE);
                tvOtherUserName.setText(senderName);
            }

            ImageView profileView = isCurrentUser ? ivCurrentUser : ivOtherUser;

            if(profilePic != null) {
                ImagePreview profileImg = new ImagePreview(profilePic);
                profileImg.loadImage(mContext, profileView, new RequestOptions().circleCrop());
            }

            bindPostLinkText(tvPostTitle, ((Post) post.fetchIfNeeded()).getTitle());
            bindPostLinkText(tvPostDescription, ((Post) post.fetchIfNeeded()).getTitle());
            bindPostLinkText(tvUserOpinion, ((Message)message.fetchIfNeeded()).getDescription());

            if (post.getHasMedia()) {
                Toast.makeText(mContext, "post link has pic", Toast.LENGTH_SHORT).show();
                ivPostImage.setVisibility(View.VISIBLE);
                ImagePreview image = new ImagePreview(post.getMedia());
                Toast.makeText(mContext, post.getMedia().getUrl(), Toast.LENGTH_SHORT).show();
                image.loadImage(mContext, ivPostImage);
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

    }

    public interface OnMessageClicked {
        void onMessageClicked(int position);
    }
}
