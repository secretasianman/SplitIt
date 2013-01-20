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
    
    @Override
    public boolean equals(Object o) {
        System.out.println("EQUALS!!!!!");
        if (o instanceof User) {
            User u = (User) o;
            System.out.println(username.equals(u.username) &&
                    first_name.equals(u.first_name) &&
                    last_name.equals(u.last_name) &&
                    phone_number.equals(u.phone_number) &&
                    venmo_id.equals(u.venmo_id));
            return username.equals(u.username) &&
                    first_name.equals(u.first_name) &&
                    last_name.equals(u.last_name) &&
                    phone_number.equals(u.phone_number) &&
                    venmo_id.equals(u.venmo_id);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return username.hashCode();
    }
    
}
