package com.playcez;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;


public class FacebookSSO extends Activity{

	private Facebook facebook = new Facebook("7347090549");
	private String FILENAME = "Placez_data";
	private SharedPreferences mPrefs;
	private AsyncFacebookRunner mAsyncRunner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		
		if(access_token != null){
			facebook.setAccessToken(access_token);
		}
		
		if(expires != 0)
			facebook.setAccessExpires(expires);
		
		
		if(!facebook.isSessionValid() || access_token == null){
			facebook.authorize(this, new String[] {"read_stream", "publish_stream"}, new DialogListener(){

				public void onComplete(Bundle values) {
					// TODO Auto-generated method stub
					Log.d("dsdhskjdskjdhshdkshdkjshdj", "dshdjkshdkkkkkkkkkkkkkk");
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires", facebook.getAccessExpires());
					editor.commit();
				}

				public void onFacebookError(FacebookError e) {
					// TODO Auto-generated method stub
				}

				public void onError(DialogError e) {
					// TODO Auto-generated method stub
					
				}

				public void onCancel() {
					// TODO Auto-generated method stub
					
				}
				
			});

		}		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.facebook_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() == R.id.logout){
			try{
				if(facebook.isSessionValid()){
					mAsyncRunner.logout(getApplicationContext(), new LogoutRequestListener());//.logout(getBaseContext(), null, this);
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		if(item.getItemId() == R.id.post){
			//facebook.dialog(FacebookSSO.this, "feed", this);
		}
			
		return super.onOptionsItemSelected(item);
	}

	private class LogoutRequestListener implements RequestListener {

		LogoutRequestListener(){
			try {
				SharedPreferences.Editor editor = mPrefs.edit();
				editor.remove("access_token");
				editor.remove("access_expires");
				
					//facebook.logout(getApplicationContext());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}

	}
}
