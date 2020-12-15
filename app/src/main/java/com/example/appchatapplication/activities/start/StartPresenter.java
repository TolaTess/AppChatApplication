package com.example.appchatapplication.activities.start;

import com.example.appchatapplication.activities.base.BasePresenter;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;

public class StartPresenter<V extends StartMvpView> extends BasePresenter<V> implements StartMvpPresenter<V> {

    public StartPresenter(DatabasePresenter dataManager) {
        super(dataManager);
    }

    @Override
    public void register() {
        getMvpView().openRegisterActivity();
    }

    @Override
    public void login() {
        getMvpView().openLoginActivity();
    }
}
