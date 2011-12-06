package com.playcez;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.LoginButton;
import com.facebook.android.SessionEvents;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;
import com.facebook.android.SessionStore;


public class PlaycezFacebook extends Activity {

    // Your Facebook Application ID must be set before running this example
    // See http://www.facebook.com/developers/createapp.php
	private static final String KEY = "facebook-session";
	private static final String APP_ID = "137231029646502";

    private LoginButton mLoginButton;
    private TextView mText;

    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;
    SharedPreferences myData;
    Editor myEdit; 

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		
        Editor myEdit = myData.edit();
		
		boolean firstTime = myData.getBoolean("firstTime", true);
		
		myEdit.putBoolean("firstTime", false);
		
		myEdit.commit();
				
        if(firstTime){
        	
            /*LocationManager mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    		
    		if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
    	    
    			buildAlertMessageNoGps();
    			
    	    }*/
        }
        
        
        if (APP_ID == null) {
           // Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " +
             //       "specified before running this example: see Example.java");
        }
        
        setContentView(R.layout.main);
        
        myData = getSharedPreferences("myData", MODE_PRIVATE);
        myEdit = myData.edit();
        
        mLoginButton = (LoginButton) findViewById(R.id.login);
        mText = (TextView) PlaycezFacebook.this.findViewById(R.id.txt);
        
       	mFacebook = new Facebook(APP_ID);
       	setmAsyncRunner(new AsyncFacebookRunner(mFacebook));

        boolean loggedIn = SessionStore.restore(mFacebook, this);
        if(loggedIn){
        	startActivity(new Intent(this, Start_Menu.class));
        }
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
        String[] perm = {"email",
		                "status_update" ,
		               "user_location" ,
		                "user_hometown" ,
		               "user_work_history", 
		                "user_likes" ,
		               "offline_access", 
		                "publish_actions"};
		
        mLoginButton.init(this, mFacebook, perm);
    }

    
    private void buildAlertMessageNoGps() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                   //launchGPSOptions();
	            	   
	            	   
	            	   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	            	   startActivity(intent);
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                   
	                   dialog.cancel();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}

    
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    public void sendFacebookCredentials(){
    	String accessToken="";
    	String id = null;
    	try {
	    	//Log.d("heyy","accessToken");
	    	accessToken = mFacebook.getAccessToken();
	    	
	    	
	    	//Log.d("heyy","accessToken "+ accessToken);
	    	accessToken = accessToken.replace("|", "%7C");
	    	//Log.d("This is my access token: ", accessToken);
			
	    	JSONObject me = new JSONObject(mFacebook.request("me"));
			id = me.getString("id");
			myEdit.putString("fbuid", id);
			myEdit.commit();
			//Log.d("This is my fuid: ", id + " " + myData.getString("fbuid", "no"));

			
		HttpClient httpclient = new DefaultHttpClient();
        String urlString = "http://playcez.com/api_getUID.php";
        urlString += "?fbid=" + id + "&fb_token=" + accessToken;
    	HttpGet httpget = new HttpGet(urlString);
    	
    	HttpResponse response = null;
    	//Log.d("heyy","heyyyyyy");
		try {
			response = httpclient.execute(httpget);
			//Log.d("hey i m here", "ohh here u are !!");
		} catch (ClientProtocolException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//Log.d("hey i m here", "ohh here u are !!");
		// 
		if(response != null)
		{
			//Log.d("hey i m here", "ohh here u are !!");
			InputStream is = null;
			try {
				is = response.getEntity().getContent();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

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
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String result = sb.toString();
			myEdit.putString("uid", result);
			myEdit.commit();
			Log.d("this is muy uid", result + " " + myData.getString("uid", "uid"));
		}

    } catch (Exception e) {
		e.printStackTrace();
	}

    }
    
    public void setmAsyncRunner(AsyncFacebookRunner mAsyncRunner) {
		this.mAsyncRunner = mAsyncRunner;
	}

	public AsyncFacebookRunner getmAsyncRunner() {
		return mAsyncRunner;
		
	}

	public class SampleAuthListener implements AuthListener {

        public void onAuthSucceed() {
            mText.setText("You have logged in! ");
            sendFacebookCredentials();
        	Intent mIntent = new Intent(getApplicationContext(), Start_Menu.class);
        	startActivity(mIntent);
        }

        public void onAuthFail(String error) {
            mText.setText("Login Failed: " + error);
        }
    }

    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
            mText.setText("Logging out...");
        }

        public void onLogoutFinish() {
            mText.setText("You have logged out! ");
        }
    }

    public void startHome(View view){
    	
    	final String TOKEN = "access_token";
        final String KEY = "facebook-session";
        
    	SharedPreferences myData = getSharedPreferences(KEY, MODE_PRIVATE);
    	String token = myData.getString(TOKEN, null);
    	
    	Log.d("hey", "hey");
    	
    	startActivity(new Intent(getApplicationContext(), Start_Menu.class));
    	if(token != null && token.length() > 0)
    		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
    	else
    		mText.setText("You are required to login first");
	}
}
