/*
 * 
 */
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.LoginButton;
import com.facebook.android.SessionEvents;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;
import com.facebook.android.SessionStore;
import com.playcez.twitter.android.TwitterApp;
import com.playcez.twitter.android.TwitterApp.TwDialogListener;

// TODO: Auto-generated Javadoc
/**
 * The Class PlaycezFacebook.
 */
public class PlaycezFacebook extends Activity {

	// Your Facebook Application ID must be set before running this example
	// See http://www.facebook.com/developers/createapp.php
	/** The Constant KEY. */
	private static final String KEY = "facebook-session";

	/** The Constant APP_ID. */
	private static final String APP_ID = "137231029646502";

	/** The m login button. */
	private LoginButton mLoginButton;

	/** The m text. */
	private TextView mText;

	/** The login progress. */
	public static ProgressDialog loginProgress;

	/** The m facebook. */
	private Facebook mFacebook;

	/** The m async runner. */
	private AsyncFacebookRunner mAsyncRunner;

	/** The my data. */
	SharedPreferences myData;

	/** The my edit. */
	Editor myEdit;

	// //////////////Twitter Variables///////////////////
	/** The m twitter. */
	private TwitterApp mTwitter;

	/** Called when the activity is first created. */
	private static final String CONSUMER_KEY = "0wi50A3yMvMQuFcTFMWMUw";

	/** The Constant CONSUMER_SECRET. */
	private static final String CONSUMER_SECRET = "ZZY5GX5Nu9MUYbpvbw8v4NVll5qKDOq2ZtaU5XbsyfU";

	/**
	 * The Enum FROM.
	 */
	private enum FROM {

		/** The TWITTE r_ post. */
		TWITTER_POST,
		/** The TWITTE r_ login. */
		TWITTER_LOGIN
	};

	/**
	 * The Enum MESSAGE.
	 */
	private enum MESSAGE {

		/** The SUCCESS. */
		SUCCESS,
		/** The DUPLICATE. */
		DUPLICATE,
		/** The FAILED. */
		FAILED,
		/** The CANCELLED. */
		CANCELLED
	};

	// /////////////////////////////////////////////////

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		boolean firstTime = myData.getBoolean("firstTime", true);

		Editor myEdit = myData.edit();
		myEdit.putBoolean("firstTime", false);
		myEdit.commit();

		/*
		 * if (firstTime) { setupShortcut(); }
		 */

		myData = getSharedPreferences("myData", MODE_PRIVATE);
		myEdit = myData.edit();

		mLoginButton = (LoginButton) findViewById(R.id.login);
		mText = (TextView) PlaycezFacebook.this.findViewById(R.id.txt);

		mFacebook = new Facebook(APP_ID);
		setmAsyncRunner(new AsyncFacebookRunner(mFacebook));

		// If logged in then redirect user to start menu.
		boolean loggedIn = SessionStore.restore(mFacebook, this);
		String uid = myData.getString("uid", null);
		if (loggedIn || uid != null) {
			loginProgress = ProgressDialog.show(PlaycezFacebook.this,
					"Please wait", "Autheticating...", true);
			startActivity(new Intent(this, Start_Menu.class));
		}
		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());
		String[] perm = { "email", "status_update", "user_location",
				"user_hometown", "user_work_history", "user_likes",
				"offline_access", "publish_actions" };

		mLoginButton.init(this, mFacebook, perm);

		mTwitter = new TwitterApp(this, CONSUMER_KEY, CONSUMER_SECRET);

		ImageView gPlus = (ImageView) findViewById(R.id.gplus);
		gPlus.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(),
						GooglePlus.class));
			}
		});

		ImageView twButton = (ImageView) findViewById(R.id.twbutton);
		twButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//
				mTwitter.setListener(mTwLoginDialogListener);
				// mTwitter.resetAccessToken();
				if (mTwitter.hasAccessToken() == true) {
					try {
						// mTwitter.updateStatus(TwitterApp.MESSAGE);
						// postAsToast(FROM.TWITTER_POST, MESSAGE.SUCCESS);
					} catch (Exception e) {
						if (e.getMessage().toString().contains("duplicate")) {
							// postAsToast(FROM.TWITTER_POST,
							// MESSAGE.DUPLICATE);
						}
						e.printStackTrace();
					}
					// mTwitter.resetAccessToken();
				} else {
					Log.d(CONSUMER_KEY, CONSUMER_KEY + "ok");
					mTwitter.authorize();
				}
			}

		});
	}

	/**
	 * Setup shortcut.
	 */
	@Deprecated
	private void setupShortcut() {
		// First, set up the shortcut intent. For this example, we simply create
		// an intent that
		// will bring us directly back to this activity. A more typical
		// implementation would use a
		// data Uri in order to display a more specific result, or a custom
		// action in order to
		// launch a specific operation.

		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.setClassName("com.playcez", ".PlaycezSplash");

		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Context context = getApplicationContext();

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "PlayCez");
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(context,
						R.drawable.icon));
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		context.sendBroadcast(addIntent);

		// Now, return the result to the launcher

		setResult(RESULT_OK, addIntent);
	}

	/**
	 * Show toast messages.
	 * 
	 * @param twitterPost
	 *            the twitter post
	 * @param success
	 *            the success
	 */
	private void postAsToast(FROM twitterPost, MESSAGE success) {
		switch (twitterPost) {
		case TWITTER_LOGIN:
			switch (success) {
			case SUCCESS:
				Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG)
						.show();
				break;
			case FAILED:
				Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
			default:
				break;
			}
			break;
		case TWITTER_POST:
			switch (success) {
			case SUCCESS:
				Toast.makeText(this, "Posted Successfully", Toast.LENGTH_LONG)
						.show();
				break;
			case FAILED:
				Toast.makeText(this, "Posting Failed", Toast.LENGTH_LONG)
						.show();
				break;
			case DUPLICATE:
				Toast.makeText(this,
						"Posting Failed because of duplicate message...",
						Toast.LENGTH_LONG).show();
			default:
				break;
			}
			break;
		}
	}

	/**
	 * Twitter Dialog Listner.
	 */
	private TwDialogListener mTwLoginDialogListener = new TwDialogListener() {

		public void onError(String value) {
			mTwitter.resetAccessToken();
		}

		public void onComplete(String value) {
			//
			try {
				sendTWCredentials();
				Toast.makeText(getApplicationContext(), "sent",
						Toast.LENGTH_LONG).show();
				// mTwitter.updateStatus(TwitterApp.MESSAGE);
				// postAsToast(FROM.TWITTER_POST, MESSAGE.SUCCESS);
			} catch (Exception e) {
				if (e.getMessage().toString().contains("duplicate")) {
					// postAsToast(FROM.TWITTER_POST, MESSAGE.DUPLICATE);
				}
				e.printStackTrace();
			}
			// mTwitter.resetAccessToken();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	/**
	 * Send tw credentials.
	 */
	public void sendTWCredentials() {
		Log.d("Sending credentials", " twitter/.............");
		String accessToken = mTwitter.getAccessToken();
		String secretKey = mTwitter.getSecretKey();

		try {
			HttpClient httpclient = new DefaultHttpClient();
			String urlString = "https://playcez.com/api_getUID.php";
			urlString += "?twitter_secret=" + secretKey + "&twitter_token="
					+ accessToken;
			HttpGet httpget = new HttpGet(urlString);

			HttpResponse response = null;
			try {
				response = httpclient.execute(httpget);
			} catch (ClientProtocolException e2) {
				//
				e2.printStackTrace();
			} catch (IOException e2) {
				//
				e2.printStackTrace();
			}
			Log.d("Sending credentials", " twitter/.............");
			if (response != null) {
				Log.d("Sending credentials", " twitter/.............");
				InputStream is = null;
				try {
					is = response.getEntity().getContent();
				} catch (IllegalStateException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");

					}
					Log.d("Sending credentials", " twitter/.............");
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
				// myEdit.putString("uid", result);
				// myEdit.commit();
				Log.d("PlaycezFacebook", "result" + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send facebook credentials.
	 */
	public void sendFacebookCredentials() {
		String accessToken = "";
		String id = null;
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		Editor myEdit = myData.edit();
		try {
			accessToken = mFacebook.getAccessToken();
			accessToken = accessToken.replace("|", "%7C");
			JSONObject me = new JSONObject(mFacebook.request("me"));
			id = me.getString("id");
			myEdit.putString("fbuid", id);
			myEdit.commit();
			HttpClient httpclient = new DefaultHttpClient();
			String urlString = "https://playcez.com/api_getUID.php";
			urlString += "?fbid=" + id + "&fb_token=" + accessToken;
			HttpGet httpget = new HttpGet(urlString);

			// Log.d("PlaycezFacebokkkkkkkkkkk", "response is not null");

			HttpResponse response = null;
			try {
				response = httpclient.execute(httpget);
			} catch (ClientProtocolException e2) {
				//
				e2.printStackTrace();
			} catch (IOException e2) {
				//
				e2.printStackTrace();
			}
			if (response != null) {
				InputStream is = null;
				try {
					is = response.getEntity().getContent();
				} catch (IllegalStateException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
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
				Log.d("placeFacebook", result + " this is result");
				JSONObject resultObj = new JSONObject(result);
				myData = getSharedPreferences("myData", MODE_PRIVATE);
				myEdit = myData.edit();
				myEdit.putString("uid", resultObj.getString("uid"));
				myEdit.putString("playcez_token",
						resultObj.getString("playcez_token"));
				myEdit.commit();
				Log.d("PlaycezFcaebook",
						"uid is " + myData.getString("uid", "nullll"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Sets the m async runner.
	 * 
	 * @param mAsyncRunner
	 *            the new m async runner
	 */
	public void setmAsyncRunner(AsyncFacebookRunner mAsyncRunner) {
		this.mAsyncRunner = mAsyncRunner;
	}

	/**
	 * Gets the m async runner.
	 * 
	 * @return the m async runner
	 */
	public AsyncFacebookRunner getmAsyncRunner() {
		return mAsyncRunner;
	}

	/**
	 * The listener interface for receiving sampleAuth events. The class that is
	 * interested in processing a sampleAuth event implements this interface,
	 * and the object created with that class is registered with a component
	 * using the component's <code>addSampleAuthListener<code> method. When
	 * the sampleAuth event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see SampleAuthEvent
	 */
	public class SampleAuthListener implements AuthListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.facebook.android.SessionEvents.AuthListener#onAuthSucceed()
		 */
		public void onAuthSucceed() {
			// mText.setText("You have logged in! ");
			sendFacebookCredentials();

			myData = getSharedPreferences("myData", MODE_PRIVATE);
			boolean showTut = myData.getBoolean("showTut", true);
			Editor myEdit = myData.edit();
			myEdit.putBoolean("showTut", false);
			myEdit.commit();

			loginProgress = ProgressDialog.show(PlaycezFacebook.this,
					"Please wait", "Loading...", true);
			if (showTut) {
				startActivity(new Intent(getApplicationContext(),
						Tutorial3.class));
			} else {
				startActivity(new Intent(getApplicationContext(),
						Start_Menu.class));
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.facebook.android.SessionEvents.AuthListener#onAuthFail(java.lang
		 * .String)
		 */
		public void onAuthFail(String error) {
			Toast.makeText(getApplicationContext(),
					"Facebook authentication failed. Please try other logins!",
					Toast.LENGTH_LONG).show();
			mText.setText("Login Failed: " + error);
		}
	}

	/**
	 * The listener interface for receiving sampleLogout events. The class that
	 * is interested in processing a sampleLogout event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>addSampleLogoutListener<code> method. When
	 * the sampleLogout event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see SampleLogoutEvent
	 */
	public class SampleLogoutListener implements LogoutListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.facebook.android.SessionEvents.LogoutListener#onLogoutBegin()
		 */
		public void onLogoutBegin() {
			mText.setText("Logging out...");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.facebook.android.SessionEvents.LogoutListener#onLogoutFinish()
		 */
		public void onLogoutFinish() {
			mText.setText("You have logged out! ");
		}
	}

	/**
	 * Start home.
	 * 
	 * @param view
	 *            the view
	 */
	public void startHome(View view) {

		final String TOKEN = "access_token";
		final String KEY = "facebook-session";
		String uid = null;

		SharedPreferences myData = getSharedPreferences(KEY, MODE_PRIVATE);
		String token = myData.getString(TOKEN, null);
		uid = myData.getString("uid", null);
		myData = getSharedPreferences("myData", MODE_PRIVATE);
		String g_token = myData.getString("access_token", null);

		if (uid != null && uid.length() > 0) {
			loginProgress = ProgressDialog.show(PlaycezFacebook.this,
					"Please wait", "Loading...", true);
			startActivity(new Intent(getApplicationContext(), Start_Menu.class));
		}

		// mText.setText("Authentication Failed");
	}
}
