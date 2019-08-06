package com.example.kpj.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kpj.R;
import com.example.kpj.model.Course;
import com.example.kpj.model.University;
import com.example.kpj.model.UserCourseRelation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.nio.channels.InterruptedByTimeoutException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class CreateNewCourse extends AppCompatActivity {

    TextView tvLandingText, tvValidCourse;
    Button btnCourseRegister;
    EditText etNewCourseName;
    private University university;
    private String landingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_course);
        try {
            initializeVariables();
        } catch (ParseException e) {
            Toast.makeText(this, "unable to register right now", Toast.LENGTH_SHORT).show();
        }
        initializeViews();
        setListeners();
        bindContent();
    }

    private void initializeVariables() throws ParseException {
        this.university = getIntent().getParcelableExtra("uni");
        this.landingText = "Register a new course for " + university.fetchIfNeeded().get("name");
    }

    private void initializeViews() {
        tvLandingText = findViewById(R.id.tvLandingText);
        tvValidCourse = findViewById(R.id.tvValidCourse);
        etNewCourseName = findViewById(R.id.etNewCourseName);
        btnCourseRegister = findViewById(R.id.btnCourseRegister);
    }

    private void setListeners() {
//        etNewCourseName.setKeyListener(new KeyListener() {
//            @Override
//            public int getInputType() {
//                return 0;
//            }
//
//            @Override
//            public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
//               // check if user pressed enter
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    checkValidCourseName();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
//                return false;
//            }
//
//            @Override
//            public boolean onKeyOther(View view, Editable text, KeyEvent event) {
//                return false;
//            }
//
//            @Override
//            public void clearMetaKeyState(View view, Editable content, int states) {
//            }
//        });

        btnCourseRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidCourseName();
            }
        });
    }

    private void checkValidCourseName() {
//        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            if (etNewCourseName.getText().toString().length() != 0) {
                // check if there exits a course of same name for this university
                Course.Query query = new Course.Query();
                query.whereEqualTo(Course.KEY_UNIVERSITY, university);
                query.whereEqualTo(Course.KEY_NAME, etNewCourseName.getText().toString());
                query.findInBackground(new FindCallback<Course>() {
                    @Override
                    public void done(List<Course> courses, ParseException e) {
                        if (e == null) {
                            if (courses.size() == 0) {
                                registerCourse(etNewCourseName.getText().toString());
                            } else {
                                setFailureText(etNewCourseName.getText().toString());
                            }

                        }
                    }
                });
//                return true;
            }
//            return false;
//        }
//        return false;
    }

    private void setSuccessText(String text) {
        tvValidCourse.setText("Successfully registered  " + text + " for " + university.getName());
        // clear edit text
        etNewCourseName.setText("");
    }

    private void sendToCourseListActivity(Course newCourse) {
        Intent intent = new Intent( CreateNewCourse.this, CourseListActivity.class);
        intent.putExtra("new course", newCourse);
        startActivity(intent);
        finish();
    }

    private void setFailureText(String text) {
        tvValidCourse.setText("Course Already Exists! Can not register " + text);
    }

    private void registerCourse(String name) {
        Course newCourse = new Course();
        newCourse.setName(name);
        newCourse.setUniversity(university);
        newCourse.saveInBackground();

        UserCourseRelation newUserCourseRelation = new UserCourseRelation();
        newUserCourseRelation.setUser(ParseUser.getCurrentUser());
        newUserCourseRelation.setCourse(newCourse);
        newUserCourseRelation.saveInBackground();

        setSuccessText(name);
        sendToCourseListActivity(newCourse);
    }

    private void bindContent() {
        tvLandingText.setText(landingText);
    }

}
