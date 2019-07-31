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

import com.example.kpj.model.University;
import com.example.kpj.model.User;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UniversityFragmentAdapter extends RecyclerView.Adapter<UniversityFragmentAdapter.ViewHolder> {

    private Context context;

    private List<University> universities;
    private List<University> universityFilteredList;

    private University userUniversity;

    public UniversityFragmentAdapter(Context context, ArrayList<University> universities) {
        this.universities = universities;
        this.context = context;
        universityFilteredList = new ArrayList<>();
    }

    @NonNull
    @Override
    public UniversityFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        // inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        // now create the view for university item
        View courseView = inflater.inflate(R.layout.university_item, viewGroup, false);
        // now return the new viewholder
        Log.d("Adapter", "Viewholder");
        return new ViewHolder(courseView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        // this method is to bind the components of the layout to the user in parse
        Log.d("Adapter", "On Bind is called");
        final University university = universityFilteredList.get(position);

        if(university.isSelected()){
            viewHolder.tvUniversity.setBackgroundColor(Color.GREEN);
        }
        else{
            viewHolder.tvUniversity.setBackgroundColor(Color.TRANSPARENT);
        }
        String universityName = university.getString("name");
        viewHolder.tvUniversity.setText(universityName);
        viewHolder.tvUniversity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                university.setSelected(!university.isSelected());
                if(university.isSelected()){
                    viewHolder.tvUniversity.setBackgroundColor(Color.GREEN);
                    userUniversity = university;
                }
                else{
                    viewHolder.tvUniversity.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

    }

    public void setList(List<University> list) {
        universityFilteredList = list;
    }

    public void filterList(String s) {
        UniversityFilter filter = new UniversityFilter(universities, this);
        filter.getFilter().filter(s);
    }

    public ParseObject selectedUniversity(ParseObject university){
        university = userUniversity;

        return university;
    }

    public void setUserUniversity(ParseUser user) {
        user.put(User.KEY_UNIVERSITY, userUniversity);
        user.saveInBackground();
    }

    @Override
    public int getItemCount() {
        Log.d("Adapter", "Item Count is called " + universities.size());
        return universityFilteredList.size();
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

