package com.example.appchatapplication.utils;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;

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
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        Messages messages = mMessageList.get(position);
        String from_user = messages.getFrom();

        if(from_user.equals(current_user_id)){
            holder.messagesText.setBackgroundResource(R.drawable.message_text_background2);
            holder.messagesText.setTextColor(Color.BLACK);
            holder.messagesText.setGravity(Gravity.RIGHT);

        } else{
            holder.messagesText.setBackgroundResource(R.drawable.message_text_background);
            holder.messagesText.setTextColor(Color.BLACK);
        }
        holder.setName(messages.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView messagesText;
        //CircleImageView mProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            messagesText = mView.findViewById(R.id.msg_text_layout);
        }

        public void setName(String name){
            messagesText.setText(name);
        }

        public void setImage(String image){
            //Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(mProfileImage);
        }
    }
}
