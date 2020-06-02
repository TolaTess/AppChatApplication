package com.example.appchatapplication.fragment.alluser;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

public interface AllUserPresenter {

    void fetchAllUsers();
    FirebaseRecyclerAdapter getAdapter();

}
