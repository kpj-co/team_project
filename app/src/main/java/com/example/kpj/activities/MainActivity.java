package com.example.kpj.activities;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.kpj.R;
import com.example.kpj.FragmentMainActivityAdapter;
import com.example.kpj.model.Course;

public class MainActivity extends AppCompatActivity {

    private Course course;
    private final static String PREF_NAME = "sharedData";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentMainActivityAdapter(getSupportFragmentManager(),
                MainActivity.this));
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupSharedPreferences();
    }

    private void setupSharedPreferences() {
        // Grab course
        course = getIntent().getParcelableExtra("selectedCourse");
        if (course != null) {
            SharedPreferences settings = getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("courseName", course.getName());
            editor.apply();
        }
    }
}
