package com.example.appchatapplication.modellayer.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.appchatapplication.activities.home.StartActivity;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthHelper {

    private Context mContext;
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private String mcurrent_user_id;

    public FirebaseAuthHelper(Context context) {
        this.mContext = context;
        mActivity = (Activity) context;
        mAuth = FirebaseAuth.getInstance();
        mcurrent_user_id = mAuth.getCurrentUser().getUid();
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public String getMcurrent_user_id() {
        return mcurrent_user_id;
    }

    public void sendToStart() {
        Intent intent = new Intent(mContext.getApplicationContext(), StartActivity.class);
        mContext.startActivity(intent);
        mActivity.finish();
    }

    public void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
    }


}
