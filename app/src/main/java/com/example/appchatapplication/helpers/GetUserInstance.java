package com.example.appchatapplication.helpers;

public class GetUserInstance {

    private String username;


    public GetUserInstance() {
        String user = DataShareHolder.getInstance().getUsername();
        username = getUserName(user);
    }

    private String getUserName(String userInstance) {
        if(userInstance != null)
            return userInstance;
        else
            //update to use ultimate artist *******
            return "admin";
    }

    public String getUsername() {
        return username;
    }


}
