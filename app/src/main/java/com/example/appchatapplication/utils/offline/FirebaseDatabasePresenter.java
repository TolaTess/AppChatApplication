package com.example.appchatapplication.utils.offline;

import android.content.Context;

import com.example.appchatapplication.utils.FirebaseAuthPresenter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabasePresenter {
    private static final String TAG = "FirebaseDatabasePresent";

    private DatabaseReference mUserProfileDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotification;
    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;

    private FirebaseAuthPresenter presenter;

    private String muser_id;
    private String mcurrent_user_id;
    private String mCurrent_state;

    private Context mContext;

    public FirebaseDatabasePresenter(Context mContext, String user_id) {
        this.mContext = mContext;
        this.muser_id = user_id;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserProfileDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(muser_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotification = FirebaseDatabase.getInstance().getReference().child("Notifications");

        presenter = new FirebaseAuthPresenter(mContext);
        presenter.setupFirebaseAuth();
        if (presenter.getmAuth().getCurrentUser() != null) {
            mcurrent_user_id = presenter.getmAuth().getCurrentUser().getUid();
        }
    }

    public String getMcurrent_user_id() {
        return mcurrent_user_id;
    }

    public DatabaseReference getmUserProfileDatabase() {
        return mUserProfileDatabase;
    }

    public DatabaseReference getmFriendReqDatabase() {
        return mFriendReqDatabase;
    }

    public DatabaseReference getmFriendsDatabase() {
        return mFriendsDatabase;
    }

    public DatabaseReference getmRootRef() {
        return mRootRef;
    }

    public DatabaseReference getmUserRef() {
        return mUserRef;
    }

    public Map setupFriendReq(){
        DatabaseReference newNotifRef = mNotification.child(muser_id).push();
        String newNotifId = newNotifRef.getKey();

        HashMap<String, String> notifs = new HashMap<>();
        notifs.put("from", mcurrent_user_id);
        notifs.put("type", "request");

        Map reqFriendMap = new HashMap();
        reqFriendMap.put("Friend_Req/" + mcurrent_user_id + "/" + muser_id + "/request_type", "sent");
        reqFriendMap.put("Friend_Req/" + muser_id + "/" + mcurrent_user_id + "/request_type", "received");
        reqFriendMap.put("Notifications/" + muser_id + "/" + newNotifId, notifs);

        return reqFriendMap;
    }

    public Map removeFriend(){
        Map friendMap = new HashMap();
        friendMap.put("Friends/" + mcurrent_user_id + "/" + muser_id, null);
        friendMap.put("Friends/" + muser_id + "/" + mcurrent_user_id, null);

        return friendMap;
    }

    public Map AcceptFriend(){
        String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        Map acceptFriendMap = new HashMap();
        acceptFriendMap.put("Friends/" + mcurrent_user_id + "/" + muser_id + "/date_time", currentDate);
        acceptFriendMap.put("Friends/" + muser_id + "/" + mcurrent_user_id + "/date_time", currentDate);

        acceptFriendMap.put("Friend_Req/" + mcurrent_user_id + "/" + muser_id, null);
        acceptFriendMap.put("Friend_Req/" + muser_id + "/" + mcurrent_user_id, null);

        return acceptFriendMap;
    }

    public Map declineCancelFriendRequest(){

        Map friendReqMap = new HashMap();
        friendReqMap.put("Friend_Req/" + mcurrent_user_id + "/" + muser_id, null);
        friendReqMap.put("Friend_Req/" + muser_id + "/" + mcurrent_user_id, null);

        return friendReqMap;

    }


}
