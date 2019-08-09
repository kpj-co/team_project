package com.example.kpj.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.kpj.R;
import com.example.kpj.fragments.SelectCoursesFragment;
import com.example.kpj.fragments.SignUpFragment;
import com.example.kpj.fragments.UniversityFragment;
import com.example.kpj.model.Course;
import com.example.kpj.model.University;
import com.example.kpj.model.User;
import com.example.kpj.model.UserCourseRelation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.File;
import java.util.List;

public class SignupFlowActivity extends AppCompatActivity {

    ParseUser user = new ParseUser();

    private File photoFile;

    private University selectedUniversity;

    private String photo;

    public void onAttachFragment(final Fragment fragment){
        if(fragment instanceof SignUpFragment) {
            SignUpFragment signUpFragment = (SignUpFragment) fragment;
            signUpFragment.setSignUpData(new SignUpFragment.SignUpFragmentListener() {
                @Override
                public void onSignUpSet(String username, String email, String password) {
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);

                }
            });
        }

        else if(fragment instanceof UniversityFragment){
            UniversityFragment universityFragment = (UniversityFragment) fragment;
            universityFragment.setUniversityData(new UniversityFragment.UniversityFragmentListener() {
                @Override
                public void onSignUpUniversity(final String university) {
                    University.Query query = new University.Query();
                    getUserUniversity(university, query);
                }
            });
        }
        else if(fragment instanceof SelectCoursesFragment){
            final SelectCoursesFragment selectCoursesFragment = (SelectCoursesFragment) fragment;
            selectCoursesFragment.setUserSelectedCoursesListener(new SelectCoursesFragment.SelectedCoursesListener() {
                @Override
                public void onSelectCourses(List<Course> selectedCourses) {
                    SignUp(user);
                    saveUserCourseRelations(selectedCourses);

                }
            });
        }
    }

    private void saveUserCourseRelations(List<Course> selectedCourses) {
        for (Course course : selectedCourses) {
            UserCourseRelation userCourseRelation = new UserCourseRelation();
            userCourseRelation.setUser(user);
            userCourseRelation.setCourse(course);
            userCourseRelation.saveInBackground();
        }
        goToCourseListActivity();
    }

    private void getUserUniversity(String university, University.Query query) {
        query.whereEqualTo("name", university);
        query.findInBackground(new FindCallback<University>() {
            @Override
            public void done(List<University> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); i++){
                        selectedUniversity = (University) objects.get(i);
                        user.put("University", selectedUniversity);
                    }
                }
                user.saveInBackground();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_flow);
        if (savedInstanceState == null) {
            Fragment signUpFragment = new SignUpFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, signUpFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void SignUp(final ParseUser user){
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    saveProfilePic();
                } else {
                    Log.e("SignUpActivity", "Login Failed" + e);
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Sign Up failed. Username or email in use", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void goToCourseListActivity() {
        Intent intent = new Intent(SignupFlowActivity.this, CourseListActivity.class);
        startActivity(intent);
        finish();
    }

    private void getSharedPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        photo = prefs.getString("photo", "");
        if (photo != null) {
            photoFile = new File(photo);
        }
        else{
            return;
        }
    }

    private void saveProfilePic() {
        getSharedPrefs();
        if (photoFile != null) {
            ParseFile parseFile = new ParseFile(photoFile);
            //send profile photo to parse
            user.put(User.KEY_PROFILE, parseFile);
            //save in background thread
        }
        Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_LONG).show();
    }
}