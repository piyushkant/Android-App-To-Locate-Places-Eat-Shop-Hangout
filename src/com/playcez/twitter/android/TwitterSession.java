/*
 * 
 */
package com.playcez.twitter.android;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

import twitter4j.http.AccessToken;

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
 * The Class TwitterSession.
 */
public class TwitterSession {
	
	/** The shared pref. */
	private SharedPreferences sharedPref;
	
	/** The editor. */
	private Editor editor;

	/** The Constant TWEET_AUTH_KEY. */
	private static final String TWEET_AUTH_KEY = "auth_key";
	
	/** The Constant TWEET_AUTH_SECRET_KEY. */
	private static final String TWEET_AUTH_SECRET_KEY = "auth_secret_key";
	
	/** The Constant TWEET_USER_NAME. */
	private static final String TWEET_USER_NAME = "user_name";
	
	/** The Constant SHARED. */
	private static final String SHARED = "Twitter_Preferences";

	/**
	 * Instantiates a new twitter session.
	 *
	 * @param context the context
	 */
	public TwitterSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);

		editor = sharedPref.edit();
	}

	/**
	 * Store access token.
	 *
	 * @param accessToken the access token
	 * @param username the username
	 */
	public void storeAccessToken(AccessToken accessToken, String username) {
		editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
		editor.putString(TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret());
		editor.putString(TWEET_USER_NAME, username);

		editor.commit();
	}

	/**
	 * Reset access token.
	 */
	public void resetAccessToken() {
		editor.putString(TWEET_AUTH_KEY, null);
		editor.putString(TWEET_AUTH_SECRET_KEY, null);
		editor.putString(TWEET_USER_NAME, null);

		editor.commit();
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return sharedPref.getString(TWEET_USER_NAME, "");
	}

	/**
	 * Gets the access token.
	 *
	 * @return the access token
	 */
	public AccessToken getAccessToken() {
		String token = sharedPref.getString(TWEET_AUTH_KEY, null);
		String tokenSecret = sharedPref.getString(TWEET_AUTH_SECRET_KEY, null);

		if (token != null && tokenSecret != null)
			return new AccessToken(token, tokenSecret);
		else
			return null;
	}
}