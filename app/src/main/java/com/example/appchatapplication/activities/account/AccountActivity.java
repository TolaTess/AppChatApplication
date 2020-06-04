package com.example.appchatapplication.activities.account;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appchatapplication.R;
import com.example.appchatapplication.coordinator.IntentPresenter;
import com.example.appchatapplication.helpers.BottomNavPresenter;
import com.example.appchatapplication.modellayer.database.FirebaseAuthHelper;
import com.example.appchatapplication.modellayer.enums.ClassName;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 2;
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    private IntentPresenter intentPresenter;
    private Context mContext = AccountActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount);

        FirebaseAuthHelper helper = new FirebaseAuthHelper();
        intentPresenter = new IntentPresenter(mContext);

        setupToolbar();
        setupBottomNav();

        mDisplayImage = findViewById(R.id.img_setting);
        mName = findViewById(R.id.text_display_name);
        mStatus = findViewById(R.id.text_status);
        Button mSettings = findViewById(R.id.setting_btn);

        fetch(helper, mSettings);

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fetch(FirebaseAuthHelper helper, Button mSettings) {
        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(helper.getMcurrent_user().getUid());
        myDatabaseRef.keepSynced(true);

        //Value Event Listener to get the data from database
        myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                //ensures default image stays on screen
                if (!image.equals("default")) {
                    //placeholder holds a picture on file before the getting database
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(mDisplayImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    //if offline image load fails load online
                                    Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(mDisplayImage);
                                }
                            });
                }
                mName.setText(name);
                mStatus.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String statusValue = mStatus.getText().toString();
                intentPresenter.presentIntent(ClassName.Setting, statusValue, null);

            }
        });
    }

    private void setupBottomNav() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavPresenter bottomNavPresenter = new BottomNavPresenter(mContext, ACTIVITY_NUM);
        bottomNavPresenter.setupBottomNavigationView(bottomNavigationViewEx);
    }
}