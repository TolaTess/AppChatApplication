package com.example.appchatapplication.modellayer.model;

public class ReceivedMessage {

    private String message, type, from;
    private long time;
    private boolean seen;

    public ReceivedMessage(){}

    public ReceivedMessage(String messages, String type, long time, boolean seen, String from) {
        this.message = messages;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

     public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}