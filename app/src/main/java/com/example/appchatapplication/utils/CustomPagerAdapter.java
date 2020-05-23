package com.example.appchatapplication.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.appchatapplication.business.ChatFragment;
import com.example.appchatapplication.business.FriendsFragment;
import com.example.appchatapplication.business.RequestFragment;

public class CustomPagerAdapter extends FragmentPagerAdapter {

    private static int TAB_NUM = 3;

    public CustomPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
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
                return "REQUESTS";
            case 1:
                return "CHATS";
            case 2:
                return  "FRIENDS";
            default:
                return null;
        }
    }
}
