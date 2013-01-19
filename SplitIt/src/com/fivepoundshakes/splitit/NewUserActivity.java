package com.fivepoundshakes.splitit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewUserActivity extends Activity {

    private String serial;
    
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText phoneNumberInput;
    private EditText venmoIdInput;
    private Button   submitButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuser);
        
        Intent i = getIntent();
        serial = (String) i.getExtras().get("serial");
        
        initViews();
        initHandlers();
    }
    
    /**
     * Initializes fields to their respective Views.
     */
    private void initViews() {
        firstNameInput   = (EditText) findViewById(R.id.firstNameInput);
        lastNameInput    = (EditText) findViewById(R.id.lastNameInput);
        phoneNumberInput = (EditText) findViewById(R.id.phoneNumberInput);
        venmoIdInput     = (EditText) findViewById(R.id.venmoIdInput);
        submitButton     = (Button)   findViewById(R.id.submitButton);
    }
    
    /**
     * Initializes button handlers.
     */
    private void initHandlers() {
        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String phoneNumber = phoneNumberInput.getText().toString();
                String venmoId = venmoIdInput.getText().toString();
                
                User self = new User(serial, firstName, lastName, phoneNumber,
                        venmoId);
                self.save();
                
                finish();
            }
        });
    }
}
