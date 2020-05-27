package com.example.appchatapplication.fragment.chats;

import com.example.appchatapplication.coordinator.IntentPresenter;
import com.google.firebase.database.DatabaseReference;

public interface ChatPresenter {

    DatabaseReference getmConvDatabase();
    DatabaseReference getmUserChatDatabase();
    DatabaseReference getmMessageDatabase();
    IntentPresenter getIntentFriendPresenter();
}
