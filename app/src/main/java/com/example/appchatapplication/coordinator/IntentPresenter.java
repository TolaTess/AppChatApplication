package com.example.appchatapplication.coordinator;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.appchatapplication.activities.IdeaActivity;
import com.example.appchatapplication.activities.allusers.AllUserActivity;
import com.example.appchatapplication.activities.auth.login.LoginActivity;
import com.example.appchatapplication.activities.auth.register.RegisterActivity;
import com.example.appchatapplication.activities.chat.ChatActivity;
import com.example.appchatapplication.activities.home.HomeActivity;
import com.example.appchatapplication.activities.profile.ProfileActivity;
import com.example.appchatapplication.activities.account.AccountActivity;
import com.example.appchatapplication.activities.start.StartActivity;
import com.example.appchatapplication.activities.setting.SettingActivity;
import com.example.appchatapplication.modellayer.enums.ClassName;

public class IntentPresenter {
    private static final String TAG = "IntentPresenter";

    private Context mContext;
    private Activity mActivity;

    public IntentPresenter(Context mContext) {
        this.mContext = mContext;
        mActivity = (Activity) mContext;

    }

    public void presentIntent(ClassName activity, String userid, String extra) {
        switch (activity) {
            case Home:
                Intent homeIntent = new Intent(mContext, HomeActivity.class);
                homeIntent.putExtra("user_id", userid);// send user id to use it to get all other info in db
                mContext.startActivity(homeIntent);
                break;
            case Account:
                Intent settingIntent = new Intent(mContext, AccountActivity.class);
                settingIntent.putExtra("user_id", userid);// send user id to use it to get all other info in db
                mContext.startActivity(settingIntent);
                break;
            case AllUsers:
                Intent allUserIntent = new Intent(mContext, AllUserActivity.class);
                allUserIntent.putExtra("user_id", userid);
                allUserIntent.putExtra("username", extra);// send user id to use it to get all other info in db
                mContext.startActivity(allUserIntent);
                break;
            case Setting:
                Intent statusIntent = new Intent(mContext, SettingActivity.class);
                statusIntent.putExtra("status_value", userid);
                mContext.startActivity(statusIntent);
                break;
            case Chats:
                Intent chatIntent = new Intent(mContext, ChatActivity.class);
                chatIntent.putExtra("user_id", userid);
                chatIntent.putExtra("username", extra);// send user id to use it to get all other info in db
                mContext.startActivity(chatIntent);
                break;
            case Start:
                Intent startIntent = new Intent(mContext, StartActivity.class);
                mContext.startActivity(startIntent);
                mActivity.finish();
                break;
            case Profile:
                Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                profileIntent.putExtra("user_id", userid);
                profileIntent.putExtra("username", extra);// send user id to use it to get all other info in db
                mContext.startActivity(profileIntent);
                break;
            case Register:
                Intent registerIntent = new Intent(mContext, RegisterActivity.class);
                mContext.startActivity(registerIntent);
                break;
            case Login:
                Intent loginIntent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(loginIntent);
                break;
            case Ideas:
                Intent ideasIntent = new Intent(mContext, IdeaActivity.class);
                mContext.startActivity(ideasIntent);
                break;
        }
    }

}
