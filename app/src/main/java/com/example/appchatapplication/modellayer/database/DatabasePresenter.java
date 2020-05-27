package com.example.appchatapplication.modellayer.database;

import android.content.Context;

import com.example.appchatapplication.modellayer.enums.State;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

public interface DatabasePresenter {

    FirebaseAuthHelper getHelper();
    String getMcurrent_user_id();
    DatabaseReference getmRootRef();
    DatabaseReference getmUserDatabase();
    Map registerUser(String display_name);
    Map setupDatabaseMap(String muser_id, State mapType);
    void loadDatabase(final Context context, String muser_id, State mapType);

}
