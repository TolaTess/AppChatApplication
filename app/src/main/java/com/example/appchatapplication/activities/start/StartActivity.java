package com.example.appchatapplication.activities.start;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.appchatapplication.R;
import com.example.appchatapplication.activities.auth.RegisterActivity;
import com.example.appchatapplication.activities.auth.login.LoginActivity;
import com.example.appchatapplication.activities.base.BaseActivity;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;

public class StartActivity extends BaseActivity implements StartMvpView {

    Button registerBtn, loginBtn;
    StartPresenter mPresenter;

    public static Intent getStartIntent(Context context){
        Intent intent = new Intent(context, StartActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        DatabasePresenter presenter = new FirebaseDatabaseHelper();
        mPresenter = new StartPresenter(presenter);
        mPresenter.onAttach(this);

        registerBtn = findViewById(R.id.reg_button);
        loginBtn = findViewById(R.id.login_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.register();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login();
            }
        });

    }

    @Override
    public void openRegisterActivity() {
        Intent intent = RegisterActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void openLoginActivity() {
        Intent intent = LoginActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

 }
