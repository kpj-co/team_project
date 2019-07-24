package com.example.kpj.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.example.kpj.R;
import com.example.kpj.model.Course;
import com.example.kpj.model.Message;
import com.example.kpj.model.University;
import com.example.kpj.utils.MessageAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.parse.Parse.getApplicationContext;


public class MessageFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";
    private final static String PREF_NAME = "sharedData";
    private int mPage;
    private RecyclerView recyclerView;
    private Button sendButton;
    private EditText etMessage;

    private ArrayList<Message> messages = new ArrayList<>();

    private MessageAdapter messageAdapter;

    //TODO: Set this variable course dynamically
    private Course course;

    //TODO: Set this variable university dynamically
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
        findViews(view);
        setListeners(view);
        //TODO: Remove this function when you can grab universities dynamically
        hardcodedFunction();
        prepareRecyclerView();
        populateRecyclerView(getCurrentCourseName());
        setParseLiveQueryClient();

        return view;
    }

    void findViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.rvMessages);
        sendButton = (Button) view.findViewById(R.id.bSend);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
    }

    void prepareRecyclerView() {
        messageAdapter = new MessageAdapter(getContext(), ParseUser.getCurrentUser().getUsername(), messages);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    void setListeners(View view) {
        sendButton.setOnClickListener(new View.OnClickListener() {
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
            newMessage.setUniversity(university);
        }

        //Set the course of the user
        if(course != null) {
            newMessage.setCourse(course);
        }

        //Put the current user as the author of the message
        newMessage.setUser(ParseUser.getCurrentUser());

        //Clean the EditText
        etMessage.setText("");

        //Save the message in background
        newMessage.saveInBackground();
    }

    void populateRecyclerView(String courseName) {

        //NOTE: THIS query will not be needed in the full version. We should know in with course are we
        final Course.Query courseQuery = new Course.Query();
        courseQuery.whereEqualTo("name", courseName);
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
                                messages.add(message);
                                messageAdapter.notifyItemInserted(messages.size() - 1);
                                Log.d("Size of list", "" + messages.size());
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

    private String getCurrentCourseName() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return settings.getString("courseName", "");
    }

    private void setParseLiveQueryClient() {
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);


        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new
                SubscriptionHandling.HandleEventCallback<Message>() {
                    @Override
                    //Add the element in the beginning of the recycler view and go to that position
                    public void onEvent(ParseQuery<Message> query, Message message) {
                        messages.add(messages.size(), message);

                        // RecyclerView updates need to be run on the UI thread
                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(messages.size() - 1);
                            }
                        });
                    }
                });
    }
}
