package com.example.kpj.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.kpj.R;
import com.example.kpj.model.University;

import java.util.ArrayList;

public class UniversityFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private ArrayList<University> universities;
    private SearchView searchView;
    private Button button;
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

        searchView = view.findViewById(R.id.svSearch);

        /////TODO: Remove this button and its listener in the merge, i am just testing the fragment flow
        button = (Button) view.findViewById(R.id.bTest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSelectCoursesFragment();
            }
        });
        //init arraylist
        return view;
    }

    public void goToSelectCoursesFragment() {
        Fragment fragment = new SelectCoursesFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}



