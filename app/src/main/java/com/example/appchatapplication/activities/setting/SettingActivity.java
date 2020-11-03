package com.example.appchatapplication.activities.setting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appchatapplication.R;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "StatusActivity";
    private static final int GALLERY_PICK = 1;

    @BindView(R.id.status_input)
    TextInputLayout mStatus;
    @BindView(R.id.status_btn)
    Button mSaveStatus;
    @BindView(R.id.change_image)
    Button mChangeImage;

    private ProgressDialog mRegProgress;
    private String userId;

    private Unbinder unbinder;

    //Firebase
    private FirebaseDatabaseHelper helper;
    private DatabaseReference mydataBaseRef;
    private ProgressDialog mProgressBar;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        unbinder = ButterKnife.bind(this);

        //Firebase
        helper = new FirebaseDatabaseHelper();
        userId = helper.getMcurrent_user_id();
        mydataBaseRef = helper.getmUserDatabase().child(userId);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        setupToolbar();

        //persist data
        String status_value = getIntent().getStringExtra("status_value");


        mStatus.getEditText().setHint(status_value);

        mSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                mRegProgress = new ProgressDialog(SettingActivity.this);
                mRegProgress.setTitle("Saving changes");
                mRegProgress.setMessage("Please wait while we save the changes");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();

                String status_v = mStatus.getEditText().getText().toString();
                mydataBaseRef.child("status").setValue(status_v).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: DataBase");
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: is Sucessfull");
                            mRegProgress.dismiss();
                        } else {
                            Log.d(TAG, "onComplete: is not sucessfull");
                            mRegProgress.hide();
                            Toast.makeText(SettingActivity.this, "Error saving changes", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.status_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                mProgressBar = new ProgressDialog(SettingActivity.this);
                mProgressBar.setTitle("Uploading Image");
                mProgressBar.setMessage("Please wait while we upload and process the image");
                mProgressBar.setCanceledOnTouchOutside(false);
                mProgressBar.show();

                Uri resultUri = result.getUri();
                File thumb_file = new File(resultUri.getPath());
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


                final StorageReference filepath = mStorageRef.child("profile_images").child(userId + ".jpg");
                StorageReference thumb_filepath = mStorageRef.child("profile_images").child("thumbs").child(userId + ".jpg");

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
                                        mydataBaseRef.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mProgressBar.dismiss();
                                                } else {
                                                    Toast.makeText(SettingActivity.this, "Error occured while uploading image", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
