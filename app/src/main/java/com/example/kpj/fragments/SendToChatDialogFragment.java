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

import com.example.kpj.R;
import com.example.kpj.model.Course;
import com.example.kpj.model.Message;
import com.example.kpj.model.Post;
import com.parse.ParseUser;

public class SendToChatDialogFragment extends DialogFragment {

    private final static String KEY_SEND_POST_TO_CHAT = "A";
    private final static String KEY_SEND_COURSE_TO_CHAT = "B";
    private Post post;
    private Course course;
    EditText etSendMessage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Toast.makeText(getContext(), "discuss this post on chat", Toast.LENGTH_SHORT).show();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // get layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        retrieveBundles();
        // set view
        View view = inflater.inflate(R.layout.dialog_send_to_chat, null);
        etSendMessage = view.findViewById(R.id.etSendToChatMessage);
        builder.setView(view)
        // add action buttons
                .setMessage("Your comments are sent to chat")
                .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String newDescription = etSendMessage.getText().toString();
                        saveMessage(newDescription, post);
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

    private void saveMessage(String newDescription, Post post) {
        Toast.makeText(getContext(), newDescription, Toast.LENGTH_LONG).show();
        Message newMessage = new Message();
        if (newDescription != null && post != null && course != null) {
            newMessage.setUser(ParseUser.getCurrentUser());
            newMessage.setDescription(newDescription);
            newMessage.setPost(post);
            newMessage.setCourse(course);
            newMessage.saveInBackground();
            Toast.makeText(getContext(), "Message sent to chat", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "ERROR: Unable to send message", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void retrieveBundles() {
        // get postId
        Bundle bundle = this.getArguments();
        post = bundle.getParcelable(KEY_SEND_POST_TO_CHAT);
        // get course
        course = bundle.getParcelable(KEY_SEND_COURSE_TO_CHAT);
        Toast.makeText(getContext(), post.getObjectId(), Toast.LENGTH_SHORT).show();
    }


}
