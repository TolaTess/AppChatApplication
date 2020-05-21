package com.example.appchatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;
    private static final int MAX_LENGTH = 30;
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    private Toolbar toolbar;

    private Button mStatusButton;
    private Button mChangeImage;

    private FirebaseUser mCurrentUser;
    private DatabaseReference myDatabaseRef;
    private StorageReference mStorageRef;

    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDisplayImage = findViewById(R.id.img_setting);
        mName = findViewById(R.id.text_display_name);
        mStatus = findViewById(R.id.text_status);
        mStatusButton = findViewById(R.id.change_status);
        mChangeImage = findViewById(R.id.change_image);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = mCurrentUser.getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        myDatabaseRef = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(userId);

        //Value Event Listener to get the data from database
        myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                //ensures default image stays on screen
                if (!image.equals("default")) {
                    //placeholder holds a picture on file before the getting database
                    Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(mDisplayImage);
                }
                mName.setText(name);
                mStatus.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //persist data
                String statusValue = mStatus.getText().toString();
                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIntent.putExtra("status_value", statusValue);
                startActivity(statusIntent);
            }
        });

        mChangeImage.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                mProgressBar = new ProgressDialog(SettingsActivity.this);
                mProgressBar.setTitle("Uploading Image");
                mProgressBar.setMessage("Please wait while we upload and process the image");
                mProgressBar.setCanceledOnTouchOutside(false);
                mProgressBar.show();

                Uri resultUri = result.getUri();
                File thumb_file = new File(resultUri.getPath());
                String userUid = mCurrentUser.getUid();
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
                byte [] thumb_byte = baos.toByteArray();


                final StorageReference filepath = mStorageRef.child("profile_images").child(userUid + ".jpg");
                StorageReference thumb_filepath = mStorageRef.child("profile_images").child("thumbs").child(userUid + ".jpg");

                uploadImage(resultUri, filepath, thumb_filepath, thumb_byte);

            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage(Uri resultUri, final StorageReference filepath, final StorageReference thumbpath, final byte[] thumb_byte) {
        filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //Toast.makeText(SettingsActivity.this, "Working", Toast.LENGTH_LONG).show();
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String download_uri = uri.toString();
                        UploadTask uploadTask = thumbpath.putBytes(thumb_byte);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                thumbpath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String thumb_download_uri = uri.toString();
                                        //use Map instead of HashMap to update rather than delete existing data
                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("image", download_uri);
                                        update_hashMap.put("thumb_image", thumb_download_uri);
                                        //use updateChildren instead of setValue
                                        myDatabaseRef.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mProgressBar.dismiss();
                                                } else {
                                                    Toast.makeText(SettingsActivity.this, "Error occured while uploading image", Toast.LENGTH_LONG).show();
                                                    mProgressBar.dismiss();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

}