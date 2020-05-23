package com.example.appchatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivityOld extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mDisplayName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileButton, mDeclineButton;
    private ProgressDialog progressDialog;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotification;
    private FirebaseUser mCurrentuser;

    private String mCurrent_state;
    private String mName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotification = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mCurrentuser = FirebaseAuth.getInstance().getCurrentUser();

        mDisplayName = findViewById(R.id.profile_name);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileImage = findViewById(R.id.profile_image);
        mProfileButton = findViewById(R.id.profile_send_reg_btn);
        mDeclineButton = findViewById(R.id.decline_req_btn);

        mCurrent_state = "not_friends";


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we load the user data. ");
        progressDialog.setCanceledOnTouchOutside(false);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mDisplayName.setText(mName);
                mProfileStatus.setText(status);

                Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(mProfileImage);

                mDeclineButton.setVisibility(View.INVISIBLE);
                mDeclineButton.setEnabled(false);

                //friends list/ request feature
                mFriendReqDatabase.child(mCurrentuser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(user_id)){
                                    String req_type = dataSnapshot.child(user_id).child("request_type")
                                            .getValue().toString();
                                    if(req_type.equals("received")){
                                        mCurrent_state = "req_received";
                                        mProfileButton.setText("Accept Friend Request");
                                        mDeclineButton.setVisibility(View.VISIBLE);
                                        mDeclineButton.setEnabled(true);

                                    } else if(req_type.equals("sent")){
                                        mCurrent_state = "req_sent";
                                        mProfileButton.setText("Cancel Friend Request");
                                    }
                                    progressDialog.dismiss();
                                } else {
                                    mFriendsDatabase.child(mCurrentuser.getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.hasChild(user_id)){
                                                        mCurrent_state = "friends";
                                                        mProfileButton.setText("Unfriend " + mName);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileButton.setEnabled(false);

                //friends request section
                if (mCurrent_state.equals("not_friends")) {
                    //current user sends a request to the user of interest using his user id
                    mFriendReqDatabase.child(mCurrentuser.getUid()).child(user_id)
                            .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //once done, add the senders details to the receivers table also
                                mFriendReqDatabase.child(user_id).child(mCurrentuser.getUid())
                                        .child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> notifs = new HashMap<>();
                                        notifs.put("from", mCurrentuser.getUid());
                                        notifs.put("type", "request");
                                        mNotification.child(user_id).push().setValue(notifs).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mProfileButton.setEnabled(true); //grey out button
                                                    mCurrent_state = "req_sent";
                                                    mProfileButton.setText(R.string.cancel_friend_req); //change the text of button
                                                    mDeclineButton.setVisibility(View.INVISIBLE);
                                                    mDeclineButton.setEnabled(false);
                                                }
                                            }
                                        });
                                        Toast.makeText(ProfileActivityOld.this, "Request sent",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivityOld.this, "Failed Sending Request",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                // cancel request section
                //don't forget to handle errors = best to use oncompleteL so you can use if else or use onFailure with on
                //SucessL
                if (mCurrent_state.equals("req_sent")) {
                    mFriendReqDatabase.child(mCurrentuser.getUid()).child(user_id)
                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase.child(user_id).child(mCurrentuser.getUid())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mProfileButton.setEnabled(true); //grey out button
                                                    mCurrent_state = "not_friends";
                                                    mProfileButton.setText("Send Friend Request");
                                                } //add onFailure
                                            });
                                }
                            });

                }

                //req recieved section
                if(mCurrent_state.equals("req_received")){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mFriendsDatabase.child(mCurrentuser.getUid()).child(user_id).setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendsDatabase.child(user_id).child(mCurrentuser.getUid())
                                            .setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendReqDatabase.child(mCurrentuser.getUid()).child(user_id)
                                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mFriendReqDatabase.child(user_id).child(mCurrentuser.getUid())
                                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mProfileButton.setEnabled(true); //grey out button
                                                            mCurrent_state = "friends";
                                                            mProfileButton.setText("Unfriend " + mName);
                                                        } //add onFailure
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                }
                // unfriend  section
                if (mCurrent_state.equals("friends")) {
                    mFriendsDatabase.child(mCurrentuser.getUid()).child(user_id)
                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabase.child(user_id).child(mCurrentuser.getUid())
                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileButton.setEnabled(true); //grey out button
                                    mCurrent_state = "not_friends";
                                    mProfileButton.setText("Send Friend Request");
                                } //add onFailure
                            });
                        }
                    });

                }
            }
        });

    }
}
