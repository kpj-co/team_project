package com.example.kpj;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kpj.fragments.CourseFeedFragment;
import com.example.kpj.fragments.MessageFragment;
import com.example.kpj.fragments.ProfileFragment;


public class FragmentMainActivityAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 3;
    String[] tabTitles = new String[] {"Feed", "Chat", "Profile"};
    private Context context;


    public FragmentMainActivityAdapter(FragmentManager fragmentManager, Context context)
                               {
        super(fragmentManager);
        this.context = context;

    }

    @Override
    public int getCount() {return PAGE_COUNT;}

    @Override
    public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return CourseFeedFragment.newInstance(position + 1);
                case 1:
                    return MessageFragment.newInstance(position + 1);
                case 2:
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
