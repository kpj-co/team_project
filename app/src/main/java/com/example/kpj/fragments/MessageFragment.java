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
import com.example.kpj.model.University;
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

    //TODO: Set this variable course dinamically
    private Course course;

    //TODO: Set this variable university dinamically
    private University university;

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
        setListeners(view);
        //TODO: Remove this function when you can grab universities dynamically
        hardcodedFunction();
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

    void setListeners(View view) {
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the text of the message EditText
                String message = etMessage.getText().toString();

                //If there is something in the message edittext
                if(!message.equals("")) {
                    //Send it to the database
                    pushMessageToDatabase(message);
                }
            }
        });
    }

    void pushMessageToDatabase(String message) {
        Message newMessage = new Message();

        //set the message body of the current user
        newMessage.setDescription(message);

        //set the university of the current user
        if(university != null) {

        }

        

        //Save the message in background


        /**
         *         Post newPost = new Post();
         *         // Grab text content from compose
         *         String newTitle = etComposePostTitle.getText().toString();
         *         String newBody = etComposeBody.getText().toString();
         *         // Set user of post
         *         newPost.setUser(ParseUser.getCurrentUser());
         *         // Set content of post
         *         if (newTitle.length() != 0) {
         *             newPost.setTitle(newTitle);
         *         }
         *         if (newBody.length() != 0) {
         *             newPost.setDescription(newBody);
         *         }
         *
         *         if (imagePath.length() != 0) {
         *             File imageFile = new File(imagePath);
         *             if (imageFile != null) {
         *                 ParseFile imageParseFile = new ParseFile(imageFile);
         *                 newPost.setMedia(imageParseFile);
         *             }
         *         }
         *
         *         if (photoFile != null) {
         *             ParseFile imageParseFile = new ParseFile(photoFile);
         *             newPost.setMedia(imageParseFile);
         *         }
         *
         *         // Setup vote count
         *         newPost.setUpVotes(0);
         *         newPost.setDownVotes(0);
         *         // Save post in background thread
         *         newPost.saveInBackground();
         */
    }

    void populateRecyclerView() {

        //NOTE: THIS query will not be needed in the full version. We should know in with course are we
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

    //TODO: Remove this when you can set it dynamically
    void hardcodedFunction() {

        final University.Query universityQuery = new University.Query();
        universityQuery.whereEqualTo("name", "Facebook University");
        universityQuery.findInBackground(new FindCallback<University>() {
            @Override
            public void done(List<University> objects, ParseException e) {
                if(e == null){
                    University university = objects.get(0);
                    Log.d("MessageFragment", university.getName());
                }
            }
        });
    }
}
