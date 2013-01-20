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
import android.widget.TextView;

import com.fivepoundshakes.splitit.UserDetailsActivity.RequestCode;
import com.fivepoundshakes.splitit.VenmoLibrary.VenmoResponse;
import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class MainActivity extends Activity {
	enum RequestCode {
        NEWUSER
    };
    
    private User self;
    private String serial;
    
    private Button   addExpenseButton;
    private TextView paymentsLabel;
    private TextView paymentAmountLabel;
    private TextView chargesLabel;
    private TextView chargeAmountLabel;
    
    
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
                System.out.println("failure");
                newUser();
            }

            @Override
            public void success(List<User> users) {
                if (users.size() == 0) {
                    System.out.println("no results");
                    newUser();
                    return;
                }
                self = users.get(0);
                System.out.println("got user");
            }
        });
    }
    
    /**
     * Initializes fields to their respective Views.
     */
    private void initViews() {
        addExpenseButton   = (Button)   findViewById(R.id.addExpenseButton);
        paymentsLabel      = (TextView) findViewById(R.id.paymentsLabel);
        paymentAmountLabel = (TextView) findViewById(R.id.paymentAmountLabel);
        chargesLabel       = (TextView) findViewById(R.id.chargesLabel);
        chargeAmountLabel  = (TextView) findViewById(R.id.chargeAmountLabel);
        
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
                i.putExtra("displayname", self.first_name + " " + self.last_name);
                startActivity(i);
            }
        });
        
        paymentsLabel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        ListPaymentsActivity.class);
                i.putExtra("serial", serial);
                i.putExtra("displayname", self.first_name + " " + self.last_name);
                startActivity(i);
            }
        });
        
        chargesLabel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        ListChargesActivity.class);
                i.putExtra("serial", serial);
                i.putExtra("displayname", self.first_name + " " + self.last_name);
                startActivity(i);
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        RequestCode request = RequestCode.values()[requestCode];
        switch(request) {
            case NEWUSER: {
                getUser();
                break;
            }
        }
    }
    
    /**
     * Prompt user for details and adds new user to database.
     */
    private void newUser() {
        Intent i = new Intent(getApplicationContext(), NewUserActivity.class);
        i.putExtra("serial", serial);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(i, RequestCode.NEWUSER.ordinal());
    }

}
