package com.example.kpj.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kpj.CourseListAdapter;
import com.example.kpj.R;
import com.example.kpj.model.Course;
import com.example.kpj.model.UserIsInCourse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class CourseListFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private ArrayList<Course> filterCourses;
    private RecyclerView recyclerView;
    private CourseListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ParseUser testUser;


    public CourseListFragment() {
    }

    public static CourseListFragment newInstance(int page) {
        CourseListFragment fragment = new CourseListFragment();
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
        final View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        //init arraylist
        filterCourses = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId", "ww3tNsw7Eg");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    testUser = (ParseUser) objects.get(0);
                }
            }
        });

        findCoursesByUserId(testUser);
        // creating the adapter
        recyclerView = view.findViewById(R.id.rvCourse);
        adapter = new CourseListAdapter(getContext(), filterCourses);
        // set the adapter
        recyclerView.setAdapter(adapter);
        //set the layout manager
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    private void findCoursesByUserId(ParseUser user){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("userIsInCourse");
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    Log.d("CourseListFragment", "Reaches the findCoursesByUserId method: " + objects);
                    for (ParseObject i : objects) {
                        UserIsInCourse userIsInCourseObj = (UserIsInCourse) i;
                        filterCourses.add(((Course) userIsInCourseObj.getCourse()));
                        adapter.notifyItemInserted(filterCourses.size() - 1);
                    }
                } else {

                    Log.e("CourseListFragment", "Error in the findCoursesByUserId method: " + e, e);
                }
            }
        });
    }
}
