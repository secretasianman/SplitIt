package com.fivepoundshakes.splitit;

import java.util.ArrayList;
import java.util.List;

import com.fivepoundshakes.splitit.MainActivity.RequestCode;
import com.fivepoundshakes.splitit.VenmoLibrary.VenmoResponse;
import com.stackmob.sdk.api.StackMobOptions;
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
    private Button	 splitWithBtn;
    private Button   submitButton;

    private List<User> parties;

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

        parties = new ArrayList<User>();
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
        splitWithBtn   	 = (Button)   findViewById(R.id.splitWithBtn);

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
                int    amount      = CurrencyCreator.toCents(amountInput.getText().toString());
                String description = descriptionInput.getText().toString();

                if (amount <= 0) {
                    System.out.println(amount);
                    Toaster.show(getApplicationContext(), "Invalid amount!");
                    return;
                }

                Expense e = new Expense(self, parties, parties.size() + 1,
                        vendor, amount, description);
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

        splitWithBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), 
                        ContactsActivity.class);
                startActivityForResult(i, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
            case 1: {
                if(resultCode == RESULT_OK) {
                    ArrayList<String> result = data.getStringArrayListExtra("selected");
                    String previewString = "";

                    TextView splitWithList   = (TextView) findViewById(R.id.splitWithList);

                    for(int i=0;i<result.size();i=i+2){
                        previewString=previewString + result.get(i) +", ";
                        String[] name = result.get(i).split(" ");
                        final String firstName = name[0];
                        String lName = null;
                        final String phoneNumber = result.get(i+1);

                        if(name.length>1){
                            lName = name[1];
                        }
                        
                        final String lastName = lName;

                        User.query(User.class,
                                new StackMobQuery().fieldIsEqualTo("phone_number", phoneNumber),
                                StackMobOptions.depthOf(1),
                                new StackMobQueryCallback<User>() {
                            @Override
                            public void failure(StackMobException arg0) { }

                            @Override
                            public void success(List<User> result) {
                                User u;
                                if (result.size() > 0) {
                                    u = result.get(0);
                                } else {
                                    u = new User(null, firstName, lastName,
                                            phoneNumber, null);
                                    u.save();
                                }
                                parties.add(u);
                            }
                        });


                    }
                    previewString = previewString.substring(0, previewString.length()-2);
                    splitWithList.setText(previewString);
                }                
            }
            break;
        }           
    }

}
