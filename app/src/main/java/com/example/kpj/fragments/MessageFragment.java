package com.example.kpj.fragments;

import android.app.Activity;
import android.content.Intent;
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
import com.example.kpj.RecyclerViewClickListener;
import com.example.kpj.activities.ComposePostActivity;
import com.example.kpj.activities.PostDetailActivity;
import com.example.kpj.model.Course;
import com.example.kpj.model.Message;
import com.example.kpj.model.Post;
import com.example.kpj.model.University;
import com.example.kpj.model.User;
import com.example.kpj.utils.EndlessRecyclerViewScrollListener;
import com.example.kpj.utils.MessageAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.parse.Parse.getApplicationContext;

public class MessageFragment extends Fragment implements RecyclerViewClickListener {
    private static final String ARG_PAGE = "ARG_PAGE";
    private final static String PREF_NAME = "sharedData";
    private int mPage;
    private RecyclerView recyclerView;
    private Button sendButton;
    private EditText etMessage;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private LinearLayoutManager linearLayoutManager;
    //boolean that indicates if the liveQuery has been set
    private boolean isQueryLiveset = false;
    private List<Message> messages = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private Course course;
    private University university;
    private ParseUser userMessage;
    private ParseUser currentUser;
    String username;

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
        initializeVariables();
        findViews(view);
        setListeners(view);
        prepareRecyclerView();
        setEndlessRecyclerViewScrollListener();
        setSharedObjects();
        return view;
    }

    private void initializeVariables() {
        this.currentUser = ParseUser.getCurrentUser();
        this.userMessage = null;
    }

    //Gets the course and university from sharedPreference. Once this is done the recycler view will be populated
    private void setSharedObjects() {
        String universityName = getUserUniveristy();
        final String courseName = getCurrentCourseName();

        //First find the university of the user
        final University.Query universityQuery = new University.Query();
        universityQuery.whereEqualTo("name", universityName);
        universityQuery.getFirstInBackground(new GetCallback<University>() {
            @Override
            public void done(University object, ParseException e) {
                university = object;
                //Then find the course of that university
                final Course.Query courseQuery = new Course.Query();
                courseQuery.whereEqualTo("name", courseName);
                courseQuery.whereEqualTo(Course.KEY_UNIVERSITY, university);
                courseQuery.getFirstInBackground(new GetCallback<Course>() {
                    @Override
                    public void done(Course object, ParseException e) {
                        course = object;
                        //Just AFTER doing that for the first time, populate the recycler view
                            populateRecyclerView(true);
                    }
                });
            }
        });
    }

    void findViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.rvMessages);
        sendButton = (Button) view.findViewById(R.id.bSend);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
    }

    void prepareRecyclerView() {
        messageAdapter = new MessageAdapter(getContext(), currentUser.getUsername(),
                messages, this, new MessageAdapter.OnMessageClicked() {
            @Override
            public void onMessageClicked(int position) {
                Post post = (Post) messages.get(position).getPost();
                Intent intent = new Intent(getContext(), PostDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("post", post);
                bundle.putStringArrayList("postHashTags", (ArrayList<String>) post.getHashtags());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(messageAdapter);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
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
//                    //refresh messages
//                    messageAdapter.notifyDataSetChanged();
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
        newMessage.setUser(currentUser);
        //Clean the EditText
        etMessage.setText("");
        //Save the message in background
        newMessage.saveInBackground();
    }

    void populateRecyclerView(final boolean scrollingDown) {
        final Message.Query messageQuery = new Message.Query();
        messageQuery.setLimit(Message.MAX_NUMBER);
        messageQuery.orderByDescending(Message.KEY_CREATED_AT);
        messageQuery.withUser().withPost();
        messageQuery.whereEqualTo("course", course);
        messageQuery.setSkip(messages.size());
        messageQuery.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); i++){
                        Message message = objects.get(i);
                        message.setUsername(message.getUser().getUsername());
                        message.setParseFileUserImage(message.getUser().getParseFile("photoImage"));
                        ParseObject postParseObject = message.getPost();
                        if(postParseObject != null) {
                            //Get the postParseObject as a Post object
                            Post post = (Post) postParseObject;
                            message.setPostReference(post);
                        }
                        messages.add(0, message);
                        messageAdapter.notifyItemInserted(0);
                        Log.d("MessageFragment", ": " +messages.size());
                    }
                    if(scrollingDown) {
                        //scroll to the last message if you are not scrolling upwards
                        recyclerView.scrollToPosition(messages.size() - 1);
                    }
                }
                if(!isQueryLiveset) {
                    //We need to set the ParseLiveQueryClient after finding the course
                    setParseLiveQueryClient();
                    isQueryLiveset = true;
                }
            }
        });
    }

    private String getCurrentCourseName() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return settings.getString("courseName", "");
    }

    private String getUserUniveristy() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return settings.getString("universityName", "");
    }

    private void setParseLiveQueryClient() {

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        parseQuery.whereEqualTo("course", course);
        parseQuery.include("user");
        parseQuery.include("post");
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new
                SubscriptionHandling.HandleEventCallback<Message>() {
                    //Add the element in the beginning of the recycler view and go to that position
                    @Override
                    public void onEvent(ParseQuery<Message> query, final Message message) {
                        message.getUser().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {

                                message.setUsername(((ParseUser) object).getUsername());
                                messages.add(message);
                                ((Activity)getContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageAdapter.notifyDataSetChanged();
                                        recyclerView.scrollToPosition(messages.size() - 1);
                                    }
                                });
                            }
                        });
                    } // end of onEvent function
                }); // end of subscription.handleEvent
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        Log.d("MessageFragment", "item position: " + position);
        //Move the content of the message to a post
        Intent intentPostMessage = new Intent(getContext(), ComposePostActivity.class);
        intentPostMessage.putExtra("message", messages.get(position));
        startActivity(intentPostMessage);
    }

    private void setEndlessRecyclerViewScrollListener() {
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager, false, 2) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("MessageFragment", "New pagination");
                populateRecyclerView(false);
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
    }
}
