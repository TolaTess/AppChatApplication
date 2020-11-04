package com.example.appchatapplication.modellayer.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class GenerateActivity {
    private String activity;
    private String type;
    private long date;
    private String image;

    public GenerateActivity() {
    }

    public GenerateActivity(String type, String activity) {
        this.type = type;
        this.activity = activity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getActivity() {
        return activity;
    }

    public String getType() {
        return type;
    }

    public long getDate() {
        return date;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("date", ServerValue.TIMESTAMP);
        postMap.put("type", type);
        postMap.put("activity", activity);

        return postMap;
    }

    @Override
    public String toString() {
        return "GenerateActivity{" +
                "activity='" + activity + '\'' +
                ", type='" + type + '\'' +
                ", date=" + date +
                '}';
    }
}
