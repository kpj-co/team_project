package com.example.kpj;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kpj.model.Course;
import com.example.kpj.model.UserCourseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SelectedCoursesFragmentAdapter extends RecyclerView.Adapter<SelectedCoursesFragmentAdapter.ViewHolder> {

    private Context context;

    private List<Course> universityCourses;
    private List<Course> selectedCourses;
    private List<Course> courseFilteredList;

    private CourseFilter filter;

    public SelectedCoursesFragmentAdapter(Context context, ArrayList<Course> courses) {
        this.context = context;
        this.universityCourses = courses;
        this.selectedCourses = new ArrayList<>();
        this.courseFilteredList = new ArrayList<>();
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
        Log.d("SelectedCourse", "On Bind Called");
        final Course course = courseFilteredList.get(position);
        if(course.isChecked())
            viewHolder.tvSelectCourses.setBackgroundColor(Color.GREEN);
        else
            viewHolder.tvSelectCourses.setBackgroundColor(Color.TRANSPARENT);

        String courseName = course.getName();
        viewHolder.tvSelectCourses.setText(courseName);
        viewHolder.tvSelectCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course.setChecked(!course.isChecked());
                if(course.isChecked())
                    viewHolder.tvSelectCourses.setBackgroundColor(Color.GREEN);
                else
                    viewHolder.tvSelectCourses.setBackgroundColor(Color.TRANSPARENT);
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
    public void addSelectedCourses(ParseUser user) {
        for (int i = 0; i < selectedCourses.size(); i++) {
            UserCourseRelation userCourseRelation = new UserCourseRelation();
            userCourseRelation.setUser(user);
            userCourseRelation.setCourse(selectedCourses.get(i));
            userCourseRelation.saveInBackground();
        }
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSelectCourses = itemView.findViewById(R.id.tvSelectCourse);
        }
    }
}