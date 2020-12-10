package com.example.appchatapplication.activities.auth.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import com.example.appchatapplication.R;
import com.example.appchatapplication.activities.IdeaActivity;
import com.example.appchatapplication.activities.base.BaseActivity;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends BaseActivity implements LoginMvpView {
    private static final String TAG = "LoginActivity";

    LoginPresenter loginPresenter;

    private TextInputLayout mEmail;
    private TextInputLayout mPassword;

    private ProgressDialog mRegProgress;

    public static Intent getStartIntent(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DatabasePresenter databasePresenter = new FirebaseDatabaseHelper();
        loginPresenter = new LoginPresenter(databasePresenter);
        loginPresenter.onAttach(this);

        mRegProgress = new ProgressDialog(this);

        Toolbar mToolbar = findViewById(R.id.login_toolbar);


        setUpToolbar(mToolbar, R.id.login_toolbar);

        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        Button mCreateButton = findViewById(R.id.login_button);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClick();
            }

        });
    }


    @Override
    public void openMainActivity() {
        Intent intent = IdeaActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginButtonClick() {
        String email = mEmail.getEditText().getText().toString();
        String password = mPassword.getEditText().getText().toString();

        //check if information entered
        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

            mRegProgress.setTitle("Logging In");
            mRegProgress.setMessage("Please wait while we check your credentials.");
            mRegProgress.setCanceledOnTouchOutside(false);
            mRegProgress.show();

            loginPresenter.startLogin(this, email, password);
        }
    }

}
