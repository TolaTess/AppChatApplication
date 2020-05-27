package com.example.appchatapplication.fragment.request;

import android.content.Context;

import com.example.appchatapplication.coordinator.IntentPresenter;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.google.firebase.database.DatabaseReference;

public class RequestPresenterImpl implements RequestPresenter {

    private DatabaseReference mFriendsReqDatabase;
    private DatabaseReference mUserDatabase;
    private IntentPresenter intentReqPresenter;

    public RequestPresenterImpl(Context context) {
        DatabasePresenter databasePresenter = new FirebaseDatabaseHelper();
        intentReqPresenter = new IntentPresenter(context);
        mFriendsReqDatabase = databasePresenter.getmRootRef().child("Friend_Req")
                .child(databasePresenter.getMcurrent_user_id());
        mFriendsReqDatabase.keepSynced(true);
        mUserDatabase = databasePresenter.getmRootRef()
                .child("Users");
        mUserDatabase.keepSynced(true);

    }

    @Override
    public DatabaseReference getmUserReqDatabase() {
        return mUserDatabase;
    }

    @Override
    public DatabaseReference getmFriendsReqDatabase() {
        return mFriendsReqDatabase;
    }

    @Override
    public IntentPresenter getIntentReqPresenter() {
        return intentReqPresenter;
    }
}
