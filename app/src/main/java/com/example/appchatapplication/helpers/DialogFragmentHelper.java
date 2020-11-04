package com.example.appchatapplication.helpers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.fragment.friends.FriendPresenter;
import com.example.appchatapplication.fragment.friends.FriendPresenterImpl;
import com.example.appchatapplication.modellayer.enums.ClassName;
import com.example.appchatapplication.modellayer.model.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class DialogFragmentHelper extends DialogFragment {

    private String type;
    private String activity;
    //private DatabasePresenter presenter;
    private FriendPresenter friendPresenter;
    private View mMainView;
    @BindView(R.id.share_recycler)
    RecyclerView recyclerView;

    private Unbinder unbinder;
    private FirebaseRecyclerAdapter<Friends, DialogFragmentHelper.FriendsViewHolder> friendsAdapter;


    public DialogFragmentHelper(String type, String activity) {
        this.type = type.toUpperCase();
        this.activity = activity;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mMainView = inflater.inflate(R.layout.popup_widget, container, false);
        unbinder = ButterKnife.bind(this, mMainView);

        //presenter = new FirebaseDatabaseHelper();
        friendPresenter = new FriendPresenterImpl(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(friendPresenter.getmFriendsDatabase(), Friends.class)
                        .build();

        friendsAdapter =
                new FirebaseRecyclerAdapter<Friends, DialogFragmentHelper.FriendsViewHolder>(
                        options) {
                    @Override
                    public DialogFragmentHelper.FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.share_item, parent, false);
                        return new DialogFragmentHelper.FriendsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final DialogFragmentHelper.FriendsViewHolder holder, final int position, @NonNull final Friends model) {
                        //holder.setDate(model.getDate_time());

                        final String list_user_id = getRef(position).getKey();
                        friendPresenter.getmUserDatabase().child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                final String userName = dataSnapshot.child("name").getValue().toString();
                                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                                if(dataSnapshot.hasChild("online")){

                                    String userOnline = dataSnapshot.child("online").getValue().toString();
                                    holder.setUserOnline(userOnline);
                                }

                                holder.setName(userName, activity);
                                holder.setImage(userThumb);

                                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CharSequence options[] = new CharSequence[]{"Share Challenge", "No, I've Got it"};
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Select Options");
                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which){
                                                    case 0:
                                                        Toast.makeText(getContext(),"Sharing is caring", Toast.LENGTH_SHORT).show();
                                                        friendPresenter.getIntentFriendPresenter().presentIntent(ClassName.Ideas, list_user_id, userName);
                                                        break;
                                                    case 1:
                                                        Toast.makeText(getContext(),"You are the best!", Toast.LENGTH_SHORT).show();
                                                        friendPresenter.getIntentFriendPresenter().presentIntent(ClassName.Ideas, list_user_id, userName);
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
        recyclerView.setAdapter(friendsAdapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        friendsAdapter.stopListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        RelativeLayout relativeLayout;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            relativeLayout = itemView.findViewById(R.id.relshare);
        }

        public void setName(String name, String activity) {
            TextView userName = mView.findViewById(R.id.friend_name);
            TextView activityView = mView.findViewById(R.id.activity);
            //userName.setTextColor(Color.BLACK);
            userName.setText(name);
            activityView.setText(activity);
        }

        public void setImage(String thumb_image) {
            CircleImageView mThumbImage = mView.findViewById(R.id.friends_image);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

