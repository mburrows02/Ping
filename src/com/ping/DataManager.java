package com.ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;

public class DataManager {
	private boolean filtersContacts;
	private ArrayList<String> inbox, messages;
	private ArrayList<Contact> contacts;
	
	public void setFilter(boolean b) { filtersContacts = b; }
	public void setMessages(ArrayList<String> newMessages) { messages = newMessages; }
	
	public boolean hasFilter() { return filtersContacts; }
	public ArrayList<String> getInbox() { return inbox; }
	public String getInbox(int i) { return inbox.get(i); }
	public ArrayList<String> getMessages() { return messages; }
	public String getMessages(int i) { return messages.get(i); }
	public ArrayList<Contact> getContacts() { return contacts; }
	public Contact getContacts(int i) { return contacts.get(i); }
	public Bundle getBundle() {
		Bundle data = new Bundle();
		data.putBoolean("filtersContacts", filtersContacts);
		data.putStringArrayList("messages", messages);
		return data;
	}
	
	public DataManager() {
		filtersContacts = true;
		inbox = new ArrayList<String>();
		messages = new ArrayList<String>();
		contacts = new ArrayList<Contact>();
	}
	
	public void addInbox(String s) { inbox.add(s); }
	
	public void removeInbox(int i) { inbox.remove(i); }
	
	public void clearInbox() { inbox = new ArrayList<String>(); }
	
	public void addContact(String n, String p, String g) { contacts.add(new Contact(n, p, g)); }
	
	public void loadContacts(ContentResolver cr) {
		contacts = new ArrayList<Contact>();
		ArrayList<String> gmails = new ArrayList<String>();
		ArrayList<String> phones = new ArrayList<String>();
		
		Cursor cur = cr.query(Email.CONTENT_URI, new String[]{Email.LOOKUP_KEY, Email.DATA}, null, null, Email.LOOKUP_KEY + " ASC");
		if(cur.moveToFirst()) {
			do {
				if(cur.getString(1).contains("@gmail.com")) {
					gmails.add(cur.getString(0));
					gmails.add(cur.getString(1));
				}
			} while(cur.moveToNext());
		}
		cur = cr.query(Phone.CONTENT_URI, new String[]{Phone.LOOKUP_KEY, Phone.NUMBER, Phone.TYPE}, null, null, Phone.LOOKUP_KEY + " ASC");
		if(cur.moveToFirst()) {
			do {
				if(cur.getInt(2) == Phone.TYPE_MOBILE) {
					phones.add(cur.getString(0));
					phones.add(cur.getString(1));
				}
			} while(cur.moveToNext());
		}
		cur = cr.query(Contacts.CONTENT_URI, new String[]{Contacts.LOOKUP_KEY, Contacts.DISPLAY_NAME}, null, null, Contacts.DISPLAY_NAME + " ASC");
		String g, p;
		if(cur.moveToFirst()) {
			do {
				g = null;
				p = null;
				if(gmails.contains(cur.getString(0)))
					g = gmails.get(gmails.indexOf(cur.getString(0)) + 1);
				if(phones.contains(cur.getString(0)))
					p = phones.get(phones.indexOf(cur.getString(0)) + 1);
				if(g != null || p != null) {
					contacts.add(new Contact(cur.getString(1), p, g));
				}
			} while(cur.moveToNext());
		}
	}
	
	public void writeData(OutputStreamWriter out) throws IOException {
		out.write("Contacts:\n");
		for(Contact c : contacts)
			c.writeContact(out);
		out.write("Inbox:\n");
		for(String s : inbox)
  			out.write(s + "\n");
  		out.write("Messages:\n");
  		for(String m : messages)
  			out.write(m + "\n");
  		out.write("Filter:\n" + filtersContacts + "\n");
	}
	
	public void readData(BufferedReader in) throws IOException {
		inbox = new ArrayList<String>();
		messages = new ArrayList<String>();
		contacts = new ArrayList<Contact>();
		String line = "Error loading data";
		line = in.readLine();
		line = in.readLine();
		while(!line.equals("Inbox:")) {
			contacts.add(new Contact(line));
			line = in.readLine();
		}
		line = in.readLine();
		while(!line.equals("Messages:")) {
			inbox.add(line);
    		line = in.readLine();
		}
		line = in.readLine();
		while(!line.equals("Filter:")) {
			messages.add(line);
    		line = in.readLine();
		}
		line = in.readLine();
		filtersContacts = line.equals("true");
	}
}
