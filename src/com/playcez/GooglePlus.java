/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.playcez;

import static com.playcez.PlaycezFacebook.loginProgress;

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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.Person;
import com.google.api.services.plus.model.PersonPlacesLived;

// TODO: Auto-generated Javadoc
/**
 * Creates a tab for each Google+ activity.
 * 
 * @author Chirag Shah
 */
public class GooglePlus extends android.app.Activity {

	/** The Constant TAG. */
	public static final String TAG = GooglePlus.class.getName();

	/** The m list view. */
	private ListView mListView;

	/** The plus. */
	private Plus plus;

	/** The my data. */
	private String myData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = getIntent();
		if (intent == null || !intent.hasExtra("token")) {
			AuthUtils.refreshAuthToken(this);
			return;
		}

		setContentView(R.layout.activity_list);
		mListView = (ListView) findViewById(R.id.activityList);

		loginProgress = ProgressDialog.show(GooglePlus.this, "Please wait",
				"Autheticating...", true);

		AsyncTask<String, Void, List<Activity>> task = new AsyncTask<String, Void, List<Activity>>() {

			@Override
			protected List<Activity> doInBackground(String... params) {
				try {
					plus = new PlusWrap(GooglePlus.this).get();
					Person mePerson = plus.people().get("me").execute();

					Log.d(TAG, "ID:\t" + mePerson.getId());
					Log.d(TAG, "Display Name:\t" + mePerson.getDisplayName());
					Log.d(TAG, "Image URL:\t" + mePerson.getImage().getUrl());
					Log.d(TAG, "Profile URL:\t" + mePerson.getUrl());

					final String TOKEN = "access_token";
					SharedPreferences myData = getSharedPreferences("myData",
							MODE_PRIVATE);
					Editor edit = myData.edit();
					edit.putString(TOKEN, "access_token");
					edit.putString("uid", mePerson.getId());
					edit.commit();
					Log.d(TAG, "hererre");
					String placesLived = "";
					String name = "";
					String json = "";
					JSONArray obj = null;
					try {
						name = mePerson.getDisplayName().toString();
						placesLived = mePerson.getPlacesLived().toString();
						List<PersonPlacesLived> object = mePerson
								.getPlacesLived();
						json = object.toString();
						obj = new JSONArray(json);
						json = obj.toString();
					} catch (Exception e) {
						e.printStackTrace();
					}

					final SharedPreferences settings = getSharedPreferences(
							AuthUtils.PREFS_NAME, 0);
					final String account_name = settings.getString(
							AuthUtils.PREF_ACCOUNT_NAME, "");
					final String accessToken = settings.getString(
							"accessToken", null);

					sendToServer("https://playcez.com/api_getUID.php", name,
							mePerson.getBirthday(), mePerson.getId(),
							mePerson.getCurrentLocation(), obj,
							mePerson.getGender(), accessToken, account_name);

					return plus.activities().list("me", "public").execute()
							.getItems();
				} catch (IOException e) {
					loginProgress.dismiss();
					Toast.makeText(getApplicationContext(),
							"Check your network connection!", Toast.LENGTH_LONG)
							.show();
					Log.e(TAG, "Unable to list recommended people for user: "
							+ params[0], e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<Activity> feed) {
				if (feed != null) {
					Log.d(TAG, feed + "");
					SharedPreferences data = getSharedPreferences("myData",
							MODE_PRIVATE);
					boolean showTut = data.getBoolean("showTut", true);
					Editor myEdit = data.edit();
					myEdit.putBoolean("showTut", false);
					myEdit.commit();

					if (showTut) {
						startActivity(new Intent(getApplicationContext(),
								Tutorial3.class));
					} else {
						startActivity(new Intent(getApplicationContext(),
								Start_Menu.class));
					}
					finish();
				} else {
				}
			}
		};
		task.execute("me");
	}

	/**
	 * Send to server.
	 * 
	 * @param url
	 *            the url
	 * @param name
	 *            the name
	 * @param birthday
	 *            the birthday
	 * @param id
	 *            the id
	 * @param current_location
	 *            the current_location
	 * @param places_lived
	 *            the places_lived
	 * @param gender
	 *            the gender
	 * @param auth
	 * @param account_name
	 */
	public void sendToServer(String url, String name, String birthday,
			String id, String current_location, JSONArray places_lived,
			String gender, String auth, String account_name) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("g_name", name));
		nameValuePairs.add(new BasicNameValuePair("g_birthday", birthday));
		nameValuePairs.add(new BasicNameValuePair("g_id", id));
		nameValuePairs.add(new BasicNameValuePair("g_current_location",
				current_location));
		try {
			nameValuePairs.add(new BasicNameValuePair("g_places_lived",
					places_lived.toString()));
		} catch (Exception e) {

		}
		nameValuePairs.add(new BasicNameValuePair("g_gender", gender));
		nameValuePairs.add(new BasicNameValuePair("g_auth", auth));
		nameValuePairs.add(new BasicNameValuePair("g_acc_name", account_name));

		Log.d(TAG, "hererre " + url);

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			Log.d(TAG, "hererre");
			// for JSON:
			if (response != null) {
				InputStream is = response.getEntity().getContent();
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
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				String result = sb.toString();
				Log.d("result", result);
				JSONObject resultObj = new JSONObject(result);
				SharedPreferences myData = getSharedPreferences("myData",
						MODE_PRIVATE);
				Editor myEdit = myData.edit();
				myEdit.putString("uid", resultObj.getString("uid"));
				myEdit.putString("playcez_token",
						resultObj.getString("playcez_token"));
				myEdit.commit();
			}
		} catch (Exception e) {
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			Editor edit = myData.edit();
			edit.putString("uid", "");
			edit.commit();
		}
	}
}
