package com.fivepoundshakes.splitit;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.ListActivity;
import android.database.Cursor;
import android.view.Menu;

public class ContactsActivity extends ListActivity {
	private TargetContact targetContacts = null;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContactAdapter adapter = new ContactAdapter(this,getContacts());
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_contacts, menu);
		return true;
	}
	
	//Get contacts
	public ArrayList<Contact> getContacts(){
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		String[] proj = {Phone.DISPLAY_NAME,Phone.NUMBER,Phone.TYPE};
		Cursor cursor = getContentResolver().query(Phone.CONTENT_URI, proj, null, null, Phone.DISPLAY_NAME);
		
		while(cursor.moveToNext()){
			//Mobile Phone
			if(cursor.getColumnIndex(Phone.NUMBER) != -1){
				if(cursor.getInt(cursor.getColumnIndex(Phone.TYPE)) == 2){
					String name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
					String phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
					
					
					if(Pattern.matches("[0-9\\-\\(\\) \\+]+", phoneNumber)){	
						if(contacts.size()>0 && contacts.get(contacts.size()-1).getName().equals(name) && !contacts.get(contacts.size()-1).contains(phoneNumber)){
							contacts.get(contacts.size()-1).addContactPoint(Contact.clean(phoneNumber), "Mobile");
						}
						else if(contacts.size()==0 || !contacts.get(contacts.size()-1).getName().equals(name)){
							contacts.add(new Contact(name,Contact.clean(phoneNumber),"Mobile"));
						}
					}
				}
			}
		}
		cursor.close();
		return contacts;
	}


	class TargetContact{
		String name,val;
		
		private TargetContact(String name, String val){
			this.name = name;
			this.val = val;
		}
		
		public String getName(){
			return this.name;
		}
		
		public String getVal(){
			return this.val;
		}
	}
}
