package com.example.kpj;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kpj.model.Course;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class CourseListFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private List<Course> courses;
    private RecyclerView rvCourses;
    private CourseListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;


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

        rvCourses = view.findViewById(R.id.rvCourse);
        // creating the list
        courses = new ArrayList<Course>();
        // creating the adapter
        adapter = new CourseListAdapter(getContext(), courses);
        //set the layout manager
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvCourses.setLayoutManager(linearLayoutManager);
        // set the adapter
        rvCourses.setAdapter(adapter);

        loadCourse();

        return view;
    }

    //    public void onViewCreated(View view, Bundle savedInstanceState) {
//
//
//        // load the courses
//        loadCourse(0);
//    }
//
    private void loadCourse() {
        final Course.Query courseQuery = new Course.Query();

        courseQuery.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Course course = objects.get(i);
                        courses.add(course);
                        adapter.notifyItemInserted(courses.size() - 1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}



