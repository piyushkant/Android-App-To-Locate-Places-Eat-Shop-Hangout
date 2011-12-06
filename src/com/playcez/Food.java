package com.playcez;


import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Food extends Activity{
	
	public JSONArray foodArray;
	Json_Fetch food;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen2);

		TextView text = (TextView) findViewById(R.id.text);
		text.setText("In the mood for?");
		
		//Fetch JSON from internet
		try{
			SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
			String csid = myData.getString("csid", "9100401107066699");
			Log.d("csid", csid);
			food = new Json_Fetch("http://playcez.com/api_getBucketList.php?csid="+csid);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			foodArray = new JSONArray(food.json);
			//Parsing JSON
			for(int i =0;i<foodArray.length();i++){
				try {
					SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
					String preference = myData.getString("preference", "");
					Log.d("Screen2", preference);
					if(foodArray.getJSONObject(i).getString("cat").toString().equals(preference)){
						final String title = foodArray.getJSONObject(i).getString("type").toString();
						int resourceId = this.getResources().getIdentifier(foodArray.getJSONObject(i).getString("icon").toString(), "drawable", this.getPackageName());
						Log.d("The resource id: ", "" + resourceId);
						//////////////////////////////START MAKING BUTTON/////////////////////////
						LinearLayout screen2_layout = (LinearLayout) findViewById(R.id.screen2_button_layout);
						
						Typeface face = Typeface.createFromAsset(getAssets(), "fonts/verdana.ttf");
						
						Button btn = new Button(this);
						btn.setText(title);
						btn.setTag(title + "_button");

						LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,130);
						btn.setLayoutParams(params);
						
						btn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.color_gradient));
						
						Drawable img = getApplicationContext().getResources().getDrawable(resourceId);
						btn.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
						
						btn.setTextSize(18);
						btn.setTypeface(face);
						
						btn.setOnClickListener(new OnClickListener(){

							public void onClick(View v) {
								
								//String className = removeSpaces(title);
								//StringBuilder classPath = new StringBuilder("com.playcez.");
								//classPath.append(className);
								
								Intent mIntent;
									//mIntent = new Intent(getApplicationContext(),Class.forName(classPath.toString()));
									mIntent = new Intent(getApplicationContext(), QuickBite.class);
									SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
									Editor myEdit = myData.edit();
									myEdit.putString("preference", title);
									myEdit.commit();
									startActivity(mIntent);
							}
						});
						
						screen2_layout.addView(btn);
					///////////////////////////////END MAKING BUTTON/////////////////////////////
					}
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
   
	public String removeSpaces(String title){
		StringTokenizer st = new StringTokenizer(title, " ", false);
		String newTitle = "";
		while(st.hasMoreElements())
			newTitle += st.nextElement();
		return newTitle;
	}

	public void startHome(View view){
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}
}
