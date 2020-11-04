package com.example.appchatapplication.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.appchatapplication.R;
import com.example.appchatapplication.activities.IdeaActivity;
import com.example.appchatapplication.activities.TodoActivity;
import com.example.appchatapplication.activities.account.AccountActivity;
import com.example.appchatapplication.activities.home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by tolaotesanya on 13/10/2019.
 */

public class BottomNavViewHelper {

    private static final String TAG = "BottomNavViewHelper";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        //bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        Intent intent1 = new Intent(context, IdeaActivity.class); //ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        break;
                    case R.id.todo:
                        Intent intent2 = new Intent(context, TodoActivity.class); //ACTIVITY_NUM = 1
                        context.startActivity(intent2);
                        break;
                    case R.id.chat:
                        Intent intent3 = new Intent(context, HomeActivity.class); //ACTIVITY_NUM = 1
                        context.startActivity(intent3);
                        break;
                    case R.id.profile:
                        Intent intent5 = new Intent(context, AccountActivity.class); //ACTIVITY_NUM = 2
                        context.startActivity(intent5);
                        break;
                }
                return false;
            }
        });
    }
}
