package com.fivepoundshakes.splitit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.stackmob.sdk.api.StackMobOptions;
import com.stackmob.sdk.api.StackMobQuery;
import com.stackmob.sdk.callback.StackMobQueryCallback;
import com.stackmob.sdk.exception.StackMobException;

public class ListPaymentsActivity extends ListActivity {

    private User self;
    private String serial;
    private String displayname;
    
    private List<ListEntry> aggregates;
    private Map<User, List<Expense>> indivs;
    
    private TextView name;
    private Button   newButton;
    private Button   paymentsButton;
    private Button   chargesButton;
    
    private ListEntryAdapter adapter;
    private LayoutInflater vi;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listaggregates);
        vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        Intent i    = getIntent();
        serial      = (String) i.getExtras().get("serial");
        displayname = (String) i.getExtras().get("displayname");
        getUser();
        
        initViews();
        
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
    
    private void initViews() {
        name           = (TextView) findViewById(R.id.name);
        newButton      = (Button)   findViewById(R.id.newButton);
        paymentsButton = (Button)   findViewById(R.id.paymentsButton);
        chargesButton  = (Button)   findViewById(R.id.chargesButton);
        
        name.setText(displayname);
        paymentsButton.setBackgroundResource(R.drawable.activebutton);
        
        aggregates = new LinkedList<ListEntry>();
        adapter = new ListEntryAdapter(getApplicationContext(),
                aggregates, vi);
        setListAdapter(adapter);
    }
    
    private void refresh() {
        Expense.query(Expense.class,
                new StackMobQuery().fieldIsIn("parties", Arrays.asList(serial)),
                StackMobOptions.depthOf(1),
                new StackMobQueryCallback<Expense>() {
            @Override
            public void failure(StackMobException arg0) {
                Toaster.show(getApplicationContext(), "Expense lookup error - fail");
            }
            
            @Override
            public void success(List<Expense> expenses) {
                System.out.println("result size: " + expenses.size());
                indivs = new HashMap<User, List<Expense>>();
                for (Expense expense : expenses) {
                    System.out.println(expense);
                    List<Expense> list;
                    if (indivs.containsKey(expense.owner)) {
                        list = indivs.get(expense.owner);
                    } else {
                        list = new LinkedList<Expense>();
                    }
                    list.add(expense);
                    indivs.put(expense.owner, list);
                }
                System.out.println(indivs.size());

                aggregates = new ArrayList<ListEntry>(indivs.size());
                for (Map.Entry<User, List<Expense>> entry : indivs.entrySet()) {
                    User user = entry.getKey();
                    List<Expense> list = entry.getValue();
                    int total = 0;
                    for (Expense expense : list) {
                        int share = Math.round(((float) expense.amount) / 
                                expense.parties.size() + 1);
                        total += share;
                    }
                    aggregates.add(new ListEntry(user, total, true));
                }
                System.out.println(aggregates.size());
                for (ListEntry entry : aggregates) {
                    System.out.println(entry);
                }
                
                adapter = new ListEntryAdapter(getParent(),
                        aggregates, vi);
                setListAdapter(adapter);
            }
        });
    }
    
}
