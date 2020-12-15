package com.example.appchatapplication.activities.auth.login;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appchatapplication.activities.base.BasePresenter;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginPresenter<V extends LoginMvpView> extends BasePresenter<V> implements LoginMvpPresenter<V> {

    public LoginPresenter(DatabasePresenter dataManager) {
        super(dataManager);
    }


    @Override
    public void startLogin(final Activity context, String email, String password) {
        getDataManager().getmAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            getMvpView().hideLoading();
                            getMvpView().openMainActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            getMvpView().hideLoading();
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
