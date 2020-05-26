package com.example.appchatapplication.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appchatapplication.activities.auth.LoginActivity;
import com.example.appchatapplication.activities.auth.RegisterActivity;
import com.example.appchatapplication.R;

public class StartActivity extends AppCompatActivity {

    Animation topAnimation, bottomAnimation, alphaAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        Button regBtn = findViewById(R.id.reg_button);
        Button loginBtn = findViewById(R.id.login_btn);
        TextView title = findViewById(R.id.text);
        ImageView logo = findViewById(R.id.start_logo);

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_animation);

        title.setAnimation(topAnimation);
        regBtn.setAnimation(bottomAnimation);
        loginBtn.setAnimation(bottomAnimation);
        logo.setAnimation(alphaAnimation);
        logo.setVisibility(View.INVISIBLE);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
