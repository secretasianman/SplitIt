package com.fivepoundshakes.splitit;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class ListEntryAdapter extends ArrayAdapter<ListEntry> {

    protected Context         context;
    protected List<ListEntry> items;
    protected LayoutInflater  vi;
    
    public ListEntryAdapter(Context context, List<ListEntry> objects,
            LayoutInflater vi) {
        super(context, 0, objects);
        this.context = context;
        this.items   = objects;
        this.vi      = vi;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View v;
        ListEntry item = items.get(position);
        if (!item.pending) {
            v = inflater.inflate(R.layout.unpaid_split, null);
        } else {
            v = inflater.inflate(R.layout.pending_split, null);
        }
        
        TextView          name    = (TextView) v.findViewById(R.id.name);
        TextView          amount  = (TextView) v.findViewById(R.id.amount);
        QuickContactBadge picture = (QuickContactBadge) v.findViewById(R.id.picture);
        
        name.setText(item.user.first_name + " " + item.user.last_name);
        amount.setText(CurrencyCreator.toDollars(item.amount));
        //TODO set picture
        
        if (!item.pending) {
            if (item.isPayment) {
                amount.setTextColor(parent.getResources().getColor(R.color.money_red));
            } else {
                amount.setTextColor(parent.getResources().getColor(R.color.money_green));
            }
        }
        
        return v;
    }

}
