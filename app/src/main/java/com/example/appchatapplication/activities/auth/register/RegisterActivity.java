package com.example.appchatapplication.activities.auth.register;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.appchatapplication.R;
import com.example.appchatapplication.activities.IdeaActivity;
import com.example.appchatapplication.activities.base.BaseActivity;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends BaseActivity implements RegisterMvpView {
    private static final String TAG = "RegisterActivity";

    private RegisterPresenter presenter;

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;

    private ProgressDialog mRegProgress;

    public static Intent getStartIntent(Context context){
        Intent intent = new Intent(context, RegisterActivity.class);
        return intent;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        DatabasePresenter databaseHelper = new FirebaseDatabaseHelper();
        presenter = new RegisterPresenter(databaseHelper);
        presenter.onAttach(this);

        mRegProgress = new ProgressDialog(this);

        Toolbar mToolbar = findViewById(R.id.reg_toolbar);
        setUpToolbar(mToolbar, "Create Account");

        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);

        Button createButton = findViewById(R.id.reg_create_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRegistration();
            }
        });

    }

    @Override
    public void createRegistration() {
        String display_name = mDisplayName.getEditText().getText().toString();
        String email = mEmail.getEditText().getText().toString();
        String password = mPassword.getEditText().getText().toString();

        //check if information entered
        if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

            mRegProgress.setTitle("Registering User");
            mRegProgress.setMessage("Please wait while we create your account");
            mRegProgress.setCanceledOnTouchOutside(false);
            mRegProgress.show();

            presenter.startRegister(RegisterActivity.this, display_name, email, password);
        }
    }

    @Override
    public void openMainActivity() {
        Intent intent = IdeaActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

}
