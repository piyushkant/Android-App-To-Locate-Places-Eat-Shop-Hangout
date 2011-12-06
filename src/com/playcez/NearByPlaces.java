package com.playcez;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

public class NearByPlaces extends Activity{

	private JSONArray nearByPlacesArray;
	private Json_Fetch nearByPlaces;
	final String[] userCords = new String[2];
	private ProgressDialog prog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen2);
		
		Spinner sp = (Spinner) findViewById(R.id.spinner);
		sp.setVisibility(View.GONE);
		
		TextView text = (TextView) findViewById(R.id.text);
		text.setText("Places Nearby - Add Your Review");

		prog = ProgressDialog.show(this, "Please wait", "Loading...", true); 
		new DownloadActivityTask().execute();
	}	
	
	private class DownloadActivityTask extends AsyncTask<String, Integer, Long>{

		@Override
		protected Long doInBackground(String... params) {
			//Looper.prepare();
			try{
				download();
			} catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			try{
				createView();
			} catch(Exception e){
				e.printStackTrace();
			}
			prog.dismiss();
		}
	}

	
	public void download(){
		//Fetch JSON from internet
		try{
			SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
			String lat = myData.getString("lat", "");
			String lng = myData.getString("lng", "");
			String csid = myData.getString("csid", "9100401107066699");
			Log.d("csid", csid+"");
			nearByPlaces = new Json_Fetch("http://playcez.com/api_magicEndPoint.php?csid="+csid+"&id=5,6,7,9");
			Log.d("csid", csid+"");
			userCords[0] = lat;
			userCords[1] = lng;
		} catch(Exception e){
			e.printStackTrace();
		}
	
	}
	
	public void createView(){
		
		try {
			nearByPlacesArray=new JSONArray(nearByPlaces.json);
			//Parsing JSON
			for(int i =0;i<nearByPlacesArray.length();i++){
				try {
					Log.d("xxxxxx", i+"");
						final String title = nearByPlacesArray.getJSONObject(i).getString("name").toString();
						final String address = nearByPlacesArray.getJSONObject(i).getString("address").toString();
						
						final String[] placeCords = new String[2];
						placeCords[0] = nearByPlacesArray.getJSONObject(i).getString("lat").toString();
						placeCords[1] = nearByPlacesArray.getJSONObject(i).getString("lng").toString();
						
						final String distance = calculateDistance(userCords, placeCords);
						String resourceName = "m" + nearByPlacesArray.getJSONObject(i).getString("subcatid").toString();
						//String resourceName = "city_null";
						int resourceId = this.getResources().getIdentifier(resourceName, "drawable", this.getPackageName());
						//int id = Integer.getInteger(resourceId);
						
						Log.d("The resource id of the items in the list view", resourceName + "");
						//////////////////////////////START MAKING BUTTON/////////////////////////
						LinearLayout screen2_layout = (LinearLayout) findViewById(R.id.screen2_button_layout);
						
						Typeface face = Typeface.createFromAsset(getAssets(), "fonts/verdana.ttf");
						
						TextView btn = new TextView(this);
						btn.setText(Html.fromHtml("<big><b>" + " " + title + "</b></big><br/>" + " " + address + "<br/>" + " " + distance + ""));
						btn.setTag(title + "_button");

						LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, 1.0f);
						//params.gravity = Gravity.LEFT;
						btn.setLayoutParams(params);
						
						btn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.color_gradient)); 
						
						Drawable img = null;
						
						try{
							
							img = getApplicationContext().getResources().getDrawable(resourceId);
							
							
						} catch (Exception e){
							
							e.printStackTrace();
							
						}
						//img = BitmapFactory.decodeResource(getResources(), resourceId);
						//img = new PreviousResult().loadImageFromNetwork("http://playcez.com/images/markers/"+nearByPlacesArray.getJSONObject(i).getString("subcatid")+".png");
						if(img == null){
							resourceName = "city_null";
							int resId = this.getResources().getIdentifier(resourceName, "drawable", this.getPackageName());
							//img = BitmapFactory.decodeResource(getResources(), resourceId);
						}
						
						//BitmapDrawable bmp = new BitmapDrawable(getResources(),img);
						
						resourceName = "city_location";
						int resId = this.getResources().getIdentifier(resourceName, "drawable", this.getPackageName());
						
						Drawable img2 = null;
						
						try{
							
							img2 = getApplicationContext().getResources().getDrawable(resId);
							
						} catch(Exception e){
							
							e.printStackTrace();
							
						}
						
						
						
						//img = BitmapFactory.decodeResource(getResources(), resId);
						//BitmapDrawable bmp2 = new BitmapDrawable(getResources(), img);
						
						//btn.setCompoundDrawablesWithIntrinsicBounds(bmp2, null, bmp, null);
						btn.setCompoundDrawablesWithIntrinsicBounds(img2, null, img, null);
						
						
						//btn.setTypeface(face,Typeface.BOLD);
						btn.setTextColor(Color.GRAY);
						final int index = i;
						btn.setOnClickListener(new OnClickListener(){

							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent mIntent = new Intent(getApplicationContext(), Result.class);
								startActivity(mIntent);
								/*SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
								Editor myEdit = myData.edit();
								try {
									myEdit.putString("pid", nearByPlacesArray.getJSONObject(index).getString("pid"));
									myEdit.commit();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}*/
								
							}
							
						});
						
						screen2_layout.addView(btn);
					///////////////////////////////END MAKING BUTTON/////////////////////////////
						
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
	}
	
	}
	
	public double torad(double x) {
		return x * Math.PI / 180;
	}

		public String calculateDistance(String[] Cords1, String[] Cords2) {
			int R = 6371; // Radius of the earth in km
			
			double lat1 = Double.valueOf(Cords1[0]);
			double lng1 = Double.valueOf(Cords1[1]);
			
			double lat2 = Double.valueOf(Cords2[0]);
			double lng2 = Double.valueOf(Cords2[1]);
			
			double dLat = torad(lat2-lat1); // Javascript functions in radians
			double dLon = torad(lng2-lng1); 
			double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
			Math.cos(torad(lat1)) * Math.cos(torad(lat2)) * 
			Math.sin(dLon/2) * Math.sin(dLon/2); 
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			double d = R * c * 1000; // Distance in meters
			String unit;
			if(d>1000){
				d=d/1000;
			
				d=Math.round(d*10)/10;
			unit = "kms";
			}
			else{
			d = Math.round(d*10)/10;
			unit = "meters";
			}
			return Double.toString(d)+ " " + unit;
		}
	
	public void startHome(View view){
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}

}
