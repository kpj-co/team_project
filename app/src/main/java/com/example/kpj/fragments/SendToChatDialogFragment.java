package com.example.kpj.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kpj.FragmentCommunication;
import com.example.kpj.R;
import com.example.kpj.model.Post;
import com.parse.ParseObject;

public class SendToChatDialogFragment extends DialogFragment {

    private final static String KEY_SEND_POST_TO_CHAT = "A";
    private Post post;
    private String postId;
    EditText etSendMessage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Toast.makeText(getContext(), "discuss this post on chat", Toast.LENGTH_SHORT).show();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // get layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        retrievePostId();
        builder.setView(inflater.inflate(R.layout.dialog_send_to_chat, null))
        // add action buttons
                .setMessage("Your comments are sent to chat")
                .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO -- send comment to chat
                        // TODO -- save to parse
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // return to feed
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etSendMessage = view.findViewById(R.id.etSendToChatMessage);

    }

    private void retrievePostId() {
        // get postId
        Bundle bundle = this.getArguments();
        post = bundle.getParcelable(KEY_SEND_POST_TO_CHAT);
        //Toast.makeText(getContext(), post.getObjectId(), Toast.LENGTH_SHORT).show();
    }


}
