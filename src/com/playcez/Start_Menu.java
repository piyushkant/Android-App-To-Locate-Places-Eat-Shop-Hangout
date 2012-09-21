/*
 * 
 */
package com.playcez;

import static com.playcez.PlaycezFacebook.loginProgress;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.playcez.bean.StorageManager;

// TODO: Auto-generated Javadoc
/**
 * The Class Start_Menu.
 */
public class Start_Menu extends Activity implements LocationListener,
		TabContentFactory, OnClickListener {

	/** The prog. */
	public static ProgressDialog prog;
	/** The eat_button. */
	private Button eat_button;
	/** The shop_button. */
	private Button shop_button;
	/** The hangout_button. */
	private Button hangout_button;

	/** The Suggestions. */
	private static String[] Suggestions;

	/** The near by places. */
	private ImageButton nearByPlaces;

	/** The user area. */
	private TextView userArea;

	/** The sm. */
	private StorageManager sm;

	/** The ids. */
	private int[] ids = {};// {R.drawable.androidtutorial1,
							// R.drawable.androidtutorial2,
							// R.drawable.androidtutorial3,
							// R.drawable.androidtutorial4};
	/** The food array. */
	public JSONArray foodArray;

	/** The rl. */
	public RelativeLayout rl;

	/** The txt view. */
	public TextView txtView;

	/** The _path. */
	private String _path;

	/** The selected image path. */
	private String selectedImagePath;

	/** The Constant SELECT_PICTURE. */
	private static final int SELECT_PICTURE = 1;

	/** The Constant CAMERA_PIC_REQUEST. */
	private static final int CAMERA_PIC_REQUEST = 2;

	/** The Constant NEARPLACE_CAPTURE_CALLBACK. */
	private static final int NEARPLACE_CAPTURE_CALLBACK = 3;

	/** The Constant NEARPLACE_SELECT_CALLBACK. */
	private static final int NEARPLACE_SELECT_CALLBACK = 4;

	/** The food. */
	private Json_Fetch food;

	/** The display. */
	static Display display;

	/** The screen width. */
	static float screenWidth;

	/** The screen height. */
	static float screenHeight;

	/** The grid view. */
	private GridView gridView;
	// List for title

	/** The title. */
	String[] title = { "Eat", "Shop", "Hangout", "Offers" };
	// String preference;
	/** The m thumb ids. */
	Integer[] mThumbIds = { R.drawable.start_food, R.drawable.start_shop,
			R.drawable.start_hangout, R.drawable.start_offers };

	/** The Constant ID_CHANGE. */
	private static final int ID_CHANGE = 1;

	/** The Constant ID_NEARBY. */
	private static final int ID_NEARBY = 2;

	/** The Constant ID_TAKE. */
	private static final int ID_TAKE = 3;

	/** The Constant ID_SELECT. */
	private static final int ID_SELECT = 4;

	/** The Constant ID_SETTINGS. */
	private static final int ID_SETTINGS = 5;

	/** The Constant ID_REPORT. */
	private static final int ID_REPORT = 6;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_menu);

		try {
			prog.dismiss();

		} catch (Exception e) {

		}

		display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();

		screenWidth = display.getWidth();
		screenHeight = display.getHeight();

		try {
			loginProgress.dismiss();
		} catch (Exception e) {

		}
		Log.d("screenWidth", screenWidth + "");
		Log.d("screenHeight", screenHeight + "");

		// LinearLayout ln_imageView = (LinearLayout)
		// findViewById(R.id.ln_imageview);

		if (screenHeight <= 400) {
			setContentView(R.layout.start_menu_small);
			// ln_imageView.setVisibility(View.GONE);
		}

		// QuickAction.............................................
		ActionItem changeItem = new ActionItem(ID_CHANGE, "Change Locality");
		ActionItem nearbyItem = new ActionItem(ID_NEARBY, "Places Nearby");
		ActionItem takeItem = new ActionItem(ID_TAKE, "Take photo");
		ActionItem selectItem = new ActionItem(ID_SELECT, "Select photo");
		ActionItem settingsItem = new ActionItem(ID_SETTINGS, "Settings");
		ActionItem reportItem = new ActionItem(ID_REPORT, "Report Bug");

		// use setSticky(true) to disable QuickAction dialog being dismissed
		// after an item is clicked
		changeItem.setSticky(true);
		nearbyItem.setSticky(true);

		// create QuickAction. Use QuickAction.VERTICAL or
		// QuickAction.HORIZONTAL param to define layout
		// orientation
		final QuickAction quickAction1 = new QuickAction(this,
				QuickAction.VERTICAL);
		final QuickAction quickAction2 = new QuickAction(this,
				QuickAction.VERTICAL);
		final QuickAction quickAction3 = new QuickAction(this,
				QuickAction.VERTICAL);

		// add action items into QuickAction
		quickAction1.addActionItem(changeItem);
		quickAction1.addActionItem(nearbyItem);
		quickAction2.addActionItem(takeItem);
		quickAction2.addActionItem(selectItem);
		quickAction3.addActionItem(settingsItem);
		quickAction3.addActionItem(reportItem);

		// Set listener for action item clicked
		quickAction1
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						ActionItem actionItem = quickAction1.getActionItem(pos);
						Log.d("Start_Menu", actionId + "");

						if (actionId == ID_CHANGE) {
							prog = ProgressDialog.show(Start_Menu.this,
									"Please wait", "Loading...", true);
							startActivityForResult(new Intent(
									getApplicationContext(),
									ChangeLocation.class), 1);
							quickAction1.dismiss();
						} else if (actionId == ID_NEARBY) {
							prog = ProgressDialog.show(Start_Menu.this,
									"Please wait", "Loading...", true);
							Intent mIntent = new Intent(
									getApplicationContext(), NearByPlaces.class);
							startActivity(mIntent);
							quickAction1.dismiss();
						}
					}
				});

		quickAction2
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						ActionItem actionItem = quickAction2.getActionItem(pos);

						Intent mIntent = new Intent(getApplicationContext(),
								NearByPlaces.class);

						if (actionId == ID_TAKE) {
							mIntent.putExtra("callback", "capture");
							startActivityForResult(mIntent,
									NEARPLACE_CAPTURE_CALLBACK);
							// callCamera();
						} else if (actionId == ID_SELECT) {
							mIntent.putExtra("callback", "select");
							startActivityForResult(mIntent,
									NEARPLACE_SELECT_CALLBACK);
							// callGallery();
						}
					}
				});

		quickAction3
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						if (actionId == ID_REPORT) {
							Intent mIntent = new Intent(
									getApplicationContext(), ReportBug.class);
							startActivity(mIntent);
							quickAction3.dismiss();
						}

						if (actionId == ID_SETTINGS) {
							Intent mIntent = new Intent(
									getApplicationContext(),
									SettingsActivity.class);
							startActivity(mIntent);
							quickAction3.dismiss();
						}
					}
				});

		// Bottom Buttons Functioning
		ImageButton location = (ImageButton) this
				.findViewById(R.id.buttonBarImageButton1);
		ImageButton camera = (ImageButton) this
				.findViewById(R.id.buttonBarImageButton2);
		ImageButton reviews = (ImageButton) this
				.findViewById(R.id.buttonBarImageButton3);

		location.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// startActivityForResult(new Intent(getApplicationContext(),
				// ChangeLocation.class),1);
				quickAction1.show(v);
			}
		});

		camera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				quickAction2.show(v);
			}
		});

		reviews.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Intent mIntent = new
				// Intent(getApplicationContext(),NearByPlaces.class);
				// startActivity(mIntent);
				quickAction3.show(v);
			}
		});

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		firstTimeAlert(0);

		sm = new StorageManager(this);
		sm.open();
		try {
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			String preferedLocation = null;
			try {
				preferedLocation = myData.getString("userPreferedStreet", null);
				Log.d("preferedLocation", preferedLocation + "");

				if (preferedLocation != null || preferedLocation.length() != 0
						|| preferedLocation.equals("")
						|| !(preferedLocation.length() > 1)) {
					Log.d("Start_Menu", "User Prefered Street is not set");
					setUpLocation();
				}

			} catch (Exception e) {
				// e.printStackTrace();
				setUpLocation();
			}
			// setUpLocation();
			storeNearestStreets();
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			String prevUserStreet = myData.getString("prevUserStreet", "");// ////////////its
																			// not
																			// user
																			// street
																			// but
																			// locality
			String currUserStreet = myData.getString("userStreet", "");

			userArea = (TextView) findViewById(R.id.locationView);
			userArea.setText(" " + currUserStreet);

			Log.d("streets", prevUserStreet + " " + prevUserStreet.length()
					+ " " + currUserStreet + " " + currUserStreet.length());
			String csid = myData.getString("csid", "");
			if (currUserStreet.equals(prevUserStreet)) {
				Log.d("streets", "coming equal");
				// foodArray = sm.fetchJsonFromDatabase();
				foodArray = sqlStorage(
						"http://playcez.com/api_getOptionList.php?csid=" + csid,
						csid, "9999");
				Log.d("TAg", foodArray.toString());
			} else {
				/*
				 * food = new Json_Fetch(
				 * "http://playcez.com/api_getOptionList.php?csid=" + csid,
				 * myData.getString("uid", ""), myData.getString(
				 * "playcez_token", ""));
				 */
				Log.d("streets", "coming unequal");
				// foodArray = new JSONArray(food.json);
				foodArray = sqlStorage(
						"http://playcez.com/api_getOptionList.php?csid=" + csid,
						csid, "9999");
				// sm.storeJsonInDatabase(foodArray);
				Log.d("tag", foodArray.toString());
				Editor myEdit = myData.edit();
				myEdit.putString("prevUserStreet", currUserStreet);
				myEdit.commit();
			}
			//sm.close();
			// Parsing JSON
			Suggestions = new String[2000];

			for (int i = 0; i < 2000; i++) {
				Suggestions[i] = "";
			}

			for (int i = 0; i < foodArray.length(); i++) {
				/*
				 * int size = foodArray.getJSONObject(i).length(); for (int j =
				 * 0; j < size; j++) {
				 */// Log.d("Start_Menu.java", "Suggestions[i]");
				Suggestions[i] = foodArray.getJSONObject(i).getString("label");
				// }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Suggestions);

		final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.suggestions_list);

		textView.setBackgroundResource(R.drawable.searchbartext);
		textView.setThreshold(1);
		textView.setAdapter(adapter);
		textView.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				textView.setBackgroundResource(R.drawable.searchbar);
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				textView.setBackgroundResource(R.drawable.searchbartext);
			}

			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});

		textView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				for (int i = 0; i < foodArray.length(); i++) {
					try {
						Log.d("Start_Menu", "OnItemClickListener");
						String name = textView.getText().toString();
						// for (int j = 0; j <
						// foodArray.getJSONObject(i).length(); j++) {
						Log.d("Start_Menu", "OnItemClickListener" + " " + name);
						if (foodArray.getJSONObject(i).getString("label")
								.equals(name)) {
							int bkid = foodArray.getJSONObject(i).getInt("id");
							SharedPreferences myData = getSharedPreferences(
									"myData", MODE_PRIVATE);
							String bkId = foodArray.getJSONObject(i).getString(
									"id");
							Editor myEdit = myData.edit();
							myEdit.putString("bkId", bkId);
							myEdit.putString("preference", "auto");
							myEdit.commit();

							Intent a = new Intent(getApplicationContext(),
									SearchResults.class);
							Log.d("bkid to be sent", bkid + "");
							a.putExtra("bkid", bkid);
							/*
							 * prog = ProgressDialog.show(Start_Menu.this,
							 * "Please wait", "Loading...", true);
							 */
							startActivityForResult(a, 1);
							textView.getText().clear();
							textView.setBackgroundResource(R.drawable.searchbartext);
						}
						// }
					} catch (JSONException e) {
						Log.d("Start_Menu.java", "Exception");
						e.printStackTrace();
					}
				}
			}

		});

		gridView = (GridView) findViewById(R.id.GridView02);
		gridView.setAdapter(new ImageAdapter(this));
		gridView.setSelector(R.drawable.roundimage);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent mIntent;
				SharedPreferences myData = getSharedPreferences("myData",
						MODE_PRIVATE);
				Editor myEdit = myData.edit();

				switch (position) {
				case 0:
					mIntent = new Intent(getApplicationContext(),
							Recommendations.class);
					myEdit.putString("preference", "food");
					myEdit.commit();
					try {
						startActivity(mIntent);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("Start Menu", "Start Activity Failed!");
					}
					break;
				case 1:
					mIntent = new Intent(getApplicationContext(),
							Recommendations.class);
					myEdit.putString("preference", "shop");
					myEdit.commit();
					try {
						startActivity(mIntent);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("Start Menu", "Start Activity Failed!");
					}
					break;
				case 2:
					mIntent = new Intent(getApplicationContext(),
							Recommendations.class);
					myEdit.putString("preference", "hangout");
					myEdit.commit();
					try {
						startActivity(mIntent);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("Start Menu", "Start Activity Failed!");
					}
					break;
				case 3:
					mIntent = new Intent(getApplicationContext(),
							NearOffers.class);
					myEdit.putString("preference", "offers");
					myEdit.commit();
					try {
						startActivity(mIntent);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("Start Menu", "Start Activity Failed!");
					}
					break;
				}
			}
		});

		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		boolean registered = myData.getBoolean("registered", false);
		Editor edit = myData.edit();
		edit.putBoolean("registered", true);
		edit.commit();

		if (!registered) {
			register();
		}
	}

	/** The Constant AUTH. */
	public final static String AUTH = "authentication";

	/**
	 * Sql storage.
	 * 
	 * @param url
	 *            the url
	 * @param csid
	 *            the csid
	 * @param id
	 *            the id
	 * @return the jSON array
	 */
	public JSONArray sqlStorage(String url, String csid, String id) {
		sm = new StorageManager(this);
		try{
			sm.close();
		}catch(Exception e){
			
		}
		sm.open();
		JSONArray resultArray = null;
		try {
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			Json_Fetch result = new Json_Fetch(url,
					myData.getString("uid", ""), myData.getString(
							"playcez_token", ""));
			Log.d("SQLite", "sql");
			if (!sm.checkThreshold(csid, id)) {
				Log.d("SQLite", "threshold say true");
				try {
					resultArray = new JSONArray(result.json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				sm.storeResultArray(csid, id, resultArray);
			}

			resultArray = sm.fetchResultArray(csid, id);
			Log.d("SQLite", resultArray + "");

		} catch (Exception e) {
			//e.printStackTrace();
		}
		sm.close();
		return resultArray;
	}

	/**
	 * Register.
	 */
	public void register() {
		Log.w("C2DM", "start registration process");
		Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
		intent.putExtra("app",
				PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		//
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		String uid = myData.getString("uid", "nulllll");

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Editor edit = prefs.edit();
		edit.putString("uid", uid);
		edit.commit();

		intent.putExtra("sender", "info@playcez.com");
		startService(intent);
	}

	/**
	 * Show registration id.
	 * 
	 * @param view
	 *            the view
	 */
	public void showRegistrationId(View view) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String string = prefs.getString(AUTH, "n/a");
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
		Log.d("C2DM RegId", string);
	}

	/**
	 * Call camera.
	 */
	public void callCamera() {
		_path = Environment.getExternalStorageDirectory() + File.separator
				+ "DCIM" + File.separator + "Camera" + File.separator
				+ System.currentTimeMillis() + ".jpg";
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	}

	/**
	 * Call gallery.
	 */
	public void callGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				SELECT_PICTURE);
	}

	/**
	 * Gets the path.
	 * 
	 * @param uri
	 *            the uri
	 * @return the path
	 */
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * The Class ImageAdapter.
	 */
	public class ImageAdapter extends BaseAdapter {

		/** The m context. */
		private Context mContext;

		/**
		 * Instantiates a new image adapter.
		 * 
		 * @param c
		 *            the c
		 */
		public ImageAdapter(Context c) {
			// TODO Auto-generated constructor stub
			mContext = c;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		public int getCount() {
			// TODO Auto-generated method stub
			return mThumbIds.length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mThumbIds[arg0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View grid;

			if (convertView == null) {
				grid = new View(mContext);
				LayoutInflater inflater = getLayoutInflater();
				grid = inflater.inflate(R.layout.grid_item, parent, false);
			} else {
				grid = (View) convertView;
			}

			ImageView imageView = (ImageView) grid
					.findViewById(R.id.icon_image);

			TextView textView = (TextView) grid.findViewById(R.id.icon_text);
			imageView.setImageResource(mThumbIds[position]);
			textView.setText(title[position]);

			return grid;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//
		try {
			prog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
		TextView t = (TextView) findViewById(R.id.locationView);
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		storeNearestStreets();
		t.setText(" " + myData.getString("userPreferedStreet", ""));

		try {
			if (requestCode == CAMERA_PIC_REQUEST) {
				Bitmap bitmap = BitmapFactory.decodeFile(_path);
				if (bitmap != null) {
					Intent act = new Intent(getApplicationContext(),
							PhotoEditor2.class);
					act.putExtra("path", _path);
					startActivity(act);
				}
			} else if (resultCode == RESULT_OK) {
				if (requestCode == SELECT_PICTURE) {
					Uri selectedImageUri = data.getData();
					selectedImagePath = getPath(selectedImageUri);
					Log.d(selectedImagePath, selectedImagePath);

					Intent act = new Intent(getApplicationContext(),
							PhotoEditor2.class);
					act.putExtra("path", selectedImagePath);
					startActivity(act);

					Log.d(selectedImagePath, selectedImagePath);
				}
			} else if (requestCode == NEARPLACE_CAPTURE_CALLBACK) {
				callCamera();
			} else if (requestCode == NEARPLACE_SELECT_CALLBACK) {
				callGallery();
			} else if (requestCode == 5) {
				myData = getSharedPreferences("myData", MODE_PRIVATE);
				Editor myEdit = myData.edit();
				myEdit.putString("user_lng", myData.getString("lng", ""));
				myEdit.putString("user_lat", myData.getString("lat", ""));
				myEdit.commit();
			}
			super.onActivityResult(requestCode, resultCode, data);
		} catch (Exception e) {
			Log.e("Start_Camera", "exception thrown ");
			e.printStackTrace();
		}
	}

	/**
	 * Update tutorial.
	 * 
	 * @param i
	 *            the i
	 */
	public void updateTutorial(final int i) {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		final AlertDialog alert = dialog.create();
		dialog.setTitle("Set your Location");

		Log.d("here", "inside for loop");
		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.firsttimealert, null);

		Drawable drawable = getResources().getDrawable(ids[i]);
		view.setBackgroundDrawable(drawable);

		dialog.setView(view);

		if (i != 0) {
			final int prevIndex = i - 1;
			dialog.setPositiveButton("Previous",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialogInterface,
								int which) {
							updateTutorial(prevIndex);
						}
					});
		}

		String skipName = "Skip";
		Log.d("check", ids.length + " " + i);
		if (i != ids.length - 1) {
			final int nextIndex = i + 1;
			dialog.setNeutralButton("Next",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialogInterface,
								int which) {
							updateTutorial(nextIndex);
						}
					});
		} else {
			skipName = "Finish";
		}

		dialog.setNegativeButton(skipName,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialogInterface,
							int which) {
						dialog.create().hide();
					}
				});

		dialog.show();

	}

	/**
	 * First time alert.
	 * 
	 * @param i
	 *            the i
	 */
	public void firstTimeAlert(final int i) {
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		boolean firstTime = myData.getBoolean("firstTime", true);

		if (firstTime) {
			updateTutorial(i);
		}
		Editor myEdit = myData.edit();
		myEdit.putBoolean("firstTime", false);
		myEdit.commit();
	}

	/**
	 * Sets the up location.
	 */
	public void setUpLocation() {
		try {
			LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);

			String latitude = "";
			String longitude = "";

			if (!mLocationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				String locationprovider = mLocationManager.getBestProvider(
						criteria, true);
				Location mLocation = mLocationManager
						.getLastKnownLocation(locationprovider);
				latitude = mLocation.getLongitude() + "";
				Log.d("Latitudeg", latitude + "");
				longitude = mLocation.getLatitude() + "";
				Log.d("Longitudeg", longitude + "");

			} else {
				Location mLocation = mLocationManager
						.getLastKnownLocation(mLocationManager.NETWORK_PROVIDER);
				latitude = mLocation.getLongitude() + "";
				Log.d("Latitudei", latitude + "");
				longitude = mLocation.getLatitude() + "";
				Log.d("Longitudei", longitude + "");
			}

			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			Editor myEdit = myData.edit();
			myEdit.putString("lng", latitude);
			myEdit.putString("lat", longitude);
			myEdit.putString("user_lng", latitude);
			myEdit.putString("user_lat", longitude);
			myEdit.commit();

		} catch (Exception e) {
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			boolean firstCatch = myData.getBoolean("firstCatch", true);
			Editor myEdit = myData.edit();
			myEdit.putBoolean("firstCatch", false);
			myEdit.commit();
			if (firstCatch) {
				Toast.makeText(getApplicationContext(), "Set your Location",
						Toast.LENGTH_LONG).show();
				startActivityForResult(new Intent(getApplicationContext(),
						ChangeLocation.class), 5);
			}
			e.printStackTrace();
		}
	}

	/**
	 * Store nearest streets.
	 */
	public void storeNearestStreets() {
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		Editor myEdit = myData.edit();
		String lat = myData.getString("lat", "");
		String lng = myData.getString("lng", "");
		Log.d("lat and long", lat + " " + lng);
		Json_Fetch csidArray = new Json_Fetch(
				"https://playcez.com/api_getNearestCrossStreets.php?lat=" + lat
						+ "&lng=" + lng, myData.getString("uid", ""),
				myData.getString("playcez_token", ""));
		try {
			JSONArray streets = new JSONArray(csidArray.json);
			String csid = streets.getJSONObject(0).getString("cs_id");
			String street = streets.getJSONObject(0).getString("label");
			String preferedStreet = myData
					.getString("userPreferedStreet", null);

			if (preferedStreet != null)
				myEdit.putString("userStreet", preferedStreet);
			else
				myEdit.putString("userStreet", street);
			myEdit.putString("csid", csid);
			myEdit.commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onLocationChanged(android.location.
	 * Location)
	 */
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	public void onProviderDisabled(String provider) {
		//
		// Toast.makeText(getApplicationContext(), "GPS is Disabled",
		// Toast.LENGTH_LONG).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	public void onProviderEnabled(String provider) {
		//
		// Toast.makeText(getApplicationContext(), "GPS is Enabled",
		// Toast.LENGTH_LONG).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String,
	 * int, android.os.Bundle)
	 */
	public void onStatusChanged(String provider, int status, Bundle extras) {
		//
		// Toast.makeText(getApplicationContext(), "GPS status changed",
		// Toast.LENGTH_LONG).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String
	 * )
	 */
	public View createTabContent(String tag) {
		//

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets the tab color.
	 * 
	 * @param tabhost
	 *            the new tab color
	 */
	public static void setTabColor(TabHost tabhost) {
		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
			tabhost.getTabWidget().getChildAt(i)
					.setBackgroundColor(Color.BLACK); // unselected
		}
		tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
				.setBackgroundColor(Color.BLUE); // selected
	}
}
