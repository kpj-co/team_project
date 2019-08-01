package com.example.kpj.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kpj.R;
import com.example.kpj.UniversityFragmentAdapter;
import com.example.kpj.model.University;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class UniversityFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";

    private ArrayList<University> universities;

    private UniversityFragmentAdapter adapter;

    private University university;

    private UniversityFragmentListener callback;

    public void setUniversityData(UniversityFragmentListener universityFragmentListener){
        this.callback = universityFragmentListener;
    }

    public interface UniversityFragmentListener{
        void onSignUpUniversity(String university);
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
            int mPage = getArguments().getInt(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_university, container, false);

        fetchUniversities();
        setUpRecyclerView(view);
        setUpSearchView(view);
        setNextButtonListener(view);

        return view;
    }

    private void setNextButtonListener(View view) {
        Button nextButton = (Button) view.findViewById(R.id.bNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                university = (University) adapter.selectedUniversity(university);
                String selected = university.getName();

                callback.onSignUpUniversity(selected);
                setUpSharedPref(selected);
                goToSelectCourses();
            }
        });
    }

    private void setUpRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rvUniversity);
        universities = new ArrayList<>();
        adapter = new UniversityFragmentAdapter(getContext(), universities);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.filterList("");
    }

    private void setUpSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.svSearch);
        searchView.setQueryHint("University");
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

    private void setUpSharedPref(String selected){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("university",selected);
        editor.apply();
    }

    private void fetchUniversities() {
        final University.Query query = new University.Query();
        query.findInBackground(new FindCallback<University>() {
            @Override
            public void done(List<University> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); i++){
                        University university =  objects.get(i);
                        universities.add(university);
                        adapter.notifyItemInserted(universities.size()-1);
                        Log.d("UniversityFragment", "List of universities" + university.getName());
                    }
                }
            }
        });
    }

    public void goToSelectCourses() {
        Fragment fragment = new SelectCoursesFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}








