package com.fivepoundshakes.splitit;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.fivepoundshakes.splitit.MainActivity.RequestCode;
import com.fivepoundshakes.splitit.VenmoLibrary.VenmoResponse;
import com.stackmob.sdk.api.StackMobOptions;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class UserDetailsActivity extends ListActivity {
    enum RequestCode {
        VENMO
    };

    private final String VENMO_APP_ID = "1219";
    private final String VENMO_APP_SECRET = "Xzygvtey8MhKw9Fgf3PdNHMkxLZfU9pw";
    private final String VENMO_APP_NAME = "SplitIt";
    private final String VENMO_TEST_TXN = "pay";
    private final String VENMO_TAG = "Sent through Split It!";

    private String  serial;
    private User    self;
    private String  displayname;
    private User    recipient;
    private String  recipientname;
    private String  recipientserial;
    private int     amount;
    private boolean ispayment;

    private TextView          name;
    private Button            newButton;
    private Button            paymentsButton;
    private Button            chargesButton;
    private TextView          nameText;
    private TextView          amountText;
    private QuickContactBadge picture;
    private Button            payButton;

    private List<DetailEntry>  transactions;
    private List<Expense>      expenses;
    private DetailEntryAdapter adapter;
    private LayoutInflater     vi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Intent i = getIntent();
        serial          = i.getExtras().getString("serial");
        displayname     = i.getExtras().getString("displayname");
        recipientname   = i.getExtras().getString("recipient");
        recipientserial = i.getExtras().getString("recipientserial");
        amount          = i.getExtras().getInt("amount", 0);
        ispayment       = i.getExtras().getBoolean("ispayment");
        getUser();
        getRecipient();

        transactions = new LinkedList<DetailEntry>();
        expenses     = new LinkedList<Expense>();
        adapter      = new DetailEntryAdapter(this, transactions, vi);
        setListAdapter(adapter);

        initViews();
        initHandlers();

        refresh();
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

    private void getRecipient() {
        User.query(User.class,
                new StackMobQuery().fieldIsEqualTo("username", recipientserial),
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
                recipient = users.get(0);
                System.out.println("got recipient");
            }
        });
    }

    /**
     * Initializes fields to their respective Views.
     */
    private void initViews() {
        name           = (TextView)          findViewById(R.id.name);
        newButton      = (Button)            findViewById(R.id.newButton);
        paymentsButton = (Button)            findViewById(R.id.paymentsButton);
        chargesButton  = (Button)            findViewById(R.id.chargesButton);
        nameText       = (TextView)          findViewById(R.id.nameText);
        amountText     = (TextView)          findViewById(R.id.amountText);
        picture        = (QuickContactBadge) findViewById(R.id.picture);
        payButton      = (Button)            findViewById(R.id.payButton);

        name.setText(displayname);
        nameText.setText(recipientname);
        amountText.setText(CurrencyCreator.toDollars(amount));
        if (ispayment) {
            amountText.setTextColor(getResources().getColor(R.color.money_red));
        } else {
            amountText.setTextColor(getResources().getColor(R.color.money_green));
        }
        // TODO picture

        if (ispayment) {
            payButton.setVisibility(View.VISIBLE);
        } else {
            payButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Initializes button handlers.
     */
    private void initHandlers() {
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

        payButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentDialog();
            }
        });
    }

    private void refresh() {
        transactions.clear();

        // First query where you are the owner and the other person is a participant
        Expense.query(Expense.class,
                new StackMobQuery().fieldIsEqualTo("owner", serial)
                .fieldIsIn("parties", Arrays.asList(recipientserial)),
                StackMobOptions.depthOf(1),
                new StackMobQueryCallback<Expense>() {
            @Override
            public void failure(StackMobException e) {
                Toaster.show(getApplicationContext(), "Expense lookup error - fail");
            }

            @Override
            public void success(List<Expense> expenses) {
                updateList(expenses);
            }
        });

        // Second query is the opposite
        Expense.query(Expense.class,
                new StackMobQuery().fieldIsIn("parties", Arrays.asList(serial))
                .fieldIsEqualTo("owner", recipientserial),
                StackMobOptions.depthOf(1),
                new StackMobQueryCallback<Expense>() {
            @Override
            public void failure(StackMobException e) {
                Toaster.show(getApplicationContext(), "Expense lookup error - fail");
            }

            @Override
            public void success(List<Expense> expenses) {
                updateList(expenses);
            }
        });
    }

    private void updateList(List<Expense> expenses) {
        this.expenses.addAll(expenses);
        Collections.sort(expenses);
        int i = 0, j = 0;
        while (i < transactions.size()) {
            if (j >= expenses.size()) {
                break;
            }
            DetailEntry t = transactions.get(i);
            Expense   e = expenses.get(j);
            if (e.createddate < t.date) {
                transactions.add(i, new DetailEntry(e.vendor,
                        e.getSplitAmount(), e.createddate,
                        !e.owner.equals(self)));
                j++;
            }
            i++;
        }
        while (j < expenses.size()) {
            Expense e = expenses.get(j);
            transactions.add(new DetailEntry(e.vendor, e.getSplitAmount(),
                    e.createddate, !e.owner.equals(self)));
            j++;
        }
        refreshHandler.sendEmptyMessage(0);
    }

    @SuppressLint("HandlerLeak")
    Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
        };
    };

    private void showPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_paymentform, null);

        TextView recipientText     = (TextView)  view.findViewById(R.id.recipientText);
        final EditText amountInput = (EditText)  view.findViewById(R.id.amountInput);
        ImageView venmoImage       = (ImageView) view.findViewById(R.id.venmoImage);
        ImageView cashImage        = (ImageView) view.findViewById(R.id.cashImage);

        builder.setView(view);
        final AlertDialog dialog = builder.show();

        recipientText.setText(recipientname);
        amountInput.setText(CurrencyCreator.toDecimal(amount));
        venmoImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                venmoInit(recipient.phone_number,amountInput.getText().toString(),VENMO_TAG);
                Toaster.show(getApplicationContext(), "venmo!");
            }
        });
        cashImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                int amount = CurrencyCreator.toCents(amountInput.getText().toString());
                Payment payment = new Payment(self, recipient, amount, false);
                payment.save();
                Toaster.show(getApplicationContext(),
                        "Transaction complete. Go pay " + recipientname +
                        " " + CurrencyCreator.toDollars(amount) + "!");
                dialog.dismiss();
                finish();
            }
        });
    }

    /**
     * Venmo payment flow.
     */
    private void venmoInit(String recipient, String amt, String note){

        try {
            Intent venmoIntent = VenmoLibrary.openVenmoPayment(VENMO_APP_ID, VENMO_APP_NAME, recipient, amt, note, VENMO_TEST_TXN);
            startActivityForResult(venmoIntent, RequestCode.VENMO.ordinal());
        } catch (android.content.ActivityNotFoundException er) {
            //Venmo native app not install on device, so let's instead open a mobile web version of Venmo in a WebView
            Intent venmoIntent = new Intent(UserDetailsActivity.this, VenmoWebViewActivity.class);
            String venmo_uri = VenmoLibrary.openVenmoPaymentInWebView(VENMO_APP_ID, VENMO_APP_NAME, recipient, amt, note, VENMO_TEST_TXN);
            venmoIntent.putExtra("url", venmo_uri);
            startActivityForResult(venmoIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        RequestCode request = RequestCode.values()[requestCode];
        switch(request) {
            case VENMO: {
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
