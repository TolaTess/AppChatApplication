package com.example.appchatapplication.fragment.chats;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.modellayer.enums.ClassName;
import com.example.appchatapplication.modellayer.model.Conv;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    private View mMainView;
    private RecyclerView mConvList;
    private ChatPresenter chatPresenter;

    FirebaseRecyclerAdapter<Conv, ChatsViewHolder> chatsAdapter;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);

        chatPresenter = new ChatPresenterImpl(getContext());

        mConvList = mMainView.findViewById(R.id.conv_list);

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

        Query convQuery = chatPresenter.getmConvDatabase().orderByChild("timestamp");

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
                                .inflate(R.layout.black_background_list_view, parent, false);
                        return new ChatsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int position, @NonNull final Conv model) {
                        Log.d(TAG, "onBindViewHolder: ");
                        //holder.setDate(model.getDate_time());

                        final String list_user_id = getRef(position).getKey();
                        Query lastMessageQuery = chatPresenter.getmMessageDatabase().child(list_user_id).limitToLast(1);

                        lastMessageQuery.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if(dataSnapshot.hasChild("message")) {
                                    String messageType = dataSnapshot.child("type").getValue().toString();
                                        String data = dataSnapshot.child("message").getValue().toString();
                                        holder.setMessage(data, messageType, model.isSeen());

                                }
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
                        chatPresenter.getmUserChatDatabase().child(list_user_id).addValueEventListener(new ValueEventListener() {
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
                                        chatPresenter.getIntentFriendPresenter()
                                                .presentIntent(ClassName.Chats, list_user_id, userName);
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

        public void setMessage(String message, String image, boolean isSeen) {
            TextView userStatus = mView.findViewById(R.id.chat_conv);
            if(!image.equals("image")){
            userStatus.setText(message);}
            else{
                userStatus.setText("an image");
            }
            if (!isSeen) {
            userStatus.setTypeface(userStatus.getTypeface(), Typeface.BOLD);
            } else{
                userStatus.setTypeface(userStatus.getTypeface(), Typeface.NORMAL);
            }
        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineView = mView.findViewById(R.id.chat_online_icon);

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
