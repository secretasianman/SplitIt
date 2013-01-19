package com.fivepoundshakes.splitit;

import java.text.NumberFormat;

public class CurrencyCreator {

    public static String create(int cents) {
        NumberFormat nf1 = NumberFormat.getInstance();
        nf1.setMinimumIntegerDigits(1);
        NumberFormat nf2 = NumberFormat.getInstance();
        nf2.setMinimumIntegerDigits(2);
        return "$" + nf1.format(cents / 100) + "." + nf2.format(cents % 100);
    }
}
