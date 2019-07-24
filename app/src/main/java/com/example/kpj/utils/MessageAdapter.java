package com.example.kpj.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.R;
import com.example.kpj.model.Message;
import com.parse.ParseException;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessages;
    private Context mContext;
    private String username;

    public MessageAdapter(Context context, String username, List<Message> messages) {
        mMessages = messages;
        this.username = username;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.message_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        final boolean isMe = message.getUsername() != null && message.getUsername().equals(username);

        if (isMe) {
            holder.imageMe.setVisibility(View.VISIBLE);
            holder.imageOther.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            //Change the text view states
            holder.tvCurrentUserName.setVisibility(View.INVISIBLE);
            holder.tvOtherUserName.setVisibility(View.VISIBLE);
            holder.tvOtherUserName.setText(message.getUsername());

            Log.d("ME", username + " is current, the message  " + message.getUsername());
        } else {
            holder.imageOther.setVisibility(View.VISIBLE);
            holder.imageMe.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

            //Change the text view states
            holder.tvOtherUserName.setVisibility(View.INVISIBLE);
            holder.tvCurrentUserName.setVisibility(View.VISIBLE);
            holder.tvCurrentUserName.setText(message.getUsername());

            Log.d("OTHER", username + " is current, the message  " + message.getUsername());
        }

        final ImageView profileView = isMe ? holder.imageMe : holder.imageOther;

        if(message.getParseFileUserImage() != null) {
            Glide.with(mContext)
                    .load(message.getParseFileUserImage().getUrl())
                    .apply(new RequestOptions().centerCrop())
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
        holder.body.setText(message.getDescription());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOther;
        ImageView imageMe;
        TextView body;
        TextView tvCurrentUserName;
        TextView tvOtherUserName;

        public ViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            imageMe = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            body = (TextView)itemView.findViewById(R.id.tvBody);
            tvCurrentUserName = (TextView) itemView.findViewById(R.id.tvCurrentUserName);
            tvOtherUserName = (TextView) itemView.findViewById(R.id.tvAnotherUserName);
        }
    }
}
