package com.example.appchatapplication.fragment.alluser;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "UsersViewHolder";

        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

    public View getmView() {
        return mView;
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

