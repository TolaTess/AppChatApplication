package com.example.appchatapplication.business;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.account.ProfileActivity;
import com.example.appchatapplication.model.Conv;
import com.example.appchatapplication.model.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    private View mMainView;
    private RecyclerView mConvList;
    private FirebaseAuth mAuth;
    private DatabaseReference mConvDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mMessageDatabse;
    FirebaseRecyclerAdapter<Conv, ChatsViewHolder> chatsAdapter;

    private String mCurrentUserID;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);

        mConvList = mMainView.findViewById(R.id.conv_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUserID = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Chat").child(mCurrentUserID);
        mConvDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Users");
        mMessageDatabse = FirebaseDatabase.getInstance().getReference()
                .child("Messages").child(mCurrentUserID);
        mMessageDatabse.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setLayoutManager(linearLayoutManager);

        return mMainView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();

        Query convQuery = mConvDatabase.orderByChild("timestamp");

        FirebaseRecyclerOptions<Conv> options =
                new FirebaseRecyclerOptions.Builder<Conv>()
                        .setQuery(convQuery, Conv.class)
                        .build();

        chatsAdapter =
                new FirebaseRecyclerAdapter<Conv, ChatsViewHolder>(
                        options) {
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        Log.d(TAG, "onCreateViewHolder: ");
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_list_view, parent, false);
                        return new ChatsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int position, @NonNull final Conv model) {
                        Log.d(TAG, "onBindViewHolder: ");
                        //holder.setDate(model.getDate_time());

                        final String list_user_id = getRef(position).getKey();
                        Query lastMessageQuery = mMessageDatabse.child(list_user_id).limitToLast(1);

                        lastMessageQuery.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                String data = dataSnapshot.child("message").getValue().toString();
                                holder.setMessage(data, model.isSeen());
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String userName = dataSnapshot.child("name").getValue().toString();
                                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                if(dataSnapshot.hasChild("online")){

                                    String userOnline = dataSnapshot.child("online").getValue().toString();
                                    holder.setUserOnline(userOnline);
                                }
                                holder.setName(userName);
                                holder.setUserImage(userThumb);

                                holder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                        chatIntent.putExtra("user_id", list_user_id);
                                        chatIntent.putExtra("username", userName);// send user id to use it to get all other info in db
                                        startActivity(chatIntent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                };
        chatsAdapter.startListening();
        mConvList.setAdapter(chatsAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        chatsAdapter.stopListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userName = mView.findViewById(R.id.chat_username);
            userName.setText(name);
        }

        public void setMessage(String message, boolean isSeen) {
            TextView userStatus = mView.findViewById(R.id.chat_conv);
            userStatus.setText(message);
            if (!isSeen) {
            userStatus.setTypeface(userStatus.getTypeface(), Typeface.BOLD);
            } else{
                userStatus.setTypeface(userStatus.getTypeface(), Typeface.NORMAL);
            }
        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineView = mView.findViewById(R.id.user_online_icon);

            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            } else{
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }

        public void setUserImage(String thumb){
            CircleImageView userImageView = mView.findViewById(R.id.chat_users_image);
            Picasso.get().load(thumb).placeholder(R.drawable.ic_launcher_foreground).into(userImageView);
        }


    }
}
