/*
 * 
 */
package com.playcez;

import static com.playcez.Start_Menu.prog;
import org.json.JSONArray;
import org.json.JSONException;

import com.playcez.bean.StorageManager;

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
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchResults.
 */
public class SearchResults extends Activity {

	/** The search results array. */
	private JSONArray searchResultsArray;

	/** The search results. */
	private Json_Fetch searchResults;

	/** The user cords. */
	final String[] userCords = new String[2];

	/** The prog. */
	private ProgressDialog progg;

	/** The sm. */
	private StorageManager sm;

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
		Spinner sp = (Spinner) findViewById(R.id.spinner);
		sp.setVisibility(View.GONE);

		// prog = ProgressDialog.show(this, "Please wait", "Loading...", true);
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
				Looper.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				download();
				Looper.myLooper().quit();
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
				createView();
			} catch (Exception e) {
				e.printStackTrace();
			}

			ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
			pb.setVisibility(View.GONE);
		}
	}

	/**
	 * Download.
	 */
	public void download() {
		// Fetch JSON from internet
		try {
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			String lat = myData.getString("lat", "");
			String lng = myData.getString("lng", "");
			String csid = myData.getString("csid", "9100401107066699");
			String bkId = myData.getString("bkId", "");
			Log.d("csid", csid + "");
			searchResultsArray = sqlStorage(
					"https://playcez.com/api_magicEndPoint.php?csid=" + csid
							+ "&id=" + bkId, csid, bkId);
			Editor edit = myData.edit();
			edit.putString("extra_array", searchResultsArray.toString());
			edit.commit();

			Log.d("bkId", bkId + "");
			userCords[0] = lat;
			userCords[1] = lng;
		} catch (Exception e) {
			e.printStackTrace();
		}

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
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			Json_Fetch result = new Json_Fetch(url,
					myData.getString("uid", ""), myData.getString(
							"playcez_token", ""));
			Log.d("SQLite", "sql");
			if (!sm.checkThreshold(csid, id)) {
				Log.d("SQLite", "threshold say true");
				try {
					resultArray = new JSONArray(result.json);
				} catch (JSONException e) {
					//
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
	 * Creates the view.
	 */
	public void createView() {

		for (int i = 0; i < searchResultsArray.length(); i++) {
			try {
				Log.d("xxxxxx", i + "");
				final String title = searchResultsArray.getJSONObject(i)
						.getString("name").toString();
				final String address = searchResultsArray.getJSONObject(i)
						.getString("address").toString();

				final String[] placeCords = new String[2];
				placeCords[0] = searchResultsArray.getJSONObject(i)
						.getString("lat").toString();
				placeCords[1] = searchResultsArray.getJSONObject(i)
						.getString("lng").toString();

				final String distance = calculateDistance(userCords, placeCords);
				String resourceName = "m"
						+ searchResultsArray.getJSONObject(i)
								.getString("subcatid").toString();
				// String resourceName = "city_null";
				int resourceId = this.getResources().getIdentifier(
						resourceName, "drawable", this.getPackageName());
				// int id = Integer.getInteger(resourceId);

				Log.d("The resource id of the items in the list view",
						resourceName + "");
				// ////////////////////////////START MAKING
				// BUTTON/////////////////////////
				LinearLayout screen2_layout = (LinearLayout) findViewById(R.id.screen2_button_layout);

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
				// params.gravity = Gravity.LEFT;
				btn.setLayoutParams(params);

				btn.setBackgroundDrawable(this.getResources().getDrawable(
						R.drawable.color_gradient));

				Drawable img = null;

				try {
					img = getApplicationContext().getResources().getDrawable(
							resourceId);
				} catch (Exception e) {

					e.printStackTrace();

				}
				// img = BitmapFactory.decodeResource(getResources(),
				// resourceId);
				// img = new
				// PreviousResult().loadImageFromNetwork("https://playcez.com/images/markers/"+searchResultsArray.getJSONObject(i).getString("subcatid")+".png");
				if (img == null) {
					resourceName = "city_null";
					int resId = this.getResources().getIdentifier(resourceName,
							"drawable", this.getPackageName());
					// img = BitmapFactory.decodeResource(getResources(),
					// resourceId);
				}

				// BitmapDrawable bmp = new BitmapDrawable(getResources(),img);

				resourceName = "city_location";
				int resId = this.getResources().getIdentifier(resourceName,
						"drawable", this.getPackageName());

				Drawable img2 = null;

				try {

					img2 = getApplicationContext().getResources().getDrawable(
							resId);

				} catch (Exception e) {

					e.printStackTrace();

				}

				btn.setCompoundDrawablesWithIntrinsicBounds(img2, null, img,
						null);

				btn.setTextColor(Color.GRAY);
				final int index = i;
				btn.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Intent mIntent = new Intent(getApplicationContext(),
								Review.class);
						SharedPreferences myData = getSharedPreferences(
								"myData", MODE_PRIVATE);
						Editor myEdit = myData.edit();
						try {
							myEdit.putString("pid", searchResultsArray
									.getJSONObject(index).getString("pid"));
							myEdit.putInt("index", index);
							myEdit.commit();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						progg = ProgressDialog.show(SearchResults.this,
								"Please wait!", "Loading...", true);
						startActivityForResult(mIntent, 3);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//
		try {
			progg.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onActivityResult(requestCode, resultCode, data);
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
	 * Start home.
	 * 
	 * @param view
	 *            the view
	 */
	public void startHome(View view) {
		prog = ProgressDialog.show(SearchResults.this, "Please wait",
				"Loading...");
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			prog = ProgressDialog.show(SearchResults.this, "Please wait",
					"Loading...", true);
			startActivity(new Intent(getApplicationContext(), Start_Menu.class));
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
