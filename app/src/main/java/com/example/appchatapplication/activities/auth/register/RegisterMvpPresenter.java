package com.example.appchatapplication.activities.auth.register;


import android.app.Activity;

import com.example.appchatapplication.activities.base.MvpPresenter;


public interface RegisterMvpPresenter<V extends RegisterMvpView> extends MvpPresenter<V> {

    void startRegister(final Activity context, final String display_name, String email, String password);
}
