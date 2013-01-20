package com.fivepoundshakes.splitit;

import com.stackmob.sdk.model.StackMobModel;

public class Payment extends StackMobModel {

    protected User    payor; // gives the money
    protected User    payee; // gets the money
    protected int     amount;
    protected boolean completed;
    
    public Payment(User payor, User payee, int amount, boolean completed) {
        super(Payment.class);
        this.payor     = payor;
        this.payee     = payee;
        this.amount    = amount;
        this.completed = completed;
    }
    
    @Override
    public String toString() {
        return payor + " to " + payee + ": " + amount + ", compl: " + completed;
    }
}
