package com.example.appchatapplication.activities.base;

import com.example.appchatapplication.modellayer.database.DatabasePresenter;


public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    DatabasePresenter mDataManager;
    private V mMvpView;


    public BasePresenter(DatabasePresenter dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void onAttach(V mvpView) {
        mMvpView = mvpView;
    }

    public V getMvpView() {
        return mMvpView;
    }

    public DatabasePresenter getDataManager() {
        return mDataManager;
    }
}
