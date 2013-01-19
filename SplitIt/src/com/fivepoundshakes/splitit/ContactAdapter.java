package com.fivepoundshakes.splitit;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContactAdapter extends ArrayAdapter<Contact>{
	private final Context context;
	private final ArrayList<Contact> values;
	
	public ContactAdapter(Context context, ArrayList<Contact> values){
		super(context, R.layout.activity_contacts, values);
		this.context = context;
		this.values = values;
	}
	
	 @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.activity_contacts, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.label);
	    textView.setText(values.get(position).getName());
	    // Change the icon for Windows and iPhone
	    String s = values.get(position).getName();
	    	
	    return rowView;
	  }
}