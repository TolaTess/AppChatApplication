package com.example.appchatapplication.activities.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.appchatapplication.R;
import com.example.appchatapplication.helpers.GetTimeAgo;
import com.example.appchatapplication.helpers.MessageAdapter;
import com.example.appchatapplication.modellayer.model.ReceivedMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {

    private String mChatReceiverUser;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private StorageReference mImageStorage;
    private String mCurrentUserId;
    private Toolbar mToolbar;

    private static final int GALLERY_PICK = 1;

    private CircleImageView mProfileImage;
    private TextView mLastSeen;
    private ImageView mUploadImage;

    private EditText mChatMessageView;
    private RecyclerView mMessageRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<ReceivedMessage> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private static final int TOTAL_ITEM_TO_LOAD = 5;
    private int mCurrentPage = 1;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPreKey = "";

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatReceiverUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("username");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mImageStorage = FirebaseStorage.getInstance().getReference();


        mToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        this.getSupportActionBar().setCustomView(action_bar_view);

        TextView mNameView = findViewById(R.id.custom_bar_name);
        mLastSeen = findViewById(R.id.last_seen);
        mProfileImage = findViewById(R.id.custom_bar_image);
        Button mChatSendBtn = findViewById(R.id.chat_msg_send);
        mChatMessageView = findViewById(R.id.chat_message_input);
        mUploadImage = findViewById(R.id.upload_image_chat);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mAdapter = new MessageAdapter(messagesList);

        mMessageRecyclerView = findViewById(R.id.messages_list);
        mRefreshLayout = findViewById(R.id.swipe_message_layout);
        mLinearLayout = new LinearLayoutManager(this);
        mMessageRecyclerView.setLayoutManager(mLinearLayout);
        mMessageRecyclerView.setAdapter(mAdapter);

        loadMessages();

        mNameView.setText(userName);

        checkLastSeenOnline();

        createChatDatabase();

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages();
            }
        });

        mUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //option 1
                // this will direct user to the Gallery chooser (the library to choose images)
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

    }

    private void createChatDatabase() {
        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatReceiverUser)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatReceiverUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatReceiverUser + "/" + mCurrentUserId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkLastSeenOnline() {
        mRootRef.child("Users").child(mChatReceiverUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(mProfileImage);

                if (online.equals("true")) {

                    mLastSeen.setText(R.string.online_tag);

                } else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(online);

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime);
                    mLastSeen.setText(lastSeenTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                File thumb_file = new File(resultUri.getPath());
                String userUid = mAuth.getCurrentUser().getUid();
                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200).setMaxHeight(200)
                            .setQuality(75).compressToBitmap(thumb_file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumb_byte = baos.toByteArray();


                final StorageReference filepath = mStorageRef.child("messages_image").child(userUid + ".jpg");

                uploadImage(resultUri, filepath, thumb_byte);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void uploadImage(Uri resultUri, final StorageReference filepath, final byte[] thumb_byte) {
        filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //Toast.makeText(SettingsActivity.this, "Working", Toast.LENGTH_LONG).show();
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String download_uri = uri.toString();

                        String current_user_ref = "Messages/" + mCurrentUserId + "/" + mChatReceiverUser;
                        String chat_user_ref = "Messages/" + mChatReceiverUser + "/" + mCurrentUserId;

                        DatabaseReference user_message_push = mRootRef.child("Messages")
                                .child(mCurrentUserId).child(mChatReceiverUser).push();

                        String push_id = user_message_push.getKey();

                        //use Map instead of HashMap to update rather than delete existing data
                        Map messageMap = new HashMap();
                        messageMap.put("message", download_uri);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserId);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
                        //use updateChildren instead of setValue
                        mRootRef.updateChildren(messageUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                } else {
                                    Toast.makeText(ChatActivity.this, "Error occured while uploading image", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void loadMoreMessages() {
        DatabaseReference messageRef =
                mRootRef.child("Messages").child(mCurrentUserId).child(mChatReceiverUser);

        //
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(TOTAL_ITEM_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ReceivedMessage message = dataSnapshot.getValue(ReceivedMessage.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPreKey.equals(messageKey)) {
                    messagesList.add(itemPos++, message);
                } else {
                    mPreKey = mLastKey;
                }
                if (itemPos == 1) {

                    mLastKey = messageKey;

                }

                mAdapter.notifyDataSetChanged();

                //ensure we see the last item on chat
                mMessageRecyclerView.scrollToPosition(messagesList.size() - 1);

                //turn off refreshing
                mRefreshLayout.setRefreshing(false);
                //stays at the position rather than going back to size -1
                mLinearLayout.scrollToPositionWithOffset(10, 0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadMessages() {
        DatabaseReference messageRef = mRootRef.child("Messages").child(mCurrentUserId).child(mChatReceiverUser);

        //limit to last
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEM_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ReceivedMessage message = dataSnapshot.getValue(ReceivedMessage.class);

                itemPos++;

                if (itemPos == 1) {
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPreKey = messageKey;

                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                //ensure we see the last item on chat
                mMessageRecyclerView.scrollToPosition(messagesList.size() - 1);

                //turn off refreshing
                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {

        String message = mChatMessageView.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            String current_user_ref = "Messages/" + mCurrentUserId + "/" + mChatReceiverUser;
            String chat_user_ref = "Messages/" + mChatReceiverUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("Messages")
                    .child(mCurrentUserId).child(mChatReceiverUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            //set message input to blank
            mChatMessageView.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });


        }

    }
}
