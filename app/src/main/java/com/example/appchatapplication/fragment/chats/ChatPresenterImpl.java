package com.example.appchatapplication.fragment.chats;

import android.content.Context;

import com.example.appchatapplication.coordinator.IntentPresenter;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.google.firebase.database.DatabaseReference;

public class ChatPresenterImpl implements ChatPresenter{

    private DatabaseReference mConvDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mMessageDatabase;
    private IntentPresenter intentFriendPresenter;

    public ChatPresenterImpl(Context context) {
        DatabasePresenter databasePresenter = new FirebaseDatabaseHelper();
        intentFriendPresenter = new IntentPresenter(context);
        mConvDatabase = databasePresenter.getmRootRef().child("Chat")
                .child(databasePresenter.getMcurrent_user_id());
        mConvDatabase.keepSynced(true);
        mMessageDatabase = databasePresenter.getmRootRef()
                .child("Messages").child(databasePresenter.getMcurrent_user_id());
        mMessageDatabase.keepSynced(true);
        mUserDatabase = databasePresenter.getmRootRef()
                .child("Users");
        mUserDatabase.keepSynced(true);
    }

    public DatabaseReference getmConvDatabase() {
        return mConvDatabase;
    }

    public DatabaseReference getmUserChatDatabase() {
        return mUserDatabase;
    }

    public DatabaseReference getmMessageDatabase() {
        return mMessageDatabase;
    }

    public IntentPresenter getIntentFriendPresenter() {
        return intentFriendPresenter;
    }
}
