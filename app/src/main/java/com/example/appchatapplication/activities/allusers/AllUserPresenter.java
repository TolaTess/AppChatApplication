package com.example.appchatapplication.activities.allusers;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

public interface AllUserPresenter {

    void fetchAllUsers();
    FirebaseRecyclerAdapter getAdapter();

}
