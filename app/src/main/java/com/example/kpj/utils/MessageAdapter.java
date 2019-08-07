package com.example.kpj.utils;

import android.content.Context;
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
                if (message.getUsername() == null || message.getUsername().length() == 0) {
                    tvCurrentUserName.setText("USER NOT FOUND");
                } else {
                    tvCurrentUserName.setText(message.getUsername());
                }
                Log.d("ME", username + " is current, the message  " + message.getUsername());
            } else {
                ivOtherUser.setVisibility(View.VISIBLE);
                ivCurrentUser.setVisibility(View.GONE);
                tvCurrentUserName.setVisibility(View.GONE);
                // set username of other
                if (message.getUsername() == null || message.getUsername().length() == 0) {
                    tvOtherUserName.setText("USER NOT FOUND");
                } else {
                    tvOtherUserName.setText(message.getUsername());
                }
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

    /***
     * View Holder for a message with link to a post
     */
    public class linkMessageViewHolder extends RecyclerView.ViewHolder {
//  public class linkMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
//          itemView.setOnClickListener(this);
        }

        private void setPostLinkListener() {
            postLinkContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "link was clicked", Toast.LENGTH_SHORT).show();
                    onMessageClicked.onMessageClicked(getAdapterPosition());
                }
            });
        }

        private void setDetails(Message message) throws ParseException {
            final boolean isCurrentUser = message.getUsername() != null && message.getUsername().equals(username);
            Post post = (Post) message.getPost();
            if (isCurrentUser) {
                ivCurrentUser.setVisibility(View.VISIBLE);
                ivOtherUser.setVisibility(View.GONE);
                tvOtherUserName.setVisibility(View.GONE);
                // set username of current
                if (message.getUsername() == null || message.getUsername().length() == 0) {
                    tvCurrentUserName.setText("USER NOT FOUND");
                } else {
                    tvCurrentUserName.setText(message.getUsername());
                }
                Log.d("ME", username + " is current, the message  " + message.getUsername());
            } else {
                ivOtherUser.setVisibility(View.VISIBLE);
                ivCurrentUser.setVisibility(View.GONE);
                tvCurrentUserName.setVisibility(View.GONE);
                // set username of other
                if (message.getUsername() == null || message.getUsername().length() == 0) {
                    tvOtherUserName.setText("USER NOT FOUND");
                } else {
                    tvOtherUserName.setText(message.getUsername());
                }
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
                ImagePreview image = new ImagePreview(post.getMedia());
                image.loadImage(mContext, ivPostImage);;
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

//        @Override
//        public void onClick(View v) {
//            Toast.makeText(mContext, "link was clicked", Toast.LENGTH_SHORT).show();
//            onMessageClicked.onMessageClicked(getAdapterPosition());
//        }
    }

    public interface OnMessageClicked {
        void onMessageClicked(int position);
    }
}
