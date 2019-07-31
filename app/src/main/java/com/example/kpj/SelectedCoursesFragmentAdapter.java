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

import java.util.ArrayList;
import java.util.List;

public class SelectedCoursesFragmentAdapter extends RecyclerView.Adapter<SelectedCoursesFragmentAdapter.ViewHolder> {

    private Context context;

    private List<Course> universityCourses;
    private List<Course> selectedCourses;
    private List<Course> courseFilteredList;

    public SelectedCoursesFragmentAdapter(Context context, ArrayList<Course> courses) {
        this.context = context;
        this.universityCourses = courses;
        this.selectedCourses = new ArrayList<>();
        this.courseFilteredList = new ArrayList<>();
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Log.d("SelectedCourse", "On Bind Called");
        viewHolder.bind(courseFilteredList.get(position));
    }

    public void setUserSelectedCourseList(List<Course> list){
        list.addAll(selectedCourses);
        Log.d("SelectCourses", "selectedcourse" + list);
    }

    public void setSearchList(List<Course> list) {
        if (list == null) {
            courseFilteredList = universityCourses;
        } else {
            courseFilteredList = list;
        }
    }

    public void filterList(String s) {
        CourseFilter filter = new CourseFilter(universityCourses, this);
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

        void bind(final Course course) {
            tvSelectCourses.setText(course.getName());
            tvSelectCourses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("SelectedCourseAdapter", "Item Clicked");
                    course.setChecked(!course.isChecked());
                    if(course.isChecked()){
                        tvSelectCourses.setBackgroundColor(Color.GREEN);
                        selectedCourses.add(course);
                    }
                    else {
                        tvSelectCourses.setBackgroundColor(Color.TRANSPARENT);
                        selectedCourses.remove(course);
                    }
                }
            });
        }
    }
}
