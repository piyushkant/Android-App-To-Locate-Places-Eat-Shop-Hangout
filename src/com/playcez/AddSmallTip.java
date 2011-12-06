package com.playcez;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AddSmallTip extends Activity{

	private CheckBox checkFB;
	private String position;
	private int check;
	private EditText reviewEditText;
	Button submit1, submit2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_small_tip);
		
		Bundle extras = getIntent().getExtras();
		position = extras.getString("position");
		
		checkFB = (CheckBox) findViewById(R.id.shareOn_fb);
		if(checkFB.isChecked())
			check = 1;
		else
			check = 0;

		submit1 = (Button)findViewById(R.id.submit_review1);
		
		
		reviewEditText = (EditText) findViewById(R.id.smalltip);

		checkFB.setWidth(300);
		
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		final String smallReview = reviewEditText.getText().toString();
		final String uid = myData.getString("uid", "");
		final String pid = myData.getString("pid", "");
		final String fbuid = myData.getString("fbuid", "");
		submit1.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				submitReview(uid, position, smallReview + " ", pid, fbuid, check);
			}
			
		});
		
	}

	public void submitReview(String uid, String sentiment, String review, String pid, String fbuid, int toPublish){
		String revURL = "http://playcez.com/api_addReview.php";
		HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost(revURL);
    	try{
    		
    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    		nameValuePairs.add(new BasicNameValuePair("uid",uid));
    		nameValuePairs.add(new BasicNameValuePair("sentiment", sentiment));
    		nameValuePairs.add(new BasicNameValuePair("review", review));
    		nameValuePairs.add(new BasicNameValuePair("pid", pid));
    		nameValuePairs.add(new BasicNameValuePair("fbuid", fbuid));
    		nameValuePairs.add(new BasicNameValuePair("toPublish", toPublish+""));
    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    		
	    	HttpResponse response = httpclient.execute(httppost);
	    	
			// for JSON:
			if(response != null)
			{
				InputStream is = response.getEntity().getContent();
	
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
	
				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				String result = sb.toString();
				alertSuccess();
				Log.d("the result", result);
			}
    	} catch(Exception e){
    		
    	}
	}
	
	public void alertSuccess(){
		/*final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		final AlertDialog alert = dialog.create();
		dialog.setTitle("Welcome to Playcez");
		
			Log.d("here", "inside for loop");
			LayoutInflater factory = LayoutInflater.from(this);
			final View view = factory.inflate(R.layout.firsttimealert, null);
			
			Drawable drawable = getResources().getDrawable(R.drawable.android_no_results);
			view.setBackgroundDrawable(drawable);
			
			dialog.setView(view);
			
			dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// 
					Intent i = new Intent();
					setResult(1,i);
					finish();
				}
			});
			
			dialog.show();
			*/
		Toast.makeText(getApplicationContext(), "Your review is successfully uploaded!", Toast.LENGTH_LONG).show();
		Intent i = new Intent();
		setResult(1,i);
		finish();

	}
}
