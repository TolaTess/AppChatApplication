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

package com.example.appchatapplication.activities.auth.register;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appchatapplication.activities.base.BasePresenter;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterPresenter<V extends RegisterMvpView> extends BasePresenter<V> implements RegisterMvpPresenter<V> {

    public RegisterPresenter(DatabasePresenter dataManager) {
        super(dataManager);
    }

    @Override
    public void startRegister(final Activity context, final String display_name, String email, String password) {
        getDataManager().getmAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    final String userId = currentUser.getUid();

                    //send hashmap to database
                    getDataManager().getmUserDatabase().child(userId)
                            .setValue(getDataManager().registerUser(display_name)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //mRegProgress.dismiss();
                                getMvpView().openMainActivity();
                            }
                        }
                    });

                } else{
                    //mRegProgress.hide();
                    Toast.makeText(context, "An error occurred, please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
