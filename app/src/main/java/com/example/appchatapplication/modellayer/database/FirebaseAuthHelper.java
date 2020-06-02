package com.example.appchatapplication.modellayer.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthHelper {

    private FirebaseAuth mAuth;
    private FirebaseUser mcurrent_user;

    public FirebaseAuthHelper() {
        mAuth = FirebaseAuth.getInstance();
        mcurrent_user = mAuth.getCurrentUser();
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public FirebaseUser getMcurrent_user() {
        return mcurrent_user;
    }

}
