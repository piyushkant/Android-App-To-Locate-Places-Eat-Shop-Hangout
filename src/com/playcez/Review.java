/*
 * 
 */
package com.playcez;

import static com.playcez.Start_Menu.prog;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.playcez.bean.Constants;
import com.playcez.bean.StorageManager;

// TODO: Auto-generated Javadoc
/**
 * The Class Review.
 */
public class Review extends ListActivity implements TabContentFactory,
		OnClickListener {

	// public static ProgressDialog backProgress;
	/** The Constant TAG. */
	final static String TAG = "Result";

	/** The view flipper. */
	private ViewFlipper viewFlipper;

	/** The Constant SWIPE_MIN_DISTANCE. */
	private static final int SWIPE_MIN_DISTANCE = 120;

	/** The Constant SWIPE_MAX_OFF_PATH. */
	private static final int SWIPE_MAX_OFF_PATH = 250;

	/** The Constant SWIPE_THRESHOLD_VELOCITY. */
	private static final int SWIPE_THRESHOLD_VELOCITY = 400;

	/** The flipper. */
	private ViewFlipper flipper;

	/** The view_no. */
	private int view_no;

	/** The gesture detector. */
	private GestureDetector gestureDetector;

	/** The gesture listener. */
	private View.OnTouchListener gestureListener;

	/** The prog dialog. */
	private ProgressDialog progDialog;

	/** The bitmap. */
	private Bitmap bitmap;

	/** The review array. */
	private JSONArray reviewArray;

	/** The result array. */
	private JSONArray resultArray;

	/** The dialog. */
	private Dialog dialog;

	/** The pid. */
	private String[] pid;

	/** The m inflater. */
	private LayoutInflater mInflater;

	/** The data. */
	private Vector<RowData> data;

	/** The rd. */
	RowData rd;

	/** The user_image. */
	private Bitmap[] user_image;

	/** The preload_fbuid. */
	private String[] preload_fbuid;

	// List for name
	/** The title_a. */
	List<String> title_a = new ArrayList<String>();

	// List for tip
	/** The details_a. */
	List<String> details_a = new ArrayList<String>();

	// List for image id
	/** The imgid_a. */
	List<Integer> imgid_a = new ArrayList<Integer>();

	/** The title. */
	String[] title;

	/** The detail. */
	String[] detail;

	/** The imgid. */
	Integer[] imgid;

	/** The i11. */
	private ImageView i11;

	/** The array. */
	public JSONArray array;

	/** The image flip flop. */
	private LinearLayout imageFlipFlop;

	/** The pics d. */
	private Drawable[] picsD;

	/** The preview. */
	private ImageView preview;

	/** The index. */
	private int index;

	/** The photo array. */
	private JSONArray photoArray;

	/** The photo_id. */
	private String[] photo_id;

	/** The sm. */
	private StorageManager sm;

	/** The start_no. */
	private int start_no;

	/** The place_id. */
	private String place_id;

	/** The thumbnails. */
	private GridView thumbnails;

	/** The count. */
	private int count;

	private TabHost tabs;

	private int page_no;

	private boolean flag;

	private static int checkImage;

	// Asyncrpnous download activity in background
	private AsyncTask<String, Integer, Long> dr;

	private Timer timer;

	private int check;

	RelativeLayout noImage;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabview);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		noImage = (RelativeLayout) findViewById(R.id.noImage);
		noImage.setVisibility(View.GONE);

		view_no = 1;
		try {
			// Define the flipping things
			flip();
			// Download the results and provide the view
			download();
			new DownloadResultArray().execute();
		} catch (Exception e) {
			e.printStackTrace();
			// showAlert("Check your internet connection!");
		}
		Log.d(TAG, "outside here");
		Drawable mySelector = getResources().getDrawable(R.drawable.app_phone);
		Drawable mySelector1 = getResources().getDrawable(
				R.drawable.tabselector);
		Drawable mySelector2 = getResources().getDrawable(
				R.drawable.tabselector1);
		Drawable mySelector3 = getResources().getDrawable(
				R.drawable.tabselector2);
		Drawable mySelector4 = getResources().getDrawable(
				R.drawable.tabselector3);

		tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();
		TabHost.TabSpec spec = tabs.newTabSpec("tag1");

		spec.setIndicator("CALL", mySelector);
		spec.setContent(R.id.layout_tab_two);
		tabs.addTab(spec);

		spec.setIndicator("INFO", mySelector1);
		spec.setContent(R.id.layout_tab_two);
		tabs.addTab(spec);

		spec.setIndicator("REVIEWS", mySelector2);
		spec.setContent(R.id.layout_tab_two);
		tabs.addTab(spec);

		spec.setIndicator("PHOTO", mySelector3);
		spec.setContent(R.id.layout_tab_two);
		tabs.addTab(spec);

		spec.setIndicator("MAP", mySelector4);
		spec.setContent(R.id.layout_tab_two);
		tabs.addTab(spec);

		tabs.setCurrentTab(1);

		for (int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
			RelativeLayout rl = (RelativeLayout) tabs.getTabWidget()
					.getChildAt(i);
			rl.setBackgroundResource(R.drawable.tab_indicator);
			tabs.getTabWidget().getChildAt(i).setPadding(0, 15, 0, 0);
		}

		page_no = 1;
		flag = false;
		tabs.setOnTabChangedListener(new OnTabChangeListener() {

			public void onTabChanged(String tabId) {
				Button show_more = (Button) findViewById(R.id.show_more);
				ViewFlipper vf = (ViewFlipper) findViewById(R.id.result);
				TextView sorry_msg = (TextView) findViewById(R.id.sorrymsg);
				RelativeLayout smily_widget = (RelativeLayout) findViewById(R.id.smily_widget);
				thumbnails = (GridView) findViewById(R.id.GridView02);

				thumbnails.setVisibility(View.INVISIBLE);
				sorry_msg.setText("");
				smily_widget.setVisibility(View.GONE);
				setListAdapter(null);
				vf.setVisibility(View.GONE);
				show_more.setVisibility(View.GONE);
				array = null;

				int i = tabs.getCurrentTab();
				if (i == Constants.APP_PHONE) {
					noImage.setVisibility(View.GONE);
					vf.setVisibility(View.VISIBLE);
					try {
						dialer();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else if (i == Constants.MOB_INFO) {
					noImage.setVisibility(View.GONE);
					vf.setVisibility(View.VISIBLE);
				} else if (i == Constants.MOB_REVIEWS) {
					noImage.setVisibility(View.GONE);
					RelativeLayout rl = (RelativeLayout) findViewById(R.id.header);
					rl.setVisibility(View.VISIBLE);

					// defining the views////////////////////
					
					// /////////////////////////////

					show_more.setVisibility(View.VISIBLE); // bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb
					count = 0;
					setReviewsView();
				} else if (i == Constants.MOB_PHOTOS) {
					prog = ProgressDialog.show(Review.this, "Please wait!",
							"Loading...", true);
					thumbnails.setVisibility(View.VISIBLE);

					Log.d("xxxxxxxxxxxxxxx", photoLength() + "");

					if (photoLength() == 0) {
						noImage.setVisibility(View.VISIBLE);
					} else {
						noImage.setVisibility(View.GONE);
					}

					new DownloadImage().execute();

				} else if (i == Constants.MOB_DIRECTIONS) {
					noImage.setVisibility(View.GONE);
					vf.setVisibility(View.VISIBLE);
					try {
						showMap();
					} catch (JSONException e) {
						//
						e.printStackTrace();
					}
				}
			}
		});

		check = 0;
		final Handler mHandler = new Handler();
		final Runnable mUpdateResults = new Runnable() {
			public void run() {
				AnimateandSwipeTutorial();
			}
		};

		int delay = 500; // delay
		int period = 10000; // repeat.
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				mHandler.post(mUpdateResults);
			}
		}, delay, period);
	}

	/**
	 * Animateand slide show.
	 */
	private void AnimateandSwipeTutorial() {
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		Editor edit = myData.edit();
		int swipe = myData.getInt("swipe_toast", 0);
		swipe++;
		edit.putInt("swipe_toast", swipe);
		edit.commit();
		if (check == 0 && swipe <= 5) {
			Toast.makeText(getApplicationContext(),
					"Swipe to move to next result", Toast.LENGTH_LONG).show();
		} else if (check == 1) {
			timer.cancel();
		}
		check++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		//
		super.onResume();
		final TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();
		tabs.setCurrentTab(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String
	 * )
	 */
	public View createTabContent(String tag) {
		return null;
	}

	/**
	 * Start home.
	 * 
	 * @param view
	 *            the view
	 */
	public void startHome(View view) {
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}

	/**
	 * Fetch result array.
	 */
	public void fetchResultArray() {

		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		String csid = myData.getString("csid", "9100401107066699");
		String preference = myData.getString("preference", "");
		String id;
		if (preference.equals("auto")) {
			id = myData.getString("bkId", "");
		} else {
			id = new Recommendations().prefId(preference);
		}
		Log.d(TAG, "this is id " + id);
		try {
			resultArray = new JSONArray(myData.getString("extra_array", ""));// sqlStorage("https://playcez.com/api_magicEndPoint.php?csid="+csid+"&id="+id,csid,id.replace(",",
																				// ""));//new
																				// JSONArray(a.json);
			Log.d(TAG, resultArray.length() + " : length of result array");
			pid = new String[resultArray.length()];
			for (int i = 0; i < resultArray.length(); i++) {
				pid[i] = resultArray.getJSONObject(i).getString("pid");
			}
		} catch (JSONException e1) {

			e1.printStackTrace();
		}
	}

	/**
	 * Download.
	 */
	public void download() {
		fetchResultArray();
	}

	/**
	 * Fetch reviews.
	 * 
	 * @param i
	 *            the i
	 */
	public void fetchReviews(int i, int page_no) {
		try {
			String rev_req = "https://playcez.com/api_getPlaceReviews.php?pid="
					+ pid[i] + "&page=" + page_no;
			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			Json_Fetch b = new Json_Fetch(rev_req, myData.getString("uid", ""),
					myData.getString("playcez_token", ""));
			reviewArray = new JSONArray(b.json);
			Log.d(TAG, reviewArray.length() + " : length of review array");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show alert.
	 * 
	 * @param msg
	 *            the msg
	 */
	/*
	 * public void showAlert(String msg) {
	 * 
	 * final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	 * dialog.setMessage(msg); LayoutInflater factory =
	 * LayoutInflater.from(this); final View view =
	 * factory.inflate(R.layout.firsttimealert, null); Drawable drawable =
	 * getResources().getDrawable( R.drawable.android_no_results);
	 * view.setBackgroundDrawable(drawable); dialog.setView(view);
	 * dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	 * public void onClick(DialogInterface dialogInterface, int which) { Intent
	 * i = new Intent(); setResult(1, i); finish(); } }); dialog.show(); }
	 */
	/**
	 * Facade view.
	 * 
	 * @throws JSONException
	 *             the jSON exception
	 */
	public void facadeView() throws JSONException {
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		int index = myData.getInt("index", 1);
		view_no = index + 1;
		for (int i = index; i < resultArray.length(); i++) {
			// view_no = i+1;
			createView();
			view_no++;
		}
		view_no = 1;
		for (int i = 0; i < index; i++) {
			createView();
			view_no++;
		}
	}

	/**
	 * Response.
	 * 
	 * Creates the views dynamically whenever user uses viewflipper to flip to
	 * either right or left.
	 * 
	 * @throws JSONException
	 *             the jSON exception
	 */
	public void createView() throws JSONException {

		// Define the layout
		LayoutInflater layoutInflater = (LayoutInflater) (getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE));

		// defining the views
		View v = layoutInflater.inflate(R.layout.result_item, null);

		// defining the text views for name, address, cross street, number and
		// distance.
		TextView name = (TextView) v.findViewById(R.id.name);
		TextView address = (TextView) v.findViewById(R.id.address);
		TextView cross_street = (TextView) v.findViewById(R.id.cross_street);
		TextView number1 = (TextView) v.findViewById(R.id.number1);
		TextView distance = (TextView) v.findViewById(R.id.distance);
		ImageView num = (ImageView) v.findViewById(R.id.result_no);
		TextView resNum = (TextView) v.findViewById(R.id.result_text_no);
		TextView subcat = (TextView) v.findViewById(R.id.subcat);
		ImageView sentiment = (ImageView) v.findViewById(R.id.overall_smily);

		ImageButton loveit = (ImageButton) v.findViewById(R.id.loveit);
		ImageButton emotionless = (ImageButton) v
				.findViewById(R.id.emotionless);
		ImageButton hateit = (ImageButton) v.findViewById(R.id.hateit);
		Button flagButton = (Button) v.findViewById(R.id.flagButton);

		flagButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent mIntent = new Intent(getApplicationContext(),
						ReportFlag.class);
				startActivity(mIntent);
			}
		});

		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		final Editor edit = myData.edit();

		loveit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent mIntent = new Intent(getApplicationContext(),
						AddSmallTip.class);
				mIntent.putExtra("position", 2);
				edit.putString("pid", pid[view_no - 1]);
				edit.commit();
				startActivity(mIntent);
			}

		});

		emotionless.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent mIntent = new Intent(getApplicationContext(),
						AddSmallTip.class);
				mIntent.putExtra("position", 0);
				edit.putString("pid", pid[view_no - 1]);
				edit.commit();
				startActivity(mIntent);
			}

		});

		hateit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent mIntent = new Intent(getApplicationContext(),
						AddSmallTip.class);
				mIntent.putExtra("position", 6);
				edit.putString("pid", pid[view_no - 1]);
				edit.commit();
				startActivity(mIntent);
			}

		});

		if (view_no < 10) {
			resNum.setText("  " + view_no);
		} else {
			resNum.setText(" " + view_no);
		}

		resNum.setTextSize(30);
		Drawable drawable = getResources().getDrawable(R.drawable.rank_show);
		num.setImageDrawable(drawable);

		// setting name of the place
		name.setText(resultArray.getJSONObject(view_no - 1).getString("name"));

		// Setting address
		address.setText(resultArray.getJSONObject(view_no - 1).getString(
				"address"));

		// Setting overall sentiment
		if (resultArray.getJSONObject(view_no - 1).getString("sentiment")
				.equals("1")) {
			sentiment.setImageResource(R.drawable.loveit);
		} else if (resultArray.getJSONObject(view_no - 1)
				.getString("sentiment").equals("0"))
			sentiment.setImageResource(R.drawable.emotionless);
		else if (resultArray.getJSONObject(view_no - 1).getString("sentiment")
				.equals("-1"))
			sentiment.setImageResource(R.drawable.hateit);

		// Setting cross street
		cross_street.setText(resultArray.getJSONObject(view_no - 1).getString(
				"cross_street"));
		// Setting cross street
		subcat.setText(resultArray.getJSONObject(view_no - 1).getString(
				"subcategory"));

		// Setting numbers
		final String numbers = resultArray.getJSONObject(view_no - 1)
				.getString("phone");
		number1.setText(numbers);

		// Setting distance
		String[] placeCords = new String[2];
		placeCords[0] = resultArray.getJSONObject(view_no - 1).getString("lat");
		placeCords[1] = resultArray.getJSONObject(view_no - 1).getString("lng");

		String[] userCords = new String[2];
		userCords[0] = myData.getString("lat", "");
		userCords[1] = myData.getString("lng", "");
		distance.setText(new NearByPlaces().calculateDistance(userCords,
				placeCords));

		viewFlipper.addView(v);

	}

	/**
	 * Flip.
	 */
	public void flip() {

		viewFlipper = (ViewFlipper) findViewById(R.id.result);

		// Handling gesture
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};

		viewFlipper.setOnClickListener(this);
		viewFlipper.setOnTouchListener(gestureListener);

	}

	/**
	 * Dialer.
	 * 
	 * @throws JSONException
	 *             the jSON exception
	 */
	public void dialer() throws JSONException {

		final String numbers = resultArray.getJSONObject(view_no - 1)
				.getString("phone");

		dialog = new Dialog(Review.this);
		dialog.setContentView(R.layout.buttondialog);
		dialog.setCanceledOnTouchOutside(true);

		if (numbers.length() != 0) {
			dialog.setTitle("Select a number");
		} else {
			LayoutInflater layoutInflater = (LayoutInflater) (getApplicationContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			View v = layoutInflater.inflate(R.layout.result_item, null);

			ImageButton loveit = (ImageButton) v.findViewById(R.id.loveit);
			ImageButton emotionless = (ImageButton) v
					.findViewById(R.id.emotionless);
			ImageButton hateit = (ImageButton) v.findViewById(R.id.hateit);

			SharedPreferences myData = getSharedPreferences("myData",
					MODE_PRIVATE);
			final Editor edit = myData.edit();
			
			loveit.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Intent mIntent = new Intent(
							getApplicationContext(), AddSmallTip.class);
					mIntent.putExtra("position", 2);
					edit.putString("pid", pid[view_no - 1]);
					edit.commit();
					startActivity(mIntent);
				}

			});

			emotionless.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Intent mIntent = new Intent(
							getApplicationContext(), AddSmallTip.class);
					mIntent.putExtra("position", 0);
					edit.putString("pid", pid[view_no - 1]);
					edit.commit();
					startActivity(mIntent);
				}

			});

			hateit.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Intent mIntent = new Intent(
							getApplicationContext(), AddSmallTip.class);
					mIntent.putExtra("position", 6);
					edit.putString("pid", pid[view_no - 1]);
					edit.commit();
					startActivity(mIntent);
				}

			});
			
			dialog.setTitle("Sorry, no reviews found here.\n\n Be the first one to review.");
		}

		ListView l = (ListView) dialog.findViewById(R.id.numbers);

		final String[] nums = numbers.split(",");
		;

		l.setAdapter(new ArrayAdapter<String>(Review.this,
				android.R.layout.simple_list_item_1, nums));

		l.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				dialPhone(nums[position]);
			}

		});

		OnTouchListener o = new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dialog.dismiss();
				}
				return true;
			}
		};

		if (numbers.length() == 0 || nums.length != 1) {
			dialog.show();
		} else {
			dialPhone(nums[0]);
		}

	}

	/**
	 * Dial phone.
	 * 
	 * @param number
	 *            the number
	 */
	private void dialPhone(String number) {

		Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ number));

		startActivity(dialIntent);

	}

	/**
	 * Show map.
	 * 
	 * @throws JSONException
	 *             the jSON exception
	 */
	public void showMap() throws JSONException {

		final String lat = resultArray.getJSONObject(view_no - 1).getString(
				"lat");
		final String lng = resultArray.getJSONObject(view_no - 1).getString(
				"lng");

		loadMap(lat, lng);
	}

	/**
	 * Load map.
	 * 
	 * @param lat
	 *            the lat
	 * @param lng
	 *            the lng
	 */
	public void loadMap(String lat, String lng) {

		Intent mIntent = new Intent(getApplicationContext(), Maps.class);
		mIntent.putExtra("latitude2", lat);
		mIntent.putExtra("longitude2", lng);
		startActivity(mIntent);
	}

	/**
	 * Encodespace.
	 * 
	 * @param str
	 *            the str
	 * @return the string
	 */
	public String encodespace(String str) {
		return str.replace(" ", "%20");
	}

	/**
	 * Encodecomma.
	 * 
	 * @param str
	 *            the str
	 * @return the string
	 */
	public String encodecomma(String str) {
		return str.replace(",", "%2C");
	}

	/**
	 * Share.
	 * 
	 * @param subject
	 *            the subject
	 * @param text
	 *            the text
	 */
	private void share(String subject, String text) {

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(intent, "Share"));

	}

	/**
	 * The Class DownloadResultArray.
	 * 
	 * @author anmol
	 * 
	 *         Class defined for download of the results in the background and
	 *         then create the view for the user.
	 */
	private class DownloadResultArray extends AsyncTask<String, Integer, Long> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Long doInBackground(String... params) {

			try {
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Long result) {
			//
			try {
				if (resultArray != null && resultArray.length() != 0) {
					SharedPreferences myData = getSharedPreferences("myData",
							MODE_PRIVATE);
					int index = myData.getInt("index", 1);
					dr = new DownloadReviews()
							.execute("" + index, page_no + "");
					Log.d(TAG, "downloading reviews");
				} else {
					// Show alert if there are no results.
					// showAlert("Sorry, none of the type around!");
				}
				facadeView();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				prog.dismiss();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * The Class DownloadReviews.
	 */
	private class DownloadReviews extends AsyncTask<String, Integer, Long> {

		private int len;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Long doInBackground(String... params) {
			try {
				len = Integer.parseInt(params[2]);
			} catch (Exception e) {
				len = 0;
			}
			try {
				int i = Integer.parseInt(params[0]);
				fetchReviews(i, Integer.parseInt(params[1]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Long result) {
			try {
				if (user_image == null || user_image.length == 0) {
					user_image = new Bitmap[reviewArray.length()];
					/*
					 * if(tabs.getCurrentTab() == 2){ Log.d(TAG,
					 * "inside post execute setting the tab currentview");
					 * tabs.setCurrentTab(1); tabs.setCurrentTab(2); }
					 */
				} else {
					Bitmap[] extra_image = user_image.clone();
					user_image = new Bitmap[reviewArray.length()
							+ extra_image.length];
					for (int i = 0; i < extra_image.length; i++) {
						user_image[i] = extra_image[i];
					}
				}
				downloadFBImages();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				prog.dismiss();
				if (flag) {
					setReviewsView();
				}
				flag = true;
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Download fb images.
	 */
	public void downloadFBImages() {
		String fbuid = null;
		String twid = null;
		preload_fbuid = new String[reviewArray.length()];
		int start = user_image.length - reviewArray.length();
		for (int i = 0, j = start; i < reviewArray.length(); i++, j++) {
			String url = null;
			try {
				// fbuid = reviewArray.getJSONObject(i).getString("fbuid");
				// twid = reviewArray.getJSONObject(i).getString("twid");
				url = reviewArray.getJSONObject(i).getString("imageUrl");
				Log.d("url", url);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (url != null) {
				new DownloadFBImages().execute(url, j + "");
			}
		}
	}

	/**
	 * The Class DownloadImage.
	 */
	private class DownloadImage extends AsyncTask<String, Integer, Long> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Long doInBackground(String... params) {
			try {
				getThumbnail();
				Log.d("checkImage", checkImage + "");

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Long result) {
			//
			try {
				thumbnails = (GridView) findViewById(R.id.GridView02);
				thumbnails.setAdapter(new ImageAdapter(Review.this));
				thumbnails.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						Intent mIntent = new Intent(getApplicationContext(),
								PhotoSwitcher.class);
						mIntent.putExtra("index", position);
						mIntent.putExtra("photo_id", photo_id);
						prog = ProgressDialog.show(Review.this, "Please wait!",
								"Loading...", true);
						startActivity(mIntent);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			prog.dismiss();
		}
	}

	/**
	 * The Class DownloadFBImages.
	 */
	private class DownloadFBImages extends AsyncTask<String, Integer, Long> {

		/** The index. */
		int index;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Long doInBackground(String... params) {
			try {
				index = Integer.parseInt(params[1]);
				user_image[index] = loadImageFromNetwork(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			try {
				if (tabs.getCurrentTab() == 2) {
					Log.d(TAG,
							"inside post execute setting the tab currentview");
					// tabs.setCurrentTab(1);
					// tabs.setCurrentTab(2);
				}
			} catch (Exception e) {
			}
		}
	}

	// Setting Animation
	/**
	 * In from left animation.
	 * 
	 * @return the animation
	 */
	public Animation inFromLeftAnimation() {

		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	/**
	 * Out to right animation.
	 * 
	 * @return the animation
	 */
	public Animation outToRightAnimation() {

		if (view_no == 1) {
			view_no = resultArray.length();
		} else {
			view_no--;
		}

		int num = view_no - 1;
		dr = new DownloadReviews().execute("" + num, "1");

		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	/**
	 * In from right animation.
	 * 
	 * @return the animation
	 */
	public Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	/**
	 * Out to left animation.
	 * 
	 * @return the animation
	 */
	public Animation outToLeftAnimation() {

		if (view_no == resultArray.length()) {
			view_no = 1;
		} else {
			view_no++;
		}

		int num = view_no - 1;
		dr = new DownloadReviews().execute("" + num, "1");
		Log.d(TAG, "view_no : " + view_no);

		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	// Handling Gesture
	/**
	 * The Class MyGestureDetector.
	 */
	public class MyGestureDetector extends SimpleOnGestureListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.view.GestureDetector.SimpleOnGestureListener#onFling(android
		 * .view.MotionEvent, android.view.MotionEvent, float, float)
		 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				dr.cancel(true);
				page_no = 1; // reviews page number is set to one
				user_image = null;
				reviewArray = null;
				array = null; // empty the review array to null
				flag = false; // reviews are set only on click
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					viewFlipper.setInAnimation(inFromRightAnimation());
					viewFlipper.setOutAnimation(outToLeftAnimation());
					viewFlipper.showNext();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					viewFlipper.setInAnimation(inFromLeftAnimation());
					viewFlipper.setOutAnimation(outToRightAnimation());
					viewFlipper.showPrevious();
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}

	/**
	 * Load image from network.
	 * 
	 * @param urlString
	 *            the url string
	 * @return the bitmap
	 */
	public Bitmap loadImageFromNetwork(String urlString) {
		try {
			HttpGet HttpRequest = new HttpGet(new URL(urlString).toURI());
			HttpClient HttpClient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) HttpClient
					.execute(HttpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
			final long contentLength = bufHttpEntity.getContentLength();

			if (contentLength >= 0) {
				InputStream is = bufHttpEntity.getContent();
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				return bitmap;
			} else {
				return null;
			}
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sets the reviews view.
	 */
	public void setReviewsView() {
		try {
			try {
				if (array == null) {
					title_a = new ArrayList<String>();
					imgid_a = new ArrayList<Integer>();
					details_a = new ArrayList<String>();
				}
				array = reviewArray;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			// Parsing JSON
			if (array.length() == 0) {
				TextView sorryMsg = (TextView) findViewById(R.id.sorrymsg);
				sorryMsg.setText("Be the first to add review at "
						+ resultArray.getJSONObject(view_no - 1).getString(
								"name"));
				RelativeLayout smily_widget = (RelativeLayout) findViewById(R.id.smily_widget);
				smily_widget.setVisibility(View.VISIBLE);
				Button show_more = (Button) findViewById(R.id.show_more);
				show_more.setVisibility(View.GONE);
			}

			for (int i = 0; i < array.length(); i++) {
				try {
					title_a.add(array.getJSONObject(i).getString("uname")
							.toString());
					int resourceId = this.getResources().getIdentifier("icon",
							"drawable", this.getPackageName());
					imgid_a.add(resourceId);
					details_a.add(array.getJSONObject(i).getString("review")
							.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			title = new String[title_a.size()];
			imgid = new Integer[imgid_a.size()];
			detail = new String[details_a.size()];

			title_a.toArray(title);
			imgid_a.toArray(imgid);
			details_a.toArray(detail);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Inflating Layout
		inflateLayout();
	}

	public void inflateLayout() {
		mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		data = new Vector<RowData>();
		for (int i = 0; i < title.length; i++) {
			try {
				rd = new RowData(i, title[i], detail[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			data.add(rd);
		}
		CustomAdapter adapter = new CustomAdapter(this, R.layout.tips_item,
				R.id.title, data);
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
	}

	/**
	 * The Class RowData.
	 */
	private class RowData {

		/** The m id. */
		protected int mId;

		/** The m title. */
		protected String mTitle;

		/** The m detail. */
		protected String mDetail;

		/**
		 * Instantiates a new row data.
		 * 
		 * @param id
		 *            the id
		 * @param title
		 *            the title
		 * @param detail
		 *            the detail
		 */
		RowData(int id, String title, String detail) {
			mId = id;
			mTitle = title;
			mDetail = detail;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return mId + " " + mTitle + " " + mDetail;
		}
	}

	// creating my adapter
	/**
	 * The Class CustomAdapter.
	 */
	private class CustomAdapter extends ArrayAdapter<RowData> {

		/**
		 * Instantiates a new custom adapter.
		 * 
		 * @param context
		 *            the context
		 * @param resource
		 *            the resource
		 * @param textViewResourceId
		 *            the text view resource id
		 * @param objects
		 *            the objects
		 */
		public CustomAdapter(Context context, int resource,
				int textViewResourceId, List<RowData> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			TextView title = null;
			TextView detail = null;
			i11 = null;
			Button show_more = null;
			RowData rowData = getItem(position);

			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.tips_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}

			holder = (ViewHolder) convertView.getTag();
			title = holder.gettitle();
			title.setText(rowData.mTitle);

			detail = holder.getdetail();
			detail.setText(rowData.mDetail);
			i11 = holder.getImage();
			i11.setImageBitmap(user_image[position]);

			show_more = (Button) findViewById(R.id.show_more);
			if (reviewArray.length() == 10) {
				show_more.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						page_no++;
						prog = ProgressDialog.show(Review.this, "Please wait!",
								"Loading...", true);
						dr = new DownloadReviews().execute(view_no - 1 + "",
								page_no + "");
					}
				});
			} else {
				show_more.setVisibility(View.GONE);
			}
			return convertView;
		}

		/**
		 * The Class ViewHolder.
		 */
		private class ViewHolder {

			/** The m row. */
			private View mRow;

			/** The title. */
			private TextView title = null;

			/** The detail. */
			private TextView detail = null;

			/** The i11. */
			private ImageView i11 = null;

			/**
			 * Instantiates a new view holder.
			 * 
			 * @param row
			 *            the row
			 */
			public ViewHolder(View row) {
				mRow = row;
			}

			/**
			 * Gets the title.
			 * 
			 * @return the title
			 */
			public TextView gettitle() {
				if (null == title) {
					title = (TextView) mRow.findViewById(R.id.title);
				}
				return title;
			}

			/**
			 * Gets the detail.
			 * 
			 * @return the detail
			 */
			public TextView getdetail() {
				if (null == detail) {
					detail = (TextView) mRow.findViewById(R.id.detail);
				}
				return detail;
			}

			/**
			 * Gets the image.
			 * 
			 * @return the image
			 */
			public ImageView getImage() {
				if (null == i11) {
					i11 = (ImageView) mRow.findViewById(R.id.img);
				}
				return i11;
			}
		}
	}

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
					//
					e.printStackTrace();
				}
				sm.storeResultArray(csid, id, resultArray);
			}

			resultArray = sm.fetchResultArray(csid, id);
			Log.d("SQLite", resultArray + "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		sm.close();
		return resultArray;
	}

	// Creating my own image adapter
	/**
	 * The Class ImageAdapter.
	 */
	public class ImageAdapter extends BaseAdapter {

		/** The m context. */
		Context mContext;

		/** The Constant ACTIVITY_CREATE. */
		public static final int ACTIVITY_CREATE = 10;

		/**
		 * Instantiates a new image adapter.
		 * 
		 * @param c
		 *            the c
		 */
		public ImageAdapter(Context c) {
			mContext = c;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {

			View v;
			if (convertView == null) {
				LayoutInflater li = getLayoutInflater();
				v = li.inflate(R.layout.photo_grid_item, null);
				ImageView iv = (ImageView) v.findViewById(R.id.icon_image02);
				iv.setMinimumWidth(400);
				iv.setMinimumHeight(100);
				iv.setImageDrawable(picsD[position]);
			} else {
				v = convertView;
			}
			return v;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		public int getCount() {
			//
			return picsD.length;
		}
	}

	/**
	 * Initialize.
	 */
	public void initialize() {

		/*
		 * imageFlipFlop = (LinearLayout)findViewById(R.id.flip_flop); preview =
		 * (ImageView)findViewById(R.id.preview);
		 */
	}

	/**
	 * Gets the thumbnail.
	 * 
	 * @return the thumbnail
	 */
	public void getThumbnail() {

		String pic_url = "https://playcez.com/api_getPics.php?pid="
				+ pid[view_no - 1];
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		Json_Fetch a = new Json_Fetch(pic_url, myData.getString("uid", ""),
				myData.getString("playcez_token", ""));
		// "https://playcez.com/api_getPics.php?pid="+pid[view_no-1]);
		Log.d(TAG, pid[view_no - 1] + "");
		photoArray = null;

		try {
			photoArray = new JSONArray(a.json);
		} catch (Exception e) {
		}

		Log.d(TAG, " whatttt this is length");
		int length = photoArray.length();// ///////////////^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		checkImage = length;
		Log.d("length photo", length + "");

		Bitmap myThumbnail;
		picsD = new Drawable[length];
		Log.d(TAG, length + " this is length");

		photo_id = new String[length];

		for (int i = 0; i < length; i++) {
			try {
				photo_id[i] = photoArray.getString(i);
				String url = "http://images.playcez.com/places/thumbs/"
						+ photo_id[i] + ".jpg";
				myThumbnail = loadImageFromNetwork(url);
				Drawable d = new BitmapDrawable(myThumbnail);
				picsD[i] = d;
				Log.d(TAG, i + " this is length");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	int photoLength() {
		String pic_url = "https://playcez.com/api_getPics.php?pid="
				+ pid[view_no - 1];
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		Json_Fetch a = new Json_Fetch(pic_url, myData.getString("uid", ""),
				myData.getString("playcez_token", ""));

		try {
			photoArray = new JSONArray(a.json);
		} catch (Exception e) {
		}

		int length = photoArray.length();

		return length;
	}

}
