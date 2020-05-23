package com.example.appchatapplication.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.model.Messages;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;

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
            Messages messages = mMessageList.get(position);
            holder.messagesText.setText(messages.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView messagesText;
        CircleImageView profileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messagesText = itemView.findViewById(R.id.msg_text_layout);
            profileImage = itemView.findViewById(R.id.msg_image_layout);
        }
    }
}
