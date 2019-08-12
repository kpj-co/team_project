package com.example.kpj.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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

    private ArrayList<Course> courses;

    private CourseAdapter adapter;

    private TextView tvToCreateNewCourse;

    private String university;

    public Context context;

    private int REGISTER_COURSE_REQUEST = 1997;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        this.context = CourseListActivity.this;
        this.tvToCreateNewCourse = findViewById(R.id.tvToCreateNewCourse);
        this.progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        TextView userUniversity = findViewById(R.id.tvSelectedUniversity);
        getSharedPrefs();
        userUniversity.setText(university);
        courses = new ArrayList<>();
        // set up recycler view
        RecyclerView recyclerView = findViewById(R.id.rvCourse);
        // set the adapter
        adapter = new CourseAdapter(context, courses);
        // attach adapter to recycler view
        recyclerView.setAdapter(adapter);
        // set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        queryCoursesByUserId();
    }

    private void queryCoursesByUserId(){
        UserCourseRelation.Query userCourseRelationQuery = new UserCourseRelation.Query();
        userCourseRelationQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        userCourseRelationQuery.include("course");
        userCourseRelationQuery.findInBackground(new FindCallback<UserCourseRelation>() {
            @Override
            public void done(List<UserCourseRelation> relations, ParseException e) {
                if(e == null){
                    for(UserCourseRelation relation : relations){
                        insertCourseToList((Course) relation.getCourse());
                    }
                }
                progressBar.setVisibility(View.GONE);
                setCreateNewCourseListener();
            }
        });
    }

    private void setCreateNewCourseListener() {
        tvToCreateNewCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, registerNewCourse.class);
                try {
                    intent.putExtra("uni", courses.get(0).getUniversity());
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
        courses.add(course);
        adapter.notifyItemInserted(courses.size() - 1);
        Log.d("CourseListActivity", "" + courses.size());
    }

    private void getSharedPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        university = prefs.getString("university", "");
    }
}
