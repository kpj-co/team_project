package com.example.kpj.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kpj.R;
import com.example.kpj.activities.MainActivity;
import com.example.kpj.model.Course;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private Context context;
    private List<Course> courses;

    public CourseAdapter(Context context, ArrayList<Course> courses) {
        this.courses = courses;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        // inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        // now create the view for course item
        View courseView = inflater.inflate(R.layout.course_item, viewGroup, false);
        // now return the new view holder
        return new ViewHolder(courseView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder viewHolder, int position) {
        // this method is to bind the components of the layout to the user in parse
        Log.d("Adapter", "On Bind is called");
        try {
            viewHolder.tvCourse.setText(courses.get(position).fetchIfNeeded().getString("name"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        Log.d("Adapter", "Item Count is called");
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvCourse;
        private View myRectangleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("Adapter", "Viewholder is called");
            tvCourse = (TextView) itemView.findViewById(R.id.tvCourse);
            myRectangleView = (View) itemView.findViewById(R.id.myRectangleView);
            // set on click listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Intent intent = new Intent(context, MainActivity.class);
            final Course.Query courseQuery = new Course.Query();
            courseQuery.whereEqualTo("name", tvCourse.getText().toString());
            courseQuery.findInBackground(new FindCallback<Course>() {
                @Override
                public void done(List<Course> selectedCourse, ParseException e) {
                    intent.putExtra("selectedCourse", selectedCourse.get(0));
                    context.startActivity(intent);
                }
            });
        }

    }
}