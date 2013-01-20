package com.fivepoundshakes.splitit;

import java.text.NumberFormat;

public class CurrencyCreator {

    public static String toDollars(int cents) {
        return "$" + toDecimal(cents);
    }
    
    public static String toDecimal(int cents) {
        NumberFormat nf1 = NumberFormat.getInstance();
        nf1.setMinimumIntegerDigits(1);
        NumberFormat nf2 = NumberFormat.getInstance();
        nf2.setMinimumIntegerDigits(2);
        return nf1.format(cents / 100) + "." + nf2.format(cents % 100);
    }
    
    public static int toCents(String amount) {
        try {
            return Integer.parseInt(amount);
        } catch (NumberFormatException e) { }
        
        try {
            String[] nums = amount.split("\\.");
            return Integer.parseInt(nums[0]) * 100 +
                    Integer.parseInt(nums[1].substring(0, 2));
        } catch (Exception e) { }

        return 0; // error
    }
}
