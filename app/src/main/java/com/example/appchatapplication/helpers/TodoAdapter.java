package com.example.appchatapplication.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ItemsHolder> {
    private static final String TAG = "TodoAdapter";

    private ArrayList<String> type;
    private ArrayList<String> activity;
    private ArrayList<Long> date;
    private ArrayList<String> sortedType;
    private Activity mActivity;

    public TodoAdapter() {
    }

    public TodoAdapter(Context context, ArrayList<String> type, ArrayList<String> activity, ArrayList<Long> date, ArrayList<String> sortedType) {
        mActivity = (Activity) context;
        this.type = type;
        this.activity = activity;
        this.date = date;
        this.sortedType = sortedType;
    }

    @NotNull
    @Override
    public TodoAdapter.ItemsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item, parent, false);
        return new ItemsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TodoAdapter.ItemsHolder holder, final int position) {

        holder.typeV.setText(type.get(position));
        holder.activityV.setText(activity.get(position));

//        holder.bindViewColor(type.get(position));
        GetTimeAgo getTimeAgo = new GetTimeAgo();
        String timePosted = getTimeAgo.getTimeAgo(date.get(position));
        holder.postedDate.setText(timePosted);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity mActivity = (AppCompatActivity) v.getContext();
                DialogFragmentHelper fragmentHelper = new DialogFragmentHelper(type.get(position), activity.get(position));
                fragmentHelper.show(mActivity.getSupportFragmentManager(), "DIALOG_FRAGMENT");
            }
        });

    }

    @Override
    public int getItemCount() {
        return activity.size();
    }


    class ItemsHolder extends RecyclerView.ViewHolder {

        public TextView activityV, typeV, postedDate, typeViewHeader;
        RelativeLayout layout;

        ItemsHolder(View itemView) {
            super(itemView);
            activityV = itemView.findViewById(R.id.title);
            typeV = itemView.findViewById(R.id.type);
            postedDate = itemView.findViewById(R.id.date);
            layout = itemView.findViewById(R.id.reltodo);
        }

        public void bindViewColor(String type){
                ColorByType map = new ColorByType(type);
                Integer artistColor = map.ColorCheck();
                layout.setBackgroundColor(itemView.getResources().getColor(artistColor));
        }

    }

}
