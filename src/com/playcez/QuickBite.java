package com.playcez;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class QuickBite extends Activity{
	GridView grid_main;
	
	public JSONArray array;
	
	//List for title
	List<String> title_a = new ArrayList<String>();
	//List for image id
	List<Integer> imgid_a = new ArrayList<Integer>();
	
	String[] title ;	
	Integer[] imgid ;
	String preference;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen3);
		
		//JSON Fetch
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		String csid = myData.getString("csid", "9100401107066699");
		Json_Fetch a = new Json_Fetch("http://playcez.com/api_getBucketList.php?csid="+csid);


		try {
				array=new JSONArray(a.json);
		} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		}
		

		//Parsing array
		for(int i =0;i<array.length();i++){
			try {
				//if(array.getJSONObject(i).getString("cat").toString().equals("food")){
					preference = myData.getString("preference", "");
					Log.d("hereeee", array.length() + " " + i + preference + " " + array.getJSONObject(i).getString("type"));
					if(array.getJSONObject(i).getString("type").toString().equals(preference)){
						JSONObject object = array.getJSONObject(i);
						JSONArray bucket = object.getJSONArray("bucket");
						Log.d("length: ", bucket.length()+"");
						for(int j=0;j<bucket.length();j++)
						{
							Log.d("index: ", j+"");
							title_a.add(bucket.getJSONObject(j).getString("name"));
							//Log.d("title and icon", bucket.getJSONObject(j).getString("icon") + " " + title);
							int resourceId = this.getResources().getIdentifier("m"+bucket.getJSONObject(j).getString("icon").toString() + "_bw", "drawable", this.getPackageName());
							imgid_a.add(resourceId);
						}
					}
				//}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		
		Log.d("out", "out");
	title = new String[title_a.size()] ;	
	imgid = new Integer[imgid_a.size()];

	title_a.toArray(title);
	imgid_a.toArray(imgid);
	
	grid_main = (GridView)findViewById(R.id.GridView01);
	grid_main.setAdapter(new ImageAdapter(this));

	//Handling Clicks
	grid_main.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,
		    int position, long id) {
		  // When clicked, show a toast with the TextView text
			
			/*Toast imageToast = new Toast(getBaseContext());
			LinearLayout toastLayout = new LinearLayout(getBaseContext());
			toastLayout.setOrientation(LinearLayout.HORIZONTAL);
			ImageView image = new ImageView(getBaseContext());
			image.setImageResource(R.drawable.loading);
			toastLayout.addView(image);
			imageToast.setView(toastLayout);
			imageToast.setDuration(Toast.LENGTH_LONG);
			imageToast.show();
		  */
			
			Intent a =new Intent(getApplicationContext(),Result.class);
			for(int i =0; i<array.length(); i++){
				try {
					Log.d("hereeeee", title[position] + " " + i);
					JSONArray bucketArray = array.getJSONObject(i).getJSONArray("bucket");
					for(int j = 0; j < bucketArray.length(); j++){
						Log.d("check", title[position]+" "+bucketArray.getJSONObject(j).getString("name"));
						if(bucketArray.getJSONObject(j).getString("name").equals(title[position])){
							int bkid = bucketArray.getJSONObject(j).getInt("id");
							Log.d("bkid to be sent", bkid+"");
							a.putExtra("bkid", bkid);
							startActivityForResult(a,1);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		});
	}
	
	//Creating my own image adapter
	public class ImageAdapter extends BaseAdapter{
		Context mContext;
		public static final int ACTIVITY_CREATE = 10;
		public ImageAdapter(Context c){
			mContext = c;
		}
	
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
	
			View v;
			if(convertView==null){
				LayoutInflater li = getLayoutInflater();
				v = li.inflate(R.layout.grid_item, null);
				TextView tv = (TextView)v.findViewById(R.id.icon_text);
				System.out.println("Position now is:" + position);
				
				tv.setText(title[position]);
				
				ImageView iv = (ImageView)v.findViewById(R.id.icon_image);
				iv.setImageResource(imgid[position]);
			}
			else
			{
				v = convertView;
			}
			return v;
		}
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return title.length;
		}
	}
	
	public void startHome(View view){
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}
}