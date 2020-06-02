package com.example.appchatapplication.helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.appchatapplication.fragment.alluser.AllUserFragment;
import com.example.appchatapplication.fragment.challenges.ChallengesFragment;

public class CustomPagerAdapterIdeas extends FragmentPagerAdapter {

    private static int TAB_NUM = 2;

    public CustomPagerAdapterIdeas(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChallengesFragment challengesFragment = new ChallengesFragment();
                return challengesFragment;
            case 1:
                AllUserFragment allUserFragment = new AllUserFragment();
                return allUserFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TAB_NUM;
    }

    //set the title for each fragment
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Find a Challenges";
            case 1:
                return "Connect with Others";
            default:
                return null;
        }
    }
}
