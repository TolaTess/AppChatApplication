package com.example.appchatapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.helpers.TodoAdapter;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.example.appchatapplication.modellayer.model.GenerateActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class TodoFragment extends Fragment {

    private RecyclerView mainRecyclerView;
    private final ArrayList<String> type = new ArrayList<>();
    private final ArrayList<String> activity = new ArrayList<>();
    private final ArrayList<String> sortedType= new ArrayList<>();
    private final ArrayList<Long> date = new ArrayList<>();
    private final ArrayList<String> dateHeader = new ArrayList<>();

    public TodoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        getData();
        mainRecyclerView = view.findViewById(R.id.todo_view_pager);

        TodoAdapter adapter = new TodoAdapter(type, activity, date, sortedType);
        mainRecyclerView.setAdapter(adapter);
        mainRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }

    private void getData(){
        final DatabasePresenter databaseHelper = new FirebaseDatabaseHelper();
        DatabaseReference myTodoRef = databaseHelper.getmRootRef()
                .child("Users_Todo").child(databaseHelper.getMcurrent_user_id());

        myTodoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String deadline_key = dataSnapshot.getKey();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    GenerateActivity generateActivity = ds.getValue(GenerateActivity.class);

                    String newType = generateActivity.getType();
                    type.add(newType);
                    activity.add(generateActivity.getActivity());
                    long dateLong = generateActivity.getDate();
                    date.add(dateLong);
                    Log.i("******** key", deadline_key);
                    dateHeader.add(deadline_key);
                    for(String newDeadline : dateHeader){
                        if (sortedType.contains(newDeadline)){
                            sortedType.add("");
                        }else{
                            sortedType.add(newDeadline);
                        }
                    }
                    String l = sortedType.toString();
                    Log.i("*****out", l);
                }
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
}
