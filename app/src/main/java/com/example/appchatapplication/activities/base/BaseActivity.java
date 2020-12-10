package com.example.appchatapplication.activities.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.appchatapplication.R;
import com.example.appchatapplication.helpers.BottomNavPresenter;
import com.example.appchatapplication.helpers.CustomPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BaseActivity extends AppCompatActivity implements MvpView  {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void setUpToolbar(Toolbar toolbar, int message) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.bringToFront();
    }

    @Override
    public void setUpToolbar(Toolbar toolbar, String message) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.bringToFront();
    }

    @Override
    public void setupBottomNav(Context context, int ACTIVITY_NUM) {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavPresenter bottomNavPresenter = new BottomNavPresenter(context, ACTIVITY_NUM);
        bottomNavPresenter.setupBottomNavigationView(bottomNavigationViewEx);
    }

    @Override
    public void attachUI(ViewPager viewPager, TabLayout tabLayout) {
        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(customPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
