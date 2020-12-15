package com.example.appchatapplication.activities.base;

import android.content.Context;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public interface MvpView {

    void setUpToolbar(Toolbar toolbar, String message);
    void setupBottomNav(Context context, int ACTIVITY_NUM);
    void attachUI(ViewPager viewPager, TabLayout tabLayout);
    void showLoading();
    void hideLoading();

}
