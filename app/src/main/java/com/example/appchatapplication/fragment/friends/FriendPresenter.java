package com.example.appchatapplication.fragment.friends;

import com.example.appchatapplication.coordinator.IntentPresenter;
import com.google.firebase.database.DatabaseReference;

public interface FriendPresenter {

    DatabaseReference getmUserDatabase();
    DatabaseReference getmFriendsDatabase();
    IntentPresenter getIntentFriendPresenter();
}
