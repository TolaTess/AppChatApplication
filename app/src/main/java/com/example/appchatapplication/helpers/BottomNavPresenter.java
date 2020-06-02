package com.example.appchatapplication.helpers;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


/**
 * Created by tolaotesanya on 07/05/2020.
 */

public class BottomNavPresenter {

    private static final String TAG = "BottomNavPresenter";

    private Context mContext;
    private static int ACTIVITY_NUM;

    public BottomNavPresenter(Context mContext, int ACTIVITY_NUM) {
        this.mContext = mContext;
        this.ACTIVITY_NUM = ACTIVITY_NUM;
    }

    public void setupBottomNavigationView(BottomNavigationViewEx bottonView){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx =  bottonView;
        BottomNavViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        //enable the relevant activity to the nav icon display
        BottomNavViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        //check menu item (each icons - tabs) in nav bar
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
