package com.example.kpj.activities;

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
import com.example.kpj.model.University;
import com.example.kpj.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.File;
import java.util.List;

public class SignupFlowActivity extends AppCompatActivity {

    ParseUser user = new ParseUser();

    File photoFile;

    public void onAttachFragment(final Fragment fragment){
        if(fragment instanceof SignUpFragment) {
            SignUpFragment signUpFragment = (SignUpFragment) fragment;
            signUpFragment.setSignUpData(new SignUpFragment.SignUpFragmentListener() {
                @Override
                public void onSignUpSet(String username, String password, String email) {
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
                    query.whereEqualTo("name", university);
                    query.findInBackground(new FindCallback<University>() {
                        @Override
                        public void done(List<University> objects, ParseException e) {
                            if(e == null){
                                for(int i = 0; i < objects.size(); i++){
                                    University selectedUniversity = (University) objects.get(i);
                                    user.put("University", selectedUniversity);
                                }
                            }
                        }
                    });
                }
            });
        }
        else if(fragment instanceof SelectCoursesFragment){
            final SelectCoursesFragment selectCoursesFragment = (SelectCoursesFragment) fragment;
            selectCoursesFragment.setUserSelectedCoursesData(new SelectCoursesFragment.SelectedCoursesListener() {
                @Override
                public void onSelectCourse() {
                    setSignUp();
                    selectCoursesFragment.addSelectedCourses(user);
                }
            });
        }
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

    public void setSignUp(){
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    saveNewProfileAssetsToParse();
                } else {
                    Log.e("SignUpActivity", "Login Failed" + e);
                    e.printStackTrace();
                }
            }
        });
    }

    public void getSharedPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String photo = prefs.getString("photo", "");
        photoFile = new File(photo);
    }
    public void saveNewProfileAssetsToParse() {
        getSharedPrefs();
        if (photoFile != null) {
            ParseFile parseFile = new ParseFile(photoFile);
            //send profile photo to parse
            user.put(User.KEY_PROFILE, parseFile);
            //save in background thread
            user.saveInBackground();
        }
        Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_LONG).show();
    }

}

