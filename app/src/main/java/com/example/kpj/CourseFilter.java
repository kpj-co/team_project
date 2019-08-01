package com.example.kpj;

import android.widget.Filter;
import android.widget.Filterable;

import com.example.kpj.model.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseFilter implements Filterable {

    private List<Course> courseList;
    private List<Course> filteredCourses;

    private SelectedCoursesFragmentAdapter adapter;

    public CourseFilter(List<Course> courses, SelectedCoursesFragmentAdapter adapter){
        this.courseList = courses;
        this.adapter = adapter;
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if(constraint != null && constraint.length() != 0){
                    filteredCourses = new ArrayList<>();
                    for(Course course: courseList){
                        if(course.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                            filteredCourses.add(course);
                        }
                    }
                    results.values = filteredCourses;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredCourses = (List<Course>) results.values;
                adapter.setSearchList(filteredCourses);
                adapter.notifyDataSetChanged();
            }
        };
    }
}
