package com.example.kpj.activities;

import android.support.v4.app.Fragment;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kpj.R;
import com.example.kpj.fragments.CourseListFragment;
import com.example.kpj.fragments.SelectCoursesFragment;
import com.example.kpj.fragments.SignUpFragment;
import com.example.kpj.fragments.UniversityFragment;

public class SignupFlowActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_flow);

        if (savedInstanceState == null) {
            Fragment fragment = new SignUpFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            String intentFragment = getIntent().getExtras().getString("loadThisFragment");
            switch (intentFragment) {
                case "COURSE LIST":
                    // Load corresponding fragment
                    Fragment fragment = new CourseListFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
            }



        }


    }

}
