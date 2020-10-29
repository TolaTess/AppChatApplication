package com.example.appchatapplication.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ItemsHolder> {
    private static final String TAG = "TodoAdapter";

    private ArrayList<String> type;
    private ArrayList<String> activity;
    private ArrayList<Long> date;
    private ArrayList<String> sortedType;

    public TodoAdapter(ArrayList<String> type1, ArrayList<String> activity1, ArrayList<Long> date1, ArrayList<String> sortedDeadline1) {
        type = type1;
        activity = activity1;
        date = date1;
        sortedType = sortedDeadline1;
    }

    @Override
    public TodoAdapter.ItemsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item, parent, false);
        return new ItemsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TodoAdapter.ItemsHolder holder, final int position) {
        if(sortedType.get(position).isEmpty()){
            holder.typeViewHeader.setVisibility(View.GONE);
        }/*else{
            holder.typeViewHeader.setText(sortedType.get(position));
        }*/

        holder.typeV.setText(type.get(position));
        holder.activityV.setText(activity.get(position));

        //holder.bindViewColor(type.get(position));
        GetTimeAgo getTimeAgo = new GetTimeAgo();
        String timePosted = getTimeAgo.getTimeAgo(date.get(position));
        holder.postedDate.setText(timePosted);

         //final Context context = holder.itemView.getContext();
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //pop to see if completed
            }
        });
    }

    @Override
    public int getItemCount() {
        return type.size();

    }

    class ItemsHolder extends RecyclerView.ViewHolder {

        public TextView activityV, typeV, postedDate, typeViewHeader;
        View cardview;

        ItemsHolder(View itemView) {
            super(itemView);
            activityV = itemView.findViewById(R.id.title);
            typeV = itemView.findViewById(R.id.type);
            postedDate = itemView.findViewById(R.id.date);
            typeViewHeader = itemView.findViewById(R.id.year);
            cardview = itemView.findViewById(R.id.reltodo);
        }

        public void bindViewColor(String type){
                ColorByType map = new ColorByType(type);
                Integer artistColor = map.ColorCheck();
                cardview.setBackgroundColor(itemView.getResources().getColor(artistColor));
        }

    }

}
