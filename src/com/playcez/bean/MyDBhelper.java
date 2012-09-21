/*
 * 
 */
package com.playcez.bean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class MyDBhelper.
 */
public class MyDBhelper extends SQLiteOpenHelper {

	/** The Constant CREATE_TABLE_LIST. */
	private static final String CREATE_TABLE_LIST = "CREATE TABLE LIST ( "
			+ "I_id INTEGER PRIMARY KEY, " + "TYPE TEXT NOT NULL, "
			+ "ICON TEXT NOT NULL, " + "CAT TEXT NOT NULL);";

	/** The Constant CREATE_TABLE_BUCKETS. */
	private static final String CREATE_TABLE_BUCKETS = "CREATE TABLE BUCKETS ( "
			+ "I_id INTEGER, "
			+ "J INTEGER, "
			+ "ID TEXT NOT NULL, "
			+ "NAME TEXT NOT NULL, " + "BUCKETICON TEXT NOT NULL);";

	/** The Constant CREATE_TABLE_RESULT_ARRAY. */
	private static final String CREATE_TABLE_RESULT_ARRAY = "CREATE TABLE RESULT_ARRAY ( "
			+ "csid TEXT, "
			+ "id TEXT, "
			+ "json_array TEXT, "
			+ "system_time LONG);";

	/**
	 * Instantiates a new my d bhelper.
	 *
	 * @param context the context
	 * @param name the name
	 * @param factory the factory
	 * @param version the version
	 */
	public MyDBhelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//
		Log.v("MyDBHelper onCreate", "Creating all tables");
		try {
			db.execSQL(CREATE_TABLE_LIST);
			db.execSQL(CREATE_TABLE_BUCKETS);
			db.execSQL(CREATE_TABLE_RESULT_ARRAY);
		} catch (SQLiteException ex) {
			Log.v("CREATE TABLE EXCEPTION", ex.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//
		db.execSQL("DROP TABLE IF EXISTS LIST");
		db.execSQL("DROP TABLE IF EXISTS BUCKETS");
		db.execSQL("DROP TABLE IF EXISTS RESULT_ARRAY");
		onCreate(db);
	}

}
