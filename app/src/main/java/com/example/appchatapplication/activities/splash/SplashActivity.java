package com.example.appchatapplication.activities.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.appchatapplication.R;
import com.example.appchatapplication.activities.IdeaActivity;
import com.example.appchatapplication.activities.base.BaseActivity;
import com.example.appchatapplication.activities.start.StartActivity;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;

public class SplashActivity extends BaseActivity implements SplashMvpView {


    TextView title;
    ImageView logo;
    Animation topAnim;
    SplashPresenter mPresenter;

    public static Intent getStartIntent(Context context){
        Intent intent = new Intent(context, SplashActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        DatabasePresenter presenter = new FirebaseDatabaseHelper();

        mPresenter = new SplashPresenter(presenter);
        mPresenter.onAttach(this);
        
        title = findViewById(R.id.text_spl);
        logo = findViewById(R.id.spl_logo);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);

        title.setAnimation(topAnim);
        logo.setAnimation(topAnim);

        mPresenter.decideNextActivity();

    }

    @Override
    public void openMainActivity() {
        Intent intent = IdeaActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void openStartActivity() {
        //call the static intent from the act of interest
        Intent intent = StartActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }


}
