package com.example.appchatapplication.activities.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appchatapplication.R;
import com.example.appchatapplication.coordinator.IntentPresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private static final String MESSAGE_TYPE_SENT = "sent";
    private static final String MESSAGE_TYPE_RECEIVED = "received";
    private static final String REQUEST_TYPE_SENT = "req_sent";
    private static final String REQUEST_TYPE_RECEIVED = "req_received";
    private static final String FRIEND = "friend";
    private static final String NOT_FRIEND = "not_friends";


    private ImageView mProfileImage;
    private TextView mDisplayName, mProfileStatus, mProfileFriendsCount;
    private Button mRequestButton, mDeclineButton;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    /* private DatabaseReference mUserDatabase;
     private DatabaseReference mFriendReqDatabase;
     private DatabaseReference mFriendsDatabase;
     private DatabaseReference mNotification;
     private DatabaseReference mRootRef;
     private DatabaseReference mUserRef;
     private FirebaseUser mCurrentuser;*/
    private FirebaseDatabaseHelper databasePresenter;
    private Context mContext = ProfileActivity.this;
    private IntentPresenter intentPresenter;

    private String mCurrent_state;
    private String mName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");
        mName = getIntent().getStringExtra("username");

        setupToolbar();
        databasePresenter = new FirebaseDatabaseHelper(mContext, user_id);
        intentPresenter = new IntentPresenter(mContext);

        mDisplayName = findViewById(R.id.profile_name);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileImage = findViewById(R.id.profile_image);
        mRequestButton = findViewById(R.id.profile_send_reg_btn);
        mDeclineButton = findViewById(R.id.decline_req_btn);

        mCurrent_state = NOT_FRIEND;

        mDeclineButton.setVisibility(View.INVISIBLE);
        mDeclineButton.setEnabled(false);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we load the user data. ");
        progressDialog.setCanceledOnTouchOutside(false);

        databasePresenter.getmUserProfileDatabase().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                mProfileStatus.setText(status);

                Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(mProfileImage);

                //friends request feature
                databasePresenter.getmRootRef().child("Friend_Req").child(databasePresenter.getMcurrent_user_id())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(user_id)) {
                                    String req_type = dataSnapshot.child(user_id).child("request_type")
                                            .getValue().toString();
                                    if (req_type.equals(MESSAGE_TYPE_RECEIVED)) {
                                        mCurrent_state = REQUEST_TYPE_RECEIVED;
                                        mRequestButton.setText(R.string.accept_req);
                                        mDeclineButton.setVisibility(View.VISIBLE);
                                        mDeclineButton.setEnabled(true);

                                    } else if (req_type.equals(MESSAGE_TYPE_SENT)) {
                                        mCurrent_state = REQUEST_TYPE_SENT;
                                        mRequestButton.setText(R.string.cancel_friend_req);
                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                        mDeclineButton.setEnabled(false);
                                    }
                                    progressDialog.dismiss();
                                } else {
                                    databasePresenter.getmRootRef().child("Friends").child(databasePresenter.getMcurrent_user_id())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(user_id)) {
                                                        mCurrent_state = FRIEND;
                                                        mRequestButton.setText("Remove " + mName);
                                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                                        mDeclineButton.setEnabled(false);
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

                databasePresenter.getmRootRef().child("Friends").child(user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long friendsCount = dataSnapshot.getChildrenCount();
                                String stringCount = String.valueOf(friendsCount);
                                String friendsCountDisplay = "Total Friends: " + stringCount;
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

                DatabaseReference newNotifRef = databasePresenter.getmRootRef().child("Notifications")
                        .child(user_id).push();
                String newNotifId = newNotifRef.getKey();

                //friends request section
                if (databasePresenter.getMcurrent_user_id().equals(user_id)) {
                    mRequestButton.setText(R.string.accounts); // bug
                    /*Intent settingIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
                    settingIntent.putExtra("user_id", databasePresenter.getMcurrent_user_id());// send user id to use it to get all other info in db
                    startActivity(settingIntent);*/
                    intentPresenter.presentIntent("Setting", databasePresenter.getMcurrent_user_id(), null);
                } else {

                    switch (mCurrent_state) {
                        case NOT_FRIEND:
                            databasePresenter.getmRootRef().updateChildren(databasePresenter.setupFriendReq(),
                                    new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                Toast.makeText(ProfileActivity.this, "There was an error",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            mRequestButton.setEnabled(true);
                                            mCurrent_state = REQUEST_TYPE_SENT;
                                            mRequestButton.setText(R.string.cancel_friend_req);
                                            mDeclineButton.setVisibility(View.INVISIBLE);
                                            mDeclineButton.setEnabled(false);
                                        }
                                    });
                            break;
                        case FRIEND:
                            //remove Friend from Friend DB - UnFriend
                            databasePresenter.getmRootRef().updateChildren(databasePresenter.removeFriend(),
                                    new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        String error = databaseError.getMessage();
                                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        mRequestButton.setEnabled(true); //grey out button
                                        mCurrent_state = NOT_FRIEND;
                                        mRequestButton.setText(R.string.send_friend_request);
                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                        mDeclineButton.setEnabled(false);
                                    }
                                }
                            });
                            break;
                        case REQUEST_TYPE_RECEIVED:
                            //Accept Friend, Delete Friend Req data and data to Friends DB
                            databasePresenter.getmRootRef().updateChildren(databasePresenter.AcceptFriend(),
                                    new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Log.d(TAG, "Accept onComplete: working ");
                                    if (databaseError != null) {
                                        Log.d(TAG, "Accept onComplete: Not working ");
                                        Toast.makeText(ProfileActivity.this, " an error occured", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mRequestButton.setEnabled(true); //grey out button
                                        mCurrent_state = FRIEND;
                                        String removefriendsDisplay = "Remove " + mName;
                                        mRequestButton.setText(removefriendsDisplay);
                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                        mDeclineButton.setEnabled(false);
                                    }
                                }
                            });
                            break;
                        case REQUEST_TYPE_SENT:
                            //Cancel Friend Request, Delete request data from Friend_Req DB
                            databasePresenter.getmRootRef().updateChildren(databasePresenter.declineCancelFriendRequest(),
                                    new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                String error = databaseError.getMessage();
                                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                            } else {
                                                mRequestButton.setEnabled(true); //grey out button
                                                mCurrent_state = NOT_FRIEND;
                                                mRequestButton.setText(R.string.send_friend_request);
                                                mDeclineButton.setVisibility(View.INVISIBLE);
                                                mDeclineButton.setEnabled(false);
                                            }
                                        }
                                    });
                    }

                }
            }
        });

        //decline friends
        mDeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databasePresenter.getmRootRef().updateChildren(databasePresenter.declineCancelFriendRequest(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            String error = databaseError.getMessage();
                            Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                        } else {
                            mRequestButton.setEnabled(true);
                            mCurrent_state = NOT_FRIEND;
                            mRequestButton.setText(R.string.send_friend_request);
                            mDeclineButton.setVisibility(View.INVISIBLE);
                            mDeclineButton.setEnabled(false);
                        }
                    }
                });
            }
        });
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle(mName);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

