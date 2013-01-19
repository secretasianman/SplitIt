package com.fivepoundshakes.splitit;

import java.util.List;

import com.stackmob.sdk.model.StackMobModel;

public class Expense extends StackMobModel {

    private User       owner;
    private List<User> parties;
    
    private String vendor;
    private int    amount; // in cents
    private String description;
    
    public Expense(User owner, List<User> parties, String vendor, int amount,
            String description) {
        super(Expense.class);
        this.owner       = owner;
        this.parties     = parties;
        this.vendor      = vendor;
        this.amount      = amount;
        this.description = description;
    }
}
