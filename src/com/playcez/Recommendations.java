/*
 * 
 */
package com.playcez;

import static com.playcez.Start_Menu.prog;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.playcez.bean.StorageManager;

// TODO: Auto-generated Javadoc
/**
 * The Class NearFood.
 */
public class Recommendations extends Activity {

	/** The Constant EXTRA_ARRAY. */
	private static final String EXTRA_ARRAY = "Signifies Extra Array";

	/** The bucket array. */
	static private JSONArray bucketArray;

	/** The food array. */
	static private JSONArray foodArray;

	/** The foods array. */
	private JSONArray foodsArray;

	/** The extra array. */
	private JSONArray extraArray;

	/** The bucket name list. */
	static List<String> bucketNameList;

	/** The bucket id list. */
	static List<String> bucketIdList;

	/** The bucket. */
	private Json_Fetch bucket;

	/** The food. */
	static private Json_Fetch food;

	/** The full food. */
	private Json_Fetch fullFood;

	/** The user cords. */
	final String[] userCords = new String[2];
	// private static ProgressDialog prog;
	/** The id. */
	private String id;

	/** The bkid. */
	static String bkid;

	/** The i. */
	public static int i;

	/** The n. */
	public static int n = 0;

	/** The back progress. */
	public static ProgressDialog backProgress;

	/** The sm. */
	private StorageManager sm;

	/** The requery. */
	private boolean requery;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen2);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		requery = false;

		bkid = "0";
		new DownloadActivityTask().execute();
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
			try {
				download();
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
			try {
				createView(bkid);
				addItemsOnSpinner();
				addListenerOnSpinnerItemSelection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			requery = false;
			ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
			pb.setVisibility(View.GONE);
		}
	}

	/**
	 * The Class DownloadActivityTask2.
	 */
	private class DownloadActivityTask2 extends
			AsyncTask<String, Integer, Long> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Long doInBackground(String... params) {
			// Looper.prepare();
			try {
				download();
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
			try {
				createView(EXTRA_ARRAY);
			} catch (Exception e) {
				e.printStackTrace();
			}
			requery = false;
			ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
			pb.setVisibility(View.GONE);
		}
	}

	/**
	 * Download.
	 */
	public void download() {

		try {
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			String lat = myData.getString("lat", "");
			String lng = myData.getString("lng", "");
			String csid = myData.getString("csid", "");
			String preference = myData.getString("preference", "");
			id = prefId(preference);
			if (requery) {
				String pic_url = "https://playcez.com/api_magicEndPoint.php?csid="
						+ csid + "&id=" + bkid;
				Json_Fetch result = new Json_Fetch(pic_url, myData.getString(
						"uid", ""), myData.getString("playcez_token", ""));
				extraArray = new JSONArray(result.json);
			} else {
				foodArray = sqlStorage(
						"https://playcez.com/api_magicEndPoint.php?csid=" + csid
								+ "&id=" + id, csid, id.replace(",", ""));
			}
			userCords[0] = lat;
			userCords[1] = lng;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the view.
	 * 
	 * @param bkid
	 *            the bkid
	 */
	public void createView(String bkid) {
		LinearLayout screen2_layout = (LinearLayout) findViewById(R.id.screen2_button_layout);
		screen2_layout.removeAllViews();
		JSONArray foodArray = new JSONArray();
		if (bkid.equals("0")) {
			foodArray = this.foodArray;
		} else if (bkid.equals(EXTRA_ARRAY)) {
			foodArray = extraArray;
		} else {
			for (i = 0; i < this.foodArray.length(); i++) {
				try {
					try {
						if (!bkid.equals(EXTRA_ARRAY)
								&& !bkid.equals(this.foodArray.getJSONObject(i)
										.getString("buckets").toString())
								&& !bkid.equals("0")) {
							continue;
						}
						foodArray.put(this.foodArray.getJSONObject(i));
					} catch (Exception e) {
					}
				} catch (Exception e) {
				}
			}
		}

		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		Editor edit = myData.edit();
		edit.putString("extra_array", foodArray.toString());
		edit.commit();
		n = 0;

		for (i = 0; i < foodArray.length(); i++) {

			try {
				try {
					if (!bkid.equals(EXTRA_ARRAY)
							&& !bkid.equals(foodArray.getJSONObject(i)
									.getString("buckets").toString())
							&& !bkid.equals("0")) {
						continue;
					}
				} catch (Exception e) {

				}
				n++;
				final String title = foodArray.getJSONObject(i)
						.getString("name").toString();
				final String address = foodArray.getJSONObject(i)
						.getString("address").toString();
				// final String bkid =
				// foodArray.getJSONObject(i).getString("buckets").toString();

				final String[] placeCords = new String[2];
				placeCords[0] = foodArray.getJSONObject(i).getString("lat")
						.toString();
				placeCords[1] = foodArray.getJSONObject(i).getString("lng")
						.toString();

				final String distance = calculateDistance(userCords, placeCords);
				String resourceName = "m"
						+ foodArray.getJSONObject(i).getString("subcatid")
								.toString();
				int resourceId = this.getResources().getIdentifier(
						resourceName, "drawable", this.getPackageName());
				// ////////////////////////////START MAKING
				// BUTTON/////////////////////////

				Typeface face = Typeface.createFromAsset(getAssets(),
						"fonts/verdana.ttf");

				TextView btn = new TextView(this);
				btn.setText(Html.fromHtml("<big><b>" + " " + title
						+ "</b></big><br/>" + " " + address + "<br/>" + " "
						+ distance + ""));
				btn.setTag(title + "_button");

				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
						1.0f);
				btn.setLayoutParams(params);
				btn.setBackgroundDrawable(this.getResources().getDrawable(
						R.drawable.color_gradient));
				Drawable img = null;

				try {
					img = getApplicationContext().getResources().getDrawable(
							resourceId);
				} catch (Exception e) {
				}

				if (img == null) {
					resourceName = "city_null";
					int resId = this.getResources().getIdentifier(resourceName,
							"drawable", this.getPackageName());
				}

				resourceName = "city_location";
				int resId = this.getResources().getIdentifier(resourceName,
						"drawable", this.getPackageName());
				Drawable img2 = null;

				try {
					img2 = getApplicationContext().getResources().getDrawable(
							resId);
				} catch (Exception e) {
				}

				btn.setCompoundDrawablesWithIntrinsicBounds(null, null, img,
						null);
				btn.setTextColor(Color.GRAY);
				final int index = i;
				final JSONArray finalArray = foodArray; // / Defoned to use in
														// onclick button
				btn.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Intent mIntent = new Intent(getApplicationContext(),
								Review.class);
						SharedPreferences myData = getSharedPreferences(
								"myData", MODE_PRIVATE);
						Editor myEdit = myData.edit();
						try {
							myEdit.putString(
									"pid",
									finalArray.getJSONObject(index).getString(
											"pid"));
							myEdit.putInt("index", index);
							myEdit.commit();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						prog = ProgressDialog.show(Recommendations.this,
								"Please wait!", "Loading...", true);
						startActivity(mIntent);
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
		Log.d("nnnnnnnnn", n + "");
	}

	/**
	 * Torad.
	 * 
	 * @param x
	 *            the x
	 * @return the double
	 */
	public double torad(double x) {
		return x * Math.PI / 180;
	}

	/**
	 * Calculate distance.
	 * 
	 * @param Cords1
	 *            the cords1
	 * @param Cords2
	 *            the cords2
	 * @return the string
	 */
	public String calculateDistance(String[] Cords1, String[] Cords2) {
		int R = 6371; // Radius of the earth in km

		double lat1 = Double.valueOf(Cords1[0]);
		double lng1 = Double.valueOf(Cords1[1]);

		double lat2 = Double.valueOf(Cords2[0]);
		double lng2 = Double.valueOf(Cords2[1]);

		double dLat = torad(lat2 - lat1); // Javascript functions in radians
		double dLon = torad(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(torad(lat1)) * Math.cos(torad(lat2))
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c * 1000; // Distance in meters
		String unit;
		if (d > 1000) {
			d = d / 1000;

			d = Math.round(d * 10) / 10;
			unit = "kms";
		} else {
			d = Math.round(d * 10) / 10;
			unit = "meters";
		}
		return Double.toString(d) + " " + unit;
	}

	/**
	 * Adds the items on spinner.
	 */
	public void addItemsOnSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		bucketNameList = new ArrayList<String>();
		bucketIdList = new ArrayList<String>();

		bucketNameList.add("Show All");
		bucketIdList.add("0");

		try {
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			String csid = myData.getString("csid", "9100401107066699");
			String preference = myData.getString("preference", "");

			bucket = new Json_Fetch(
					"https://playcez.com/api_getBucketList.php?csid=" + csid,
					myData.getString("uid", "uid"), myData.getString(
							"playcez_token", "token"));
			bucketArray = new JSONArray(bucket.json);

			for (int i = 0; i < bucketArray.length(); i++) {
				if (bucketArray.getJSONObject(i).getString("cat").toString()
						.equals(preference)) {
					int size = bucketArray.getJSONObject(i)
							.getJSONArray("bucket").length();
					for (int j = 0; j < size; j++) {
						// Log.d("Start_Menu.java", "Suggestions[i]");
						bucketNameList.add(bucketArray.getJSONObject(i)
								.getJSONArray("bucket").getJSONObject(j)
								.getString("name"));
						bucketIdList.add(bucketArray.getJSONObject(i)
								.getJSONArray("bucket").getJSONObject(j)
								.getString("id"));
					}
				}
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_spinner_item,
						bucketNameList);
				dataAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(dataAdapter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the listener on spinner item selection.
	 */
	public void addListenerOnSpinnerItemSelection() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// your code here
				SharedPreferences myData = getSharedPreferences("myData",
						MODE_PRIVATE);
				Editor myEdit = myData.edit();
				myEdit.putString("bucketName",
						parent.getItemAtPosition(position).toString());
				myEdit.commit();
				Log.d("bucketName", parent.getItemAtPosition(position)
						.toString() + "");
				Log.d("size", bucketNameList.size() + "");

				for (int j = 0; j < bucketNameList.size(); j++) {
					if (parent.getItemAtPosition(position).toString()
							.equals(bucketNameList.get(j))) {
						bkid = bucketIdList.get(j);
						Log.d("bkidddddd", bkid + "");
						break;
					}
				}
				checkList(bkid);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}
		});
	}

	/**
	 * Check list.
	 * 
	 * @param bkid
	 *            the bkid
	 */
	public void checkList(String bkid) {
		n = 0;
		for (i = 0; i < foodArray.length(); i++) {

			try {
				if (!bkid.equals(foodArray.getJSONObject(i)
						.getString("buckets").toString())
						&& !bkid.equals("0")) {
					continue;
				}
				n++;
			} catch (Exception e) {

			}
		}
		Log.d("Near Food class", n + " this is new size");
		if (n >= 3) {
			createView(bkid);
		} else {
			Log.d("near food class", "hihihihi");
			this.bkid = bkid;
			requery = true;
			new DownloadActivityTask2().execute();
		}
	}

	/**
	 * Pref id.
	 * 
	 * @param pref
	 *            the pref
	 * @return the string
	 */
	public String prefId(String pref) {
		String id = "9";

		if (pref.equals("food")) {
			id = "9";
		} else if (pref.equals("shop")) {
			id = "5";
		} else if (pref.equals("hangout")) {
			id = "6,7";
		}

		return id;
	}

	/**
	 * Start home.
	 * 
	 * @param view
	 *            the view
	 */
	public void startHome(View view) {

		prog = ProgressDialog.show(Recommendations.this,
				"Please wait", "Loading...", true);
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
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
	public JSONArray sqlStorage(String url, String csid, String id) {
		sm = new StorageManager(this);
		sm.open();

		JSONArray resultArray = null;
		try {

			if (!sm.checkThreshold(csid, id)
					|| !(id.equals("9") && id.equals("67") && id.equals("5"))) {
				try {
					SharedPreferences myData = getSharedPreferences("myData",
							MODE_PRIVATE);
					Json_Fetch result = new Json_Fetch(url, myData.getString(
							"uid", "uid"), myData.getString("playcez_token", "token"));
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

}
