/*
 * 
 */
package com.playcez.twitter.android;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.http.AccessToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

// TODO: Auto-generated Javadoc
/*
Copyright [2010] [Abhinava Srivastava]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
/**
 * The Class TwitterApp.
 */
public class TwitterApp {
	
	/** The m twitter. */
	private Twitter mTwitter;
	
	/** The m session. */
	private TwitterSession mSession;
	
	/** The m access token. */
	private AccessToken mAccessToken;
	
	/** The m http oauth consumer. */
	private CommonsHttpOAuthConsumer mHttpOauthConsumer;
	
	/** The m http oauthprovider. */
	private OAuthProvider mHttpOauthprovider;
	
	/** The m consumer key. */
	private String mConsumerKey;
	
	/** The m secret key. */
	private String mSecretKey;
	
	/** The m progress dlg. */
	private ProgressDialog mProgressDlg;
	
	/** The m listener. */
	private TwDialogListener mListener;
	
	/** The context. */
	private Activity context;
	

	/** The Constant CALLBACK_URL. */
	public static final String CALLBACK_URL = "twitterapp://connect";
	
	/** The Constant TWITTER_ACCESS_TOKEN_URL. */
	private static final String TWITTER_ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
	
	/** The Constant TWITTER_AUTHORZE_URL. */
	private static final String TWITTER_AUTHORZE_URL = "https://api.twitter.com/oauth/authorize";
	
	/** The Constant TWITTER_REQUEST_URL. */
	private static final String TWITTER_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	
	/** The Constant MESSAGE. */
	public static final String MESSAGE = "Hi all";

	/**
	 * Instantiates a new twitter app.
	 *
	 * @param context the context
	 * @param consumerKey the consumer key
	 * @param secretKey the secret key
	 */
	public TwitterApp(Activity context, String consumerKey, String secretKey) {
		this.context = context;

		mTwitter = new TwitterFactory().getInstance();
		mSession = new TwitterSession(context);
		mProgressDlg = new ProgressDialog(context);

		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mConsumerKey = consumerKey;
		mSecretKey = secretKey;

		mHttpOauthConsumer = new CommonsHttpOAuthConsumer(mConsumerKey,
				mSecretKey);
		 
		String request_url=TWITTER_REQUEST_URL;
		String access_token_url=TWITTER_ACCESS_TOKEN_URL;
		String authorize_url=TWITTER_AUTHORZE_URL;
		
		mHttpOauthprovider = new DefaultOAuthProvider(
				request_url,
				access_token_url,
				authorize_url);
		mAccessToken = mSession.getAccessToken();
		
		configureToken();
	}

	/**
	 * Gets the access token.
	 *
	 * @return the access token
	 */
	public String getAccessToken(){
		return mHttpOauthConsumer.getToken();
	}
	
	/**
	 * Gets the secret key.
	 *
	 * @return the secret key
	 */
	public String getSecretKey(){
		return mHttpOauthConsumer.getTokenSecret();
	}
	
	/**
	 * Sets the listener.
	 *
	 * @param listener the new listener
	 */
	public void setListener(TwDialogListener listener) {
		mListener = listener;
	}

	/**
	 * Configure token.
	 */
	@SuppressWarnings("deprecation")
	private void configureToken() {
		if (mAccessToken != null) {
			mTwitter.setOAuthConsumer(mConsumerKey, mSecretKey);
			mTwitter.setOAuthAccessToken(mAccessToken);
		}
	}

	/**
	 * Checks for access token.
	 *
	 * @return true, if successful
	 */
	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	/**
	 * Reset access token.
	 */
	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();

			mAccessToken = null;
		}
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return mSession.getUsername();
	}

	/**
	 * Update status.
	 *
	 * @param status the status
	 * @throws Exception the exception
	 */
	public void updateStatus(String status) throws Exception {
		try {
			mTwitter.updateStatus(status);
		} catch (TwitterException e) {
			throw e;
		}
	}

	/**
	 * Authorize.
	 */
	public void authorize() {
		mProgressDlg.setMessage("Initializing ...");
		mProgressDlg.show();

		new Thread() {
			@Override
			public void run() {
				String authUrl = "";
				int what = 1;

				try {
					authUrl = mHttpOauthprovider.retrieveRequestToken(
							mHttpOauthConsumer, CALLBACK_URL);
					what = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(mHandler
						.obtainMessage(what, 1, 0, authUrl));
			}
		}.start();
	}

	/**
	 * Process token.
	 *
	 * @param callbackUrl the callback url
	 */
	public void processToken(String callbackUrl) {
		mProgressDlg.setMessage("Finalizing ...");
		mProgressDlg.show();

		final String verifier = getVerifier(callbackUrl);

		new Thread() {
			@Override
			public void run() {
				int what = 1;

				try {
					mHttpOauthprovider.retrieveAccessToken(mHttpOauthConsumer,
							verifier);

					mAccessToken = new AccessToken(
							mHttpOauthConsumer.getToken(),
							mHttpOauthConsumer.getTokenSecret());

					configureToken();

					User user = mTwitter.verifyCredentials();

					mSession.storeAccessToken(mAccessToken, user.getName());

					what = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

	/**
	 * Gets the verifier.
	 *
	 * @param callbackUrl the callback url
	 * @return the verifier
	 */
	private String getVerifier(String callbackUrl) {
		String verifier = "";

		try {
			callbackUrl = callbackUrl.replace("twitterapp", "http");

			URL url = new URL(callbackUrl);
			String query = url.getQuery();

			String array[] = query.split("&");

			for (String parameter : array) {
				String v[] = parameter.split("=");

				if (URLDecoder.decode(v[0]).equals(
						oauth.signpost.OAuth.OAUTH_VERIFIER)) {
					verifier = URLDecoder.decode(v[1]);
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return verifier;
	}

	/**
	 * Show login dialog.
	 *
	 * @param url the url
	 */
	private void showLoginDialog(String url) {
		final TwDialogListener listener = new TwDialogListener() {

			public void onComplete(String value) {
				processToken(value);
			}

			public void onError(String value) {
				mListener.onError("Failed opening authorization page");
			}
		};

		new TwitterDialog(context, url, listener).show();
	}

	/** The m handler. */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgressDlg.dismiss();

			if (msg.what == 1) {
				if (msg.arg1 == 1)
					mListener.onError("Error getting request token");
				else
					mListener.onError("Error getting access token");
			} else {
				if (msg.arg1 == 1)
					showLoginDialog((String) msg.obj);
				else
					mListener.onComplete("");
			}
		}
	};

	/**
	 * The listener interface for receiving twDialog events.
	 * The class that is interested in processing a twDialog
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addTwDialogListener<code> method. When
	 * the twDialog event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see TwDialogEvent
	 */
	public interface TwDialogListener {
		
		/**
		 * On complete.
		 *
		 * @param value the value
		 */
		public void onComplete(String value);

		/**
		 * On error.
		 *
		 * @param value the value
		 */
		public void onError(String value);
	}
}