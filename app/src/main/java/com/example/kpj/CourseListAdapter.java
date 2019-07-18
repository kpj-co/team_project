package com.example.kpj;

import android.content.Context;
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

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    private Context context;
    private List<Course> courses;


    public CourseListAdapter(Context context, ArrayList<Course> courses) {
        this.courses = courses;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        // inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        // now create the view for course item
        View courseView = inflater.inflate(R.layout.course_item, viewGroup, false);
        // now return the new viewholder
        return new ViewHolder(courseView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseListAdapter.ViewHolder viewHolder, int position) {
        // this method is to bind the components of the layout to the user in parse
        Log.d("Adapter", "On Bind is called");
        // TODO - change the way you get course to getting the course associated with each user
        viewHolder.tvCourse.setText(courses.get(position).getName());
    }

    @Override
    public int getItemCount() {
        Log.d("Adapter", "Item Count is called");
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCourse;
        private View myRectangleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("Adapter", "Viewholder is called");
            tvCourse = (TextView) itemView.findViewById(R.id.tvCourse);
            myRectangleView = (View) itemView.findViewById(R.id.myRectangleView);
        }
    }


}