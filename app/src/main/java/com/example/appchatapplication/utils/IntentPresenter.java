package com.example.appchatapplication.utils;


import android.content.Context;
import android.content.Intent;

import com.example.appchatapplication.StartActivity;
import com.example.appchatapplication.account.ProfileActivity;
import com.example.appchatapplication.account.SettingsActivity;
import com.example.appchatapplication.account.StatusActivity;

public class IntentPresenter {
    private static final String TAG = "IntentPresenter";

    private Context mContext;

    public IntentPresenter(Context mContext) {
        this.mContext = mContext;
    }

    public void presentIntent(String activity, String extra){
        switch (activity){
            case "Start":
                Intent startIntent = new Intent(mContext, StartActivity.class);
                startIntent.putExtra("user_id", extra);// send user id to use it to get all other info in db
                mContext.startActivity(startIntent);
                break;
            case "Setting":
                Intent settingIntent = new Intent(mContext, SettingsActivity.class);
                settingIntent.putExtra("user_id", extra);// send user id to use it to get all other info in db
                mContext.startActivity(settingIntent);
                break;
            case "Profile":
                Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                profileIntent.putExtra("user_id", extra);// send user id to use it to get all other info in db
                mContext.startActivity(profileIntent);
                break;
            case "Status":
                Intent statusIntent = new Intent(mContext, StatusActivity.class);
                statusIntent.putExtra("status_value", extra);
                mContext.startActivity(statusIntent);
        }
    }

}
