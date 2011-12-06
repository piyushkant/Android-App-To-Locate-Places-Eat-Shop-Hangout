package com.playcez.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class StorageManager {

	private SQLiteDatabase db;
	private Context context;
	private MyDBhelper dbhelper;
	
	public StorageManager(Context c){
		context = c;
		dbhelper = new MyDBhelper(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
	}
	
	public void close(){
		db.close();
	}
	
	public void open() throws SQLiteException{
		try{
			db = dbhelper.getWritableDatabase();
		} catch(SQLiteException ex){
			Log.v("Open database exception caught", ex.getMessage());
			db = dbhelper.getReadableDatabase();
		}
	}
	
	public boolean storeJsonInDatabase(JSONArray json){
		Log.d("StorageManager", "storeJsonInDatabase() start");
		dbhelper.onUpgrade(db, 1, 1);
		try{
			Log.d("StorageManager", "storeJsonInDatabase() try");
			for(int i = 0; i < json.length(); i++){
				String type = json.getJSONObject(i).getString("type");
				String icon = json.getJSONObject(i).getString("icon");
				String cat = json.getJSONObject(i).getString("cat");
				
				ContentValues newTaskValue1 = new ContentValues();
				newTaskValue1.put("I_id", i);
				newTaskValue1.put("TYPE", type);
				newTaskValue1.put("ICON", icon);
				newTaskValue1.put("CAT", cat);
				
				db.insert(Constants.TABLE_NAME1, null, newTaskValue1);
				
				JSONArray bucket = json.getJSONObject(i).getJSONArray("bucket");
				for(int j = 0; j < bucket.length(); j++){
					String id = bucket.getJSONObject(j).getString("id");
					String name = bucket.getJSONObject(j).getString("name");
					String bucketicon = bucket.getJSONObject(j).getString("icon");
					
					ContentValues newTaskValue2 = new ContentValues();
					newTaskValue2.put("I_id", i);
					newTaskValue2.put("J", j);
					newTaskValue2.put("ID", id);
					newTaskValue2.put("NAME", name);
					newTaskValue2.put("BUCKETICON", bucketicon);
					
					db.insert(Constants.TABLE_NAME2, null, newTaskValue2);
				}
			}
		} catch(JSONException ex){
			ex.printStackTrace();
			Log.d("StorageManager", "storeJsonInDatabase() exception caught");
			return false;
		}
		Log.d("StorageManager", "storeJsonInDatabase() end");
		return true;
	}
	
	public JSONArray fetchJsonFromDatabase(){
		Log.d("StorageManager", "fetchJsonFromDatabase() start");
		String query = "SELECT * FROM " +
				Constants.TABLE_NAME1  + " T1 INNER JOIN " +
						Constants.TABLE_NAME2 + " T2 ON T1.I_id = T2.I_id " +
								"ORDER BY T1.I_id, T2.J";
		Cursor c = db.rawQuery(query, null);

		JSONArray json = new JSONArray();
		c.moveToFirst();
		boolean loop = true;
		try{
			Log.d("StorageManager", "fetchJsonFromDatabase() try start");
			while(loop){
				int i = c.getInt(c.getColumnIndex("I_id"));
				
				JSONObject tempObj1 = new JSONObject();
				tempObj1.put("icon", c.getString(c.getColumnIndex("ICON")));
				tempObj1.put("cat", c.getString(c.getColumnIndex("CAT")));
				
				JSONArray tempArr = new JSONArray();
				while(loop && i == c.getInt(c.getColumnIndex("I_id"))){
					JSONObject tempObj2 = new JSONObject();
				
					tempObj2.put("id", c.getString(c.getColumnIndex("ID")));
					tempObj2.put("name", c.getString(c.getColumnIndex("NAME")));
					tempObj2.put("icon", c.getString(c.getColumnIndex("BUCKETICON")));
					
					tempArr.put(c.getInt(c.getColumnIndex("J")), tempObj2);
					loop = c.moveToNext();
				}
				tempObj1.put("bucket", tempArr);
				json.put(i, tempObj1);
			}		
		} catch(Exception ex){
			Log.d("StorageManager", "fetchJsonFromDatabase() caught exception");
			ex.printStackTrace();
		}
		Log.d("StorageManager", "fetchJsonFromDatabase() end");
			return json;
	}
}
