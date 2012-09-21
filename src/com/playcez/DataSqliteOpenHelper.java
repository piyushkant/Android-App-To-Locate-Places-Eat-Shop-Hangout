/*
 * 
 */
package com.playcez;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class DataSqliteOpenHelper.
 */
public class DataSqliteOpenHelper extends SQLiteOpenHelper 
{
	
	/** The Constant version. */
	public static final int version = 1;
	
	/** The Constant db_name. */
	public static final String db_name = "data";
	
	/** The Constant info_table. */
	public static final String info_table = "info";
	
	/** The Constant place_id. */
	public static final String place_id = "id";
	
	/** The Constant place_name. */
	public static final String place_name = "placeName";
	
	/** The Constant place_address. */
	public static final String place_address = "placeAddress";
	
	/** The Constant place_distance. */
	public static final String place_distance = "placeDistance";
	
	/** The Constant place_resource. */
	public static final String place_resource = "placeResource";
	
	/** The Constant title. */
	public static final String title = "title";
	
	/** The Constant value. */
	public static final String value = "value";

	/**
	 * Instantiates a new data sqlite open helper.
	 *
	 * @param context the context
	 */
	public DataSqliteOpenHelper(Context context)
	{
		super(context, db_name, null, version);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase data)
	{
		data.execSQL("create table info (id text, placeName text, placeAddress text, placeDistance text);");	
		data.execSQL("insert into info values ('initial', 'initial', 'initial', 'initial');");
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase data, int oldVersion, int newVersion)
	{
	}

}
