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

public class ExpenseFormActivity extends Activity {

    User self;
    String serial;
    
    EditText vendorInput;
    EditText amountInput;
    EditText descriptionInput;
    Button   submitButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenseform);
        
        Intent i = getIntent();
        serial = (String) i.getExtras().get("serial");
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
        vendorInput      = (EditText) findViewById(R.id.vendorInput);
        amountInput      = (EditText) findViewById(R.id.amountInput);
        descriptionInput = (EditText) findViewById(R.id.descriptionInput);
        submitButton     = (Button)   findViewById(R.id.submitButton);
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
                
                Expense e = new Expense(self, new ArrayList<User>(), vendor,
                        amount, description);
                e.save();
                
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
