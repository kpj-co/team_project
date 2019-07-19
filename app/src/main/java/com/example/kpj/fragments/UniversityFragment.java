package com.example.kpj.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.example.kpj.R;
import com.example.kpj.UniversityFragmentAdapter;
import com.example.kpj.model.University;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class UniversityFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private ArrayList<University> filteredUniversities;
    private RecyclerView recyclerView;
    private UniversityFragmentAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private SearchView searchView;

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
        //set up the searchview
        searchView = (SearchView) view.findViewById(R.id.svSearch);
        searchView.setQueryHint("University");
        filteredUniversities = new ArrayList<>();
        findUniversityByName();
        recyclerView = view.findViewById(R.id.rvUniversity);
        adapter = new UniversityFragmentAdapter(getContext(), filteredUniversities);
        recyclerView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    private void findUniversityByName() {
        final University.Query query = new University.Query();
        query.findInBackground(new FindCallback<University>() {
            @Override
            public void done(List<University> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); i++){
                        University university =  objects.get(i);
                        filteredUniversities.add(university);
                        adapter.notifyItemInserted(filteredUniversities.size()-1);
                    }
                }
            }
        });
    }
}
