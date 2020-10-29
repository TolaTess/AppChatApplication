package com.example.appchatapplication.modellayer.model;

public class GenerateActivity {
    private String activity;
    private String type;
    private double price;

    public GenerateActivity() {
    }

    public GenerateActivity(String activity, String type, double price) {
        this.activity = activity;
        this.type = type;
        this.price = price;
    }

    public String getActivity() {
        return activity;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "GenerateActivity{" +
                "activity='" + activity + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                '}';
    }
}
