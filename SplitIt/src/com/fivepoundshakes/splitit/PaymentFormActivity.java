package com.fivepoundshakes.splitit;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class PaymentFormActivity extends Activity {

    private User self;
    private String serial;
    private String displayname;
    private String recipient;
    private int amount;

    private TextView  name;
    private Button    newButton;
    private Button    paymentsButton;
    private Button    chargesButton;
    private TextView  recipientText;
    private EditText  amountInput;
    private ImageView cashButton;
    private ImageView venmoButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentform);
        
        Intent i = getIntent();
        serial = i.getExtras().getString("serial");
        displayname = i.getExtras().getString("displayname");
        recipient = i.getExtras().getString("recipient");
        amount = i.getExtras().getInt("amount", 0);
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
        name             = (TextView)  findViewById(R.id.name);
        newButton        = (Button)    findViewById(R.id.newButton);
        paymentsButton   = (Button)    findViewById(R.id.paymentsButton);
        chargesButton    = (Button)    findViewById(R.id.chargesButton);
//        recipientText    = (TextView)  findViewById(R.id.recipientText);
        amountInput      = (EditText)  findViewById(R.id.amountInput);
//        cashButton       = (ImageView) findViewById(R.id.cashImageButton);
//        venmoButton      = (ImageView) findViewById(R.id.venmoImageButton);
        
        name.setText(displayname);
        recipientText.setText(recipient);
        amountInput.setText(CurrencyCreator.toDecimal(amount));
    }
    
    /**
     * Initializes button handlers.
     */
    private void initHandlers() {
        // TODO handlers for cash and venmo buttons
        
        newButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), 
                        ExpenseFormActivity.class);
                i.putExtra("serial", serial);
                i.putExtra("displayname", displayname);
                startActivity(i);
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

}
