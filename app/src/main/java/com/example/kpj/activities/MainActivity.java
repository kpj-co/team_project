package com.example.kpj.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.kpj.R;
import com.example.kpj.FragmentMainActivityAdapter;
import com.example.kpj.model.Course;

public class MainActivity extends AppCompatActivity {

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

        // Grab course title
        Course course =  getIntent().getParcelableExtra("selectedCourse");
        String message = "You are in " + course.getName();
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
