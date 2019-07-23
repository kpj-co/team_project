package com.example.kpj;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kpj.model.University;

import java.util.ArrayList;
import java.util.List;

public class UniversityFragmentAdapter extends RecyclerView.Adapter<UniversityFragmentAdapter.ViewHolder>{

    private Context context;
    private List<University> universities;
    private List<University> universityFilteredList;
    private UniversityFilter filter;

    public UniversityFragmentAdapter(Context context, ArrayList<University> universities) {
        this.universities = universities;
        this.context = context;
        this.universityFilteredList = universities;
        filter = new UniversityFilter(universities, this);
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // this method is to bind the components of the layout to the user in parse
        Log.d("Adapter", "On Bind is called");
       String university = universityFilteredList.get(position).getString("name");
       viewHolder.tvUniversity.setText(university);
    }


//    public void setList(List<University> list){
//        universityFilteredList = list;
//    }
//
//    public void filterList(String text){
//        filter.
//    }
    @Override
    public int getItemCount() {
       Log.d("Adapter", "Item Count is called "+ universities.size());
        return universities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvUniversity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("Adapter", "Viewholder is called");
            tvUniversity = itemView.findViewById(R.id.tvUniversity);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
