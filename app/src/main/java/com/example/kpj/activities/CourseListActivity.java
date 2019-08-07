package com.example.kpj.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    private TextView tvToCreateNewCourse;
    public Context context;
    private int REGISTER_COURSE_REQUEST = 1997;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        this.context = CourseListActivity.this;
        this.tvToCreateNewCourse = findViewById(R.id.tvToCreateNewCourse);
        filterCourses = new ArrayList<>();
        findCoursesByUserId(ParseUser.getCurrentUser());
        // set up recycler view
        recyclerView = findViewById(R.id.rvCourse);
        // set the adapter
        adapter = new CourseAdapter(context, filterCourses);
        // attach adapter to recycler view
        recyclerView.setAdapter(adapter);
        // set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
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
                        setCreateNewCourseListener();
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

    private void setCreateNewCourseListener() {
        tvToCreateNewCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, registerNewCourse.class);
                try {
                    intent.putExtra("uni", filterCourses.get(0).getUniversity());
                } catch (NullPointerException e) {
                    // do nothing
                }
                startActivityForResult(intent, REGISTER_COURSE_REQUEST);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REGISTER_COURSE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Course newCourse = data.getParcelableExtra("new course");
                if (newCourse != null) {
                    insertCourseToList(newCourse);
                } else {
                    // do nothing
                }
            }
        }
    }

    public void insertCourseToList(Course course) {
        filterCourses.add(course);
        adapter.notifyItemInserted(filterCourses.size() - 1);
    }
}
