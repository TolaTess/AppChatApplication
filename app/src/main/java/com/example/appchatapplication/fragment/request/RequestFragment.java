package com.example.appchatapplication.fragment.request;

import android.graphics.Color;
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
import com.example.appchatapplication.modellayer.enums.ClassName;
import com.example.appchatapplication.modellayer.model.Requests;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestFragment extends Fragment {
    private static final String TAG = "RequestFragment";

    private View mMainView;
    private RecyclerView mReceivedList;
    private RequestPresenter requestPresenter;
    private TextView noReqReceived;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_request, container, false);
        requestPresenter = new RequestPresenterImpl(getContext());

        mReceivedList = mMainView.findViewById(R.id.allrecived_recycler);
        noReqReceived = mMainView.findViewById(R.id.received_req_msg);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mReceivedList.setLayoutManager(linearLayoutManager);

        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();
        receivedAdapterSetup();

    }

    private void receivedAdapterSetup() {

        Query friendsQuery = requestPresenter.getmFriendsReqDatabase().orderByChild("request_type");

        FirebaseRecyclerOptions<Requests> options =
                new FirebaseRecyclerOptions.Builder<Requests>()
                        .setQuery(friendsQuery, Requests.class)
                        .build();

        FirebaseRecyclerAdapter<Requests, RequestsViewHolder> reqAdapter =
                new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(
                        options) {
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        Log.d(TAG, "onCreateViewHolder: ");
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.request_list_view, parent, false);
                        return new RequestsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, final int position, @NonNull final Requests model) {
                        Log.d(TAG, "onBindViewHolder: ");

                        final String list_user_id = getRef(position).getKey();
                        requestPresenter.getmFriendsReqDatabase().child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String type = dataSnapshot.child("request_type").getValue().toString();
                                if(dataSnapshot.hasChild("request_type")){
                                    noReqReceived.setVisibility(View.INVISIBLE);
                                    requestPresenter.getmUserReqDatabase().child(list_user_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            final String userName = dataSnapshot.child("name").getValue().toString();
                                            String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                            if (dataSnapshot.hasChild("online")) {

                                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                                holder.setUserOnline(userOnline);
                                            }
                                            holder.setName(userName);
                                            holder.setImage(userThumb);
                                            holder.setStatus(type);

                                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    requestPresenter.getIntentReqPresenter()
                                                            .presentIntent(ClassName.Profile, list_user_id, userName);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                            }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                };
        reqAdapter.startListening();
        mReceivedList.setAdapter(reqAdapter);
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "RequestsViewHolder";

        View mView;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userName = mView.findViewById(R.id.req_users_name);
            userName.setText(name);
    }
        public void setStatus(String request_type) {
            TextView request = mView.findViewById(R.id.req_users_status);
            if (request_type.equals("received")){
                request.setText(request_type);
            request.setTextColor(Color.GREEN);}
            else{
                request.setText(request_type);
            }
        }

        public void setImage(String thumb_image) {
            CircleImageView mThumbImage = mView.findViewById(R.id.req_users_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.ic_launcher_foreground).into(mThumbImage);
        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineView = mView.findViewById(R.id.req_online_icon);

            if (online_status.equals("true")) {
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }

    }
}
