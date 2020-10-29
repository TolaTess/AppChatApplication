package com.example.appchatapplication.fragment.challenges;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appchatapplication.R;
import com.example.appchatapplication.helpers.DialogFragmentHelper;
import com.example.appchatapplication.modellayer.model.GenerateActivity;
import com.google.gson.Gson;

import org.json.JSONObject;

public class ChallengesFragment extends Fragment {
    private RequestQueue requestQueue;
    private TextView name;

    public ChallengesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenges, container, false);

        requestQueue = Volley.newRequestQueue(getContext());
        name = view.findViewById(R.id.user_name);
        defineView(view);
        return view;
    }

    private void parseJason(final String activityType) {
        final String URL = "http://www.boredapi.com/api/activity?type=" + activityType;
        final JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setupGson(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error Response", error.toString());
                    }
                }
        );
        requestQueue.add(objectRequest);
    }

    private void setupGson(JSONObject response) {
        Gson gson = new Gson();
        String result = response.toString();
        GenerateActivity activities = gson.fromJson(result, GenerateActivity.class);
        String type = activities.getType();
        String activity = activities.getActivity();

        DialogFragmentHelper fragmentHelper = new DialogFragmentHelper(type, activity);
        fragmentHelper.show(getFragmentManager(), "DIALOG_FRAGMENT");
    }

    private void defineView(View view) {
        RelativeLayout education, recreational, social, diy, charity, cooking, relaxation, music;
        education = view.findViewById(R.id.education_rel);
        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJason("education");
            }
        });

        recreational = view.findViewById(R.id.recre_rel);
        recreational.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJason("recreational");
            }
        });
        social = view.findViewById(R.id.social_rel);
        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJason("social");
            }
        });
        diy = view.findViewById(R.id.diy_rel);
        diy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJason("diy");
            }
        });
        charity = view.findViewById(R.id.charity_rel);
        charity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJason("charity");
            }
        });
        cooking = view.findViewById(R.id.cooking_rel);
        cooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJason("cooking");
            }
        });
        relaxation = view.findViewById(R.id.relaxation_rel);
        relaxation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJason("relaxation");
            }
        });
        music = view.findViewById(R.id.music_rel);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJason("music");
            }
        });

    }
}
