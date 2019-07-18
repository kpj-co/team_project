package com.example.kpj;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.kpj.model.Course;

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    private Context context;
    private List<Course> courses;

    public CourseListAdapter(List<Course> courses){
        this.courses = courses;
    }


    @NonNull
    @Override
    public CourseListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CourseListAdapter.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
