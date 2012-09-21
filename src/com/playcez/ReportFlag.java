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
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class ReportFlag.
 */
public class ReportFlag extends Activity {

	/** The wrong. */
	String wrong;

	/** The uid. */
	String uid;

	/** The pid. */
	String pid;

	/** The comment. */
	String comment;

	EditText flagArea;
	RadioButton radio1;
	RadioButton radio2;
	RadioButton radio3;
	RadioButton radio4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int screenHeight = display.getHeight();

		try {
			if (screenHeight < 400) {
				setContentView(R.layout.reportflagsmall);
			}

			else {
				setContentView(R.layout.reportflag);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		radio1 = (RadioButton) findViewById(R.id.radio1);
		radio2 = (RadioButton) findViewById(R.id.radio2);
		radio3 = (RadioButton) findViewById(R.id.radio3);
		radio4 = (RadioButton) findViewById(R.id.radio4);

		Button flagReportButton = (Button) findViewById(R.id.flagReportButton);

		flagReportButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if ((radio1.isChecked()) || (radio2.isChecked())
						|| (radio3.isChecked()) || (radio4.isChecked())) {

					if (radio1.isChecked()) {
						wrong = "1";
					}

					if (radio2.isChecked()) {
						wrong = "2";
					}

					if (radio3.isChecked()) {
						wrong = "3";
					}

					if (radio4.isChecked()) {
						wrong = "4";
					}

					flagArea = (EditText) findViewById(R.id.flagArea);
					comment = flagArea.getText().toString();

					SharedPreferences myData = getSharedPreferences("myData",
							MODE_PRIVATE);
					uid = myData.getString("fbuid", "");
					pid = myData.getString("pid", "");

					Log.d("uid", uid + "");
					Log.d("pid", pid + "");
					Log.d("comment", comment + "");
					Log.d("wrong", wrong + "");

					String addPlaceURL = "http://playcez.com/api_feedbackNew.php";
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(addPlaceURL);

					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
								2);
						nameValuePairs.add(new BasicNameValuePair("wrong",
								wrong));
						nameValuePairs.add(new BasicNameValuePair("comment",
								comment));
						nameValuePairs.add(new BasicNameValuePair("uid", uid));
						nameValuePairs.add(new BasicNameValuePair("pid", pid));

						httppost.setEntity(new UrlEncodedFormEntity(
								nameValuePairs));
						HttpResponse response = httpclient.execute(httppost);

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

							Log.d("Checking..", result + "");
							Toast.makeText(getApplicationContext(),
									"Report Successfully Sent",
									Toast.LENGTH_LONG).show();
							flagArea.getText().clear();
							startActivity(new Intent(getApplicationContext(),
									Review.class));
							Log.d("the result", result);
						}
					} catch (Exception e) {

					}
				}

				else {
					new AlertDialog.Builder(ReportFlag.this).setTitle("")
							.setMessage("No option selected!")
							.setPositiveButton("OK", null).show();
				}
			}
		});
	}
}
