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
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class AddPlaces.
 */
public class AddPlaces extends Activity {

	/** The catg. */
	static String[] catg = { "Others", "Emergency",
			"Buildings and Religious Places", "College and Education",
			"Services", "Shops and Stores", "Tourism and Nature",
			"Entertainment and Outings", "Transportation", "Food" };

	/** The spin cat. */
	static Spinner spinCat;
	
	/** The check box. */
	static CheckBox checkBox;
	
	/** The check id. */
	static String checkId;
	
	/** The catg id. */
	static String catgId;
	
	/** The pb. */
	static ProgressBar pb;
	
	/** The prog. */
	private ProgressDialog prog;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addplaces);

		spinCat = (Spinner) findViewById(R.id.spinCat);
		checkBox = (CheckBox) findViewById(R.id.hereBox);
		Button addButton = (Button) findViewById(R.id.addButton);
		
		addItemsOnSpinner();
		addListenerOnSpinnerItemSelection();

		addButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//pb.setVisibility(View.VISIBLE);
				addPlace();
			}
		});
	}

	/**
	 * Adds the items on spinner.
	 */
	void addItemsOnSpinner() {
		spinCat = (Spinner) findViewById(R.id.spinCat);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, catg);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinCat.setAdapter(dataAdapter);
	}

	/**
	 * Adds the listener on spinner item selection.
	 */
	void addListenerOnSpinnerItemSelection() {
		spinCat.setOnItemSelectedListener(new CustomOnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				catgId = parent.getItemAtPosition(position).toString();
			}
		});
	}

	/**
	 * Adds the place.
	 */
	void addPlace() {
		EditText nameArea = (EditText) findViewById(R.id.nameArea);
		EditText addressArea = (EditText) findViewById(R.id.addressArea);
		EditText localityArea = (EditText) findViewById(R.id.localityArea);
		EditText cityArea = (EditText) findViewById(R.id.cityArea);
		EditText stateArea = (EditText) findViewById(R.id.stateArea);
		EditText zipArea = (EditText) findViewById(R.id.zipArea);
		EditText phoneArea = (EditText) findViewById(R.id.phoneArea);

		if (checkBox.isChecked())
			checkId = "yes";
		else
			checkId = "no";

		if ((nameArea.getText().toString().trim().equals(""))
				|| (addressArea.getText().toString().trim().equals(""))
				|| (localityArea.getText().toString().trim().equals(""))
				|| (cityArea.getText().toString().trim().equals(""))) {
			//pb.setVisibility(View.GONE);
			new AlertDialog.Builder(AddPlaces.this).setTitle("")
					.setMessage("Please fill all the required fields.")
					.setPositiveButton("OK", null).show();
		}
		else {
			String addPlaceURL = "https://playcez.com/api_createPlace.php";
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(addPlaceURL);

			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			String lat = myData.getString("lat", "");
			String lng = myData.getString("lng", "");

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("name", nameArea
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("address",
						addressArea.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("cross_street",
						localityArea.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("city", cityArea
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("state", stateArea
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("postal_code",
						zipArea.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("phone", phoneArea
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("category", catgId));
				nameValuePairs.add(new BasicNameValuePair("here", checkId));
				nameValuePairs.add(new BasicNameValuePair("lat", lat));
				nameValuePairs.add(new BasicNameValuePair("lng", lng));

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
					
					Log.d("Checking..nwlnlsvnlew///", result+"");
					
					Editor myEdit = myData.edit();
					result = result.replace("\n", "");
					result = result.replace("\r", "");
					myEdit.putString("pid", result);
					myEdit.commit();
					
					Intent prevIntent = getIntent();
					String callback = prevIntent.getStringExtra("callback");
					final int NEARPLACE_CAPTURE_CALLBACK = 3;
					final int NEARPLACE_SELECT_CALLBACK = 4;
					
					/*
					 * Throws exception when callback is null
					 */
					try{
						if (callback.equals("capture")) {
							setResult(NEARPLACE_CAPTURE_CALLBACK, prevIntent);
							finish();
						} else if (callback.equals("select")) {
							setResult(NEARPLACE_SELECT_CALLBACK, prevIntent);
							finish();
						}
					}catch(Exception e){
						Intent mIntent = new Intent(getApplicationContext(), AddSmallTip.class);
						mIntent.putExtra("position", 0);
						startActivity(mIntent);
					}
					
					Toast.makeText(getApplicationContext(),
							"Place Successfully Added!", Toast.LENGTH_LONG)
							.show();
					Log.d("the result", result);
				}
			} catch (Exception e) {

			}
		}
	}
}
