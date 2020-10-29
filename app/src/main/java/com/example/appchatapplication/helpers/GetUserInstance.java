package com.example.appchatapplication.helpers;

public class GetUserInstance {

    private String username;


    public GetUserInstance() {
        String artist = DataShareHolder.getInstance().getUsername();
        username = getUserName(artist);
    }

    private String getUserName(String userInstance) {
        if(userInstance != null)
            return userInstance;
        else
            //update to use ultimate artist *******
            return " ";
    }

    public String getUsername() {
        return username;
    }


}
