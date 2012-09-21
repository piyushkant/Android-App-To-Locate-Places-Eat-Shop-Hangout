/*
 * 
 */
package com.playcez;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class OfferPage.
 */
public class OfferPage extends Activity {

	/** The bm. */
	Bitmap bm;
	
	/** The fb share url. */
	String fbShareUrl = "https://www.facebook.com/sharer/sharer.php?u=";
	
	/** The email id. */
	static String emailId;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.offerpage);

		final ImageView fbOfferShare = (ImageView) findViewById(R.id.fbOfferShare);

		final SharedPreferences myData = getSharedPreferences("myData",
				MODE_PRIVATE);

		String title = myData.getString("title", "");
		String price = myData.getString("offerPrice", "");
		String value = myData.getString("offerValue", "");
		String savings = myData.getString("offerSavings", "");
		// String buyUrl = myData.getString("buyUrl", "");
		String imageUrl = myData.getString("imageUrl", "");
		String address = myData.getString("address", "");
		String startdate = myData.getString("startDate", "");
		String expirydate = myData.getString("expiryDate", "");
		String bannerUrl = myData.getString("dealBanner", "");
		final String latitude = myData.getString("offerLat", "");
		final String longitude = myData.getString("offerLng", "");
		final String dealid = myData.getString("dealid", "");
		final String uid = myData.getString("uid", "1");
		emailId = myData.getString("email", "");

		imageLoader(imageUrl);
		bannerLoader(bannerUrl);
		TextView offerTitle = (TextView) findViewById(R.id.offerTitle);
		offerTitle.setText(title);
		TextView offerPrice = (TextView) findViewById(R.id.priceText);
		offerPrice.setText(price);
		TextView offerDiscount = (TextView) findViewById(R.id.discountText);
		offerDiscount.setText(value);
		TextView offerSavings = (TextView) findViewById(R.id.savingsText);
		offerSavings.setText(savings);
		TextView offerAddress = (TextView) findViewById(R.id.addressText);
		offerAddress.setText(address);
		TextView startDate = (TextView) findViewById(R.id.startDate);
		startDate.setText(startdate);
		TextView expiryDate = (TextView) findViewById(R.id.expiryDate);
		expiryDate.setText(expirydate);

		final RelativeLayout offerShareBar = (RelativeLayout) findViewById(R.id.offerShareBar);
		final RelativeLayout enterEmail = (RelativeLayout) findViewById(R.id.enterEmail);
		final Button emailButton = (Button) findViewById(R.id.emailButton);

		emailButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (emailId.equals("")) {
					offerShareBar.setVisibility(View.GONE);
					emailButton.setVisibility(View.GONE);
					enterEmail.setVisibility(View.VISIBLE);

					final EditText provideEmailArea = (EditText) findViewById(R.id.provideEmailArea);
					Button provideEmailButton = (Button) findViewById(R.id.provideEmailButton);

					provideEmailButton
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {
									Editor myEdit = myData.edit();
									String email = provideEmailArea.getText()
											.toString();

									if (checkemail(email) == true) {
										myEdit.putString("email", email);

										sendData(email, dealid, uid);
										enterEmail.setVisibility(View.GONE);
										offerShareBar
												.setVisibility(View.VISIBLE);
										emailButton.setVisibility(View.VISIBLE);
									}

									else {
										new AlertDialog.Builder(OfferPage.this)
												.setTitle("")
												.setMessage(
														"Please enter valid Email address.")
												.setPositiveButton("OK", null)
												.show();
									}
								}
							});
				}

				else {
					sendData(emailId, dealid, uid);
				}
			}
		});

		fbOfferShare.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				fbOfferShare.setFocusable(true);
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(fbShareUrl));
				startActivity(myIntent);
			}
		});

		final ImageView offerLocater = (ImageView) findViewById(R.id.offerLocator);

		offerLocater.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				fbOfferShare.setFocusable(true);
				Intent mIntent = new Intent(getApplicationContext(), Maps.class);
				mIntent.putExtra("latitude2", latitude);
				mIntent.putExtra("longitude2", longitude);
				startActivity(mIntent);
			}
		});
	}

	/**
	 * Image loader.
	 *
	 * @param imageUrl the image url
	 */
	void imageLoader(String imageUrl) {
		BitmapFactory.Options bmOptions;
		bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 1;
		bm = LoadImage(imageUrl, bmOptions);

		ImageView offerImage = (ImageView) findViewById(R.id.offerImage);
		offerImage.setImageBitmap(bm);
	}

	/**
	 * Banner loader.
	 *
	 * @param imageUrl the image url
	 */
	void bannerLoader(String imageUrl) {
		BitmapFactory.Options bmOptions;
		bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 1;
		bm = LoadImage(imageUrl, bmOptions);

		ImageView offerImage = (ImageView) findViewById(R.id.offerBanner);
		offerImage.setImageBitmap(bm);
	}

	/**
	 * Load image.
	 *
	 * @param URL the uRL
	 * @param options the options
	 * @return the bitmap
	 */
	private Bitmap LoadImage(String URL, BitmapFactory.Options options) {
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			in = OpenHttpConnection(URL);
			bitmap = BitmapFactory.decodeStream(in, null, options);
			in.close();
		} catch (IOException e1) {
		}
		return bitmap;
	}

	/**
	 * Open http connection.
	 *
	 * @param strURL the str url
	 * @return the input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private InputStream OpenHttpConnection(String strURL) throws IOException {
		InputStream inputStream = null;
		URL url = new URL(strURL);
		URLConnection conn = url.openConnection();

		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("GET");
			httpConn.connect();

			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = httpConn.getInputStream();
			}
		} catch (Exception ex) {
		}

		return inputStream;
	}

	/**
	 * Checkemail.
	 *
	 * @param email the email
	 * @return true, if successful
	 */
	public boolean checkemail(String email) {

		Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();

	}

	/**
	 * Send data.
	 *
	 * @param email the email
	 * @param dealid the dealid
	 * @param uid the uid
	 */
	public void sendData(String email, String dealid, String uid) {
		String addPlaceURL = "http://playcez.com/api_sendDeal.php";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(addPlaceURL);

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("dealid", dealid));
			nameValuePairs.add(new BasicNameValuePair("uid", uid));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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

				Log.d("Checking..enteredEmail", result + "");
				Toast.makeText(getApplicationContext(),
						"Request Sent Successfully!", Toast.LENGTH_LONG).show();
				Log.d("the result", result);
			}
		} catch (Exception e) {

		}
	}
}
