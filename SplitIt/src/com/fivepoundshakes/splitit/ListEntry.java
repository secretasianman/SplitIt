package com.fivepoundshakes.splitit;

public class ListEntry {

    protected User    user;
    protected int     amount;
    protected int     date;
    protected boolean isPayment; // else is charge
    protected boolean pending;
    
    public ListEntry(User user, int amount, boolean isPayment) {
        this.user      = user;
        this.amount    = amount;
        this.isPayment = isPayment;
    }
    
    public ListEntry(User user, int amount, boolean isPayment, boolean pending) {
        this.user      = user;
        this.amount    = amount;
        this.isPayment = isPayment;
        this.pending   = pending;
    }
    
    public ListEntry(User user, int amount, int date, boolean isPayment) {
        this.user      = user;
        this.amount    = amount;
        this.date      = date;
        this.isPayment = isPayment;
    }
    
    public String toString() {
        return user.first_name + " " + user.last_name + ": " + amount + ", " +
                date + ", " + isPayment;
    }
    
}
