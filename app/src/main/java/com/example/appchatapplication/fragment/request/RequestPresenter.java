package com.example.appchatapplication.fragment.request;

import com.example.appchatapplication.coordinator.IntentPresenter;
import com.google.firebase.database.DatabaseReference;

public interface RequestPresenter {

    DatabaseReference getmUserReqDatabase();
    DatabaseReference getmFriendsReqDatabase();
    IntentPresenter getIntentReqPresenter();
}
