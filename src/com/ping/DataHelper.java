package com.ping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {
	
	public static final String _ID = "_id";
	public static final String MESSAGE = "message";
	public static final String SENDER = "sender";
	public static final String TIME_SENT = "time_sent";
	public static final String LOOKUP_KEY = "lookup_key";
	public static final String NAME = "name";
	public static final String PHONE = "phone";
	public static final String GMAIL = "gmail";
	public static final String SHOWN = "shown";
	
	private static final String TAG = "DataHelper";
	private static final int DATABASE_VERSION = 2;
	private static final String DB_NAME = "data";
	private static final String TABLE_INBOX = "inbox";
	private static final String TABLE_MESSAGES = "messages";
	private static final String TABLE_CONTACTS = "contacts";
	private static final String TABLE_PREFS = "preferences";
	
	private static final String CREATE_INBOX = "create table " + TABLE_INBOX + " (" + _ID + " integer primary key autoincrement, " + MESSAGE + " text, " + 
				SENDER + " text, " + TIME_SENT + " integer)";
	private static final String CREATE_MESSAGES = "create table " + TABLE_MESSAGES + " (" + _ID + " integer primary key autoincrement, " + 
				MESSAGE + " text)";
	private static final String CREATE_CONTACTS = "create table " + TABLE_CONTACTS + " (" + _ID + " integer primary key autoincrement, " +
				LOOKUP_KEY + " text, " + NAME + " text, " + PHONE + " text, " + GMAIL + " text, " + SHOWN + " integer)";
	private static final String CREATE_PREFS = "create table " + TABLE_PREFS + " (" + _ID + " integer primary key autoincrement, " + LOOKUP_KEY + " text, " + SHOWN + " integer);";
	
	private SQLiteDatabase base;
	
	public DataHelper(Context c) {
		super(c, DB_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_INBOX);
		db.execSQL(CREATE_MESSAGES);
		db.execSQL(CREATE_CONTACTS);
		db.execSQL(CREATE_PREFS);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		Log.w(TAG, "Upgrading database from version " + oldV + " to " + newV + " which will destroy all old data");
		db.execSQL("drop table if exists " + TABLE_INBOX);
		db.execSQL("drop table if exists " + TABLE_MESSAGES);
		db.execSQL("drop table if exists " + TABLE_CONTACTS);
		db.execSQL("drop table if exists " + TABLE_PREFS);
		onCreate(db);
	}
	
	public DataHelper open() throws SQLException {
		base = getWritableDatabase();
		return this;
	}
	
	public long createInbox(String msg, String from, long time) {
		ContentValues initValues = new ContentValues();
		initValues.put(MESSAGE, msg);
		initValues.put(SENDER, from);
		initValues.put(TIME_SENT, time);
		return base.insert(TABLE_INBOX, null, initValues);
	}
	
	public long createMessage(String msg) {
		ContentValues initValues = new ContentValues();
		initValues.put(MESSAGE, msg);
		return base.insert(TABLE_MESSAGES, null, initValues);
	}
	
	public long createContact(String key, String name, String phone, String gmail, boolean shown) {
		ContentValues initValues = new ContentValues();
		initValues.put(LOOKUP_KEY, key);
		initValues.put(NAME, name);
		initValues.put(PHONE, phone);
		initValues.put(GMAIL, gmail);
		initValues.put(SHOWN, shown);
		return base.insert(TABLE_CONTACTS, null, initValues);
	}
	
	public long createPref(String key, boolean shown) {
		ContentValues initValues = new ContentValues();
		initValues.put(LOOKUP_KEY, key);
		initValues.put(SHOWN, shown);
		return base.insert(TABLE_PREFS, null, initValues);
	}
	
	public boolean deleteInbox(long rowId) {
		return base.delete(TABLE_INBOX, _ID + "=" + rowId, null) > 0;
	}
	
	public boolean deleteMessage(long rowId) {
		return base.delete(TABLE_MESSAGES, _ID + "=" + rowId, null) > 0;
	}
	
	public boolean deleteContact(long rowId) {
		return base.delete(TABLE_CONTACTS, _ID + "=" + rowId, null) > 0;
	}
	
	public boolean deletePref(long rowId) {
		return base.delete(TABLE_PREFS, _ID + "=" + rowId, null) > 0;
	}
	public void clearInbox() {
		base.delete(TABLE_INBOX, null, null);
	}
	
	public void clearMessages() {
		base.delete(TABLE_MESSAGES, null, null);
	}
	
	public void clearContactList() {
		base.delete(TABLE_CONTACTS, null, null);
	}
	
	public void clearPrefs() {
		base.delete(TABLE_PREFS, null, null);
	}
	
	public Cursor fetchInbox() {
		return base.query(TABLE_INBOX, new String[]{_ID, SENDER, MESSAGE, TIME_SENT}, null, null, null, null, null);
	}
	
	public Cursor fetchMessages() {
		return base.query(TABLE_MESSAGES, new String[]{_ID, MESSAGE}, null, null, null, null, null);
	}
	
	public Cursor fetchContacts() {
		return base.query(TABLE_CONTACTS, new String[]{_ID, LOOKUP_KEY, NAME, PHONE, GMAIL, SHOWN}, null, null, null, null, null);
	}
	
	public Cursor fetchPrefs() {
		return base.query(TABLE_PREFS, new String[]{_ID, LOOKUP_KEY, SHOWN}, null, null, null, null, null);
	}
	
	public Cursor fetchPref(String key) {
		return base.query(TABLE_PREFS, new String[]{LOOKUP_KEY, SHOWN}, LOOKUP_KEY + "=\'" + key + "\'", null, null, null, null);
	}
	
	public boolean contactExists(String key) {
		return base.query(TABLE_PREFS, new String[]{LOOKUP_KEY}, LOOKUP_KEY + "=\'" + key + "\'", null, null, null, null).moveToFirst();
	}
	
	public boolean updateInbox(long rowId, String message, String sender, String timeSent) {
		ContentValues args = new ContentValues();
		args.put(MESSAGE, message);
		args.put(SENDER, sender);
		args.put(TIME_SENT, timeSent);
		return base.update(TABLE_INBOX, args, _ID + "=" + rowId, null) > 0;
	}
	
	public boolean updateMessage(long rowId, String message) {
		ContentValues args = new ContentValues();
		args.put(MESSAGE, message);
		return base.update(TABLE_MESSAGES, args, _ID + "=" + rowId, null) > 0;
	}
	
	public boolean updateContactPhone(String key, String phone) {
		ContentValues args = new ContentValues();
		args.put(PHONE, phone);
		return base.update(TABLE_CONTACTS, args, LOOKUP_KEY + "=\'" + key + "\'", null) > 0;
	}

	public boolean updateContactGmail(String key, String gmail) {
		ContentValues args = new ContentValues();
		args.put(GMAIL, gmail);
		return base.update(TABLE_CONTACTS, args, LOOKUP_KEY + "=\'" + key + "\'", null) > 0;
	}
	
	public boolean updateContactConfig(String key, boolean show) {
		ContentValues args = new ContentValues();
		args.put(SHOWN, show);
		return base.update(TABLE_CONTACTS, args, LOOKUP_KEY + "=\'" + key + "\'", null) > 0;
	}
	
	public boolean updatePref(String key, boolean show) {
		ContentValues args = new ContentValues();
		args.put(SHOWN, show);
		return base.update(TABLE_PREFS, args, LOOKUP_KEY + "=\'" + key + "\'", null) > 0;
	}
}
