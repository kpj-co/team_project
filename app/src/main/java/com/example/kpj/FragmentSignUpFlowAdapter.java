package com.example.kpj;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kpj.fragments.SelectCoursesFragment;
import com.example.kpj.fragments.SignUpFragment;

public class FragmentSignUpFlowAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNTER = 3;
    String[] tabTitles = new String[]{"Sign up", "University", "Select courses"};
    private Context context;
    public FragmentSignUpFlowAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return SignUpFragment.newInstance(position + 1);



            case 1:
                //TODO : CHANGE THIS TO UNIVERSITY FRAGMENT
               return SignUpFragment.newInstance(position + 1);

            case 2:
                return SelectCoursesFragment.newInstance(position + 1);


            default:
                return null;


        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNTER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //get title based on page position
        return tabTitles[position];
    }
}
