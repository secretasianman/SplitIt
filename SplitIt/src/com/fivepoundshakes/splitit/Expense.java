package com.fivepoundshakes.splitit;

import java.util.List;

import com.stackmob.sdk.model.StackMobModel;

public class Expense extends StackMobModel {

    protected User       owner;
    protected List<User> parties;
    protected String     vendor;
    protected int        amount; // in cents
    protected String     description;
    
    public Expense(User owner, List<User> parties, String vendor, int amount,
            String description) {
        super(Expense.class);
        this.owner       = owner;
        this.parties     = parties;
        this.vendor      = vendor;
        this.amount      = amount;
        this.description = description;
    }
    
    @Override
    public String toString() {
        String s = owner + " paid " + vendor + " " + amount + " for " +
                description + ", ";
        for (User user : parties) {
            s += user + ", ";
        }
        return s.substring(0, s.length() - 2);
    }
}
