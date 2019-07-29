package com.example.kpj;

import android.widget.Filter;
import android.widget.Filterable;

import com.example.kpj.model.University;

import java.util.ArrayList;
import java.util.List;

public class UniversityFilter implements Filterable {

    private List<University> universityList;
    private UniversityFragmentAdapter adapter;
    private List<University> filteredUniversities = new ArrayList<>();

    public UniversityFilter(List<University> universities, UniversityFragmentAdapter adapter) {
        this.adapter = adapter;
        this.universityList = universities;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null && constraint.length() != 0) {
                    for (University university : universityList) {
                        if (university.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            filteredUniversities.add(university);
                        }
                    }
                    results.values = filteredUniversities;
                }
                return results;
            }

                @Override
                protected void publishResults (CharSequence constraint, FilterResults results){
                    filteredUniversities = (List<University>) results.values;
                    if(filteredUniversities == null){
                        filteredUniversities = universityList;
                    }
                    adapter.setList(filteredUniversities);
                    adapter.notifyDataSetChanged();
                }
            };

        }
    }
