package com.ping;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Contact implements Comparable<Contact>, Serializable {
	private String 		key;
	private String 		name;
	private String 		phone;
	private String 		gmail;
	private boolean 	shown;
	
	public void setKey(String s) { key = s; }
	public void setName(String s) { name = s; }
	public void setPhone(String s) { phone = s; }
	public void setGmail(String s) { gmail = s; }
	public void setShown(boolean b) { shown = b; }
	
	public String getKey() { return key; }
	public String getName() { return name; }
	public String getPhone() { return phone; }
	public String getGmail() { return gmail; }
	public boolean isShown() { return shown; }
	
	public Contact() {
		key = "Unknown";
		name = "Unknown";
		phone = "Unknown";
		gmail = "Unknown";
		shown = true;
	}
	
	public Contact(String k, String n) {
		key = k;
		name = n;
		phone = "Unknown";
		gmail = "Unknown";
		shown = false;
	}
	
	public Contact(String k, String n, String p, String g) {
		key = k;
		name = n;
		phone = p;
		gmail = g;
		shown = true;
	}
	
	public Contact(String k, String n, String p, String g, boolean show) {
		key = k;
		name = n;
		phone = p;
		gmail = g;
		shown = show;
	}
	
	public Contact(String s) {
		String[] data = s.split(",");
		name = data[0];
		phone = data[1];
		gmail = data[2];
	}
	
	public void writeContact(OutputStreamWriter out) throws IOException {
			out.write(name + "," + phone + "," + gmail + "\n");
	}
	
	public String toString() {
		return "Name: " + name + "\nPhone: " + phone + "\nGmail: " + gmail + "\nShown? " + shown;
	}
	@Override
	public int compareTo(Contact c) {
		return name.compareTo(c.getName());
	}
}
