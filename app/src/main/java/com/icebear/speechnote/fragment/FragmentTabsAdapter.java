package com.icebear.speechnote.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentTabsAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;


    public FragmentTabsAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ListNotefrg tab1 = new ListNotefrg();
                return tab1;
            case 1:
                Reminderfrg reminderfrg = new Reminderfrg();
                return  reminderfrg;
            case 2:
                Categoryfrg tab2 = new Categoryfrg();
                return tab2;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
