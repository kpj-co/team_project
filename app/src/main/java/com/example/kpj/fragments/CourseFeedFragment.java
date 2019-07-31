package com.example.kpj.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kpj.activities.ComposePostActivity;
import com.example.kpj.activities.PostDetailActivity;
import com.example.kpj.model.Course;
import com.example.kpj.model.PostHashtagRelation;
import com.example.kpj.model.User;
import com.example.kpj.utils.PostAdapter;
import com.example.kpj.R;
import com.example.kpj.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.parse.Parse.getApplicationContext;

public class CourseFeedFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private final static String PREF_NAME = "sharedData";
    private int mPage;

    private SearchView svSearch;
    private ImageButton ibCompose;
    private TextView tvFeedTitle;

    public RecyclerView rvCourseFeed;
    public ArrayList<Post> postArrayList;
    private PostAdapter postAdapter;

    public FragmentActivity fragmentActivity;
    private Course course;

    public CourseFeedFragment() {
        // Required empty public constructor
    }

    public static CourseFeedFragment newInstance(int page) {
        CourseFeedFragment fragment = new CourseFeedFragment();
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
        View view = inflater.inflate(R.layout.fragment_course_feed, container, false);
        fragmentActivity = getActivity();
        initializeViews(view);
        initializeVariables();
        setComposeButtonListener();
        // Get course from main activity
        String courseName = getCurrentCourseName();
        // Set feed title to match course
        tvFeedTitle.setText(courseName);
        // Query for course by name
        final Course.Query courseQuery = new Course.Query();
        courseQuery.whereEqualTo("name", courseName);
        courseQuery.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> selectedCourse, ParseException e) {
                course = selectedCourse.get(0);
                // tell user they are in selected course
                String message = "You are in " + course.getName();
                Toast.makeText(fragmentActivity, message, Toast.LENGTH_LONG).show();
                // query for posts associated
                setUpAdapter();
                queryPosts();
            }
        });
        setUpSearchView(view);



        return view;
    }

    private String getCurrentCourseName() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return settings.getString("courseName", "");
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        query.include(Post.KEY_USER);
        query.include(Post.KEY_USER).include(User.KEY_UNIVERSITY);
        query.include(Post.KEY_COURSE);
        query.whereEqualTo("course", course);
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e == null) {
                    for (Post post : posts) {
                        postArrayList.add(post);
                        postAdapter.notifyItemInserted(postArrayList.size() - 1);
                    }

                    //We require two separate loops to maintain the order of the post despite the arrival time of the hashtags
                    for (final Post post : postArrayList) {
                        //For each post, fetch its hashtags
                        final PostHashtagRelation.Query innerQuery = new PostHashtagRelation.Query();
                        innerQuery.whereEqualTo("post", post);
                        innerQuery.findInBackground(new FindCallback<PostHashtagRelation>() {
                            @Override
                            public void done(List<PostHashtagRelation> objects, ParseException e) {
                                for (PostHashtagRelation relation : objects) {
                                    post.addHashtag(((PostHashtagRelation) relation).getHashtag());
                                    postAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                    postAdapter.updateFullList(posts);

                } else {
                    Toast.makeText(getContext(), "Fail!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initializeViews(View view) {
        // Find views from xml
        rvCourseFeed = view.findViewById(R.id.rvCourseFeed);
        ibCompose = view.findViewById(R.id.ibCompose);
        tvFeedTitle = view.findViewById(R.id.tvFeedTitle);
        // TODO -- Figure out how to set up a search view
        //svSearch = view.findViewById(R.id.svSearch);
    }

    private void initializeVariables() {
        postArrayList = new ArrayList<>();
    }

    private void setComposeButtonListener() {
        ibCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ComposePostActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpAdapter() {
        postAdapter = new PostAdapter(fragmentActivity, course, postArrayList, new PostAdapter.OnPostClicked() {
            @Override
            public void onPostClickListener(int position) {
                Toast.makeText(fragmentActivity, "post clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), PostDetailActivity.class);
                intent.putExtra("post", postArrayList.get(position));
                startActivity(intent);
            }
        });
        //create linear layout manager for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentActivity);
        rvCourseFeed.setLayoutManager(linearLayoutManager);
        //attach adapter to recycler view
        rvCourseFeed.setAdapter(postAdapter);
    }

    private void setUpSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.svSearch);
        searchView.setQueryHint("#YourHashtags #here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                postAdapter.filterList(s);
                postAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }
}

































