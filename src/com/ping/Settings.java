package com.ping;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Settings extends Activity implements OnClickListener {
	private ArrayList<String> messages;
	private EditText textBox;
	private LinearLayout messageList;
	
	//private boolean filtersContacts;
	//private RadioButton filterButton;
	//private RadioButton smsButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Bundle data = getIntent().getExtras();
		messages = data.getStringArrayList("messages");
		//filtersContacts = data.getBoolean("filtersContacts");
		
		textBox = (EditText) findViewById(R.id.new_field);
		messageList = (LinearLayout) findViewById(R.id.delete_list);
		//filterButton = (RadioButton) findViewById(R.id.radio_filter);
		//smsButton = (RadioButton) findViewById(R.id.radio_sms);
		//if(filtersContacts) filterButton.setChecked(true);
		//else smsButton.setChecked(true);


		for(int i = 0; i < messages.size(); i ++) {
			newPreset(i);
		}
	}
	
	public void newPreset(int i) {
		LinearLayout l;
		TextView t;
		Button b;
		l = new LinearLayout(this);
		l.setOrientation(LinearLayout.HORIZONTAL);
		l.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		b = new Button(this);
		b.setTypeface(null, Typeface.BOLD);
		b.setText("   X   ");
		b.setTextSize(TypedValue.COMPLEX_UNIT_PT, 8);
		b.setTextColor(getResources().getColor(R.color.red));
		b.setBackgroundColor(getResources().getColor(R.color.black));
		b.setOnClickListener(this);
		l.addView(b, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		t = new TextView(this);
		t.setText(messages.get(i));
		t.setTextSize(TypedValue.COMPLEX_UNIT_PT, 7);
		l.addView(t, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		messageList.addView(l, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	}
	
	public void onClick(View view) {
		for(int i = 0; i < messages.size(); i ++) {
			if(view == ((LinearLayout)messageList.getChildAt(i)).getChildAt(0)) {
				messageList.removeViewAt(i);
				messages.remove(i);
				break;
			}
		}
	}
	
	public void addPreset(View view) {
		messages.add(textBox.getText().toString());
		textBox.setText("");
		newPreset(messages.size() - 1);
	}
	
	@Override
	public void finish() {
		//if(filterButton.isChecked()) filtersContacts = true;
		//else filtersContacts = false;
		Intent data = new Intent(this, Ping.class);
		Bundle contents = new Bundle();
		//contents.putBoolean("filtersContacts", filtersContacts);
		contents.putStringArrayList("messages", messages);
		data.putExtras(contents);
		this.setResult(RESULT_OK, data);
		super.finish();
	}
}



