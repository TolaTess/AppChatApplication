package com.example.appchatapplication.activities.allusers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.coordinator.IntentPresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.example.appchatapplication.modellayer.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AllUserPresenterImpl implements AllUserPresenter{
    private static final String TAG = "AllUserPresenterImpl";

    private FirebaseDatabaseHelper databaseHelper;
    private FirebaseRecyclerAdapter adapter;
    private IntentPresenter intentPresenter;
    private Context mContext;
    private RecyclerView mUserList;

    public AllUserPresenterImpl(Context context, RecyclerView mUserList) {
        mContext = context;
        this.mUserList = mUserList;
        databaseHelper = new FirebaseDatabaseHelper();
        intentPresenter = new IntentPresenter(mContext);
    }

    public FirebaseRecyclerAdapter getAdapter() {
        return adapter;
    }

    public void fetchAllUsers(){
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(databaseHelper.getmUserDatabase(), Users.class)
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

                databaseHelper.getmUserDatabase().child(list_user_id).addValueEventListener(new ValueEventListener() {
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
                        intentPresenter.presentIntent("Profile", userid, name);
                    }
                });


            }
        };
        mUserList.setAdapter(adapter);
    }
}
