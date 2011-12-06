package com.playcez;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class Tips extends ListActivity {
	
private LayoutInflater mInflater;
private Vector<RowData> data;
RowData rd;

//List for name
List<String> title_a = new ArrayList<String>();

//List for tip
List<String> details_a = new ArrayList<String>();

//List for image id
List<Integer> imgid_a = new ArrayList<Integer>();

String[] title ;
String[] detail; 
Integer[] imgid;

public JSONArray array;
private ProgressDialog progDialog;

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.tips);

	
	try {
		new Result().setButtonClickListeners();
	} catch (JSONException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	

//	progDialog = ProgressDialog.show(Tips.this, "Please wait", "Loading...", true);
	try{
	//	Json_Fetch a=new Json_Fetch("http://10.0.2.2/tip.html");
		Intent mIntent = getIntent();
		String pid = mIntent.getStringExtra("pid");
		Log.d("pid of the place", pid);
	    String rev_req="http://playcez.com/api_getPlaceReviews.php?pid="+pid+"&page=1";
	    
	    //Fetching JSON
	    Json_Fetch a=new Json_Fetch(rev_req);
	   try {
	   		array = new JSONArray(a.json);
	   } catch (Exception e1) {
	   		// 
	   		e1.printStackTrace();
	   }
	
	//Parsing JSON
	   if(array.length() == 0){
		   TextView sorryMsg = (TextView) findViewById(R.id.sorrymsg);
		   sorryMsg.setText("Sorry, no tips found!");
	   }
	   for(int i=0;i<array.length();i++){
	   	try {
	   			title_a.add(array.getJSONObject(i).getString("uname").toString());
	   			int resourceId = this.getResources().getIdentifier("icon", "drawable", this.getPackageName());
	   			imgid_a.add(resourceId);
	   			details_a.add(array.getJSONObject(i).getString("review").toString());
	   			//Toast.makeText(this, array.getJSONObject(i).getString("review").toString(), Toast.LENGTH_SHORT).show();
	   	} catch (Exception e) {
	   		e.printStackTrace();
	   	}
	   }
	
	   Log.d("tips","Sizes : "+title_a.size()+ "  "+imgid_a.size()+"   "+details_a.size());
	   //Toast.makeText(this, "Sizes : "+title_a.size()+ "  "+imgid_a.size()+"   "+details_a.size(), Toast.LENGTH_SHORT).show();
	   title = new String[title_a.size()] ;	
	   imgid = new Integer[imgid_a.size()];
	   detail = new String[details_a.size()];
	   
	   title_a.toArray(title);
	   imgid_a.toArray(imgid);
	   details_a.toArray(detail);
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	
		//Inflating Layout
		mInflater = (LayoutInflater) getSystemService(
		Activity.LAYOUT_INFLATER_SERVICE);
		data = new Vector<RowData>();
		for(int i=0;i<title.length;i++){
		try {
		 	rd = new RowData(i,title[i],detail[i]);
		    } catch (Exception e) {
		    	e.printStackTrace();
		   }
		   data.add(rd);
		}
		CustomAdapter adapter = new CustomAdapter(this, R.layout.tips_item,
		        R.id.title, data);
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
}
   
private class RowData {
   protected int mId;
   protected String mTitle;
   protected String mDetail;
   RowData(int id,String title,String detail){
	   mId=id;
	   mTitle = title;
	   mDetail=detail;
	}
   
   
   @Override
   public String toString() {
           return mId+" "+mTitle+" "+mDetail;
   }
}
       
       //creating my adapter
private class CustomAdapter extends ArrayAdapter<RowData> {
  public CustomAdapter(Context context, int resource,
		int textViewResourceId, List<RowData> objects) {               
			super(context, resource, textViewResourceId, objects);
		}

		@Override
       public View getView(int position, View convertView, ViewGroup parent) {   
       ViewHolder holder = null;
       TextView title = null;
       TextView detail = null;
       ImageView i11=null;
       RowData rowData= getItem(position);
       if(null == convertView){
            convertView = mInflater.inflate(R.layout.tips_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
		}
             holder = (ViewHolder) convertView.getTag();
             title = holder.gettitle();
             title.setText(rowData.mTitle);
             Log.d("title", rowData.mTitle);
             detail = holder.getdetail();
             detail.setText(rowData.mDetail);                                                     
             i11=holder.getImage();
             
             String fbuid = null;
             
             for(int i = 0; i < array.length(); i++){
            	 try {
					if(array.getJSONObject(i).getString("uname").equals(rowData.mTitle))
						 fbuid = array.getJSONObject(i).getString("fbuid");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
             }
             
             i11.setImageBitmap(new PreviousResult().loadImageFromNetwork("http://graph.facebook.com/"+fbuid+"/picture?type=square")); //.setImageResource(imgid[rowData.mId]);             
             return convertView;
	}
    
	private class ViewHolder {
		private View mRow;
		private TextView title = null;
		private TextView detail = null;
		private ImageView i11=null; 
		
		public ViewHolder(View row) {
			mRow = row;
		}
	 
	public TextView gettitle() {
		if(null == title){
			 title = (TextView) mRow.findViewById(R.id.title);
			}
		return title;
	 }     

	public TextView getdetail() {
		 if(null == detail){
			  detail = (TextView) mRow.findViewById(R.id.detail);
				}
	   return detail;
	 }

	 public ImageView getImage() {
		 if(null == i11){
			  i11 = (ImageView) mRow.findViewById(R.id.img);
		 }
			return i11;
	}
  }
 } 

	public void startHome(View view){
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}
}