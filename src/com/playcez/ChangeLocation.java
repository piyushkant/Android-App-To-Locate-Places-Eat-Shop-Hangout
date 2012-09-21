/*
 * 
 */
package com.playcez;

import static com.playcez.Start_Menu.prog;

import static com.playcez.sqlite.DataSqliteOpenHelper.city_id;
import static com.playcez.sqlite.DataSqliteOpenHelper.cs_id;
import static com.playcez.sqlite.DataSqliteOpenHelper.info_table;
import static com.playcez.sqlite.DataSqliteOpenHelper.st_label;
import static com.playcez.sqlite.DataSqliteOpenHelper.st_value;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.playcez.bean.StorageManager;
import com.playcez.sqlite.DataSqliteOpenHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class ChangeLocation.
 * 
 * @author Anmol, Piyush
 */
public class ChangeLocation extends Activity {

	/** The prog. */
	private ProgressDialog progg;

	/** The database. */
	private SQLiteDatabase database;

	/** The data. */
	private DataSqliteOpenHelper data = null;

	private StorageManager sm;

	/** The data cursor. */
	static Cursor dataCursor;

	/** The street label. */
	static String streetLabel;

	/** The filter. */
	static EditText filter;

	/** The streets. */
	static JSONArray streets;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen2);

		try {
			prog.dismiss();
		} catch (Exception e) {
		}

		try{
			RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
			header.setVisibility(View.GONE);
	
			filter = (EditText) findViewById(R.id.filter);
			filter.setVisibility(View.VISIBLE);
			filter.setBackgroundResource(R.drawable.search_by_name);
			filter.addTextChangedListener(filterTextWatcher);
	
			ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
			pb.setVisibility(View.GONE);
	
			data = new DataSqliteOpenHelper(this);
			database = data.getWritableDatabase();
	
			Toast.makeText(getApplicationContext(),
					"Select a locality to get recommendation.", Toast.LENGTH_LONG)
					.show();
	
			Spinner sp = (Spinner) findViewById(R.id.spinner);
			sp.setVisibility(View.GONE);
	
			TextView text = (TextView) findViewById(R.id.text);
			text.setText("Choose Locality");
	
			ImageButton refresh = (ImageButton) findViewById(R.id.mobile_refresh_button);
			refresh.setVisibility(View.GONE);
	
			SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
			Editor myEdit = myData.edit();
	
			String lat = myData.getString("lat", "");
			String lng = myData.getString("lng", "");
			String csid = myData.getString("csid", "");
			// Log.d("lat and long", lat + " " + lng);
	
			/*
			 * Json_Fetch csidArray = new Json_Fetch(
			 * "https://playcez.com/api_getNearestCrossStreets.php?lat=" + lat +
			 * "&lng=" + lng);
			 */
			ListView l = (ListView) findViewById(R.id.nearbylocalities);
	
			dataCursor = database.query(info_table, new String[] { city_id, cs_id,
					st_label, st_value }, null, null, null, null, null);
	
			dataCursor.moveToFirst();
			final Geocoder code = new Geocoder(this);
		
		
			try {
				// streets = new JSONArray(csidArray.json);
				int delay = 999999999; // delay for 0 sec.
				streets = sqlStorage(
						"https://playcez.com/api_getNearestCrossStreets.php?lat="
								+ lat + "&lng=" + lng, csid, "-1", delay);
	
				if (dataCursor != null && dataCursor.getCount() == 0) {
					// sqliteStorage(streets);
					addDynamically(streets, "all");
				} else {
					// int delay = 999999999; // delay for 0 sec.
					int period = 999999999; // repeat every 10 days.
					Timer timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {
						public void run() {
							System.out.println("done");
							// sqliteStorage(streets);
						}
					}, delay, period);
				}
				// getSqliteData("all");
				addDynamically(streets, "all");
	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch(Exception e){}
	}

	/**
	 * Sql storage.
	 * 
	 * @param url
	 *            the url
	 * @param csid
	 *            the csid
	 * @param id
	 *            the id
	 * @return the jSON array
	 */
	public JSONArray sqlStorage(String url, String csid, String id, int max_time) {
		sm = new StorageManager(this);
		sm.open();

		JSONArray resultArray = null;
		try {

			if (!sm.checkThreshold(csid, id, max_time)
					|| !(id.equals("9") && id.equals("67") && id.equals("5"))) {
				try {
					SharedPreferences myData = getSharedPreferences("myData",
							MODE_PRIVATE);
					Json_Fetch result = new Json_Fetch(url, myData.getString(
							"uid", ""), myData.getString("playcez_token", ""));
					resultArray = new JSONArray(result.json);
					sm.close();
					return resultArray;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				sm.storeResultArray(csid, id, resultArray);
			}

			resultArray = sm.fetchResultArray(csid, id);
			Log.d("SQLite", resultArray + "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		sm.close();
		return resultArray;
	}

	/**
	 * Gets the sqlite data.
	 * 
	 * @param stLabel
	 *            the st label
	 * @return the sqlite data
	 */
	void getSqliteData(String stLabel) {

		LinearLayout screen2_layout = (LinearLayout) findViewById(R.id.screen2_button_layout);
		screen2_layout.removeAllViews();

		dataCursor.moveToFirst();

		if (!dataCursor.isAfterLast()) {
			do {
				streetLabel = dataCursor.getString(2);

				String lower_streetLabel = streetLabel.toLowerCase();
				stLabel = stLabel.toLowerCase();

				if (!stLabel.equals("all")
						&& !stLabel.equals(lower_streetLabel.substring(0,
								stLabel.length()))) {
					continue;
				}

				// LinearLayout screen2_layout = (LinearLayout)
				// findViewById(R.id.screen2_button_layout);

				TextView btn = new TextView(this);
				btn.setText(streetLabel);

				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, 70, 1.0f);

				btn.setLayoutParams(params);

				btn.setBackgroundDrawable(this.getResources().getDrawable(
						R.drawable.color_gradient));

				btn.setTextColor(Color.BLACK);

				final Geocoder code = new Geocoder(this);

				btn.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						prog = ProgressDialog.show(ChangeLocation.this,
								"Please wait", "Loading...", true);

						List<Address> addresses;
						try {
							addresses = code
									.getFromLocationName(streetLabel, 1);
							SharedPreferences myData = getSharedPreferences(
									"myData", MODE_PRIVATE);
							Editor myEdit = myData.edit();
							String addressline1 = addresses.get(0)
									.getAddressLine(1);
							myEdit.putString(
									"userPreferedStreet",
									addresses.get(0).getAddressLine(0)
											+ ", "
											+ addressline1.substring(0,
													addressline1.indexOf(',')));
							myEdit.putString("lat", addresses.get(0)
									.getLatitude() + "");
							myEdit.putString("lng", addresses.get(0)
									.getLongitude() + "");
							myEdit.commit();
							finish();
						} catch (IOException e) {
							//
							e.printStackTrace();
						}
					}
				});

				screen2_layout.addView(btn);

			} while (dataCursor.moveToNext());
		}

		// dataCursor.close();
	}

	/**
	 * Adds the dynamically.
	 * 
	 * @param streets
	 *            the streets
	 * @param stLabel
	 *            the st label
	 */
	public void addDynamically(JSONArray streets, String stLabel) {

		LinearLayout screen2_layout = (LinearLayout) findViewById(R.id.screen2_button_layout);
		screen2_layout.removeAllViews();

		for (int i = 0; i < streets.length(); i++) {
			try {
				final String label = streets.getJSONObject(i)
						.getString("label");

				String lower_streetLabel = label.toLowerCase();
				stLabel = stLabel.toLowerCase();

				if (!stLabel.equals("all")
						&& !stLabel.equals(lower_streetLabel.substring(0,
								stLabel.length()))) {
					continue;
				}

				TextView btn = new TextView(this);
				btn.setText(label);

				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, 70, 1.0f);
				// params.gravity = Gravity.LEFT;
				btn.setLayoutParams(params);

				btn.setBackgroundDrawable(this.getResources().getDrawable(
						R.drawable.color_gradient));

				// btn.setTypeface(face,Typeface.BOLD);

				btn.setTextColor(Color.BLACK);

				final Geocoder code = new Geocoder(this);

				btn.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						//
						List<Address> addresses;
						try {
							prog = ProgressDialog.show(ChangeLocation.this,
									"Please wait", "Loading...", true);

							addresses = code.getFromLocationName(label, 1);
							SharedPreferences myData = getSharedPreferences(
									"myData", MODE_PRIVATE);
							Editor myEdit = myData.edit();
							String addressline1 = addresses.get(0)
									.getAddressLine(1);
							myEdit.putString(
									"userPreferedStreet",
									addresses.get(0).getAddressLine(0)
											+ ", "
											+ addressline1.substring(0,
													addressline1.indexOf(',')));
							myEdit.putString("lat", addresses.get(0)
									.getLatitude() + "");
							myEdit.putString("lng", addresses.get(0)
									.getLongitude() + "");
							myEdit.commit();
							
							Toast.makeText(getApplicationContext(),
									"Location Changed", Toast.LENGTH_LONG)
									.show();
							
							finish();
						} catch (IOException e) {
							//
							e.printStackTrace();
						}
					}
				});

				screen2_layout.addView(btn);
				// /////////////////////////////END MAKING
				// BUTTON/////////////////////////////

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Refresh.
	 * 
	 * @param v
	 *            the v
	 */
	public void refresh(View v) {

		progg = ProgressDialog.show(this, "Please wait", "Refreshing...", true);

		new DownloadActivityTask().execute();

	}

	/**
	 * Start home.
	 * 
	 * @param v
	 *            the v
	 */
	public void startHome(View v) {

		prog = ProgressDialog.show(ChangeLocation.this, "Please wait",
				"Loading...", true);
		startActivity(new Intent(this, Start_Menu.class));

	}

	/**
	 * The Class DownloadActivityTask.
	 */
	private class DownloadActivityTask extends AsyncTask<String, Integer, Long> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Long doInBackground(String... params) {
			Looper.prepare();
			try {
				new Start_Menu().setUpLocation();
				new Start_Menu().storeNearestStreets();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			try {
				progg.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
			progg.dismiss();
		}
	}

	/**
	 * Sqlite storage.
	 * 
	 * @param streets
	 *            the streets
	 * @throws JSONException
	 *             the jSON exception
	 */
	void sqliteStorage(JSONArray streets) throws JSONException {
		for (int i = 0; i < streets.length(); i++) {
			final String cityId = streets.getJSONObject(i).getString("city_id");
			final String csId = streets.getJSONObject(i).getString("cs_id");
			final String label = streets.getJSONObject(i).getString("label");
			final String value = streets.getJSONObject(i).getString("value");

			ContentValues values = new ContentValues();
			values.put(city_id, cityId);
			values.put(cs_id, csId);
			values.put(st_label, label);
			values.put(st_value, value);
			database.insert("nearby_localities", null, values);
		}
	}

	/** The filter text watcher. */
	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			filter.setBackgroundResource(R.drawable.search_by_name);
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			filter.setBackgroundResource(R.drawable.searchbar);

			if (dataCursor != null && dataCursor.getCount() == 0) {
				addDynamically(streets, s.toString());
			} else {
				getSqliteData(s.toString());
			}

		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			prog = ProgressDialog.show(ChangeLocation.this, "Please wait",
					"Loading...", true);
			startActivity(new Intent(getApplicationContext(), Start_Menu.class));
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
