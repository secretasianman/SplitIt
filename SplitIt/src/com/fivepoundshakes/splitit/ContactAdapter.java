package com.fivepoundshakes.splitit;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ContactAdapter extends ArrayAdapter<Contact>{
	private final Context context;
	private final ArrayList<Contact> values;
	private HashMap<Integer,Boolean> selectedContacts = new HashMap<Integer,Boolean>();

	public ContactAdapter(Context context, ArrayList<Contact> values){
		super(context, R.layout.activity_contacts, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if(rowView==null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.contact, parent, false);
		}

		TextView textView = (TextView) rowView.findViewById(R.id.listEl);
		textView.setText(values.get(position).getName());

		Contact contact = values.get(position);
		if (contact != null) {
			CheckBox nameCheckBox = (CheckBox) rowView.findViewById(R.id.checkBox);
			if(selectedContacts.containsKey(position)){
				nameCheckBox.setChecked(selectedContacts.get(position));
			}
			else{
				nameCheckBox.setChecked(false);
			}
			nameCheckBox.setOnClickListener(new OnItemClickListener(position));
		}
		return rowView;
	}
	
	public ArrayList<String> getResult(){
		ArrayList<String> res = new ArrayList<String>();
		
		for(Integer curr : selectedContacts.keySet()){
			if(selectedContacts.get(curr)){
				res.add(values.get(curr).getName());
				res.add(values.get(curr).getContactPoints().get(0).getVal());
			}
		}
		
		return res;
	}

	private class OnItemClickListener implements OnClickListener{           
		private int position;
		OnItemClickListener(int position){
			this.position = position;
		}
		@Override
		public void onClick(View arg0) {
			if(selectedContacts.containsKey(position) && selectedContacts.get(position)){
				selectedContacts.put(position,false);
			}
			else{
				selectedContacts.put(position,true);
			}
		}               
	}
}


