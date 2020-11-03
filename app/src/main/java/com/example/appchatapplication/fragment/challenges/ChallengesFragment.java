package com.example.appchatapplication.fragment.challenges;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appchatapplication.R;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.example.appchatapplication.modellayer.model.GenerateActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChallengesFragment extends Fragment {
    private RequestQueue requestQueue;
    private DatabasePresenter presenter;

    @BindView(R.id.wheel)
    ImageView wheel;
    @BindView(R.id.indicator)
    ImageView indicator;
    @BindView(R.id.type_wheel)
    TextView typeView;
    @BindView(R.id.activity_wheel)
    TextView challengeView;
    @BindView(R.id.button_wheel)
    Button acceptChallenge;

    private Unbinder unbinder;

    public ChallengesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenges, container, false);
        unbinder = ButterKnife.bind(this, view);

        wheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinWheel(v);
            }
        });

        requestQueue = Volley.newRequestQueue(getContext());
        presenter = new FirebaseDatabaseHelper();
        return view;
    }

    private void spinWheel(View view){
        Random random = new Random();
        final int degree = random.nextInt(360);

        int endDegree = degree + 720;

        RotateAnimation rotateAnimation = new RotateAnimation(0, endDegree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(3000);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //calculateSpinEnd(degree);
                displayChallenge();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        wheel.startAnimation(rotateAnimation);
    }

//    private void calculateSpinEnd(int degree){
//        int initialPoint = 0;
//        int endPoint = 30;
//
//        do{
//            if(degree > initialPoint && degree < endPoint){
//
//            }
//        }
//    }


    private void displayChallenge() {
        final String URL = "http://www.boredapi.com/api/activity/";
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
        final String type = activities.getType();
        final String activity = activities.getActivity();

        typeView.setText(type);
        challengeView.setText(activity);

        acceptChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Challenge Accepted", Toast.LENGTH_SHORT).show();
                Map setupMap = presenter.writeTodo(type, activity);
                presenter.getmRootRef().updateChildren(setupMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Saved in your challenge list", Toast.LENGTH_SHORT).show();
                            acceptChallenge.setVisibility(View.INVISIBLE);
                        } else
                        {
                            Toast.makeText(getContext(), "An Error Occurred while Posting", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        //DialogFragmentHelper fragmentHelper = new DialogFragmentHelper(type, activity);
        //fragmentHelper.show(getFragmentManager(), "DIALOG_FRAGMENT");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
