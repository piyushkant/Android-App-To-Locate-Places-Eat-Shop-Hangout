/*
 * 
 */
package com.playcez;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.maps.MapView;

// TODO: Auto-generated Javadoc
/**
 * The Class Maps.
 */
public class Maps extends Activity {

	/** The map. */
	protected MapView map;

	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);

		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);

		String latitude1 = myData.getString("user_lat", "");
		String longitude1 = myData.getString("user_lng", "");

		Intent mIntent = getIntent();
		String latitude2 = mIntent.getStringExtra("latitude2");
		String longitude2 = mIntent.getStringExtra("longitude2");

		String url = "http://maps.google.com/maps?saddr=" + latitude1 + ","
				+ longitude1 + "&daddr=" + latitude2 + "," + longitude2;
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse(url));
		startActivity(intent);
		finish();

	}
}