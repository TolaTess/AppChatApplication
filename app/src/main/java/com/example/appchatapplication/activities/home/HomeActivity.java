package com.example.appchatapplication.activities.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.appchatapplication.R;
import com.example.appchatapplication.activities.base.BaseActivity;
import com.example.appchatapplication.activities.splash.SplashActivity;
import com.example.appchatapplication.coordinator.IntentPresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.example.appchatapplication.modellayer.enums.ClassName;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ServerValue;

public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 2;

    private FirebaseDatabaseHelper databaseHelper;
    private IntentPresenter intentPresenter;

    private Context mContext = HomeActivity.this;

    public static Intent getStartIntent(Context context){
        Intent intent = new Intent(context, HomeActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new FirebaseDatabaseHelper();
        intentPresenter = new IntentPresenter(mContext);

        Toolbar toolbar = findViewById(R.id.main_page_toolbar);
        setUpToolbar(toolbar, R.string.challenge);
        onlineCheck();
        ViewPager mViewPager = findViewById(R.id.main_view_pager);
        TabLayout tabLayout = findViewById(R.id.main_tabs);
        attachUI(mViewPager, tabLayout);
        setupBottomNav(this, ACTIVITY_NUM);

    }

    private void onlineCheck() {
        if(databaseHelper.getMcurrent_user_id() != null){
            databaseHelper.getmUserDatabase().child(databaseHelper.getMcurrent_user_id())
            .child(getString(R.string.online_tag)).setValue("true");
        }
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
            //intentPresenter.presentIntent(ClassName.Start, null, null);
            openSplashActivity();
        }
        return true;
    }

    public void openSplashActivity() {
        Intent intent = SplashActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

}
