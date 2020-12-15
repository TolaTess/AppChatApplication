package com.example.appchatapplication.activities.splash;

import android.os.Handler;

import com.example.appchatapplication.activities.base.BasePresenter;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;

public class SplashPresenter<V extends SplashMvpView> extends BasePresenter<V> implements SplashMvpPresenter<V> {

    private static final int SPLASH_SCREEN = 3000;

    public SplashPresenter(DatabasePresenter dataManager) {
        super(dataManager);
    }

    @Override
    public void decideNextActivity() {
        if (getDataManager().getMcurrent_user_id() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   getMvpView().openMainActivity();
                }
            }, SPLASH_SCREEN);
        } else {
            getMvpView().openStartActivity();
        }
    }


}
