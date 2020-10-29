package com.example.appchatapplication.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.appchatapplication.R;
import com.example.appchatapplication.helpers.BottomNavPresenter;
import com.example.appchatapplication.helpers.CustomPagerAdapterIdeas;
import com.example.appchatapplication.helpers.DataShareHolder;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class IdeaActivity extends AppCompatActivity {
    private static final String TAG = "IdeaActivity";

    private static final int ACTIVITY_NUM = 0;
    private Context mContext = IdeaActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea);

        getUserName();
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
        Toolbar mToolbar = findViewById(R.id.idea_main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Challenge Yourself");
    }

    private void getUserName(){
        DatabasePresenter databaseHelper = new FirebaseDatabaseHelper();
        databaseHelper.getmUserDatabase().child(databaseHelper.getMcurrent_user_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                DataShareHolder.getInstance().setUsername(name);
                DataShareHolder.getInstance().setUserImage(image);
                DataShareHolder.getInstance().setStatus(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
