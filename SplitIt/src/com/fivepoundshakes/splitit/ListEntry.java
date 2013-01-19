package com.fivepoundshakes.splitit;

public class ListEntry {

    protected User    user;
    protected int     amount;
    protected boolean isPayment; // else is charge
    
    public ListEntry(User user, int amount, boolean isPayment) {
        this.user      = user;
        this.amount    = amount;
        this.isPayment = isPayment;
    }
    
    public String toString() {
        return user.first_name + " " + user.last_name + ": " + amount + ", " + isPayment;
    }
    
}
