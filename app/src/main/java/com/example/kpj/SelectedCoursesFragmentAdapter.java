package com.example.kpj;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kpj.model.Course;
import com.example.kpj.model.University;
import com.example.kpj.model.UserCourseRelation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;


import java.util.ArrayList;
import java.util.List;

public class SelectedCoursesFragmentAdapter extends RecyclerView.Adapter<SelectedCoursesFragmentAdapter.ViewHolder> {

    private Context context;

    private List<Course> universityCourses;
    private List<Course> selectedCourses;
    private List<Course> courseFilteredList;

    private CourseFilter filter;

    public SelectedCoursesFragmentAdapter(Context context, List<Course> courses) {
        this.context = context;
        this.selectedCourses = new ArrayList<>();
        this.courseFilteredList = new ArrayList<>();
        this.universityCourses = courses;
        this.filter = new CourseFilter(universityCourses, this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        // inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        // now create the view for university item
        View courseView = inflater.inflate(R.layout.select_course_item, viewGroup, false);
        // now return the new viewholder
        Log.d("SelectCourseAdapter", "Viewholder");
        return new ViewHolder(courseView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        Log.d("SelectedCourses", "On Bind Called");
        final Course course = courseFilteredList.get(position);
        if(selectedCourses.contains(course)){
            viewHolder.cdCourse.setBackgroundColor(Color.GREEN);
        }else{
            viewHolder.cdCourse.setBackgroundColor(Color.TRANSPARENT);
        }
        String courseName = course.getName();
        viewHolder.tvSelectCourses.setText(courseName);
        viewHolder.cdCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = selectedCourses.contains(course);
                if(!isChecked){
                    viewHolder.cdCourse.setBackgroundColor(Color.GREEN);
                    selectedCourses.add(course);
                }
                else{
                    viewHolder.cdCourse.setBackgroundColor(Color.TRANSPARENT);
                    selectedCourses.remove(course);
                }
            }
        });
    }

    public void setSearchList(List<Course> list) {
        if (list == null) {
            courseFilteredList = universityCourses;
        } else {
            courseFilteredList = list;
        }
    }

    public List<Course> getSelectedCoursesList(){
        String msg = "SelectedCourseFragment list size = " + selectedCourses.size();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        return selectedCourses;
    }

    public void filterList(String s) {
        filter.getFilter().filter(s);
    }

    @Override
    public int getItemCount() {
        Log.d("SelectedCourse", "Item Count");
        return courseFilteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSelectCourses;
        private CardView cdCourse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSelectCourses = itemView.findViewById(R.id.tvSelectCourse);
            cdCourse = itemView.findViewById(R.id.cdCourse);
        }
    }
}