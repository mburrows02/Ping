package com.whatup;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class Settings extends Activity {
	private ArrayList<String> messages;
	private Spinner spinner;
	private EditText textBox;
	private boolean filtersContacts;
	private RadioButton filterButton;
	private RadioButton smsButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Bundle data = getIntent().getExtras();
		messages = data.getStringArrayList("messages");
		filtersContacts = data.getBoolean("filtersContacts");
		spinner = (Spinner) findViewById(R.id.settings_spinner);
		textBox = (EditText) findViewById(R.id.new_field);
		filterButton = (RadioButton) findViewById(R.id.radio_filter);
		smsButton = (RadioButton) findViewById(R.id.radio_sms);
		if(filtersContacts) filterButton.setChecked(true);
		else smsButton.setChecked(true);

    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, messages);
    	spinner.setAdapter(adapter);
	}
	
	public void deletePreset(View view) {
		if(!(spinner.getSelectedItem() == null)) messages.remove(spinner.getSelectedItemPosition());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, messages);
    	spinner.setAdapter(adapter);
	}
	
	public void addPreset(View view) {
		messages.add(textBox.getText().toString());
		textBox.setText("");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, messages);
    	spinner.setAdapter(adapter);
	}
	
	@Override
	public void finish() {
		if(filterButton.isChecked()) filtersContacts = true;
		else filtersContacts = false;
		Intent data = new Intent(this, WhatUp.class);
		Bundle contents = new Bundle();
		contents.putBoolean("filtersContacts", filtersContacts);
		contents.putStringArrayList("messages", messages);
		data.putExtras(contents);
		this.setResult(RESULT_OK, data);
		super.finish();
	}
}



