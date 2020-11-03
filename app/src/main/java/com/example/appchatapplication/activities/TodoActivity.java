package com.example.appchatapplication.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;
import com.example.appchatapplication.helpers.BottomNavPresenter;
import com.example.appchatapplication.helpers.TodoAdapter;
import com.example.appchatapplication.modellayer.database.DatabasePresenter;
import com.example.appchatapplication.modellayer.database.FirebaseDatabaseHelper;
import com.example.appchatapplication.modellayer.model.GenerateActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TodoActivity extends AppCompatActivity {
    private static final int ACTIVITY_NUM = 1;

    @BindView(R.id.todo_view_pager)
    RecyclerView mainRecyclerView;

    private Unbinder unbinder;

    private final ArrayList<String> type = new ArrayList<>();
    private final ArrayList<String> activity = new ArrayList<>();
    private final ArrayList<String> sortedType = new ArrayList<>();
    private final ArrayList<Long> date = new ArrayList<>();
    private final ArrayList<String> dateHeader = new ArrayList<>();

    private TodoAdapter adapter;
    private DatabasePresenter databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        unbinder = ButterKnife.bind(this);

        setupToolbar();
        setupBottomNav();
        databaseHelper = new FirebaseDatabaseHelper();

        adapter = new TodoAdapter(this, type, activity, date, sortedType);

        mainRecyclerView.setAdapter(adapter);
        new ItemTouchHelper(deleteCallback).attachToRecyclerView(mainRecyclerView);
        new ItemTouchHelper(completedCallback).attachToRecyclerView(mainRecyclerView);
        mainRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        getData();

    }

    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.todo_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("To Do");
    }

    private void getData(){
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
                    Log.i("*****frag", activity.toString());
                    long dateLong = generateActivity.getDate();
                    date.add(dateLong);
                    dateHeader.add(deadline_key);
                    for(String newDeadline : dateHeader){
                        if (sortedType.contains(newDeadline)){
                            sortedType.add("");
                        }else{
                            sortedType.add(newDeadline);
                        }
                    }
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
    ItemTouchHelper.SimpleCallback deleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            activity.remove(viewHolder.getAdapterPosition());
            Toast toast = Toast.makeText(TodoActivity.this, "Removed", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 90, 0);
            toast.show();
            adapter.notifyDataSetChanged();
            //remove from database
        }
    };

    ItemTouchHelper.SimpleCallback completedCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            activity.remove(viewHolder.getAdapterPosition());
            Toast toast = Toast.makeText(TodoActivity.this, "Well done! How about another?", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 90, 0);
            toast.show();
            adapter.notifyDataSetChanged();
            //add to challenge completed database
        }
    };

    private void setupBottomNav() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavPresenter bottomNavPresenter = new BottomNavPresenter(this, ACTIVITY_NUM);
        bottomNavPresenter.setupBottomNavigationView(bottomNavigationViewEx);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
