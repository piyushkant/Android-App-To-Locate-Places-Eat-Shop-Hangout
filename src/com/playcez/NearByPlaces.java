/*
 * 
 */
package com.playcez;

import static com.playcez.Start_Menu.prog;

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
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.playcez.bean.StorageManager;

// TODO: Auto-generated Javadoc
/**
 * The Class NearByPlaces.
 */
public class NearByPlaces extends Activity {

	/** The near by places array. */
	private JSONArray nearByPlacesArray;

	/** The near by places. */
	private Json_Fetch nearByPlaces;

	/** The user cords. */
	final String[] userCords = new String[2];

	/** The prog. */
	private ProgressDialog progg;

	/** The filter. */
	private EditText filter;

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
		
		try {
			prog.dismiss();

		} catch (Exception e) {

		}
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
		//header.setVisibility(View.GONE);

		Toast.makeText(
				getApplicationContext(),
				"Search and select a"
						+ " location to add photo. You can also add a new "
						+ "location by using plus icon in toolbar.",
				Toast.LENGTH_LONG).show();

		Spinner sp = (Spinner) findViewById(R.id.spinner);
		sp.setVisibility(View.GONE);

		ImageButton addPlace = (ImageButton) findViewById(R.id.add_place);
		addPlace.setVisibility(View.VISIBLE);
		addPlace.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//
				Intent prevIntent = getIntent();
				String callback = prevIntent.getStringExtra("callback");

				Intent mIntent = new Intent(getApplicationContext(),
						AddPlaces.class);
				mIntent.putExtra("callback", callback);

				final int NEARPLACE_CAPTURE_CALLBACK = 3;
				final int NEARPLACE_SELECT_CALLBACK = 4;
				final int ADD_REVIEW_CALLBACK = 7;
				/*
				 * Throws exception when callback is null
				 */
				try {
					if (callback.equals("capture")) {
						Log.d("near by places", "cccccccccccccccccc");
						startActivityForResult(mIntent,
								NEARPLACE_CAPTURE_CALLBACK);
					} else if (callback.equals("select")) {
						Log.d("near by places", "ssssssssssssssssssss");
						startActivityForResult(mIntent,
								NEARPLACE_SELECT_CALLBACK);
					}
				} catch (Exception e) {
					Log.d("near by places", "rrrrrrrrrrrrrrrrrrrrrrrr");
					startActivityForResult(mIntent, ADD_REVIEW_CALLBACK);
				}
			}
		});
		Log.d("NearByPlaces", "herere");
		new DownloadActivityTask().execute();

		filter = (EditText) findViewById(R.id.filter);
		filter.setVisibility(View.VISIBLE);
		filter.setBackgroundResource(R.drawable.search_by_name);
		filter.addTextChangedListener(filterTextWatcher);
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
			createView(s.toString());
		}

	};

	/** The sm. */
	private StorageManager sm;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			prog = ProgressDialog.show(NearByPlaces.this, "Please wait",
					"Loading...", true);
			startActivity(new Intent(getApplicationContext(), Start_Menu.class));
			finish();
		}
		return super.onKeyDown(keyCode, event);
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
				createView("all");
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
			String lat = myData.getString("user_lat", "");
			String lng = myData.getString("user_lng", "");
			String csid = myData.getString("csid", "");
			String id = "5,6,7,9";
			nearByPlacesArray = sqlStorage("https://playcez.com/"
					+ "api_magicEndPoint.php?csid=" + csid + "&id=5,6,7,9"
					+ "&lat=" + lat + "&lng=" + lng, csid, id.replace(",", ""));
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
			if (!sm.checkThreshold(csid, id)
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
	 * Creates the view.
	 * 
	 * @param name
	 *            the name
	 */
	public void createView(String name) {
		LinearLayout screen2_layout = (LinearLayout) findViewById(R.id.screen2_button_layout);
		screen2_layout.removeAllViews();
		try {
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			Editor edit = myData.edit();
			edit.putString("extra_array", nearByPlacesArray.toString());
			edit.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Parsing JSON
		for (int i = 0; i < nearByPlacesArray.length(); i++) {
			try {

				final String title = nearByPlacesArray.getJSONObject(i)
						.getString("name").toString();

				String lower_title = title.toLowerCase();
				name = name.toLowerCase();

				if (!name.equals("all")
						&& !name.equals(lower_title.substring(0, name.length()))) {
					continue;
				}

				final String address = nearByPlacesArray.getJSONObject(i)
						.getString("address").toString();

				final String[] placeCords = new String[2];
				placeCords[0] = nearByPlacesArray.getJSONObject(i)
						.getString("lat").toString();
				placeCords[1] = nearByPlacesArray.getJSONObject(i)
						.getString("lng").toString();

				final String distance = calculateDistance(userCords, placeCords);
				String resourceName = "m"
						+ nearByPlacesArray.getJSONObject(i)
								.getString("subcatid").toString();
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
					e.printStackTrace();
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
					e.printStackTrace();
				}

				btn.setCompoundDrawablesWithIntrinsicBounds(null, null, img,
						null);
				btn.setTextColor(Color.GRAY);
				final int index = i;
				btn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						//
						Intent mIntent = new Intent(getApplicationContext(),
								Review.class);
						SharedPreferences myData = getSharedPreferences(
								"myData", MODE_PRIVATE);
						Editor myEdit = myData.edit();
						try {
							myEdit.putString("pid", nearByPlacesArray
									.getJSONObject(index).getString("pid"));
							myEdit.putInt("index", index);
							myEdit.commit();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Intent prevIntent = getIntent();
						String callback = prevIntent.getStringExtra("callback");
						final int NEARPLACE_CAPTURE_CALLBACK = 3;
						final int NEARPLACE_SELECT_CALLBACK = 4;

						/*
						 * Throws exception when callback is null
						 */
						try {
							if (callback.equals("capture")) {
								setResult(NEARPLACE_CAPTURE_CALLBACK,
										prevIntent);
								finish();
							} else if (callback.equals("select")) {
								setResult(NEARPLACE_SELECT_CALLBACK, prevIntent);
								finish();
							}
						} catch (Exception e) {
							progg = ProgressDialog.show(NearByPlaces.this,
									"Please wait!", "Loading...", true);
							startActivityForResult(mIntent, 0);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			progg.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}

		final int NEARPLACE_CAPTURE_CALLBACK = 3;
		final int NEARPLACE_SELECT_CALLBACK = 4;
		Log.d("near by places", "requestcei jgjgkj  " + requestCode);
		try {
			if (requestCode == NEARPLACE_CAPTURE_CALLBACK) {
				Log.d("near by places", "yyyyyyyyyyyyyy");
				setResult(NEARPLACE_CAPTURE_CALLBACK, getIntent());
				finish();
			}
			if (requestCode == NEARPLACE_SELECT_CALLBACK) {
				Log.d("near by places", "nnmnnbnnnnnn");
				setResult(NEARPLACE_SELECT_CALLBACK, getIntent());
				finish();
			}
		} catch (Exception e) {
			Log.d("near by places", "njnnnnnnnnnnnnnnnnnnnkkkkk");
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
		prog = ProgressDialog.show(NearByPlaces.this, "Please wait",
				"Loading...", true);
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}

}
