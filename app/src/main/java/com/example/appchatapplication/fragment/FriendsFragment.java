package com.example.appchatapplication.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.coordinator.IntentPresenter;
import com.example.appchatapplication.modellayer.model.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {
    private static final String TAG = "FriendsFragment";

    private View mMainView;
    private RecyclerView mFriendsList;
    private FirebaseAuth mAuth;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUserDatabase;
    private IntentPresenter intentPresenter;
    FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsAdapter;

    private String mCurrentUserID;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = mMainView.findViewById(R.id.allfriends_recycler);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUserID = mAuth.getCurrentUser().getUid();

        intentPresenter = new IntentPresenter(getContext());


        //mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Friends").child(mCurrentUserID);
        mUserDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Users");

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(mFriendsDatabase, Friends.class)
                        .build();

        friendsAdapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                        options) {
                    @Override
                    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        Log.d(TAG, "onCreateViewHolder: ");
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.layout_list_view, parent, false);
                        return new FriendsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, final int position, @NonNull final Friends model) {
                        Log.d(TAG, "onBindViewHolder: ");
                        holder.setDate(model.getDate_time());

                        final String list_user_id = getRef(position).getKey();
                        mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d(TAG, "onDataChange: ");

                                final String userName = dataSnapshot.child("name").getValue().toString();
                                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                                if(dataSnapshot.hasChild("online")){

                                    String userOnline = dataSnapshot.child("online").getValue().toString();
                                    holder.setUserOnline(userOnline);
                                }

                                holder.setName(userName);
                                holder.setImage(userThumb);

                                holder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Select Options");
                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which){
                                                    case 0:
                                                       intentPresenter.presentIntent("Profile", list_user_id, userName);
                                                        break;
                                                    case 1:
                                                        intentPresenter.presentIntent("Chats", list_user_id, userName);
                                                        break;
                                                    default:
                                                }

                                            }
                                        });
                                        builder.show();

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                };
        friendsAdapter.startListening();
        mFriendsList.setAdapter(friendsAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        friendsAdapter.stopListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "FriendsViewHolder";

        View mView;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date) {
            TextView datestring = mView.findViewById(R.id.users_status);
            datestring.setText(date);
        }

        public void setName(String name) {
            TextView userName = mView.findViewById(R.id.users_name);
            userName.setText(name);
        }

        public void setImage(String thumb_image) {
            CircleImageView mThumbImage = mView.findViewById(R.id.users_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.ic_launcher_foreground).into(mThumbImage);
        }
        public void setUserOnline(String online_status) {
            ImageView userOnlineView = mView.findViewById(R.id.user_online_icon);

            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            } else{
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }

    }
}
