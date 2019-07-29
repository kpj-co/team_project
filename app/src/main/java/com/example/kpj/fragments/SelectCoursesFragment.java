package com.example.kpj.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kpj.R;
import com.example.kpj.SelectedCoursesFragmentAdapter;
import com.example.kpj.activities.CourseListActivity;
import com.example.kpj.model.Course;
import com.example.kpj.model.User;
import com.example.kpj.model.UserCourseRelation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SelectCoursesFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    private ArrayList<Course> filterCourses;
    private ArrayList<Course> userSelectedCourses;

    private RecyclerView recyclerView;
    private Button bToUserCourseList;

    private SelectedCoursesFragmentAdapter adapter;

    public static SelectCoursesFragment newInstance(int page) {
        SelectCoursesFragment fragment = new SelectCoursesFragment();
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
        View view = inflater.inflate(R.layout.fragment_select_courses, container, false);
        // Attach button to view
        bToUserCourseList = view.findViewById(R.id.bToUserCourseList);

        ParseUser user = ParseUser.getCurrentUser();
        ParseObject university = (ParseObject) user.get(User.KEY_UNIVERSITY);

        fetchCourseByUniversity(university);
        setUpRecyclerView(view);
        setUpToUserCourseListListener();

        return view;
    }

    private void setUpToUserCourseListListener() {
        bToUserCourseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                addSelectedCourses(user);
                Intent intent = new Intent(getActivity(), CourseListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rvSelectCourse);
        filterCourses = new ArrayList<>();
        adapter = new SelectedCoursesFragmentAdapter(getContext(), filterCourses);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void fetchCourseByUniversity(ParseObject university) {
        final Course.Query courseQuery = new Course.Query();
        courseQuery.whereEqualTo("University", university);
        courseQuery.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Course course = (Course) objects.get(i);
                        filterCourses.add(course);
                        adapter.notifyItemInserted(filterCourses.size() - 1);
                        Log.d("SelectCourseFragment", "List" + filterCourses);
                    }
                }
            }
        });
    }

    private void addSelectedCourses(ParseUser user) {
        userSelectedCourses = new ArrayList<>();
        adapter.setList(userSelectedCourses);
        for (int i = 0; i < userSelectedCourses.size(); i++) {
            UserCourseRelation userCourseRelation = new UserCourseRelation();
            userCourseRelation.setUser(user);
            userCourseRelation.setCourse(userSelectedCourses.get(i));
            userCourseRelation.saveInBackground();
        }
    }
}