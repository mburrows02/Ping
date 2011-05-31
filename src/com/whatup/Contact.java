package com.whatup;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class Contact {
	private String key;
	private String name;
	private String phone;
	private String gmail;
	
	public void setKey(String s) { key = s; }
	public void setName(String s) { name = s; }
	public void setPhone(String s) { phone = s; }
	public void setGmail(String s) { gmail = s; }
	
	public String getName() { return name; }
	public String getPhone() { return phone; }
	public String getGmail() { return gmail; }
	
	public Contact() {
		key = "";
		name = "Unknown";
		phone = "Unknown";
		gmail = "Unknown";
	}
	
	public Contact(String k, String n, String p, String g) {
		key = k;
		name = n;
		phone = p;
		gmail = g;
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
		return name + "," + phone + "," + gmail;
	}
}
