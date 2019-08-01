package com.example.kpj.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.kpj.R;
import com.example.kpj.model.Course;
import com.example.kpj.model.UserCourseRelation;
import com.example.kpj.utils.CourseAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity {

    private ArrayList<Course> filterCourses;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        this.context = CourseListActivity.this;
        filterCourses = new ArrayList<>();
        // TODO - Uncomment later
        findCoursesByUserId(ParseUser.getCurrentUser());
        // set up recycler view
        recyclerView = findViewById(R.id.rvCourse);
        // set the adapter
        adapter = new CourseAdapter(context, filterCourses);
        // attach adapter to recycler view
        recyclerView.setAdapter(adapter);
        // set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        Toast.makeText(context, "IN CourseListActivity", Toast.LENGTH_LONG).show();
    }

    private void findCoursesByUserId(ParseUser user){
        final UserCourseRelation.Query userCourseRelationQuery = new UserCourseRelation.Query();
        userCourseRelationQuery.whereEqualTo("user", user);
        userCourseRelationQuery.findInBackground(new FindCallback<UserCourseRelation>() {
            @Override
            public void done(List<UserCourseRelation> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); i++){
                        Course course = (Course) objects.get(i).getCourse();
                        filterCourses.add(course);
                        adapter.notifyItemInserted(filterCourses.size() - 1);
                        try {
                            Log.d("UserCourseListFragment", "List of courses:" + course.fetchIfNeeded().getString("name"));
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
