package com.example.appchatapplication;

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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    FirebaseUser mCurrentUser;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private CustomPagerAdapter customPagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("App Chat");

        if(mAuth.getCurrentUser() != null){
            mUserRef = FirebaseDatabase.getInstance()
                    .getReference().child("Users")
                    .child(mAuth.getCurrentUser().getUid());
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
        //check if user is logged in
        mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser == null)
        {
            sendToStart();
        } else {
            mUserRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCurrentUser = mAuth.getCurrentUser();

        if(mCurrentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendToStart() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        //finish so user can't come back to this page until sign in
        finish();
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
            FirebaseAuth.getInstance().signOut();
            sendToStart();
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
