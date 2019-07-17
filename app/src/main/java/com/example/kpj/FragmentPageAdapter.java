package com.example.kpj;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class FragmentPageAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] {"List", "Feed", "Chat", "Profile"};
    private Context context;

    public FragmentPageAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public int getCount() {return PAGE_COUNT;}

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CourseListFragment.newInstance(position + 1);
            case 1:
                return CourseFeedFragment.newInstance(position + 1);
            case 2:
                return MessageFragment.newInstance(position + 1);
            case 3:
                return ProfileFragment.newInstance(position + 1);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //get title based on page position
        return tabTitles[position];
    }

}
