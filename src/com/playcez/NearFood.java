package com.playcez;

import java.util.ArrayList;
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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

public class NearFood extends Activity{

	static private JSONArray bucketArray;
	static private JSONArray foodArray;
	static List<String> bucketNameList;
	static List<String> bucketIdList;
	private Json_Fetch bucket;
	private Json_Fetch food;
	final String[] userCords = new String[2];
	private ProgressDialog prog;
	private String id;
	static String bkid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen2);
		
		bkid = "0";
		Log.d("xxxxxxxxxxxx", bkid+"");
		prog = ProgressDialog.show(this, "Please wait", "Loading...", true);
		Log.d("xxxxxxxxxxxx", bkid+"");
		new DownloadActivityTask().execute();
		Log.d("xxxxxxxxxxxx", bkid+"");
		addItemsOnSpinner ();
		//bkid = bucketNameList.get(3);
		Log.d("xxxxxxxxxxxx", bkid+"");
		addListenerOnSpinnerItemSelection();
		Log.d("xxxxxxxxxxxx", bkid+"");
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
				createView(bkid);
			} catch(Exception e){
				e.printStackTrace();
			}
			prog.dismiss();
		}
	}
	
	public void download(){
		
		try{
			SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
			String lat = myData.getString("lat", "");
			String lng = myData.getString("lng", "");
			String csid = myData.getString("csid", "9100401107066699");
			String preference = myData.getString("preference", "");
			id = prefId(preference);
			Log.d(preference + " " + id, "hereeeeeeeee");
			
			food = new Json_Fetch("http://playcez.com/api_magicEndPoint.php?csid="+csid+"&id="+id); 
			Log.d("csid", csid+"");
			userCords[0] = lat;
			userCords[1] = lng;
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void createView(String bkid){		
		try {
			LinearLayout screen2_layout = (LinearLayout) findViewById(R.id.screen2_button_layout);;
			screen2_layout.removeAllViews();
			
			foodArray = new JSONArray(food.json);
			
			for(int i =0;i<foodArray.length();i++){
				
				try {
					if (!bkid.equals(foodArray.getJSONObject(i).getString("buckets").toString())
							&& !bkid.equals("0"))
					{continue;}
						final String title = foodArray.getJSONObject(i).getString("name").toString();
						final String address = foodArray.getJSONObject(i).getString("address").toString();
						//final String bkid = foodArray.getJSONObject(i).getString("buckets").toString();
						
						final String[] placeCords = new String[2];
						placeCords[0] = foodArray.getJSONObject(i).getString("lat").toString();
						placeCords[1] = foodArray.getJSONObject(i).getString("lng").toString();
						
						final String distance = calculateDistance(userCords, placeCords);
						String resourceName = "m" + foodArray.getJSONObject(i).getString("subcatid").toString();
						//String resourceName = "city_null";
						int resourceId = this.getResources().getIdentifier(resourceName, "drawable", this.getPackageName());
						//int id = Integer.getInteger(resourceId);
						
						Log.d("The resource id of the items in the list view", resourceName + "");
						//////////////////////////////START MAKING BUTTON/////////////////////////
						
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
								Intent mIntent = new Intent(getApplicationContext(), ReviewBySmily.class);
								SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
								Editor myEdit = myData.edit();
								try {
									myEdit.putString("pid", foodArray.getJSONObject(index).getString("pid"));
									myEdit.commit();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								startActivity(mIntent);
							}
							
						});
						
						screen2_layout.addView(btn);
					///////////////////////////////END MAKING BUTTON/////////////////////////////
						
				}
					catch (JSONException e) {
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
	
		
	public void addItemsOnSpinner ()
	{
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		bucketNameList = new ArrayList<String>();
		bucketIdList = new ArrayList<String>();
				
		bucketNameList.add("Show All");
		bucketIdList.add("0");
		
		try{
			SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
			String csid = myData.getString("csid", "9100401107066699");
			String preference = myData.getString("preference", "");
			
			bucket = new Json_Fetch("http://playcez.com/api_getBucketList.php?csid="+csid);
			bucketArray = new JSONArray(bucket.json);
			
			for(int i =0;i<bucketArray.length();i++){
				if(bucketArray.getJSONObject(i).getString("cat").toString().equals(preference)){
					int size = bucketArray.getJSONObject(i).getJSONArray("bucket").length();
					for(int j = 0; j < size; j++){
						//Log.d("Start_Menu.java", "Suggestions[i]");
						bucketNameList.add(bucketArray.getJSONObject(i).getJSONArray("bucket").getJSONObject(j).getString("name"));
						bucketIdList.add(bucketArray.getJSONObject(i).getJSONArray("bucket").getJSONObject(j).getString("id"));
					}	
				}
					
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, bucketNameList);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner.setAdapter(dataAdapter);
			}
			} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void addListenerOnSpinnerItemSelection() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener(){
			 @Override
			    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			        // your code here
				 	SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
					Editor myEdit = myData.edit();
					myEdit.putString("bucketName", parent.getItemAtPosition(position).toString());
					myEdit.commit();
					Log.d("bucketName", parent.getItemAtPosition(position).toString()+"");
					
					for (int j=0; j<bucketNameList.size(); j++)
					{
						//Log.d(bucketName, bucketNameList.get(j));
						if (parent.getItemAtPosition(position).toString().equals(bucketNameList.get(j)))
						{
							bkid = bucketIdList.get(j);
							break;
						}
					}
					
					createView(bkid);
			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parentView) {
			        // your code here
			    }
		});
	  }
		
	public String prefId (String pref)
	{
		String id = "9";
		
		if (pref.equals("food"))
		{
			id = "9";
		}
		
		else if (pref.equals("shop"))
		{
			id = "5";
		}
		
		else if (pref.equals("hangout"))
		{
			id = "6,7";
		}
		
		return id;
	}
		
	public void startHome(View view){
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}

}

