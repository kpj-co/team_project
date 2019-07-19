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
import com.example.kpj.model.UserCourseRelation;
import com.parse.FindCallback;
import com.parse.ParseException;
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
        findCoursesByUserId(ParseUser.getCurrentUser());
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
        final UserCourseRelation.Query userCourseRelationQuery = new UserCourseRelation.Query();
        userCourseRelationQuery.whereEqualTo("user", user);
        userCourseRelationQuery.findInBackground(new FindCallback<UserCourseRelation>() {
            @Override
            public void done(List<UserCourseRelation> objects, ParseException e) {
                    if(e == null){
                        for(int i = 0; i < objects.size(); i++){
                            Course course = (Course) objects.get(i).getCourse();
                            filterCourses.add(course);
                            adapter.notifyItemInserted(filterCourses.size() - 1);
                            try {
                                Log.d("CourseListFragment", "List of courses:" + course.fetchIfNeeded().getString("name"));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                }
            }
        });
    }
}

