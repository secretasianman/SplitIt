package com.fivepoundshakes.splitit;

import java.util.ArrayList;
import java.util.List;

import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ExpenseFormActivity extends Activity {

    private User self;
    private String serial;
    private String displayname;

    private TextView name;
    private Button   newButton;
    private Button   paymentsButton;
    private Button   chargesButton;
    private EditText vendorInput;
    private EditText amountInput;
    private EditText descriptionInput;
    private Button   submitButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenseform);
        
        Intent i = getIntent();
        serial = (String) i.getExtras().get("serial");
        displayname = (String) i.getExtras().get("displayname");
        getUser();
        
        initViews();
        initHandlers();
        
    }
    
    private void getUser() {
        User.query(User.class,
                new StackMobQuery().fieldIsEqualTo("username", serial),
                new StackMobQueryCallback<User>() {
            @Override
            public void failure(StackMobException e) {
                Toaster.show(getApplicationContext(), "User lookup error - fail");
            }

            @Override
            public void success(List<User> users) {
                if (users.size() == 0) {
                    Toaster.show(getApplicationContext(), "User lookup error - no results");
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
        name             = (TextView) findViewById(R.id.name);
        newButton        = (Button)   findViewById(R.id.newButton);
        paymentsButton   = (Button)   findViewById(R.id.paymentsButton);
        chargesButton    = (Button)   findViewById(R.id.chargesButton);
        vendorInput      = (EditText) findViewById(R.id.vendorInput);
        amountInput      = (EditText) findViewById(R.id.amountInput);
        descriptionInput = (EditText) findViewById(R.id.descriptionInput);
        submitButton     = (Button)   findViewById(R.id.submitButton);
        
        name.setText(displayname);
    }
    
    /**
     * Initializes button handlers.
     */
    private void initHandlers() {
        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String vendor      = vendorInput.getText().toString();
                int    amount      = toCents(amountInput.getText().toString());
                String description = descriptionInput.getText().toString();
                
                if (amount <= 0) {
                    System.out.println(amount);
                    Toaster.show(getApplicationContext(), "Invalid amount!");
                    return;
                }
                
                List<User> parties = new ArrayList<User>();
                parties.add(self);
                
                Expense e = new Expense(self, parties, vendor,
                        amount, description); // TODO fix this
                e.save();
                
                finish();
            }
        });
        
        paymentsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), 
                        ListPaymentsActivity.class);
                i.putExtra("serial", serial);
                i.putExtra("displayname", displayname);
                startActivity(i);
                finish();
            }
        });
        
        chargesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), 
                        ListChargesActivity.class);
                i.putExtra("serial", serial);
                i.putExtra("displayname", displayname);
                startActivity(i);
                finish();
            }
        });
    }
    
    private int toCents(String amount) {
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
