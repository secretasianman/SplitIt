package com.fivepoundshakes.splitit;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

    public static void show(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
