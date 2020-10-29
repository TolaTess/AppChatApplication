package com.example.appchatapplication.helpers;

public class DataShareHolder {

    //design pattern to share arguments between activities
    private static DataShareHolder dataHolder = null;

    private DataShareHolder() {
    }

    public static DataShareHolder getInstance() {
        if (dataHolder == null)
        {
            dataHolder = new DataShareHolder();
        }
        return dataHolder;
    }

    private String username;
    private String userImage;
    private String status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
