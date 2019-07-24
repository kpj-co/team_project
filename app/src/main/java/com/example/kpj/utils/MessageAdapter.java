package com.example.kpj.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

import java.math.BigInteger;
import java.security.MessageDigest;
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
            holder.tvUsername.setGravity(Gravity.CENTER );
        } else {
            holder.imageOther.setVisibility(View.VISIBLE);
            holder.imageMe.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            holder.tvUsername.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
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
        holder.tvUsername.setText(message.getUsername());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOther;
        ImageView imageMe;
        TextView body;
        TextView tvUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            imageMe = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            body = (TextView)itemView.findViewById(R.id.tvBody);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
        }
    }


}
