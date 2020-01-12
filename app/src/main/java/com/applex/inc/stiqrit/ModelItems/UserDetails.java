package com.applex.inc.stiqrit.ModelItems;

public class UserDetails {
    public String user_name;

    public UserDetails(){
    }

    public UserDetails(String username) {
        this.user_name = username;
    }

//    public static String getUser_name() {
//        return user_name;
//    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
