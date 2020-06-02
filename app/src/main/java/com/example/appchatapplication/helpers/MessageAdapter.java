package com.example.appchatapplication.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.modellayer.model.ReceivedMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<ReceivedMessage> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabse;

    public MessageAdapter(List<ReceivedMessage> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();

        ReceivedMessage message = mMessageList.get(position);

        if (message.getFrom().equals(current_user_id)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_message_list, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_message_list, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();

        ReceivedMessage messages =  mMessageList.get(position);
        switch (holder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).setMessage(messages);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(messages);
                getUserDetails(holder, messages);
                break;
        }

    }

    private void getUserDetails(@NonNull final RecyclerView.ViewHolder holder, ReceivedMessage messages) {
        String from_user = messages.getFrom();
        mUserDatabse = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(from_user);

        mUserDatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                ((ReceivedMessageHolder) holder).setDisplayName(name);
                ((ReceivedMessageHolder) holder).setImage(userThumb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView messageImage;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText =  itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            messageImage = itemView.findViewById(R.id.sent_image_view);
        }

        public void setMessage(ReceivedMessage message) {
            messageText.setVisibility(View.VISIBLE);
            messageText.setText(message.getMessage());
            if(message.getType().equals("image")){
                messageImage.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.GONE);
                Picasso.get().load(message.getMessage()).placeholder(R.drawable.ic_launcher_foreground).into(messageImage);
            }
        }

        public void setTime(String time) {
            timeText.setText(time);
        }
    }


    public static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        CircleImageView profileImage;
        ImageView messageImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
            nameText =  itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
            messageImage = itemView.findViewById(R.id.rec_image_view);

        }

        public void bind(ReceivedMessage message) {
            messageText.setVisibility(View.VISIBLE);
            messageText.setText(message.getMessage());
            if(message.getType().equals("image")){
                messageImage.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.GONE);
                Picasso.get().load(message.getMessage())
                        .placeholder(R.drawable.ic_launcher_foreground).into(messageImage);
            }
        }

        public void setImage(String thumb_image) {
            Picasso.get().load(thumb_image)
                    .placeholder(R.drawable.ic_launcher_foreground).into(profileImage);
        }

        public void setDisplayName(String name) {
            nameText.setText(name);
        }

    }

}
