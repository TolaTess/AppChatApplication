package com.example.appchatapplication.account;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private ImageView mProfileImage;
    private TextView mDisplayName, mProfileStatus, mProfileFriendsCount;
    private Button mRequestButton, mDeclineButton;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotification;
    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;
    private FirebaseUser mCurrentuser;

    private String mCurrent_state;
    private String mName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");
        mName = getIntent().getStringExtra("username");

        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle(mName);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotification = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mCurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid());

        mDisplayName = findViewById(R.id.profile_name);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileImage = findViewById(R.id.profile_image);
        mRequestButton = findViewById(R.id.profile_send_reg_btn);
        mDeclineButton = findViewById(R.id.decline_req_btn);

        mCurrent_state = "not_friends";

        mDeclineButton.setVisibility(View.INVISIBLE);
        mDeclineButton.setEnabled(false);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we load the user data. ");
        progressDialog.setCanceledOnTouchOutside(false);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                mProfileStatus.setText(status);

                Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(mProfileImage);

                //friends request feature
                mFriendReqDatabase.child(mCurrentuser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(user_id)) {
                                    String req_type = dataSnapshot.child(user_id).child("request_type")
                                            .getValue().toString();
                                    if (req_type.equals("received")) {
                                        mCurrent_state = "req_received";
                                        mRequestButton.setText("Accept Friend Request");
                                        mDeclineButton.setVisibility(View.VISIBLE);
                                        mDeclineButton.setEnabled(true);

                                    } else if (req_type.equals("sent")) {
                                        mCurrent_state = "req_sent";
                                        mRequestButton.setText("Cancel Friend Request");
                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                        mDeclineButton.setEnabled(false);
                                    }
                                    progressDialog.dismiss();
                                } else {
                                    mFriendsDatabase.child(mCurrentuser.getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(user_id)) {
                                                        mCurrent_state = "friends";
                                                        mRequestButton.setText("Unfriend " + mName);
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

                mRootRef.child("Friends").child(user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long friendsCount = dataSnapshot.getChildrenCount();
                                String stringCount = String.valueOf(friendsCount);
                                mProfileFriendsCount.setText("Total Friends: " + stringCount);
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

                DatabaseReference newNotifRef = mRootRef.child("Notifications").child(user_id).push();
                String newNotifId = newNotifRef.getKey();

                String current_user_id = mCurrentuser.getUid();

                //friends request section
                if (current_user_id.equals(user_id)) {
                    mRequestButton.setText(R.string.accounts); // bug
                    Intent settingIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
                    settingIntent.putExtra("user_id", mCurrentuser);// send user id to use it to get all other info in db
                    startActivity(settingIntent);
                } else {
                    if (mCurrent_state.equals("not_friends")) {

                        HashMap<String, String> notifs = new HashMap<>();
                        notifs.put("from", mCurrentuser.getUid());
                        notifs.put("type", "request");

                        Map reqMap = new HashMap();
                        reqMap.put("Friend_Req/" + mCurrentuser.getUid() + "/" + user_id + "/request_type", "sent");
                        reqMap.put("Friend_Req/" + user_id + "/" + mCurrentuser.getUid() + "/request_type", "received");
                        reqMap.put("Notifications/" + user_id + "/" + newNotifId, notifs);

                        mRootRef.updateChildren(reqMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Toast.makeText(ProfileActivity.this, "There was an error",
                                            Toast.LENGTH_LONG).show();
                                }
                                mRequestButton.setEnabled(true);
                                mCurrent_state = "req_sent";
                                mRequestButton.setText(R.string.cancel_friend_req);
                                mDeclineButton.setVisibility(View.INVISIBLE);
                                mDeclineButton.setEnabled(false);
                            }
                        });
                    }
                }

                //cancel request section
                if (mCurrent_state.equals("req_sent")) {

                    Map friendMap = new HashMap();
                    friendMap.put("Friend_Req/" + mCurrentuser.getUid() + "/" + user_id, null);
                    friendMap.put("Friend_Req/" + user_id + "/" + mCurrentuser.getUid(), null);

                    mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                mRequestButton.setEnabled(true); //grey out button
                                mCurrent_state = "not_friends";
                                mRequestButton.setText("Send Friend Request");
                                mDeclineButton.setVisibility(View.INVISIBLE);
                                mDeclineButton.setEnabled(false);
                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                //req received section
                if (mCurrent_state.equals("req_received")) {

                    String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map fMap = new HashMap();
                    fMap.put("Friends/" + mCurrentuser.getUid() + "/" + user_id + "/date_time", currentDate);
                    fMap.put("Friends/" + user_id + "/" + mCurrentuser.getUid() + "/date_time", currentDate);

                    fMap.put("Friend_Req/" + mCurrentuser.getUid() + "/" + user_id, null);
                    fMap.put("Friend_Req/" + user_id + "/" + mCurrentuser.getUid(), null);

                    mRootRef.updateChildren(fMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Log.d(TAG, "Accept onComplete: working ");
                            if (databaseError != null) {
                                mRequestButton.setEnabled(true); //grey out button
                                mCurrent_state = "friends";
                                mRequestButton.setText("Unfriend " + mName);
                                mDeclineButton.setVisibility(View.INVISIBLE);
                                mDeclineButton.setEnabled(false);
                            } else {
                                Log.d(TAG, "Accept onComplete: Not working ");
                                Toast.makeText(ProfileActivity.this, " an error occured", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                // unfriend  section
                if (mCurrent_state.equals("friends")) {
                    Map friendMap = new HashMap();
                    friendMap.put("Friends/" + mCurrentuser.getUid() + "/" + user_id, null);
                    friendMap.put("Friends/" + user_id + "/" + mCurrentuser.getUid(), null);

                    mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                mRequestButton.setEnabled(true); //grey out button
                                mCurrent_state = "not_friends";
                                mRequestButton.setText("Send Friend Request");
                                mDeclineButton.setVisibility(View.INVISIBLE);
                                mDeclineButton.setEnabled(false);
                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                //decline friends
                mDeclineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map friendMap = new HashMap();
                        friendMap.put("Friend_Req/" + mCurrentuser.getUid() + "/" + user_id, null);
                        friendMap.put("Friend_Req/" + user_id + "/" + mCurrentuser.getUid(), null);

                        mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    if(mCurrentuser.getUid().equals(user_id)){
                                        mRequestButton.setEnabled(true); //grey out button
                                        mCurrent_state = "not_friends";
                                        mRequestButton.setText(R.string.accounts);
                                    } else {
                                        mRequestButton.setEnabled(true); //grey out button
                                        mCurrent_state = "not_friends";
                                        mRequestButton.setText("Send Friend Request");
                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                        mDeclineButton.setEnabled(false);
                                    }
                                } else {
                                    String error = databaseError.getMessage();
                                    Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

    }

}
