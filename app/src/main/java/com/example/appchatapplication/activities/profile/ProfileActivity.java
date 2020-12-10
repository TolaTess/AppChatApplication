package com.example.appchatapplication.activities.profile;

import android.app.ProgressDialog;
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
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.example.appchatapplication.modellayer.enums.ClassName;
import com.example.appchatapplication.modellayer.enums.State;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";

    private CircleImageView mProfileImage;
    private TextView mProfileStatus;
    private TextView mProfileFriendsCount;
    private Button mRequestButton, mDeclineButton;
    private ProgressDialog progressDialog;

    private FirebaseDatabaseHelper databaseHelper;
    private Context mContext = ProfileActivity.this;
    private IntentPresenter intentPresenter;

    private State mCurrent_state;
    private String mName;

    public static Intent getStartIntent(Context context){
        Intent intent = new Intent(context, ProfileActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mName = getIntent().getStringExtra("username");

        databaseHelper = new FirebaseDatabaseHelper();
        intentPresenter = new IntentPresenter(mContext);

        String user_id_from_all_user = getIntent().getStringExtra("user_id");
        final String user_id = getUser_id(user_id_from_all_user);

        Toolbar mToolbar = findViewById(R.id.profile_toolbar);
        setUpToolbar(mToolbar, mName);
        attachUI();

        fetch(user_id);
    }



    private void fetch(final String user_id) {
        mCurrent_state = State.not_friend;
        mDeclineButton.setVisibility(View.INVISIBLE);
        mDeclineButton.setEnabled(false);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we load the user data. ");
        progressDialog.setCanceledOnTouchOutside(false);

        databaseHelper.getmUserDatabase().child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                mProfileStatus.setText(status);

                Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(mProfileImage);

                //friends request feature
                databaseHelper.getmRootRef().child("Friend_Req").child(databaseHelper.getMcurrent_user_id())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(user_id)) {
                                    String req_type = dataSnapshot.child(user_id).child("request_type")
                                            .getValue().toString();
                                    if (req_type.equals(getString(R.string.received_req))) {
                                        mCurrent_state = State.request_received;
                                        mRequestButton.setText(R.string.accept_req);
                                        mDeclineButton.setVisibility(View.VISIBLE);
                                        mDeclineButton.setEnabled(true);

                                    } else if (req_type.equals(getString(R.string.sent_req))) {
                                        mCurrent_state = State.request_sent;
                                        mRequestButton.setText(R.string.cancel_friend_req);
                                    }
                                    progressDialog.dismiss();
                                } else {
                                    databaseHelper.getmRootRef().child(getString(R.string.friends_table)).child(databaseHelper.getMcurrent_user_id())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(user_id)) {
                                                        mCurrent_state = State.friend;
                                                        mRequestButton.setText("Remove " + mName);
                                                        progressDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                databaseHelper.getmRootRef().child(getString(R.string.friends_table)).child(user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long friendsCount = dataSnapshot.getChildrenCount();
                                String stringCount = String.valueOf(friendsCount);
                                String friendsCountDisplay = getString(R.string.total_friends) + " " + stringCount;
                                mProfileFriendsCount.setText(friendsCountDisplay);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequestButton.setEnabled(false);

                //friends request section
                if (databaseHelper.getMcurrent_user_id().equals(user_id)) {
                    mRequestButton.setText(R.string.accounts); // bug
                    intentPresenter.presentIntent(ClassName.Account, databaseHelper.getMcurrent_user_id(), null);
                } else {

                    switch (mCurrent_state) {
                        case not_friend:
                            //Request Friends
                          databaseHelper.loadDatabase(mContext, user_id, State.not_friend);
                            btnSettings();
                            mCurrent_state = State.request_sent;
                            mRequestButton.setText(R.string.cancel_friend_req);
                            break;
                        case friend:
                            //remove Friend from Friend DB - UnFriend
                            databaseHelper.loadDatabase(mContext, user_id, State.friend);
                            btnSettings();
                            mCurrent_state = State.not_friend;
                            mRequestButton.setText(R.string.send_friend_request);
                            break;
                        case request_received:
                            //Accept Friend, Delete Friend Req data and data to Friends DB
                            databaseHelper.loadDatabase(mContext, user_id, State.request_received);
                            btnSettings();
                            mCurrent_state = State.friend;
                            String removefriendsDisplay = getString(R.string.remove_name) + getString(R.string.space) + mName;
                            mRequestButton.setText(removefriendsDisplay);
                            break;
                        case request_sent:
                            //Cancel Friend Request
                            cancelFriendRequest(user_id);
                    }

                }
            }
        });

        //Decline friends
        mDeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelFriendRequest(user_id);
            }
        });
    }

    private String getUser_id(String user_id_from_all_user) {
        if(user_id_from_all_user != null)
        return user_id_from_all_user;
        else
        return databaseHelper.getMcurrent_user_id();
    }

    private void attachUI() {
        //TextView mDisplayName = findViewById(R.id.profile_name);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileImage = findViewById(R.id.profile_image);
        mRequestButton = findViewById(R.id.profile_send_reg_btn);
        mDeclineButton = findViewById(R.id.decline_req_btn);
    }

    private void btnSettings() {
        mRequestButton.setEnabled(true);
        mDeclineButton.setVisibility(View.INVISIBLE);
        mDeclineButton.setEnabled(false);
    }

    private void cancelFriendRequest(String user_id) {
        databaseHelper.loadDatabase(mContext, user_id, State.request_sent);
        btnSettings();
        mCurrent_state = State.not_friend;
        mRequestButton.setText(R.string.send_friend_request);
    }



}

