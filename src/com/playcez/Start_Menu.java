package com.playcez;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.playcez.bean.StorageManager;

/**
 * The Class Start_Menu.
 */
public class Start_Menu extends Activity implements LocationListener{

	/** The eat_button. */
	private Button eat_button;
	
	/** The shop_button. */
	private Button shop_button;
	
	/** The hangout_button. */
	private Button hangout_button;
	
	private static String[] Suggestions;
	
	private ImageButton nearByPlaces;
	
	private TextView userArea;
	
	private StorageManager sm;
	
	private int[] ids = {};//{R.drawable.androidtutorial1, R.drawable.androidtutorial2, R.drawable.androidtutorial3, R.drawable.androidtutorial4};
	
	public JSONArray foodArray;
	Json_Fetch food;
	
	GridView gridView;
	//List for title
			
		String[] title = {"Food", "Shop", "Hangout", "Offers"};
		//String preference;
		Integer[] mThumbIds = {
				   R.drawable.start_food,
				   R.drawable.start_shop,
				   R.drawable.start_hangout,
				   R.drawable.start_offers
		};
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_menu);
		
		Log.d("Start_Menu", "Start of activity");
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		firstTimeAlert(0);
		
		sm = new StorageManager(this);
		sm.open();
		try{
			SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
			
			String preferedLocation = null;
			
			try{
				
				 preferedLocation = myData.getString("userPreferedStreet", null);
				
			
			if(preferedLocation != null || preferedLocation.length() != 0 || preferedLocation.equals("") || !(preferedLocation.length() > 1)){
				Log.d("Start_Menu", "User Prefered Street is not set");
				setUpLocation();
			}
			
			} catch(Exception e){
				e.printStackTrace();
				
				Log.d("hey", "hey");
				setUpLocation();
			}
			
			//setUpLocation();
			storeNearestStreets();
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
			String prevUserStreet = myData.getString("prevUserStreet", "");
			String currUserStreet = myData.getString("userStreet", "");
			
			userArea = (TextView) findViewById(R.id.userarea);
			userArea.setText(" " + currUserStreet);
			
			Log.d("streets", prevUserStreet + " " + prevUserStreet.length() + " " + currUserStreet + " " + currUserStreet.length());
			if(currUserStreet.equals(prevUserStreet)){
				Log.d("streets", "coming equal");
				foodArray = sm.fetchJsonFromDatabase();
			} else{
				food = new Json_Fetch("http://playcez.com/api_getBucketList.php");
				Log.d("streets", "coming unequal");
				foodArray=new JSONArray(food.json);
				sm.storeJsonInDatabase(foodArray);
				
				Editor myEdit = myData.edit();
				myEdit.putString("prevUserStreet", currUserStreet);
				myEdit.commit();
			}
			sm.close();
				//Parsing JSON
				Suggestions = new String[100];
				
				for(int i=0;i<100;i++){
					Suggestions[i] = "";
				}
				
				for(int i =0;i<foodArray.length();i++){
					int size = foodArray.getJSONObject(i).getJSONArray("bucket").length();
					for(int j = 0; j < size; j++){
						//Log.d("Start_Menu.java", "Suggestions[i]");
						Suggestions[i*foodArray.length()+j] = foodArray.getJSONObject(i).getJSONArray("bucket").getJSONObject(j).getString("name");
					}
				}
				
		}catch(Exception e){
			e.printStackTrace();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,Suggestions);
		
		final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.suggestions_list);
		
		textView.setThreshold(1);
		textView.setAdapter(adapter);
			
		textView.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				for(int i = 0; i < foodArray.length(); i++){
					try {
						Log.d("Start_Menu", "OnItemClickListener");
						String name = textView.getText().toString();
						for(int j = 0; j < foodArray.getJSONObject(i).getJSONArray("bucket").length(); j++){
							Log.d("Start_Menu", "OnItemClickListener" + " " + name);
							if(foodArray.getJSONObject(i).getJSONArray("bucket").getJSONObject(j).getString("name").equals(name)){
								int bkid = foodArray.getJSONObject(i).getJSONArray("bucket").getJSONObject(j).getInt("id");
								Intent a = new Intent(getApplicationContext(),Result.class);
								Log.d("bkid to be sent", bkid+"");
								a.putExtra("bkid", bkid);
								startActivityForResult(a,1);
							}
						}
					} catch (JSONException e) {
						Log.d("Start_Menu.java", "Exception");
						e.printStackTrace();
					}
				}
			}
			
		});

		//TextView locality = (TextView) findViewById(R.id.userarea);
		Button locality = (Button) findViewById(R.id.changeButton);
		locality.setOnClickListener(new OnClickListener(){

			public void onClick(View v){
				startActivityForResult(new Intent(getApplicationContext(), ChangeLocation.class),1);
			}
			
		});
			
		nearByPlaces = (ImageButton) findViewById(R.id.nearest_places_ib);
		nearByPlaces.setOnClickListener(new OnClickListener(){

			public void onClick(View v){
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(getApplicationContext(),NearByPlaces.class);
				startActivity(mIntent);
			}
		});
			
		gridView = (GridView)findViewById(R.id.GridView02);
		gridView.setAdapter(new ImageAdapter(this));
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
				
				Intent mIntent;
				SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
				Editor myEdit = myData.edit();
				
				switch(position){
				case 0:
					mIntent = new Intent(getApplicationContext(),NearFood.class );
					myEdit.putString("preference", "food");
					myEdit.commit();
					try{
						startActivity(mIntent);
					} catch(Exception e){
						e.printStackTrace();
						Log.e("Start Menu", "Start Activity Failed!");
					}
					break;
				case 1:
					mIntent = new Intent(getApplicationContext(),NearFood.class );
					myEdit.putString("preference", "shop");
					myEdit.commit();
					try{
						startActivity(mIntent);
					} catch(Exception e){
						e.printStackTrace();
						Log.e("Start Menu", "Start Activity Failed!");
					}
					break;
				case 2:
					mIntent = new Intent(getApplicationContext(),NearFood.class );
					myEdit.putString("preference", "hangout");
					myEdit.commit();
					try{
						startActivity(mIntent);
					} catch(Exception e){
						e.printStackTrace();
						Log.e("Start Menu", "Start Activity Failed!");
					}
					break;
				}
			}
		});
	}
	
	  public class ImageAdapter extends BaseAdapter {
		     
		     private Context mContext;

		  public ImageAdapter(Context c) {
		   // TODO Auto-generated constructor stub
		   mContext = c;
		  }

		  public int getCount() {
		   // TODO Auto-generated method stub
		   return mThumbIds.length;
		  }

		  public Object getItem(int arg0) {
		   // TODO Auto-generated method stub
		   return mThumbIds[arg0];
		  }

		  public long getItemId(int arg0) {
		   // TODO Auto-generated method stub
		   return arg0;
		  }

		  public View getView(int position, View convertView, ViewGroup parent) {
		   // TODO Auto-generated method stub
		   
		   View grid;
		    
		   if(convertView==null){
		    grid = new View(mContext);
		    LayoutInflater inflater=getLayoutInflater();
		    grid=inflater.inflate(R.layout.grid_item, parent, false);
		   }else{
		    grid = (View)convertView;
		   }
		   
		   ImageView imageView = (ImageView)grid.findViewById(R.id.icon_image);
		   TextView textView = (TextView)grid.findViewById(R.id.icon_text);
		   imageView.setImageResource(mThumbIds[position]);
		   textView.setText(title[position]);

		   return grid;
		  }
	  }
		  
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 
		super.onActivityResult(requestCode, resultCode, data);
		
		TextView t = (TextView)findViewById(R.id.userarea);
		
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		
		storeNearestStreets();
		
		t.setText(" "+ myData.getString("userPreferedStreet", ""));
		
	}
		
	public void updateTutorial(final int i){
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		final AlertDialog alert = dialog.create();
		dialog.setTitle("Welcome to Playcez");
		
			Log.d("here", "inside for loop");
			LayoutInflater factory = LayoutInflater.from(this);
			final View view = factory.inflate(R.layout.firsttimealert, null);
			
			Drawable drawable = getResources().getDrawable(ids[i]);
			view.setBackgroundDrawable(drawable);
			
			dialog.setView(view);
		
		if(i != 0){
			final int prevIndex = i-1;
			dialog.setPositiveButton("Previous", new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialogInterface, int which) {
					updateTutorial(prevIndex); 
				}
			});
		}		
		
		String skipName= "Skip";
		Log.d("check", ids.length + " "+ i);
		if(i != ids.length-1){
			final int nextIndex = i+1;
			dialog.setNeutralButton("Next", new DialogInterface.OnClickListener(){
	
				public void onClick(DialogInterface dialogInterface, int which) {
					updateTutorial(nextIndex);
				}
			});
		} else{
			skipName = "Finish";
		}
		
		dialog.setNegativeButton(skipName, new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface dialogInterface, int which) {
				dialog.create().hide();
			}
		});
		       
		dialog.show();

	}
	
	public void firstTimeAlert(final int i){
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		boolean firstTime = myData.getBoolean("firstTime", true);
		
		if(firstTime){
			updateTutorial(i);
		}
		Editor myEdit = myData.edit();
		myEdit.putBoolean("firstTime", false);
		myEdit.commit();
	}
	
	public void setUpLocation(){
		try{
			
			LocationManager mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			
			String latitude = "";
			String longitude = "";
			
    		
    		if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
    	    
    			String locationprovider = mLocationManager.getBestProvider(criteria,true);
    			
    			Location mLocation = mLocationManager.getLastKnownLocation(locationprovider);
    			
    			latitude = mLocation.getLongitude()+"";
    			
    			longitude = mLocation.getLatitude()+"";
    			
    	    } else{
    	    	
    	    	Location mLocation = mLocationManager.getLastKnownLocation(mLocationManager.NETWORK_PROVIDER);
    	    	
    			latitude = mLocation.getLongitude()+"";
    			
    			longitude = mLocation.getLatitude()+"";
    	    	
    	    }
				
    							
			SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
			
			Editor myEdit = myData.edit();
			
			myEdit.putString("lng",latitude);
			myEdit.putString("lat", longitude);
			myEdit.commit();
	        
			
	        
        } catch(Exception e){
        	e.printStackTrace();
        }

	}

	/**
	 * Store nearest streets.
	 */
	public void storeNearestStreets(){
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		Editor myEdit = myData.edit();
		
		String lat = myData.getString("lat", "");
		String lng = myData.getString("lng", "");
		
		Log.d("lat and long", lat +  " " + lng);
		
		Json_Fetch csidArray = new Json_Fetch("http://playcez.com/api_getNearestCrossStreets.php?lat="+lat+"&lng="+lng);
		try {
			JSONArray streets = new JSONArray(csidArray.json);
			
			String csid = streets.getJSONObject(0).getString("cs_id");
			String street = streets.getJSONObject(0).getString("label");
			String preferedStreet = myData.getString("userPreferedStreet", null);
			
			if(preferedStreet != null)
				myEdit.putString("userStreet", preferedStreet);
			else
				myEdit.putString("userStreet", street);
			myEdit.putString("csid", csid);
			myEdit.commit();
			
		} catch (JSONException e) {
			
			e.printStackTrace();
			
		}
	}
	
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
	}

	public void onProviderDisabled(String provider) {
		// 
		//Toast.makeText(getApplicationContext(), "GPS is Disabled", Toast.LENGTH_LONG).show();
	}

	public void onProviderEnabled(String provider) {
		// 
		//Toast.makeText(getApplicationContext(), "GPS is Enabled", Toast.LENGTH_LONG).show();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// 
		//Toast.makeText(getApplicationContext(), "GPS status changed", Toast.LENGTH_LONG).show();
	}
	
}