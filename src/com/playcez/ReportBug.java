/*
 * 
 */

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
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class ReportBug.
 */
public class ReportBug extends Activity {

	/** The version name. */
	String versionName;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportbug);

		TextView deviceName = (TextView) findViewById(R.id.deviceName);
		deviceName.setText("Device: " + Build.MODEL);

		try {
			versionName = getApplicationContext()
					.getPackageManager()
					.getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		TextView androidVersion = (TextView) findViewById(R.id.osVersion);
		androidVersion.setText("Android Version: " + Build.VERSION.RELEASE);

		Button submitButton = (Button) findViewById(R.id.submitButton);
		final EditText bugDes = (EditText) findViewById(R.id.bugDes);

		submitButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String addPlaceURL = "https://playcez.com/api_reportBug.php";
				HttpClient Httpclient = new DefaultHttpClient();
				HttpPost Httppost = new HttpPost(addPlaceURL);

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs.add(new BasicNameValuePair("device",
							Build.MODEL));
					nameValuePairs.add(new BasicNameValuePair("os",
							Build.VERSION.RELEASE));
					nameValuePairs.add(new BasicNameValuePair("version",
							versionName));
					nameValuePairs.add(new BasicNameValuePair("comment", bugDes
							.getText().toString()));

					Httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = Httpclient.execute(Httppost);

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

						Log.d("Checking..nwlnlsvnlew///", result + "");
						Toast.makeText(getApplicationContext(),
								"Report Successfully Sent", Toast.LENGTH_LONG)
								.show();
						Log.d("the result", result);
					}
				} catch (Exception e) {

				}
			}
		});
	}

}