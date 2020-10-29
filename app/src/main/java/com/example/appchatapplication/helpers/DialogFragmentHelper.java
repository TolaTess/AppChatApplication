package com.example.appchatapplication.helpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.appchatapplication.R;

public class DialogFragmentHelper extends DialogFragment {

    private String type;
    private String activity;


    public DialogFragmentHelper(String type, String activity) {
        this.type = type.toUpperCase();
        this.activity = activity;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.popup_widget, container, false);

        TextView typeHolder = view.findViewById(R.id.type_pholder);
        TextView activityHolder = view.findViewById(R.id.activity_pholder);
        Button accept = view.findViewById(R.id.accept_act_btn);
        Button decline = view.findViewById(R.id.decline_act_btn);

        typeHolder.setText(type);
        activityHolder.setText(activity);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Challenge Accepted", Toast.LENGTH_SHORT).show();
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Try Again", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
