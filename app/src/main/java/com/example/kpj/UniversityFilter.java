package com.example.kpj;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.kpj.model.University;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class UniversityFilter implements Filterable {

    private List<University> universities;
    private UniversityFragmentAdapter adapter;
    private List<University> filteredUniversities;

    public UniversityFilter(ArrayList<University> universities, UniversityFragmentAdapter adapter){
        this.adapter = adapter;
        this.universities = universities;
        this.filteredUniversities = new ArrayList<>();
    }

    @Override
    public Filter getFilter() {
        return universityFilter;
    }
    private Filter universityFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint == null || constraint.length() == 0) {
                filteredUniversities.addAll(universities);
            } else {
                String filteredPattern = constraint.toString().toLowerCase().trim();

                for (University university : universities) {
                    if (university.getName().toLowerCase().contains(filteredPattern)) {
                        filteredUniversities.add(university);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredUniversities;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //adapter.setList(filteredUniversities);
            adapter.notifyDataSetChanged();

        }

    };
}