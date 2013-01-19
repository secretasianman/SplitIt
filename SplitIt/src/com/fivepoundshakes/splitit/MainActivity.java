package com.fivepoundshakes.splitit;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fivepoundshakes.splitit.VenmoLibrary.VenmoResponse;
import com.stackmob.android.sdk.common.StackMobAndroid;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class MainActivity extends Activity {
	final String VENMO_APP_ID = "1219";
	final String VENMO_APP_SECRET = "Xzygvtey8MhKw9Fgf3PdNHMkxLZfU9pw";
	final String VENMO_APP_NAME = "SplitIt";
	final String VENMO_TEST_TXN = "pay";
	
	//Test vars
	String VENMO_TEST_RECIPIENT = "robertli";
	String VENMO_TEST_AMT = "1";
	String VENMO_TEST_NOTE = "Testing this shit, foo!";
	

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
    
    /**
     * Venmo payment flow.
     */
    private void venmoInit(String recipient, String amt, String note){
    	
        try {
        	//Needs input from split input page
            Intent venmoIntent = VenmoLibrary.openVenmoPayment(VENMO_APP_ID, VENMO_APP_NAME, recipient, amt, note, VENMO_TEST_TXN);
            startActivityForResult(venmoIntent, 1); //1 is the requestCode we are using for Venmo. Feel free to change this to another number. 
        }
        catch (android.content.ActivityNotFoundException er) //Venmo native app not install on device, so let's instead open a mobile web version of Venmo in a WebView
        {
            Intent venmoIntent = new Intent(MainActivity.this, VenmoWebViewActivity.class);
            String venmo_uri = VenmoLibrary.openVenmoPaymentInWebView(VENMO_APP_ID, VENMO_APP_NAME, recipient, amt, note, VENMO_TEST_TXN);
            venmoIntent.putExtra("url", venmo_uri);
            startActivityForResult(venmoIntent, 1);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
            case 1: { //1 is the requestCode we picked for Venmo earlier when we called startActivityForResult
                if(resultCode == RESULT_OK) {
                    String signedrequest = data.getStringExtra("signedrequest");
                    if(signedrequest != null) {
                        VenmoResponse response = (new VenmoLibrary()).validateVenmoPaymentResponse(signedrequest, VENMO_APP_SECRET);
                        if(response.getSuccess().equals("1")) {
                            //Payment successful.  Use data from response object to display a success message
                            String note = response.getNote();
                            String amount = response.getAmount();
                        }
                    }
                    else {
                        String error_message = data.getStringExtra("error_message");
                        //An error occurred.  Make sure to display the error_message to the user
                    }                               
                }
                else if(resultCode == RESULT_CANCELED) {
                    //The user cancelled the payment
                }
            break;
            }           
        }
    }

}