package com.fivepoundshakes.splitit;

import com.stackmob.sdk.model.StackMobModel;

public class User extends StackMobModel {

    protected String username;
    protected String first_name;
    protected String last_name;
    protected String phone_number;
    protected String venmo_id;
    
    public User(String username, String first_name, String last_name,
            String phone_number, String venmo_id) {
        super(User.class);
        this.username     = username;
        this.first_name   = first_name;
        this.last_name    = last_name;
        this.phone_number = phone_number;
        this.venmo_id     = venmo_id;
        setID(username);
    }

    public String toString() {
        return username + ": " + first_name + " " + last_name;
    }
    
}
