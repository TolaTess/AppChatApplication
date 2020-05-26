package com.example.appchatapplication.coordinator;


import android.content.Context;
import android.content.Intent;

import com.example.appchatapplication.activities.chat.ChatActivity;
import com.example.appchatapplication.activities.home.StartActivity;
import com.example.appchatapplication.activities.profile.ProfileActivity;
import com.example.appchatapplication.activities.settings.SettingsActivity;
import com.example.appchatapplication.activities.status.StatusActivity;

public class IntentPresenter {
    private static final String TAG = "IntentPresenter";

    private Context mContext;

    public IntentPresenter(Context mContext) {
        this.mContext = mContext;
    }

    public void presentIntent(String activity, String userid, String extra){
        switch (activity){
            case "Start":
                Intent startIntent = new Intent(mContext, StartActivity.class);
                startIntent.putExtra("user_id", userid);// send user id to use it to get all other info in db
                mContext.startActivity(startIntent);
                break;
            case "Setting":
                Intent settingIntent = new Intent(mContext, SettingsActivity.class);
                settingIntent.putExtra("user_id", userid);// send user id to use it to get all other info in db
                mContext.startActivity(settingIntent);
                break;
            case "Profile":
            case "AllUsers":
                Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                profileIntent.putExtra("user_id", userid);
                profileIntent.putExtra("username", extra);// send user id to use it to get all other info in db
                mContext.startActivity(profileIntent);
                break;
            case "Status":
                Intent statusIntent = new Intent(mContext, StatusActivity.class);
                statusIntent.putExtra("status_value", userid);
                mContext.startActivity(statusIntent);
                break;
            case "Chats":
                Intent chatIntent = new Intent(mContext, ChatActivity.class);
                chatIntent.putExtra("user_id", userid);
                chatIntent.putExtra("username", extra);// send user id to use it to get all other info in db
                mContext.startActivity(chatIntent);
                break;
        }
    }

}
