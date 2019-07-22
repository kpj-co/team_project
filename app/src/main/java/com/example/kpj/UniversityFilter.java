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
    private List<University> universitiesFull;


    public UniversityFilter(ArrayList<University> universities){
        this.universities = universities;
        this.universitiesFull = new ArrayList<University>(universities);
    }

    @Override
    public Filter getFilter() {
        return universityFilter;
    }
    private Filter universityFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<University> filteredUniversities = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredUniversities.addAll(universitiesFull);
            } else {
                String filteredPattern = constraint.toString().toLowerCase().trim();

                for (University university : universitiesFull) {
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
            universities.clear();
            universities.addAll((List) results.values);

        }

    };
}