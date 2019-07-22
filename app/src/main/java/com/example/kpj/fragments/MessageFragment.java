package com.example.kpj.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.kpj.R;
import com.example.kpj.model.Course;
import com.example.kpj.model.Message;
import com.example.kpj.model.UserCourseRelation;
import com.example.kpj.utils.MessageAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private RecyclerView rvMessages;
    private Button bSend;
    private EditText etMessage;

    private ArrayList<Message> messageArrayList;

    private MessageAdapter messageAdapter;

    //TODO: Set this variable dinamically
    private Course course;

    public MessageFragment() {
    }

    public static MessageFragment newInstance(int page) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_message, container, false);

        initializeObjects(view);
        prepareRecyclerView();
        populateRecyclerView();

        return view;
    }

    void initializeObjects(View view) {
        rvMessages = (RecyclerView) view.findViewById(R.id.rvMessages);
        bSend = (Button) view.findViewById(R.id.bSend);
        etMessage = (EditText) view.findViewById(R.id.etMessage);

        messageArrayList = new ArrayList<>();
    }

    void prepareRecyclerView() {
        messageAdapter = new MessageAdapter(getContext(), ParseUser.getCurrentUser().getUsername(), messageArrayList);
        rvMessages.setAdapter(messageAdapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    void populateRecyclerView() {

        final Course.Query courseQuery = new Course.Query();
        courseQuery.whereEqualTo("name", "math");
        courseQuery.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> objects, ParseException e) {
                course = objects.get(0);

                final Message.Query messageQuery = new Message.Query();
                messageQuery.whereEqualTo("course", course);
                messageQuery.findInBackground(new FindCallback<Message>() {
                    @Override
                    public void done(List<Message> objects, ParseException e) {
                        if(e == null){
                            for(int i = 0; i < objects.size(); i++){
                                Message message = objects.get(i);
                                messageArrayList.add(message);
                                messageAdapter.notifyItemInserted(messageArrayList.size() - 1);
                                Log.d("Size of list", "" + messageArrayList.size());
                                try {
                                    Log.d("MessageFragment", "Message:" + message.fetchIfNeeded().getString("description"));
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}
