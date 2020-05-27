package com.example.appchatapplication.fragment.friends;

import android.content.Context;

import com.example.appchatapplication.coordinator.IntentPresenter;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.google.firebase.database.DatabaseReference;

public class FriendPresenterImpl implements FriendPresenter {

    private IntentPresenter intentFriendPresenter;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUserDatabase;

    public FriendPresenterImpl(Context context) {
        DatabasePresenter databasePresenter = new FirebaseDatabaseHelper();
        intentFriendPresenter = new IntentPresenter(context);
        mFriendsDatabase = databasePresenter.getmRootRef().child("Friends")
                .child(databasePresenter.getMcurrent_user_id());
        mUserDatabase = databasePresenter.getmRootRef()
                .child("Users");
    }

    public IntentPresenter getIntentFriendPresenter() {
        return intentFriendPresenter;
    }

    public DatabaseReference getmFriendsDatabase() {
        return mFriendsDatabase;
    }

    public DatabaseReference getmUserDatabase() {
        return mUserDatabase;
    }
}
