package com.ping;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {
	
	public static final String _ID = "_id";
	public static final String MESSAGE = "message";
	public static final String SENDER = "sender";
	public static final String TIME_SENT = "time_sent";
	public static final String TIME_RECEIVED = "time_received";
	public static final String LOOKUP_KEY = "lookup_key";
	public static final String NAME = "name";
	public static final String PHONE = "phone";
	public static final String GMAIL = "gmail";
	public static final String SHOWN = "shown";
	public static final String STARRED = "starred";
	
	private static final String TAG = "DataHelper";
	private static final String DB_NAME = "data";
	private static final String TABLE_INBOX = "inbox";
	private static final String TABLE_MESSAGES = "messages";
	private static final String TABLE_CONTACTS = "contacts";
	
	private static final String DB_CREATE = "create table " + TABLE_INBOX + " (" + _ID + " integer primary key autoincrement, " + MESSAGE + " text, " + 
				SENDER + " text, " + TIME_SENT + " text, " + TIME_RECEIVED + "text); create table " + TABLE_MESSAGES + " (" + _ID + 
				" integer primary key autoincrement, " + MESSAGE + " text); create table " + TABLE_CONTACTS + " (" + _ID + " integer primary key autoincrement, " +
				LOOKUP_KEY + " text, " + NAME + " text, " + PHONE + " text, " + GMAIL + " text, " + SHOWN + " integer, " + STARRED + "integer);";
	
	private SQLiteDatabase base;
	//private final Context context;
	
	public DataHelper(Context c) {
		super(c, DB_NAME, null, 1);
		//context = c;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		Log.w(TAG, "Upgrading database from version " + oldV + " to " + newV + " which will destroy all old data");
		db.execSQL("drop table if exists" + TABLE_INBOX);
		db.execSQL("drop table if exists" + TABLE_MESSAGES);
		db.execSQL("drop table if exists" + TABLE_CONTACTS);
		onCreate(db);
	}
	
	public DataHelper open() throws SQLException {
		base = getWritableDatabase();
		return this;
	}
	
	public long createInbox(String msg, String from, String sent, String received) {
		ContentValues initValues = new ContentValues();
		initValues.put(MESSAGE, msg);
		initValues.put(SENDER, from);
		initValues.put(TIME_SENT, sent);
		initValues.put(TIME_RECEIVED, received);
		return base.insert(TABLE_INBOX, null, initValues);
	}
	
	
	
	

}
