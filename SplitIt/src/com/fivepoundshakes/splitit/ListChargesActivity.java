package com.fivepoundshakes.splitit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.stackmob.sdk.api.StackMobOptions;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class ListChargesActivity extends ListActivity {

    private String serial;
    private User   self;
    private String displayname;
    
    private TextView name;
    private Button   newButton;
    private Button   paymentsButton;
    private Button   chargesButton;
    private TextView noResults;
    
    private Map<User, ListEntry> payments;
    private Map<User, ListEntry> charges;
    private int count = 0;
    
    private List<Payment> pending;
    
    private List<ListEntry>  aggregated;
    private ListEntryAdapter adapter;
    private LayoutInflater   vi;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listaggregates);
        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        Intent i = getIntent();
        serial = (String) i.getExtras().get("serial");
        displayname = (String) i.getExtras().get("displayname");
        getUser();
        
        payments   = new HashMap<User, ListEntry>();
        charges    = new HashMap<User, ListEntry>();
        aggregated = new LinkedList<ListEntry>();
        adapter    = new ListEntryAdapter(this, aggregated, vi);
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
    
    /**
     * Initializes fields to their respective Views.
     */
    private void initViews() {
        name             = (TextView) findViewById(R.id.name);
        newButton        = (Button)   findViewById(R.id.newButton);
        paymentsButton   = (Button)   findViewById(R.id.paymentsButton);
        chargesButton    = (Button)   findViewById(R.id.chargesButton);
        noResults        = (TextView) findViewById(R.id.noResults);
        
        name.setText(displayname);
        chargesButton.setBackgroundResource(R.drawable.activebutton);
    }
    
    /**
     * Initializes button handlers.
     */
    private void initHandlers() {
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
    }
    
    private void refresh() {
        // Get charges
        Expense.query(Expense.class,
                new StackMobQuery().fieldIsEqualTo("owner", serial),
                StackMobOptions.depthOf(1),
                new StackMobQueryCallback<Expense>() {
                    @Override
                    public void failure(StackMobException e) {
                        Toaster.show(getApplicationContext(), "Expense lookup error - fail");
                    }

                    @Override
                    public void success(List<Expense> expenses) {
                        System.out.println("qc" + expenses.size());
                        System.out.println(expenses.get(0));
                        updateCharges(expenses);
                    }
                });
        
        // Get payments
        Expense.query(Expense.class,
                new StackMobQuery().fieldIsIn("parties", Arrays.asList(serial)),
                StackMobOptions.depthOf(1),
                new StackMobQueryCallback<Expense>() {
                    @Override
                    public void failure(StackMobException e) {
                        Toaster.show(getApplicationContext(), "Expense lookup error - fail");
                    }

                    @Override
                    public void success(List<Expense> expenses) {
                        System.out.println("qp" + expenses.size());
                        updatePayments(expenses);
                    }
                });
        
        // Get pending
        Payment.query(Payment.class,
                new StackMobQuery().fieldIsEqualTo("completed", "false")
                    .fieldIsEqualTo("payee", serial),
                StackMobOptions.depthOf(1),
                new StackMobQueryCallback<Payment>() {
                    @Override
                    public void failure(StackMobException e) {
                        Toaster.show(getApplicationContext(), "Payment lookup error - fail");
                    }

                    @Override
                    public void success(List<Payment> payments) {
                        pending = payments;
                        count++;
                        if (count == 3) {
                            combineLists();
                        }
                    }
                });
    }
    
    private void updatePayments(List<Expense> expenses) {
        HashMap<User, LinkedList<Expense>> individual =
                new HashMap<User, LinkedList<Expense>>();
        for (Expense expense : expenses) {
            User owner = expense.owner;
            LinkedList<Expense> list;
            if (individual.containsKey(owner)) {
                list = individual.get(owner);
            } else {
                list = new LinkedList<Expense>();
                individual.put(owner, list);
            }
            list.add(expense);
        }
        
        payments.clear();
        for (Map.Entry<User, LinkedList<Expense>> entry : individual.entrySet()) {
            User user = entry.getKey();
            LinkedList<Expense> list = entry.getValue();
            int total = 0;
            for (Expense e : list) {
                total += e.getSplitAmount();
            }
            payments.put(user, new ListEntry(user, total, true));
        }
        System.out.println("P"+payments.size());
        
        count++;
        if (count == 3) {
            combineLists();
        }
    }
    
    private void updateCharges(List<Expense> expenses) {
        HashMap<User, LinkedList<Expense>> individual =
                new HashMap<User, LinkedList<Expense>>();
        for (Expense expense : expenses) {
            for (User user : expense.parties) {
                System.out.println(user);
                LinkedList<Expense> list;
                if (individual.containsKey(user)) {
                    list = individual.get(user);
                } else {
                    list = new LinkedList<Expense>();
                }
                list.add(expense);
                individual.put(user, list);
                System.out.println(user);
            }
        }
        System.out.println("ind" + individual.size());
        
        charges.clear();
        for (Map.Entry<User, LinkedList<Expense>> entry : individual.entrySet()) {
            User user = entry.getKey();
            LinkedList<Expense> list = entry.getValue();
            int total = 0;
            for (Expense e : list) {
                total += e.getSplitAmount();
            }
            charges.put(user, new ListEntry(user, total, false));
        }
        System.out.println("C"+charges.size());
        
        count++;
        if (count == 3) {
            combineLists();
        }
    }
    
    private void combineLists() {
        aggregated.clear();
        for (Map.Entry<User, ListEntry> entry : charges.entrySet()) {
            User      u = entry.getKey();
            ListEntry e = entry.getValue();
            if (payments.containsKey(u)) {
                ListEntry e2 = payments.get(u);
                if (e.amount > e2.amount) {
                    e.amount -= e2.amount;
                    aggregated.add(e);
                }
            } else {
                aggregated.add(e);
            }
        }
        
        for (Payment payment : pending) {
            aggregated.add(new ListEntry(payment.payor, payment.amount, false, true));
        }
        
        count = 0;
        refreshHandler.sendEmptyMessage(0);
    }
    
    @SuppressLint("HandlerLeak")
    Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            if (aggregated.size() == 0) {
                noResults.setVisibility(View.VISIBLE);
            } else if (noResults.getVisibility() == View.VISIBLE) {
                noResults.setVisibility(View.GONE);
            }
        };
    };
    
    @Override
    protected void onListItemClick(ListView l, View v, int pos, long id) {
        ListEntry clicked = aggregated.get(pos);
        if (clicked.pending) {
            confirmPending(clicked);
            return;
        }
        
        super.onListItemClick(l, v, pos, id);
        User user = clicked.user;
        Intent i = new Intent(getApplicationContext(), UserDetailsActivity.class);
        i.putExtra("serial", serial);
        i.putExtra("displayname", displayname);
        i.putExtra("recipient", user.first_name + " " + user.last_name);
        i.putExtra("recipientserial", user.username);
        i.putExtra("amount", clicked.amount);
        i.putExtra("ispayment", clicked.isPayment);
        startActivity(i);
    }
    
    private void confirmPending(final ListEntry entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Payment");
        builder.setMessage("Did you receive " +
                CurrencyCreator.toDollars(entry.amount) + " from " +
                entry.user.first_name + " " + entry.user.last_name + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (Payment payment : pending) {
                    if (entry.amount == payment.amount &&
                            entry.user.equals(payment.payor)) {
                        payment.completed = true;
                        payment.save();
                        refresh();
                        dialog.dismiss();
                        return;
                    }
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
