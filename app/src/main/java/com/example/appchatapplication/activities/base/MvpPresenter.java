package com.example.appchatapplication.activities.base;

public interface MvpPresenter<V extends MvpView> {

    void onAttach(V mvpView);

}
