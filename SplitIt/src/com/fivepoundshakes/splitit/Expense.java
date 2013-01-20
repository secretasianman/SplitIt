package com.fivepoundshakes.splitit;

import java.util.List;

import com.stackmob.sdk.model.StackMobModel;

public class Expense extends StackMobModel implements Comparable<Expense>{

    protected User       owner;
    protected List<User> parties;
    protected int        size;
    protected String     vendor;
    protected int        amount; // in cents
    protected String     description;
    protected int        createddate;
    
    public Expense(User owner, List<User> parties, int size, String vendor,
            int amount, String description) {
        super(Expense.class);
        this.owner       = owner;
        this.parties     = parties;
        this.size        = size;
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
    
    @Override
    public int compareTo(Expense e) {
        return e.createddate - createddate;
    }

    public int getSplitAmount() {
        return Math.round(amount / (float) size);
    }
}
