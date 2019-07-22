package com.example.kpj;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.kpj.model.Course;
import com.example.kpj.model.University;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class UniversityFragmentAdapter extends RecyclerView.Adapter<UniversityFragmentAdapter.ViewHolder>{

    private Context context;
    private List<University> universities;
    private List<University> universitiesFull;

    public UniversityFragmentAdapter(Context context, ArrayList<University> universities) {
        this.universities = universities;
        this.context = context;
        universitiesFull = new ArrayList<University>(universities);
    }

    @NonNull
    @Override
    public UniversityFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        // inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        // now create the view for course item
        View courseView = inflater.inflate(R.layout.fragment_university_item, viewGroup, false);
        // now return the new viewholder
        return new UniversityFragmentAdapter.ViewHolder(courseView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // this method is to bind the components of the layout to the user in parse
        Log.d("Adapter", "On Bind is called");
        // TODO - change the way you get course to getting the course associated with each user
    }


    @Override
    public int getItemCount() {
        Log.d("Adapter", "Item Count is called");
        return universities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUniversity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("Adapter", "Viewholder is called");
            tvUniversity = itemView.findViewById(R.id.tvUniversity);
        }
    }
}
