package com.example.appchatapplication.activities.auth.login;


import android.app.Activity;

import com.example.appchatapplication.activities.base.MvpPresenter;


public interface LoginMvpPresenter<V extends LoginMvpView> extends MvpPresenter<V> {

    void startLogin(final Activity context, String email, String password);

}
