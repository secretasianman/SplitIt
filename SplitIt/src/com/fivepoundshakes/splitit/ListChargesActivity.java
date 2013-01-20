package com.fivepoundshakes.splitit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
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
    
    private List<ListEntry>  aggregated;
    private ListView         lv;
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
        
        aggregated = new LinkedList<ListEntry>();
        adapter = new ListEntryAdapter(this, aggregated, vi);
        setListAdapter(adapter);
        
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
    
    /**
     * Initializes fields to their respective Views.
     */
    private void initViews() {
        name             = (TextView) findViewById(R.id.name);
        newButton        = (Button)   findViewById(R.id.newButton);
        paymentsButton   = (Button)   findViewById(R.id.paymentsButton);
        chargesButton    = (Button)   findViewById(R.id.chargesButton);
        lv               = (ListView) findViewById(android.R.id.list);
        
        name.setText(displayname);
        chargesButton.setBackgroundResource(R.drawable.activebutton);
    }
    
    private void refresh() {
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
                        updateList(expenses);
                    }
                });
    }
    
    private void updateList(List<Expense> expenses) {
        //TODO fuck shit fuck!!!!
        HashMap<User, LinkedList<Expense>> individual =
                new HashMap<User, LinkedList<Expense>>();
        for (Expense expense : expenses) {
            for (User user : expense.parties) {
                LinkedList<Expense> list;
                if (individual.containsKey(user)) {
                    list = individual.get(user);
                } else {
                    list = new LinkedList<Expense>();
                    individual.put(user, list);
                }
                list.add(expense);
            }
        }
        
        aggregated.clear();
        for (Map.Entry<User, LinkedList<Expense>> entry : individual.entrySet()) {
            User user = entry.getKey();
            LinkedList<Expense> list = entry.getValue();
            int total = 0;
            for (Expense e : list) {
                total += Math.round(e.amount / ((float) e.parties.size() + 1));
            }
            aggregated.add(new ListEntry(user, total, false));
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
}