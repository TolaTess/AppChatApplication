package com.example.appchatapplication.utils;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabse;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_view, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        final Messages messages = mMessageList.get(position);
        String from_user = messages.getFrom();
        String message_type = messages.getType();
        mUserDatabse = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(from_user);

        mUserDatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();

                holder.displayName.setText(name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")){
            holder.messagesText.setText(messages.getMessage());
            holder.messageImage.setVisibility(View.INVISIBLE);
            if(from_user.equals(current_user_id)) {
                holder.messagesText.setBackgroundResource(R.drawable.message_text_background2);
                holder.messagesText.setTextColor(Color.BLACK);
                holder.messagesText.setGravity(Gravity.RIGHT);
            }else{

                holder.messagesText.setBackgroundResource(R.drawable.message_text_background);
                holder.messagesText.setTextColor(Color.BLACK);
            }
        } else {
            holder.messagesText.setVisibility(View.INVISIBLE);
        }
        if(message_type.equals("image")){
            holder.messageImage.setVisibility(View.VISIBLE);
            Picasso.get().load(messages.getMessage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.messageImage);
        } else{
            holder.messageImage.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView messagesText;
        TextView displayName;
        ImageView messageImage;

        //CircleImageView mProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messagesText = itemView.findViewById(R.id.msg_text_layout);
            displayName = itemView.findViewById(R.id.msg_display_name);
            messageImage = itemView.findViewById(R.id.message_image_input);
        }
    }
}
