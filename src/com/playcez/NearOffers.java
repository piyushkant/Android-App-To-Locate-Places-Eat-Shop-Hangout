/*
 * 
 */
package com.playcez;

import static com.playcez.Start_Menu.prog;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import twitter4j.Category;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class NearOffers.
 */
public class NearOffers extends Activity {

	/** The near by places array. */
	static private JSONArray nearByPlacesArray;

	/** The near by places. */
	private Json_Fetch nearByPlaces;

	/** The user cords. */
	final String[] userCords = new String[2];

	/** The prog. */
	private ProgressDialog progg;

	/** The bucket name list. */
	static List<String> bucketNameList;

	/** The bucket id list. */
	static List<String> bucketIdList;

	/** The cat name list. */
	static List<String> catNameList;

	/** The cat id list. */
	static List<String> catIdList;

	/** The bucket. */
	private Json_Fetch bucket;

	/** The bucket array. */
	static private JSONArray bucketArray;

	/** The food array. */
	static private JSONArray foodArray;

	/** The is there. */
	boolean isThere = true;

	/** The cat id. */
	static String catId = "Show All";

	/** The deal size. */
	static int dealSize;

	/** The no offers. */
	static TextView noOffers;
	String resourceName;

	static Display display;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offerview);

		noOffers = (TextView) findViewById(R.id.noOffers);
		noOffers.setVisibility(View.GONE);

		bucketNameList = new ArrayList<String>();
		bucketIdList = new ArrayList<String>();
		catNameList = new ArrayList<String>();
		catNameList.add("Show All");

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
			// TODO Auto-generated method stub
			try {
				createView(catId);
				addItemsOnSpinner();
				addListenerOnSpinnerItemSelection();
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
		try {
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			String lat = myData.getString("lat", "");
			String lng = myData.getString("lng", "");
			String csid = myData.getString("csid", "");
			Log.d("csid", csid + "");
			nearByPlaces = new Json_Fetch(
					"https://playcez.com/api_ogDeals.php?csid=" + csid,
					myData.getString("uid", ""), myData.getString(
							"playcez_token", ""));
			nearByPlacesArray = new JSONArray(nearByPlaces.json);
			Log.d("csid", csid + "");
			userCords[0] = lat;
			userCords[1] = lng;

			for (int i = 0; i < nearByPlacesArray.length(); i++) {
				bucketNameList.add(nearByPlacesArray.getJSONObject(i)
						.getString("catName").toString());
			}

			for (int i = 0; i < bucketNameList.size(); i++) {
				for (int j = 0; j < catNameList.size(); j++) {
					if (catNameList.get(j).equals(bucketNameList.get(i))) {
						isThere = true;
						break;
					}

					else if ((!catNameList.get(j).equals(bucketNameList.get(i)))
							&& (j == catNameList.size() - 1)) {
						isThere = false;
					}
				}

				if (isThere == false) {
					catNameList.add(bucketNameList.get(i));
				}

			}

			dealSize = bucketNameList.size();

			Log.d("dealSizezzzzzzzzzzz:-", dealSize + "");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the view.
	 * 
	 * @param catID
	 *            the cat id
	 */
	public void createView(String catID) {

		LinearLayout screen2_layout = (LinearLayout) findViewById(R.id.screen2_button_layout);
		screen2_layout.removeAllViews();

		try {
			Log.d("catId", catId + "");
			Log.d("nearby", nearByPlacesArray.length() + "");
			Log.d("nearby",
					nearByPlacesArray.getJSONObject(0).getString("title")
							.toString()
							+ "");

			for (int i = 0; i < nearByPlacesArray.length(); i++) {
				try {
					try {
						if (!catId.equals(bucketNameList.get(i))
								&& !catId.equals("Show All")) {
							continue;
						}
					} catch (Exception e) {

					}

					final String title = nearByPlacesArray.getJSONObject(i)
							.getString("title").toString();
					final String address = nearByPlacesArray.getJSONObject(i)
							.getString("address").toString();
					final String buyUrl = nearByPlacesArray.getJSONObject(i)
							.getString("buyurl").toString();
					final String offerValue = nearByPlacesArray
							.getJSONObject(i).getString("value").toString();
					final String offerSavings = nearByPlacesArray
							.getJSONObject(i).getString("savings").toString();
					final String offerPrice = nearByPlacesArray
							.getJSONObject(i).getString("price").toString();
					final String imageUrl = nearByPlacesArray.getJSONObject(i)
							.getString("dealimage").toString();
					final String startDate = nearByPlacesArray.getJSONObject(i)
							.getString("startdate").toString();
					final String expiryDate = nearByPlacesArray
							.getJSONObject(i).getString("expirydate")
							.toString();
					final String dealBanner = nearByPlacesArray
							.getJSONObject(i).getString("dealSourceBanner")
							.toString();
					final String offerLat = nearByPlacesArray.getJSONObject(i)
							.getString("lat").toString();
					final String offerLng = nearByPlacesArray.getJSONObject(i)
							.getString("lng").toString();
					final String dealid = nearByPlacesArray.getJSONObject(i)
							.getString("dealid").toString();
					final String crossStreet = nearByPlacesArray
							.getJSONObject(i).getString("cross_street")
							.toString();
					final String offerDiscount = nearByPlacesArray
							.getJSONObject(i).getString("discount").toString();

					final String[] placeCords = new String[2];
					placeCords[0] = nearByPlacesArray.getJSONObject(i)
							.getString("lat").toString();
					placeCords[1] = nearByPlacesArray.getJSONObject(i)
							.getString("lng").toString();
					final String distance = calculateDistance(userCords,
							placeCords);

					display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
							.getDefaultDisplay();

					resourceName = "offerpatch";

					if (display.getHeight() < 400) {
						resourceName = "smallofferpatch";
					}

					int resourceId = this.getResources().getIdentifier(
							resourceName, "drawable", this.getPackageName());

					final BitmapDrawable bmd = writeOnDrawable(resourceId,
							offerDiscount+"%"); // /////////////////**********************

					Typeface face = Typeface.createFromAsset(getAssets(),
							"fonts/verdana.ttf");

					TextView btn = new TextView(this);
					btn.setText(Html.fromHtml("" + " " + title + "<br/>" + " "
							+ distance + "\t" + crossStreet + ""));
					btn.setTag(title + "_button");

					LayoutParams params = new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT, 1.0f);

					btn.setLayoutParams(params);

					btn.setBackgroundDrawable(this.getResources().getDrawable(
							R.drawable.color_gradient));

					Drawable img = null;

					try {

						// img = getApplicationContext().getResources()
						// .getDrawable(resourceId);
						img = drawText(getApplicationContext().getResources()
								.getDrawable(resourceId), "10");

					} catch (Exception e) {

						e.printStackTrace();

					}

					if (img == null) {
						resourceName = "city_null";
						int resId = this.getResources()
								.getIdentifier(resourceName, "drawable",
										this.getPackageName());
					}

					resourceName = "city_location";
					int resId = this.getResources().getIdentifier(resourceName,
							"drawable", this.getPackageName());

					Drawable img2 = null;

					try {

						img2 = getApplicationContext().getResources()
								.getDrawable(resId);

					} catch (Exception e) {

						e.printStackTrace();

					}

					btn.setCompoundDrawablesWithIntrinsicBounds(bmd, null, // ???????????????????????????????????/???/
							null, null);

					btn.setTextColor(Color.GRAY);
					final int index = i;
					btn.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							SharedPreferences myData = getSharedPreferences(
									"myData", MODE_PRIVATE);
							Editor myEdit = myData.edit();

							myEdit.putString("title", title);
							myEdit.putString("offerValue", offerValue);
							myEdit.putString("offerSavings", offerSavings);
							myEdit.putString("offerPrice", offerPrice);
							myEdit.putString("buyUrl", buyUrl);
							myEdit.putString("imageUrl", imageUrl);
							myEdit.putString("address", address);
							myEdit.putString("startDate", startDate);
							myEdit.putString("expiryDate", expiryDate);
							myEdit.putString("dealBanner", dealBanner);
							myEdit.putString("offerLat", offerLat);
							myEdit.putString("offerLng", offerLng);
							myEdit.putString("dealid", dealid);
							myEdit.commit();

							startActivity(new Intent(getApplicationContext(),
									OfferPage.class));
						}

					});

					screen2_layout.addView(btn);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (dealSize == 0) {
			noOffers.setVisibility(View.VISIBLE);
		}
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
		prog = ProgressDialog.show(NearOffers.this,
				"Please wait", "Loading...", true);
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}

	/**
	 * Adds the items on spinner.
	 */
	public void addItemsOnSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, catNameList);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

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
				catId = parent.getItemAtPosition(position).toString();
				createView(catId);
				// Log.d("catId", catId + "");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}
		});
	}

	Drawable drawText(Drawable image, String imageText) {
		// Store our image size as a constant
		final int IMAGE_WIDTH = image.getIntrinsicWidth();
		final int IMAGE_HEIGHT = image.getIntrinsicHeight();

		// You can also use Config.ARGB_4444 to conserve memory or ARGB_565 if
		// you don't have any transparency.
		Bitmap canvasBitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT,
				Bitmap.Config.ARGB_8888);
		// Create a canvas, that will draw on to canvasBitmap. canvasBitmap is
		// currently blank.
		Canvas imageCanvas = new Canvas(canvasBitmap);
		// Set up the paint for use with our Canvas
		Paint imagePaint = new Paint();
		imagePaint.setTextAlign(Align.CENTER);
		imagePaint.setTextSize(20f);

		// Draw the image to our canvas
		image.draw(imageCanvas);
		// Draw the text on top of our image
		imageCanvas.drawText(imageText, IMAGE_WIDTH / 2, IMAGE_HEIGHT / 2,
				imagePaint);
		// This is the final image that you can use
		BitmapDrawable finalImage = new BitmapDrawable(canvasBitmap);
		return finalImage;
	}

	public BitmapDrawable writeOnDrawable(int drawableId, String text) {

		Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
				.copy(Bitmap.Config.ARGB_8888, true);

		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.WHITE);

		if (display.getHeight() <= 400) {
			paint.setTextSize(12);

			Canvas canvas = new Canvas(bm);
			canvas.drawText(text, (bm.getWidth() / 2) - 9, bm.getHeight() / 2,
					paint);

			return new BitmapDrawable(bm);
		} else {
			paint.setTextSize(40);

			Canvas canvas = new Canvas(bm);
			canvas.drawText(text, (bm.getWidth() / 2) - 33, bm.getHeight() / 2,
					paint);

			return new BitmapDrawable(bm);
		}
	}
}
