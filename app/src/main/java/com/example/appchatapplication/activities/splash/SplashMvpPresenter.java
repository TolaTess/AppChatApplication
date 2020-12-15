package com.example.appchatapplication.activities.splash;


import com.example.appchatapplication.activities.base.MvpPresenter;


public interface SplashMvpPresenter<V extends SplashMvpView> extends MvpPresenter<V> {

    void decideNextActivity();


}
