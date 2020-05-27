package com.example.appchatapplication.activities.home;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.appchatapplication.R;
import com.example.appchatapplication.coordinator.IntentPresenter;
import com.example.appchatapplication.helpers.CustomPagerAdapter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.example.appchatapplication.modellayer.enums.ClassName;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ServerValue;

public class HomeActivity extends AppCompatActivity {

    private FirebaseDatabaseHelper databaseHelper;
    private IntentPresenter intentPresenter;

    private Context mContext = HomeActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new FirebaseDatabaseHelper();
        intentPresenter = new IntentPresenter(mContext);

        setupToolbar();
        onlineCheck();
        attachUI();

    }

    private void attachUI() {
        //Tabs
        ViewPager mViewPager = findViewById(R.id.main_view_pager);
        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(customPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void onlineCheck() {
        if(databaseHelper.getMcurrent_user_id() != null){
            databaseHelper.getmUserDatabase().child(databaseHelper.getMcurrent_user_id())
            .child(getString(R.string.online_tag)).setValue("true");
        }
    }

    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("App Chat");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //
        if (databaseHelper.getHelper().getMcurrent_user() == null) {
            intentPresenter.presentIntent(ClassName.Start, null, null);
        }
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
            databaseHelper.getHelper().getmAuth().signOut();
            databaseHelper.getmUserDatabase().child(databaseHelper.getMcurrent_user_id())
                    .child(getString(R.string.online_tag)).setValue(ServerValue.TIMESTAMP);
            intentPresenter.presentIntent(ClassName.Start, null, null);
        }
        if(item.getItemId() == R.id.main_setting_btn){
            intentPresenter.presentIntent(ClassName.Setting, null, null);
        }
        if(item.getItemId() == R.id.main_all_user_btn){
            intentPresenter.presentIntent(ClassName.AllUsers, null, null);
        }
        return true;
    }


}
