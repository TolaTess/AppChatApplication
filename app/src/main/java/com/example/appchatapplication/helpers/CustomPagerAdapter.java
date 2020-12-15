package com.example.appchatapplication.helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.appchatapplication.fragment.chats.ChatFragment;
import com.example.appchatapplication.fragment.friends.FriendsFragment;
import com.example.appchatapplication.fragment.request.RequestFragment;

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
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 2:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
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
                return  "FRIENDS";
            case 1:
                return "CHATS";
            case 2:
                return "REQUESTS";
            default:
                return null;
        }
    }
}
