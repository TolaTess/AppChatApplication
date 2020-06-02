package com.example.appchatapplication.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.appchatapplication.R;
import com.example.appchatapplication.helpers.BottomNavPresenter;
import com.example.appchatapplication.helpers.CustomPagerAdapterIdeas;
import com.google.android.material.tabs.TabLayout;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class IdeaActivity extends AppCompatActivity {
    private static final String TAG = "IdeaActivity";

    private static final int ACTIVITY_NUM = 1;
    private Context mContext = IdeaActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea);

        attachUI();
        setupToolbar();
        setupBottomNav();
    }

    private void setupBottomNav() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavPresenter bottomNavPresenter = new BottomNavPresenter(mContext, ACTIVITY_NUM);
        bottomNavPresenter.setupBottomNavigationView(bottomNavigationViewEx);
    }

    private void attachUI() {
        //Tabs
        ViewPager mViewPager = findViewById(R.id.idea_view_pager);
        CustomPagerAdapterIdeas pagerAdapterIdeas = new CustomPagerAdapterIdeas(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapterIdeas);
        TabLayout tabLayout = findViewById(R.id.idea_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.idea_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Challenge Yourself");
    }

}
