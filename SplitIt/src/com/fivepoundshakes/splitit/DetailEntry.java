package com.fivepoundshakes.splitit;

public class DetailEntry {

    protected String  vendor;
    protected int     amount;
    protected int     date;
    protected boolean isPayment; // else is charge
    
    public DetailEntry(String vendor, int amount, boolean isPayment) {
        this.vendor    = vendor;
        this.amount    = amount;
        this.isPayment = isPayment;
    }
    
    public DetailEntry(String vendor, int amount, int date, boolean isPayment) {
        this.vendor    = vendor;
        this.amount    = amount;
        this.date      = date;
        this.isPayment = isPayment;
    }
    
    public String toString() {
        return vendor + ": " + amount + ", " + date + ", " + isPayment;
    }
    
}
