package com.example.kpj;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kpj.fragments.SelectCoursesFragment;
import com.example.kpj.model.Course;

import java.util.ArrayList;
import java.util.List;

public class SelectedCoursesFragmentAdapter extends RecyclerView.Adapter<SelectedCoursesFragmentAdapter.ViewHolder> {

    private Context context;
    private List<Course> universityCourses;

    public SelectedCoursesFragmentAdapter(Context context, ArrayList<Course> courses){
        this.context = context;
        this.universityCourses = courses;
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
        String course = universityCourses.get(position).getName();
        viewHolder.tvSelectCourses.setText(course);
    }

    @Override
    public int getItemCount() {
        Log.d("SelectedCourse", "Item Count");
        return universityCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvSelectCourses;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSelectCourses = itemView.findViewById(R.id.tvSelectCourse);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
