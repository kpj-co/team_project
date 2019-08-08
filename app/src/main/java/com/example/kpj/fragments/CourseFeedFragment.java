package com.example.kpj.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.widget.AutoCompleteTextView;
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
import com.example.kpj.model.Hashtag;
import com.example.kpj.model.PostHashtagRelation;
import com.example.kpj.model.User;
import com.example.kpj.utils.EndlessRecyclerViewScrollListener;
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
    private ImageButton ibCompose;
    private SearchView svSearch;
    public RecyclerView rvCourseFeed;
    private ArrayList<Post> postArrayList;
    private PostAdapter postAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Course course;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private LinearLayoutManager linearLayoutManager;
    private String lastConstraint = "";

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
        initializeViews(view);
        initializeVariables();
        setComposeButtonListener();
        setSwipeRefreshLayout();
        // Set feed title to match course
//        tvFeedTitle.setText(courseName);
        queryForCourseByName(getCurrentCourseName());
        return view;
    }

    public void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postAdapter.clearFullList();
                endlessRecyclerViewScrollListener.resetState();
                queryPosts(true);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private String getCurrentCourseName() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return settings.getString("courseName", "");
    }

    private void queryForCourseByName(String courseName) {
        final Course.Query courseQuery = new Course.Query();
        courseQuery.whereEqualTo("name", courseName);
        courseQuery.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> selectedCourse, ParseException e) {
                course = selectedCourse.get(0);
                // tell user they are in selected course
                String message = "You are in " + course.getName();
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                // query for posts associated with course
                setUpAdapter();
                setEndlessRecyclerViewScrollListener();
                queryPosts(true);
                setUpSearchView(svSearch);
            }
        });
    }

    private void queryPosts(final boolean clearPostAdapter) {
        ParseQuery<Post> query = ParseQuery.getQuery("Post");
        query.include(Post.KEY_USER);
        query.include(Post.KEY_USER).include(User.KEY_UNIVERSITY);
        query.include(Post.KEY_COURSE);
        query.whereEqualTo("course", course);
        query.orderByDescending(Post.KEY_CREATED_AT);
        //Do not query all of them with one call
        query.setLimit(Post.MAX_NUMBER);
        //Do not query the same posts always
        query.setSkip(postAdapter.getFullListSize());

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e == null) {
                    if(clearPostAdapter) {
                        // clear recycler view just after finding new results
                        postArrayList.clear();
                        postAdapter.notifyDataSetChanged();
                    }

                    // add posts to adapter
                    for (Post post : posts) {
                        postArrayList.add(post);
                        postAdapter.notifyItemInserted(postArrayList.size() - 1);
                    }
                    // query for hash tags of each post
                    for (final Post post : postArrayList) {
                        queryHashTags(post);
                    }

                    postAdapter.updateFullList(posts);

                } else {
                    Toast.makeText(getContext(), "Fail!", Toast.LENGTH_LONG).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void queryHashTags(final Post post) {
        final PostHashtagRelation.Query innerQuery = new PostHashtagRelation.Query();
        innerQuery.whereEqualTo("post", post);
        innerQuery.findInBackground(new FindCallback<PostHashtagRelation>() {
            @Override
            public void done(List<PostHashtagRelation> objects, ParseException e) {
                List<String> hashtags = new ArrayList<>();
                for (PostHashtagRelation relation : objects) {
                    hashtags.add(relation.getHashtag());
                }
                post.setHashtags(hashtags);
                postAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initializeViews(View view) {
        // Find views from xml
        this.rvCourseFeed = view.findViewById(R.id.rvCourseFeed);
        this.swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        this.ibCompose = view.findViewById(R.id.ibCompose);
        this.svSearch = view.findViewById(R.id.svSearch);
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
        postAdapter = new PostAdapter(getActivity(), course, postArrayList, new PostAdapter.OnPostClicked() {
            @Override
            public void onPostClickListener(int position) {
                Toast.makeText(getActivity(), "post clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), PostDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("post", postArrayList.get(position));
                bundle.putStringArrayList("postHashTags", (ArrayList) postArrayList.get(position).getHashtags());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //create linear layout manager for recycler view
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvCourseFeed.setLayoutManager(linearLayoutManager);
        //attach adapter to recycler view
        rvCourseFeed.setAdapter(postAdapter);
    }

    private void setUpSearchView(SearchView searchView) {
        // change text size
//        AutoCompleteTextView autoCompleteTextViewSearch = (AutoCompleteTextView) searchView
//                .findViewById(searchView
//                .getContext()
//                .getResources()
//                .getIdentifier("app:id/search_src_text", null, null));
//        if (autoCompleteTextViewSearch != null) {
//            autoCompleteTextViewSearch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//        }
        searchView.setQueryHint("search #tags in " + course.getName());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                try {
                    postAdapter.filterList(s);
                    postAdapter.notifyDataSetChanged();
                    lastConstraint = s;
                    s = s.trim();
                    if(postAdapter.getFullListSize() == 0 && (s.equals("") || s.equals("#"))) {
                        postAdapter.clearFullList();
                        postAdapter.updateFullList(postArrayList);
                    }
                    return true;
                } catch (NullPointerException e) {
                    return false;
                }
            }
        });
    }

    private void setEndlessRecyclerViewScrollListener() {
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager, true) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(lastConstraint.equals("")) {
                    queryPosts(false);
                }
            }
        };
        rvCourseFeed.addOnScrollListener(endlessRecyclerViewScrollListener);
    }
}

































