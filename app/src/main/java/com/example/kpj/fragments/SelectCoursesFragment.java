package com.example.kpj.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kpj.R;
import com.example.kpj.activities.CourseListActivity;
import com.example.kpj.activities.LoginActivity;
import com.example.kpj.activities.MainActivity;

public class SelectCoursesFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    private Button bToUserCourseList;
    public SelectCoursesFragment() {
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
        setUpToUserCourseListListener();
        return view;
    }

    private void setUpToUserCourseListListener() {
        bToUserCourseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CourseListActivity.class);
                startActivity(intent);
            }
        });
    }
}
