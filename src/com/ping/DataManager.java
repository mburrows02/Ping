package com.ping;

import java.util.ArrayList;
import java.util.Collections;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;

public class DataManager {
	private ArrayList<String> messages;
	private ArrayList<Message> inbox;
	private ArrayList<Contact> contacts;
	
	public void setMessages(ArrayList<String> newMessages) { messages = newMessages; }
	public void setContacts(ArrayList<Contact> newContacts) { 
		contacts = new ArrayList<Contact>();
		for(Contact c : newContacts)
			contacts.add(c); 
	}
	
	public ArrayList<Message> getInbox() { return inbox; }
	public Message getInbox(int i) { return inbox.get(i); }
	public ArrayList<String> getMessages() { return messages; }
	public String getMessages(int i) { return messages.get(i); }
	public ArrayList<Contact> getContacts() { return contacts; }
	public Contact getContacts(int i) { return contacts.get(i); }
	
	public Contact getVisibleContact(int x) {
		int y = -1;
		for(int i = 0; i < contacts.size(); i ++) {
			if(contacts.get(i).isShown()) y++;
			if(y == x) return contacts.get(i);
		}
		return null;
	}
	
	public DataManager() {
		inbox = new ArrayList<Message>();
		messages = new ArrayList<String>();
		contacts = new ArrayList<Contact>();
	}
	
	public void addInbox(Message s) { inbox.add(s); }
	
	public void removeInbox(int i) { inbox.remove(i); }
	
	public void clearInbox() { inbox = new ArrayList<Message>(); }
	
	public String findSender(String s) {
		System.out.println("*****looking for sender...");
		for(Contact c : contacts) {
			System.out.println("*****Checking if " + s + " contains " + c.getPhone());
			if(s.contains(c.getPhone())) {
				System.out.println("*****Found sender - name = " + c.getName());
				return c.getName();
			}
		}
		return s;
	}
	
	public void loadContacts(ContentResolver cr, DataHelper helper) {
		helper.clearContactList();
		Cursor cur = cr.query(Contacts.CONTENT_URI, new String[]{Contacts.LOOKUP_KEY, Contacts.DISPLAY_NAME}, 
				null, null, Contacts.LOOKUP_KEY + " ASC");
		if(cur.moveToFirst()) {
			do {
				helper.createContact(cur.getString(0), cur.getString(1), "Unknown", "Unknown", true);
				Cursor c = helper.fetchPref(cur.getString(0));
				if(c.moveToFirst()) 
					helper.updateContactConfig(cur.getString(0), c.getInt(c.getColumnIndex(DataHelper.SHOWN)) > 0);
				else
					helper.createPref(cur.getString(0), true);
			} while(cur.moveToNext());
		}
		cur = cr.query(Phone.CONTENT_URI, new String[]{Phone.LOOKUP_KEY, Phone.NUMBER, Phone.TYPE}, 
				Phone.TYPE + "=" + Phone.TYPE_MOBILE, null, Phone.LOOKUP_KEY + " ASC");
		if(cur.moveToFirst()) {
			do {
				helper.updateContactPhone(cur.getString(0), cur.getString(1));
			} while(cur.moveToNext());
		}
		cur = cr.query(Email.CONTENT_URI, new String[]{Email.LOOKUP_KEY, Email.DATA}, null, null, Email.LOOKUP_KEY + " ASC");
		if(cur.moveToFirst()) {
			do {
				if(cur.getString(1).contains("@gmail.com")) helper.updateContactGmail(cur.getString(0), cur.getString(1));
			} while(cur.moveToNext());
		}
	}
	
	public void writeData(DataHelper helper) {
		helper.clearInbox();
		for(Message m : inbox) {
			helper.createInbox(m.getBody(), m.getSender(), m.getTimeSent().getTimeInMillis());
		}
		helper.clearMessages();
		for(String s : messages) {
			helper.createMessage(s);
		}
		for(Contact c : contacts) {
			helper.updateContactConfig(c.getKey(), c.isShown());
			helper.updatePref(c.getKey(), c.isShown());
		}
	}
	
	public void readData(DataHelper helper) {
		inbox = new ArrayList<Message>();
		messages = new ArrayList<String>();
		contacts = new ArrayList<Contact>();
		Cursor cur = helper.fetchInbox();
		if(cur.moveToFirst()) {
			do {
				inbox.add(new Message(cur.getString(cur.getColumnIndex(DataHelper.SENDER)), cur.getString(cur.getColumnIndex(DataHelper.MESSAGE)), 
						cur.getLong(cur.getColumnIndex(DataHelper.TIME_SENT))));
			} while(cur.moveToNext());
		}
		cur = helper.fetchMessages();
		if(cur.moveToFirst()) {
			do {
				messages.add(cur.getString(cur.getColumnIndex(DataHelper.MESSAGE)));
			} while(cur.moveToNext());
		}
		cur = helper.fetchContacts();
		if(cur.moveToFirst()) {
			do {
				if(!cur.getString(cur.getColumnIndex(DataHelper.PHONE)).equals("Unknown") || 
						!cur.getString(cur.getColumnIndex(DataHelper.GMAIL)).equals("Unknown")) {
					contacts.add(new Contact(cur.getString(cur.getColumnIndex(DataHelper.LOOKUP_KEY)), cur.getString(cur.getColumnIndex(DataHelper.NAME)),
							cur.getString(cur.getColumnIndex(DataHelper.PHONE)), cur.getString(cur.getColumnIndex(DataHelper.GMAIL)),
							cur.getInt(cur.getColumnIndex(DataHelper.SHOWN)) > 0));
				}
			} while(cur.moveToNext());
		}
		Collections.sort(inbox);
		Collections.sort(contacts);
	}
}
