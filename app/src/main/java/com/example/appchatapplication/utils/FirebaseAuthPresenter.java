package com.example.appchatapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.appchatapplication.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class FirebaseAuthPresenter {

    private Context mContext;
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootDatabase;
    private DatabaseReference mUserDB;

    public FirebaseAuthPresenter(Context context) {
        this.mContext = context;
        mActivity = (Activity) context;
        //mAuth = FirebaseAuth.getInstance();
       // mUserDB = FirebaseDatabase.getInstance().getReference()
               // .child("Users").child(mAuth.getCurrentUser().getUid());
       /* mUserDatabase = FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid())*/
        //.child("online");
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void sendToStart() {
        Intent intent = new Intent(mContext.getApplicationContext(), StartActivity.class);
        mContext.startActivity(intent);
        mActivity.finish();
    }

    public void checkUserIsOnline(){
            mUserDB.child("online").setValue("true");
    }


    public void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
    }


}
