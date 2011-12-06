package com.playcez.bean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBhelper extends SQLiteOpenHelper{

	private static final String CREATE_TABLE_LIST = "CREATE TABLE LIST ( " +
			"I_id INTEGER PRIMARY KEY, " +
			"TYPE TEXT NOT NULL, " +
			"ICON TEXT NOT NULL, " +
			"CAT TEXT NOT NULL);";
	
	private static final String CREATE_TABLE_BUCKETS = "CREATE TABLE BUCKETS ( " +
			"I_id INTEGER, " +
			"J INTEGER, " +
			"ID TEXT NOT NULL, " +
			"NAME TEXT NOT NULL, " +
			"BUCKETICON TEXT NOT NULL);";
	
	public MyDBhelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 
		Log.v("MyDBHelper onCreate", "Creaeting all tables");
		try{
			db.execSQL(CREATE_TABLE_LIST);
			db.execSQL(CREATE_TABLE_BUCKETS);
		} catch(SQLiteException ex){
			Log.v("CREATE TABLE EXCEPTION", ex.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 
		db.execSQL("DROP TABLE IF EXISTS LIST");
		db.execSQL("DROP TABLE IF EXISTS BUCKETS");
		onCreate(db);
	}

}
