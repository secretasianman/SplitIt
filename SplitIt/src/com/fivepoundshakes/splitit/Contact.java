package com.fivepoundshakes.splitit;

import java.util.ArrayList;

import android.util.Log;

public class Contact {
	String name;
	ArrayList<ContactPoint> contactPoints;
	
	public Contact(String name){
		this.name = name;
		this.contactPoints = new ArrayList<ContactPoint>();
	}
	
	public Contact(String name, String value, String type){
		this.name = name;
		this.contactPoints = new ArrayList<ContactPoint>();
		addContactPoint(value,type);
	}
	
	public void addContactPoint(String value, String type){
		contactPoints.add(new ContactPoint(value,type));
	}
	
	public String getName(){
		return name;
	}
	
	public ArrayList<ContactPoint> getContactPoints(){
		return contactPoints;
	}
	
	public boolean contains(String comp){
		for(ContactPoint curr : contactPoints){
			Log.e("..",clean(curr.getVal())+ " : " + clean(comp));
			System.out.println(clean(curr.getVal()).equals(clean(comp)));
			if(clean(curr.getVal()).equals(clean(comp))){
				return true;
			}
		}
		return false;
	}
	
	public static String clean(String in){
		return in.replaceAll("[\\- \\(\\)\\+]*","");
	}
	
	public class ContactPoint{
		private String value;
		private String type;
		
		private ContactPoint(String value, String type){
			this.value = value;
			this.type = type;
		}
		
		public String getVal(){
			return value;
		}
		
		public String getType(){
			return type;
		}
	}
}
