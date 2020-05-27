package com.example.appchatapplication.modellayer.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appchatapplication.modellayer.enums.State;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseHelper {
    private static final String TAG = "FirebaseDatabasePresent";

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotification;
    private DatabaseReference mRootRef;
    private DatabaseReference mUserDatabase;
    private String userid;

    private FirebaseAuthHelper helper;

    private String mcurrent_user_id;

    public FirebaseDatabaseHelper() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotification = FirebaseDatabase.getInstance().getReference().child("Notifications");

        helper = new FirebaseAuthHelper();
        helper.setupFirebase();
        if (helper.getmAuth().getCurrentUser() != null) {
            mcurrent_user_id = helper.getmAuth().getCurrentUser().getUid();
        }
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public FirebaseAuthHelper getHelper() {
        return helper;
    }

    public String getMcurrent_user_id() {
        return mcurrent_user_id;
    }

    public DatabaseReference getmRootRef() {
        return mRootRef;
    }

    public DatabaseReference getmUserDatabase() {
        return mUserDatabase;
    }

    public Map registerUser(String display_name) {

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", display_name);
        userMap.put("status", "Chatting with friends the right way");
        userMap.put("image", "default");
        userMap.put("online", "true");
        userMap.put("thumb_image", "default");

        return userMap;
    }

    public Map setupDatabaseMap(String muser_id, State mapType) {
        Map setupMap = new HashMap();

        switch (mapType) {
            case not_friend:
                Log.d(TAG, "setupDatabaseMap: Not friends");
                //req friend
                DatabaseReference newNotifRef = mNotification.child(muser_id).push();
                String newNotifId = newNotifRef.getKey();

                HashMap<String, String> notifs = new HashMap<>();
                notifs.put("from", mcurrent_user_id);
                notifs.put("type", "request");

                setupMap.put("Friend_Req/" + mcurrent_user_id + "/" + muser_id + "/request_type", "sent");
                setupMap.put("Friend_Req/" + muser_id + "/" + mcurrent_user_id + "/request_type", "received");
                setupMap.put("Notifications/" + muser_id + "/" + newNotifId, notifs);
                break;
            case friend:
                //remove friend
                setupMap.put("Friends/" + mcurrent_user_id + "/" + muser_id, null);
                setupMap.put("Friends/" + muser_id + "/" + mcurrent_user_id, null);
                break;
            case request_received:
                //accept friend req
                String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                setupMap.put("Friends/" + mcurrent_user_id + "/" + muser_id + "/date_time", currentDate);
                setupMap.put("Friends/" + muser_id + "/" + mcurrent_user_id + "/date_time", currentDate);

                setupMap.put("Friend_Req/" + mcurrent_user_id + "/" + muser_id, null);
                setupMap.put("Friend_Req/" + muser_id + "/" + mcurrent_user_id, null);
                break;
            case request_sent:
                //decline friend request
                setupMap.put("Friend_Req/" + mcurrent_user_id + "/" + muser_id, null);
                setupMap.put("Friend_Req/" + muser_id + "/" + mcurrent_user_id, null);
                break;
        }
        return setupMap;
    }

    public void loadDatabase(final Context context, String muser_id, State mapType){
        Map friendMap = setupDatabaseMap(muser_id, mapType);
        getmRootRef().updateChildren(friendMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(context, "There was an error",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
