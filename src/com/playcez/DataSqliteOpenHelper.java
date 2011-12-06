package com.playcez;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataSqliteOpenHelper extends SQLiteOpenHelper 
{
	public static final int version = 1;
	public static final String db_name = "data";
	public static final String info_table = "info";
	public static final String place_id = "id";
	public static final String place_name = "placeName";
	public static final String place_address = "placeAddress";
	public static final String place_distance = "placeDistance";
	public static final String place_resource = "placeResource";
	public static final String title = "title";
	public static final String value = "value";

	public DataSqliteOpenHelper(Context context)
	{
		super(context, db_name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase data)
	{
		data.execSQL("create table info (id text, placeName text, placeAddress text, placeDistance text);");	
		data.execSQL("insert into info values ('initial', 'initial', 'initial', 'initial');");
	}

	@Override
	public void onUpgrade(SQLiteDatabase data, int oldVersion, int newVersion)
	{
	}

}
