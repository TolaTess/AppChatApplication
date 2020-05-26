package com.example.appchatapplication.activities.status;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.appchatapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private static final String TAG = "StatusActivity";

    private Toolbar toolbar;
    private TextInputLayout mStatus;
    private Button mButton;
    private ProgressDialog mRegProgress;

    //Firebase
    private DatabaseReference mydataBaseRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        mydataBaseRef = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(userId);

        toolbar = findViewById(R.id.status_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //persist data
        String status_value = getIntent().getStringExtra("status_value");

        mStatus = findViewById(R.id.status_input);
        mButton = findViewById(R.id.status_btn);

        mStatus.getEditText().setHint(status_value);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                mRegProgress = new ProgressDialog(StatusActivity.this);
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
                            Toast.makeText(StatusActivity.this, "Error saving changes", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
