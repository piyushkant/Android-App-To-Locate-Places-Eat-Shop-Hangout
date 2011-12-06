package com.playcez;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class ChangeLocation extends Activity {

	private ProgressDialog prog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen2);
		
		Spinner sp = (Spinner) findViewById(R.id.spinner);
		sp.setVisibility(View.GONE);
		
		TextView text = (TextView) findViewById(R.id.text);
		text.setText("Choose Locality");
		
		ImageButton refresh = (ImageButton) findViewById(R.id.mobile_refresh_button);
		refresh.setVisibility(View.VISIBLE);

		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		Editor myEdit = myData.edit();

		String lat = myData.getString("lat", "");
		String lng = myData.getString("lng", "");

		Log.d("lat and long", lat + " " + lng);

		Json_Fetch csidArray = new Json_Fetch(
				"http://playcez.com/api_getNearestCrossStreets.php?lat=" + lat
						+ "&lng=" + lng);
		
		ListView l = (ListView) findViewById(R.id.nearbylocalities);
		
		final Geocoder code = new Geocoder(this);
		
		try {
		
			JSONArray streets = new JSONArray(csidArray.json);

			addDynamically(streets);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}

	}

	public void addDynamically(JSONArray streets) {
		for (int i = 0; i < streets.length(); i++) {
			try {
				final String label = streets.getJSONObject(i)
						.getString("label");

				LinearLayout screen2_layout = (LinearLayout) findViewById(R.id.screen2_button_layout);

				Typeface face = Typeface.createFromAsset(getAssets(),
						"fonts/verdana.ttf");

				TextView btn = new TextView(this);
				btn.setText(label);

				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, 100,
						1.0f);
				// params.gravity = Gravity.LEFT;
				btn.setLayoutParams(params);

				btn.setBackgroundDrawable(this.getResources().getDrawable(
						R.drawable.color_gradient));

				// btn.setTypeface(face,Typeface.BOLD);
				
				btn.setTextColor(Color.BLACK);
				
				final int index = i;
				
				final Geocoder code = new Geocoder(this);
				
				btn.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						//
						List<Address> addresses;
						try {
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
	
	public void refresh(View v){
		
		prog = ProgressDialog.show(this, "Please wait", "Refreshing...", true);
		
		new DownloadActivityTask().execute();
		
	}

	public void startHome(View v){
		
		startActivity(new Intent(this, Start_Menu.class));
		
		
	}
	private class DownloadActivityTask extends AsyncTask<String, Integer, Long>{

		@Override
		protected Long doInBackground(String... params) {
			Looper.prepare();
			try{
				new Start_Menu().setUpLocation();
				new Start_Menu().storeNearestStreets();
			} catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			try{
				prog.dismiss();
			} catch(Exception e){
				e.printStackTrace();
			}
			prog.dismiss();
		}
	}

}
