package com.example.kpj.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kpj.R;
import com.example.kpj.SelectedCoursesFragmentAdapter;
import com.example.kpj.activities.CourseListActivity;
import com.example.kpj.model.Course;
import com.example.kpj.model.University;
import com.example.kpj.model.UserCourseRelation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SelectCoursesFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";

    private ArrayList<Course> filterCourses;

    private Button bToUserCourseList;

    private TextView tvUserUniversity;

    private SelectedCoursesFragmentAdapter adapter;

    private SelectedCoursesListener callback;

    private String university;

    public void setUserSelectedCoursesListener(SelectedCoursesListener selectedCoursesListener){
        this.callback = selectedCoursesListener;
    }

    public interface SelectedCoursesListener{
        void onSelectCourses(Set<Course> selectedCourses);

    }

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
            int mPage = getArguments().getInt(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_courses, container, false);
        bToUserCourseList = view.findViewById(R.id.bToUserCourseList);
        tvUserUniversity = view.findViewById(R.id.tvUserUniversity);
        getSharedPrefs(view);
        tvUserUniversity.setText(university);
        return view;
    }

    private void setUpSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.svCourse);
        searchView.setQueryHint("Course");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.filterList(s);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void setUpToUserCourseListListener() {
        bToUserCourseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSelectCourses(adapter.getSelectedCoursesList());
            }
        });
    }



    private void setUpRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rvSelectCourse);
        adapter = new SelectedCoursesFragmentAdapter(getContext(), filterCourses);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.filterList("");
    }

    private void getSharedPrefs(final View view){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        university = prefs.getString("university", "");
        University.Query query = new University.Query();
        query.whereEqualTo("name", university);
        query.findInBackground(new FindCallback<University>() {
            @Override
            public void done(List<University> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); i++){
                        fetchCoursesByUniversity(objects.get(i),view);
                    }
                }
            }
        });
    }

    private void fetchCoursesByUniversity(ParseObject university, final View view) {
        filterCourses = new ArrayList<>();
        final Course.Query courseQuery = new Course.Query();
        courseQuery.whereEqualTo("University", university);
        courseQuery.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Course course = (Course) objects.get(i);
                        filterCourses.add(course);
                        Log.d("SelectCourseFragment", "List" + filterCourses);
                    }
                }
                setUpRecyclerView(view);
                setUpSearchView(view);
                setUpToUserCourseListListener();
            }
        });

    }
}



