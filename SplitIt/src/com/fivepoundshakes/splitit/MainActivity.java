package com.fivepoundshakes.splitit;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.callback.StackMobModelCallback;
import com.stackmob.sdk.exception.StackMobException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StackMobAndroid.init(getApplicationContext(), 0,
                "ce86bf3d-f248-4c91-ac03-10e4244faa4c");
        
        // Testing User
        User u = new User("robert", "Robert", "Li", "robertli");
        u.save(new StackMobModelCallback() {
            @Override
            public void failure(StackMobException arg0) {
                System.out.println("Failure: " + arg0.getMessage());
            }
            
            @Override
            public void success() {
                System.out.println("Success!");
            }
        });
        
        // Make more!
        User u1 = new User("jean", "Jean", "Kim", "jeankim");
        u1.save(new StackMobModelCallback() {
            @Override
            public void failure(StackMobException arg0) {
                System.out.println("Failure: " + arg0.getMessage());
            }
            
            @Override
            public void success() {
                System.out.println("Success!");
            }
        });
        
        User u2 = new User("aimee", "Aimee", "Kim", "aimeekim");
        u2.save(new StackMobModelCallback() {
            @Override
            public void failure(StackMobException arg0) {
                System.out.println("Failure: " + arg0.getMessage());
            }
            
            @Override
            public void success() {
                System.out.println("Success!");
            }
        });
        
        // Test Expense
        ArrayList<User> parties = new ArrayList<User>();
        parties.add(u1);
        parties.add(u2);
        Expense e = new Expense(u, parties, "McDonald's", 50, "50 McChickens!");
        e.save(new StackMobModelCallback() {
            @Override
            public void failure(StackMobException arg0) {
                System.out.println("Failure: " + arg0.getMessage());
            }
            
            @Override
            public void success() {
                System.out.println("Success!");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
