package com.example.appchatapplication.activities.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.appchatapplication.R;
import com.example.appchatapplication.activities.base.BaseActivity;
import com.example.appchatapplication.coordinator.IntentPresenter;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.example.appchatapplication.modellayer.enums.ClassName;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends BaseActivity {

    private static final int ACTIVITY_NUM = 3;

    @BindView(R.id.img_setting)
    CircleImageView mDisplayImage;
    @BindView(R.id.text_display_name)
    TextView mName;
    @BindView(R.id.text_status)
    TextView mStatus;
    @BindView(R.id.setting_btn)
    Button mSettings;

    private Unbinder unbinder;

    private IntentPresenter intentPresenter;
    private DatabasePresenter presenter;
    private Context mContext = AccountActivity.this;

    public static Intent getStartIntent(Context context){
        Intent intent = new Intent(context, AccountActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount);
        unbinder = ButterKnife.bind(this);

        presenter = new FirebaseDatabaseHelper();
        intentPresenter = new IntentPresenter(mContext);

        Toolbar toolbar = findViewById(R.id.setting_toolbar);
        setUpToolbar(toolbar, "Account Settings");
        setupBottomNav(this, ACTIVITY_NUM);

        fetch();

    }

    private void fetch() {
        DatabaseReference mUserDatabase;
            mUserDatabase = presenter.getmUserDatabase().child(presenter.getMcurrent_user_id());

        //Value Event Listener to get the data from database
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                    //Ensures default image stays on screen
                    if (!image.equals("default")) {
                        //Ensure placeholder stays on screen until image downloads from database
                        Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(mDisplayImage);
                    }
                    mName.setText(name);
                    mStatus.setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //persist data
                String statusValue = mStatus.getText().toString();
                String dNameValue = mName.getText().toString();
                intentPresenter.presentIntent(ClassName.Account, statusValue, dNameValue);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}