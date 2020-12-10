/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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