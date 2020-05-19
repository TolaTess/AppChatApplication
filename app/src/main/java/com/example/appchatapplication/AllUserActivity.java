package com.example.appchatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity {
    private static final String TAG = "AllUserActivity";

    private Toolbar toolbar;
    private RecyclerView mUserList;
    private DatabaseReference mUserDatabase;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        toolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserList = findViewById(R.id.alluser_recycler);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
        //mUserList.setHasFixedSize(true);
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
            protected void onBindViewHolder(UsersViewHolder holder, int position, Users model) {
                Log.d(TAG, "onBindViewHolder: ");
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImage(model.getImage());
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
        public void setImage(String image){
            CircleImageView mImage = mView.findViewById(R.id.users_image);
            Picasso.get().load(image).into(mImage);
        }
    }
}
