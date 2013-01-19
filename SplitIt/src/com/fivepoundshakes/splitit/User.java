package com.fivepoundshakes.splitit;

import com.stackmob.sdk.model.StackMobModel;

public class User extends StackMobModel {

    private String username;
    private String first_name;
    private String last_name;
    private String venmo_id;
    
    public User(String username, String first_name, String last_name,
            String venmo_id) {
        super(User.class);
        this.username   = username;
        this.first_name = first_name;
        this.last_name  = last_name;
        this.venmo_id   = venmo_id;
    }
}
