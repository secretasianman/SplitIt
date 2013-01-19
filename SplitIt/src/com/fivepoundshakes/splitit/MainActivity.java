package com.fivepoundshakes.splitit;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class MainActivity extends Activity {

    User self;
    String serial;
    
    Button addExpenseButton;
    Button addGroupButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StackMobAndroid.init(getApplicationContext(), 0,
                "ce86bf3d-f248-4c91-ac03-10e4244faa4c");
        
        getUser();
        
        initViews();
        initHandlers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void getUser() {
        TelephonyManager tMgr =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        serial = tMgr.getDeviceId();
        System.out.println("serial: " + serial);
        
        User.query(User.class,
                new StackMobQuery().fieldIsEqualTo("username", serial),
                new StackMobQueryCallback<User>() {
            @Override
            public void failure(StackMobException e) {
                newUser();
            }

            @Override
            public void success(List<User> users) {
                if (users.size() == 0) {
                    newUser();
                    return;
                }
                self = users.get(0);
                System.out.println("got user");
            }
            
            private void newUser() {
                self = new User(serial, "Robert", "Li", "4085135799",
                        "robertli");
                self.save();
                System.out.println("made new user");
            }
        });
    }
    
    /**
     * Initializes fields to their respective Views.
     */
    private void initViews() {
        addExpenseButton = (Button) findViewById(R.id.addExpenseButton);
        addGroupButton   = (Button) findViewById(R.id.addGroupButton);
    }
    
    /**
     * Initializes button handlers.
     */
    private void initHandlers() {
        addExpenseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        ExpenseFormActivity.class);
                i.putExtra("serial", serial);
                startActivity(i);
            }
        });
    }

}
