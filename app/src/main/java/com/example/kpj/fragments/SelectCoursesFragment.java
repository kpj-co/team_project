package com.example.kpj.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kpj.R;

public class SelectCoursesFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;


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
        return view;

    }
}
