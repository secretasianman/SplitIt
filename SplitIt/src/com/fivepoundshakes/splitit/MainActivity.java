package com.fivepoundshakes.splitit;

import android.os.Bundle;

import android.app.Activity;
import android.view.Menu;

import com.stackmob.android.sdk.common.StackMobAndroid;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StackMobAndroid.init(getApplicationContext(), 0, "ce86bf3d-f248-4c91-ac03-10e4244faa4c");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
