package com.ping;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class Ping extends Activity implements OnClickListener {
	private DataManager			data;
	private LinearLayout 		mList, cList;
	private TabHost 			host;
	private DataHelper			helper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        helper = new DataHelper(this);
        helper.open();
        System.out.println("*****");
        data = new DataManager();
        Resources res = getResources();
        host = new TabHost(this);
        host = (TabHost) findViewById(R.id.tabhost);
        host.setup();
        TabHost.TabSpec spec;
        
        spec = host.newTabSpec("send");
        spec.setContent(R.id.contactlist);
        spec.setIndicator("Send", res.getDrawable(R.drawable.ic_tab_out));
        host.addTab(spec);
        spec = host.newTabSpec("messages");
        spec.setContent(R.id.messagelist);
        spec.setIndicator("Messages", res.getDrawable(R.drawable.ic_tab_in));
        host.addTab(spec);
    	data.loadContacts(getContentResolver(), helper);
        
		mList = (LinearLayout) findViewById(R.id.messageList);
		cList = (LinearLayout) findViewById(R.id.contactList);
	    Integer tab = (Integer) getLastNonConfigurationInstance();
	    if(tab != null) host.setCurrentTab(tab);
	    else host.setCurrentTab(0);
	    
        int code = getIntent().getIntExtra("code", 0);
        if(code != 0 && code == R.string.receiver) {
        	String[] senders = getIntent().getStringArrayExtra("senders");
        	String[] bodies = getIntent().getStringArrayExtra("bodies");
        	long[] times = getIntent().getLongArrayExtra("times");
        	for(int i = 0; i < senders.length; i ++) {
        		helper.createInbox(bodies[i], senders[i], times[i]);
        		System.out.println("*****Received message: " + bodies[i] + " at " + times[i]);
        	}
        }
    }
        
    @Override
    public void onResume() {
    	super.onResume();
    	try {
    		data.readData(helper);
    	} catch (Exception e) {
    		System.out.println("*****Error reading data");
    		e.printStackTrace();
    	}
    	loadMessages();
    	loadContacts();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	try {
    		  data.writeData(helper);
    		} catch(Exception e) {
    			System.out.println("*****Error writing data");
    			e.printStackTrace();
    		}
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	return host.getCurrentTab();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case R.id.settings:
        	Intent intent = new Intent(this, Settings.class);
        	Bundle b = data.getBundle();
        	intent.putExtras(b);
        	startActivityForResult(intent, 0);
            return true;
        case R.id.clear_messages:
        	mList.removeAllViews();
        	data.clearInbox();
            return true;
        case R.id.reload_contacts:
            loadContacts();
            return true;
        case R.id.help:
        	Toast.makeText(getApplicationContext(), "No!", Toast.LENGTH_SHORT).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void loadContacts() {
    	data.loadContacts(getContentResolver(), helper);
    	cList.removeAllViews();
    	for(int i = 0; i < data.getContacts().size(); i ++) {
			cList.addView(new CheckBox(this));
			((CheckBox)cList.getChildAt(i)).setText(data.getContacts(i).toString());
			((CheckBox)cList.getChildAt(i)).setId(100 + i);
    	}    	
    }
    
    public void loadMessages() {
    	mList.removeAllViews();
    	for (int i = 0; i < data.getInbox().size(); i ++) {
    		mList.addView(new TextView(this));
    		mList.getChildAt(i).setOnClickListener(this);
    		((TextView)mList.getChildAt(i)).setTextSize(TypedValue.COMPLEX_UNIT_PT, 8);
    		((TextView)mList.getChildAt(i)).setText(data.getInbox(i).toString());
    		((TextView)mList.getChildAt(i)).setId(1000 + i);
    	}
    }
    
    public void selectMessage(View view) {
    	CharSequence[] list = new CharSequence[data.getMessages().size()];
    	for(int i = 0; i < data.getMessages().size(); i ++) list[i] = data.getMessages(i);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Select a message:");
    	builder.setCancelable(true);
    	builder.setItems(list, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int item)
    		 {
    			sendMessage(data.getMessages(item));
    		}
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    public void sendMessage(String message) {
    	ArrayList<Contact> recipients = new ArrayList<Contact>();
    	for(int i = 0; i < cList.getChildCount(); i ++) {
    		if (((CheckBox)cList.getChildAt(i)).isChecked()) {
    			recipients.add(data.getContacts(i));
    			((CheckBox)cList.getChildAt(i)).setChecked(false);
    		}
    	}
    	Sender.sendSMS(getApplicationContext(), recipients, message);
    }
    
    @Override
    public void onClick(View view) { //Listener for inbox list
    	for(int i = 0; i < mList.getChildCount(); i ++) {
    		if (mList.getChildAt(i).equals(view)) {
    			mList.removeView(view);
    			data.removeInbox(i);
    			i--;
    			break;
    		}
    	}
    }
    
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent intent) {
    	Bundle b = intent.getExtras();
    	data.setFilter(b.getBoolean("filtersContacts"));
    	data.setMessages(b.getStringArrayList("messages"));
    	try {
  		  data.writeData(helper);
  		} catch(Exception e) {
  			System.out.println("*****Error writing data");
  			e.printStackTrace();
  		}
    }
    
}