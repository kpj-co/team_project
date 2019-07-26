package com.example.kpj;

import android.widget.Filter;
import android.widget.Filterable;

import com.example.kpj.model.University;

import java.util.ArrayList;
import java.util.List;

public class UniversityFilter implements Filterable {

    private List<University> universityList;
    private UniversityFragmentAdapter adapter;

    public UniversityFilter(ArrayList<University> universities, UniversityFragmentAdapter adapter) {
        this.adapter = adapter;
        this.universityList = universities;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    adapter.setList(universityList);
                    adapter.notifyDataSetChanged();
                } else {
                    ArrayList<University> filteredUniversities = new ArrayList<>();
                    for (University university : universityList) {
                        if (university.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                           results.values = filteredUniversities;
                            filteredUniversities.add(university);
                        }
                    }
                    adapter.setList(filteredUniversities);
                    adapter.notifyDataSetChanged();
                }
                return results;
            }

                @Override
                protected void publishResults (CharSequence constraint, FilterResults results){
                    results.values = universityList;
                    adapter.setList(universityList);
                    adapter.notifyDataSetChanged();
                }
            };

        }
    }
