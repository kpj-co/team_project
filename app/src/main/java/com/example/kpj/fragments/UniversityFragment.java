package com.example.kpj.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kpj.R;
import com.example.kpj.model.University;

import java.util.ArrayList;

public class UniversityFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private ArrayList<University> universities;
    public UniversityFragment() {
    }

    public static UniversityFragment newInstance(int page) {
        UniversityFragment fragment = new UniversityFragment();
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

        final View view = inflater.inflate(R.layout.fragment_university, container, false);

        //init arraylist
        return view;
    }


}
