package com.fivepoundshakes.splitit;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DetailEntryAdapter extends ArrayAdapter<DetailEntry> {

    protected Context           context;
    protected List<DetailEntry> items;
    protected LayoutInflater    vi;
    
    public DetailEntryAdapter(Context context, List<DetailEntry> objects,
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
        View v = inflater.inflate(R.layout.details_item, null);
        
        DetailEntry       item    = items.get(position);
        TextView          vendor  = (TextView) v.findViewById(R.id.vendorText);
        TextView          amount  = (TextView) v.findViewById(R.id.amountText);
        
        vendor.setText(item.vendor);
        amount.setText(CurrencyCreator.toDollars(item.amount));
        
        if (item.isPayment) {
            amount.setTextColor(parent.getResources().getColor(R.color.money_red));
        } else {
            amount.setTextColor(parent.getResources().getColor(R.color.money_green));
        }
        
        return v;
    }

}
