package com.example.kpj.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.example.kpj.R;


public class CourseFeedFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    RecyclerView rvCourseFeed;
    SearchView svSearch;
    ImageButton ibCompose;

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

        rvCourseFeed = view.findViewById(R.id.rvCourseFeed);
        ibCompose = view.findViewById(R.id.ibCompose);
        //svSearch = view.findViewById(R.id.svSearch);

        return view;
    }

}

































