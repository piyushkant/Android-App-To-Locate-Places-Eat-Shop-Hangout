/*
 * 
 */
package com.playcez.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playcez.Json_Fetch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class StorageManager.
 */
public class StorageManager {

	/** The db. */
	private SQLiteDatabase db;

	/** The context. */
	private Context context;

	/** The dbhelper. */
	private MyDBhelper dbhelper;

	/**
	 * Instantiates a new storage manager.
	 * 
	 * @param c
	 *            the c
	 */
	public StorageManager(Context c) {
		context = c;
		dbhelper = new MyDBhelper(context, Constants.DATABASE_NAME, null,
				Constants.DATABASE_VERSION);
	}

	/**
	 * Close.
	 */
	public void close() {
		db.close();
	}

	/**
	 * Open.
	 * 
	 * @throws SQLiteException
	 *             the sQ lite exception
	 */
	public void open() throws SQLiteException {
		try {
			db = dbhelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			db = dbhelper.getReadableDatabase();
		}
	}

	/**
	 * Check max limit.
	 * 
	 * @return true, if successful
	 */
	public boolean checkMaxLimit() {

		String query = "SELECT csid FROM " + Constants.RESULT_ARRAY;

		Cursor c = db.rawQuery(query, null);
		if (Constants.MAX_ROWS > c.getCount()) {
			Log.d("StorageManager", "true inn checkMAxLimit");
			c.close();
			return true;
		} else {
			delMaxTimeData();
			c.close();
			return true;
		}

	}

	/**
	 * Del max time data.
	 */
	public void delMaxTimeData() {
		String query = "SELECT * FROM " + Constants.RESULT_ARRAY;
		Cursor c = db.rawQuery(query, null);
		c.moveToFirst();
		String csid = c.getString(c.getColumnIndex("csid"));
		String id = c.getString(c.getColumnIndex("id"));
		query = "SELECT system_time FROM " + Constants.RESULT_ARRAY
				+ " where csid=" + csid + " and id=" + id;
		Cursor c2 = db.rawQuery(query, null);
		c2.moveToFirst();
		long ntime = System.currentTimeMillis();
		long ptime = c2.getLong(c.getColumnIndex("system_time"));
		long max_time = ntime - ptime;
		String max_csid = csid;
		String max_id = id;
		while (c.moveToNext()) {
			csid = c.getString(c.getColumnIndex("csid"));
			id = c.getString(c.getColumnIndex("id"));
			query = "SELECT system_time FROM " + Constants.RESULT_ARRAY
					+ " where csid=" + csid + " and id=" + id;
			c2 = db.rawQuery(query, null);
			ptime = c2.getLong(c.getColumnIndex("system_time"));

			if ((ntime - ptime) > max_time) {
				max_time = ntime - ptime;
				max_csid = csid;
				max_id = id;
			}
		}
		deleteTodo(csid, id);
		c.close();
		c2.close();
	}

	/**
	 * Check threshold.
	 * 
	 * @param csid
	 *            the csid
	 * @param id
	 *            the id
	 * @return true, if successful
	 */
	public boolean checkThreshold(String csid, String id) {
		long ntime = System.currentTimeMillis();
		String query = "SELECT system_time FROM " + Constants.RESULT_ARRAY
				+ " where csid=" + csid + " and id=" + id;
		Cursor c = db.rawQuery(query, null);
		boolean check = c.moveToFirst();
		long ptime = 0;
		if (check) {
			ptime = c.getLong(c.getColumnIndex("system_time"));
			if ((ntime - ptime) > Constants.MAX_TIME) {
				deleteTodo(csid, id);
				c.close();
				return false;
			}
			c.close();
			return true;
		}
		c.close();
		return false;
	}

	public boolean checkThreshold(String csid, String id, int max_time) {
		long ntime = System.currentTimeMillis();
		String query = "SELECT system_time FROM " + Constants.RESULT_ARRAY
				+ " where csid=" + csid + " and id=" + id;
		Cursor c = db.rawQuery(query, null);
		boolean check = c.moveToFirst();
		long ptime = 0;
		if (check) {
			ptime = c.getLong(c.getColumnIndex("system_time"));
			if ((ntime - ptime) > max_time) {
				deleteTodo(csid, id);
				c.close();
				return false;
			}
			c.close();
			return true;
		}
		c.close();
		return false;
	}

	/**
	 * Delete todo.
	 * 
	 * @param csid
	 *            the csid
	 * @param id
	 *            the id
	 * @return true, if successful
	 */
	public boolean deleteTodo(String csid, String id) {
		return db.delete(Constants.RESULT_ARRAY, "csid" + "=" + csid
				+ " and id=" + id, null) > 0;
	}

	/**
	 * Update todo.
	 * 
	 * @param csid
	 *            the csid
	 * @param id
	 *            the id
	 * @param resultArray
	 *            the result array
	 * @return true, if successful
	 */
	public boolean updateTodo(String csid, String id, JSONArray resultArray) {
		ContentValues values = createContentValues(resultArray.toString(),
				System.currentTimeMillis());
		return db.update(Constants.RESULT_ARRAY, values, "csid" + "=" + csid
				+ " and id=" + id, null) > 0;
	}

	/**
	 * Creates the content values.
	 * 
	 * @param json_array
	 *            the json_array
	 * @param time
	 *            the time
	 * @return the content values
	 */
	private ContentValues createContentValues(String json_array, long time) {
		ContentValues values = new ContentValues();
		values.put("json_array", json_array);
		values.put("system_time", time);
		return values;
	}

	/**
	 * Store result array.
	 * 
	 * @param csid
	 *            the csid
	 * @param id
	 *            the id
	 * @param resultArray
	 *            the result array
	 */
	public void storeResultArray(String csid, String id, JSONArray resultArray) {

		if (resultArray == null) {
			return;
		}
		try {
			checkMaxLimit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String s = resultArray.toString();

		ContentValues newTaskValue1 = new ContentValues();
		newTaskValue1.put("csid", csid);
		newTaskValue1.put("id", id);
		newTaskValue1.put("json_array", s);
		newTaskValue1.put("system_time", System.currentTimeMillis());

		db.insert(Constants.RESULT_ARRAY, null, newTaskValue1);
	}

	/**
	 * Fetch result array.
	 * 
	 * @param csid
	 *            the csid
	 * @param id
	 *            the id
	 * @return the jSON array
	 */
	public JSONArray fetchResultArray(String csid, String id) {

		String query = "SELECT json_array FROM " + Constants.RESULT_ARRAY
				+ " WHERE csid=" + csid + " and id=" + id;
		Cursor c = db.rawQuery(query, null);
		c.moveToFirst();
		String s = c.getString(c.getColumnIndex("json_array"));
		JSONArray json = null;
		try {
			json = new JSONArray(s);
		} catch (JSONException e) {
		}
		c.close();
		return json;
	}

	/**
	 * Store json in database.
	 * 
	 * @param json
	 *            the json
	 * @return true, if successful
	 */
	public boolean storeJsonInDatabase(JSONArray json) {
		dbhelper.onUpgrade(db, 1, 1);
		try {
			Log.d("StorageManager", "storeJsonInDatabase()");
			for (int i = 0; i < json.length(); i++) {
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
				for (int j = 0; j < bucket.length(); j++) {
					String id = bucket.getJSONObject(j).getString("id");
					String name = bucket.getJSONObject(j).getString("name");
					String bucketicon = bucket.getJSONObject(j).getString(
							"icon");

					ContentValues newTaskValue2 = new ContentValues();
					newTaskValue2.put("I_id", i);
					newTaskValue2.put("J", j);
					newTaskValue2.put("ID", id);
					newTaskValue2.put("NAME", name);
					newTaskValue2.put("BUCKETICON", bucketicon);

					db.insert(Constants.TABLE_NAME2, null, newTaskValue2);
				}
			}
		} catch (JSONException ex) {
			db.close();
			return false;
		}
		db.close();
		return true;
	}

	/**
	 * Fetch json from database.
	 * 
	 * @return the jSON array
	 */
	public JSONArray fetchJsonFromDatabase() {
		Log.d("StorageManager", "fetchJsonFromDatabase() start");
		String query = "SELECT * FROM " + Constants.TABLE_NAME1
				+ " T1 INNER JOIN " + Constants.TABLE_NAME2
				+ " T2 ON T1.I_id = T2.I_id " + "ORDER BY T1.I_id, T2.J";
		Cursor c = db.rawQuery(query, null);

		JSONArray json = new JSONArray();
		c.moveToFirst();
		boolean loop = true;
		try {
			Log.d("StorageManager", "fetchJsonFromDatabase() try start");
			while (loop) {
				int i = c.getInt(c.getColumnIndex("I_id"));

				JSONObject tempObj1 = new JSONObject();
				tempObj1.put("icon", c.getString(c.getColumnIndex("ICON")));
				tempObj1.put("cat", c.getString(c.getColumnIndex("CAT")));

				JSONArray tempArr = new JSONArray();
				while (loop && i == c.getInt(c.getColumnIndex("I_id"))) {
					JSONObject tempObj2 = new JSONObject();

					tempObj2.put("id", c.getString(c.getColumnIndex("ID")));
					tempObj2.put("name", c.getString(c.getColumnIndex("NAME")));
					tempObj2.put("icon",
							c.getString(c.getColumnIndex("BUCKETICON")));

					tempArr.put(c.getInt(c.getColumnIndex("J")), tempObj2);
					loop = c.moveToNext();
				}
				tempObj1.put("bucket", tempArr);
				json.put(i, tempObj1);
			}
		} catch (Exception ex) {

		}
		c.close();
		return json;
	}
}
