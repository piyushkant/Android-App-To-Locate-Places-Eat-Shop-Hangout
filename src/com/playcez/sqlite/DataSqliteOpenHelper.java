/*
 * 
 */
package com.playcez.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class DataSqliteOpenHelper.
 */
public class DataSqliteOpenHelper extends SQLiteOpenHelper {
	
	/** The Constant version. */
	public static final int version = 1;
	
	/** The Constant db_name. */
	public static final String db_name = "streetData";
	
	/** The Constant info_table. */
	public static final String info_table = "nearby_localities";
	
	/** The Constant row_id. */
	public static final int row_id = 0;
	
	/** The Constant city_id. */
	public static final String city_id = "cityId";
	
	/** The Constant cs_id. */
	public static final String cs_id = "csId";
	
	/** The Constant st_label. */
	public static final String st_label = "label";
	
	/** The Constant st_value. */
	public static final String st_value = "value";

	/**
	 * Instantiates a new data sqlite open helper.
	 *
	 * @param context the context
	 */
	public DataSqliteOpenHelper(Context context) {
		super(context, db_name, null, version);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase data) {

		data.execSQL("create table nearby_localities (row_id integer primary key autoincrement not null, cityId text, csId text, label text, value text);");

	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase data, int oldVersion, int newVersion) {

	}

}
