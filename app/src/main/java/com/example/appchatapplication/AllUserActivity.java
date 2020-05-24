package com.example.appchatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appchatapplication.account.ProfileActivity;
import com.example.appchatapplication.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity {
    private static final String TAG = "AllUserActivity";

    private Toolbar toolbar;
    private RecyclerView mUserList;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mUserRef;
    FirebaseRecyclerAdapter adapter;
    String currentuserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        toolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentuserId = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid());

        mUserList = findViewById(R.id.alluser_recycler);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
        //mUserList.setHasFixedSize(true); nothing displayed with this... don't use
        fetch();
    }

    private void fetch() {
        mUserDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(mUserDatabase, Users.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                options) {
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_list_view, parent, false);
                return new UsersViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(final UsersViewHolder holder, int position, Users model) {
                Log.d(TAG, "onBindViewHolder: ");
                final String list_user_id = getRef(position).getKey();
                final String name = model.getName();

                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("online")){
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.setName(name);
                holder.setStatus(model.getStatus());
                holder.setImage(model.getThumb_image());

                final String userid = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(AllUserActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", userid);
                        profileIntent.putExtra("username", name);// send user id to use it to get all other info in db
                        startActivity(profileIntent);
                    }
                });


            }
        };
        mUserList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        adapter.stopListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "UsersViewHolder";

        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.users_name);
            userNameView.setText(name);
        }
        public void setStatus(String status){
            TextView statusView = mView.findViewById(R.id.users_status);
            statusView.setText(status);
        }
        public void setImage(String thumb_image){
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
