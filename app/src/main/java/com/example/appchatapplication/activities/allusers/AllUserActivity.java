package com.example.appchatapplication.activities.allusers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.fragment.alluser.AllUserPresenter;
import com.example.appchatapplication.fragment.alluser.AllUserPresenterImpl;

public class AllUserActivity extends AppCompatActivity {
    private static final String TAG = "AllUserActivity";

    private RecyclerView mUserList;
    private AllUserPresenter allUserPresenter;
    private Context mContext = AllUserActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        setupToolbar();

        mUserList = findViewById(R.id.alluser_recycler);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
        allUserPresenter = new AllUserPresenterImpl(mContext, mUserList);
        allUserPresenter.fetchAllUsers();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        allUserPresenter.getAdapter().startListening();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        allUserPresenter.getAdapter().stopListening();
    }


}
