package com.example.appchatapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.appchatapplication.account.SettingsActivity;
import com.example.appchatapplication.utils.CustomPagerAdapter;
import com.example.appchatapplication.utils.FirebaseAuthPresenter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;

    private FirebaseAuthPresenter presenter;

    private Toolbar mToolbar;
    private Context mContext = MainActivity.this;

    private ViewPager mViewPager;
    private CustomPagerAdapter customPagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new FirebaseAuthPresenter(mContext);
        presenter.setupFirebaseAuth();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("App Chat");

        if(presenter.getmAuth().getCurrentUser() != null){
            mUserDatabase = FirebaseDatabase.getInstance()
                    .getReference().child("Users")
                    .child(presenter.getmAuth().getCurrentUser().getUid());
            mUserDatabase.child("online").setValue("true");
        }
        //Tabs
        mViewPager = findViewById(R.id.main_view_pager);
        customPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(customPagerAdapter);
        tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(presenter.getmAuth().getCurrentUser() == null)
       presenter.sendToStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_item, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout_btn){
            presenter.getmAuth().signOut();
            mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
            presenter.sendToStart();
        }
        if(item.getItemId() == R.id.main_setting_btn){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.main_all_user_btn){
            Intent usersIntent = new Intent(MainActivity.this, AllUserActivity.class);
            startActivity(usersIntent);
        }
        return true;
    }


}
