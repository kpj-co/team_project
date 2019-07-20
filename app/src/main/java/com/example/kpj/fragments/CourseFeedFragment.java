package com.example.kpj.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kpj.ComposePostActivity;
import com.example.kpj.MainActivity;
import com.example.kpj.PostAdapter;
import com.example.kpj.R;
import com.example.kpj.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CourseFeedFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    private SearchView svSearch;
    private ImageButton ibCompose;

    public RecyclerView rvCourseFeed;
    public ArrayList<Post> postArrayList;
    private PostAdapter postAdapter;

    public FragmentActivity fragmentActivity;

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
        setUpAdapter();
        setComposeButtonListener();
        queryPosts();
        return view;
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        query.include(Post.KEY_USER);
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e == null) {
                    for (Post post : posts) {
                        postArrayList.add(post);
                        postAdapter.notifyItemInserted(postArrayList.size() - 1);
                    }
                } else {
                    Toast.makeText(getContext(), "Fail!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initializeViews(View view) {
        // Find views from xml
        rvCourseFeed = view.findViewById(R.id.rvUniversity);
        ibCompose = view.findViewById(R.id.ibCompose);
        // TODO -- Figure out how to set up a search view
        //svSearch = view.findViewById(R.id.svSearch);
    }

    private void initializeVariables() {
        postArrayList = new ArrayList<>();
        postAdapter = new PostAdapter(fragmentActivity, postArrayList);
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
        //create linear layout manager for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentActivity);
        rvCourseFeed.setLayoutManager(linearLayoutManager);
        //attach adapter to recycler view
        rvCourseFeed.setAdapter(postAdapter);
    }


}

































