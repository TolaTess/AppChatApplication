package com.example.appchatapplication.activities.start;


import com.example.appchatapplication.activities.base.MvpPresenter;


public interface StartMvpPresenter<V extends StartMvpView> extends MvpPresenter<V> {

     void register();

     void login();


}
