package com.fivepoundshakes.splitit;

public class ListEntry {

    private User    user;
    private int     amount;
    private boolean isPayment; // else is charge
    
    public ListEntry(User user, int amount, boolean isPayment) {
        this.user      = user;
        this.amount    = amount;
        this.isPayment = isPayment;
    }
    
}
