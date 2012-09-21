package com.playcez;

import static com.playcez.Start_Menu.prog;

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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class PhotoSwitcher extends Activity implements OnClickListener {

	private int totPhotos;
	private LinearLayout imageFlipFlop;
	private Drawable[] picsD;
	private String[] photo_id;
	private Bitmap[] user_image;
	private int i;

	// ////////////////////View Flipper /////////////////////////////
	private ViewFlipper viewFlipper;

	/** The Constant SWIPE_MIN_DISTANCE. */
	private static final int SWIPE_MIN_DISTANCE = 120;

	/** The Constant SWIPE_MAX_OFF_PATH. */
	private static final int SWIPE_MAX_OFF_PATH = 250;

	/** The Constant SWIPE_THRESHOLD_VELOCITY. */
	private static final int SWIPE_THRESHOLD_VELOCITY = 400;

	/** The view_no. */
	private int view_no;

	/** The gesture detector. */
	private GestureDetector gestureDetector;

	/** The gesture listener. */
	private View.OnTouchListener gestureListener;

	private int index;

	String flagEndPoint = "http://playcez.com/api_flagPic.php";
	static String fbid;
	ImageView flagPhoto;
	ImageView sharePhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_switcher);

		RelativeLayout switcherLayout = (RelativeLayout) findViewById(R.id.switcherLayout);
		switcherLayout.setVisibility(View.GONE);

		index = getIntent().getIntExtra("index", 0);
		photo_id = getIntent().getStringArrayExtra("photo_id");
		totPhotos = photo_id.length;

		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		fbid = myData.getString("fbuid", "");

		flip();
		user_image = new Bitmap[totPhotos];
		for (int i = index; i < totPhotos; i++) {
			download(i);
			new DownloadImages().execute(i + "");
		}

		for (int i = 0; i < index; i++) {
			download(i);
			new DownloadImages().execute(i + "");

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
			view_no = totPhotos;
		} else {
			view_no--;
		}

		int num = view_no - 1;

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

		if (view_no == totPhotos) {
			view_no = 1;
		} else {
			view_no++;
		}

		int num = view_no - 1;

		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	/**
	 * Flip.
	 */
	public void flip() {

		viewFlipper = (ViewFlipper) findViewById(R.id.photo_flipper);

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

	public void download(int i) {
		String url = "http://images.playcez.com/places/pics/" + photo_id[i]
				+ ".jpg";
		user_image[i] = new Review().loadImageFromNetwork(url);
		Log.d("PhotoSwitcher", photo_id[i] + " " + url);
	}

	private class DownloadImages extends AsyncTask<String, Integer, Long> {
		// private int i;

		@Override
		protected Long doInBackground(String... params) {
			// Looper.prepare();
			try {
				i = Integer.parseInt(params[0]);
				// download(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			//
			try {
				LayoutInflater layoutInflater = (LayoutInflater) (getApplicationContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
				View v = layoutInflater.inflate(R.layout.image_item, null);
				ImageView photo = (ImageView) v.findViewById(R.id.big_photo);
				photo.setImageBitmap(user_image[i]);

				flagPhoto = (ImageView) findViewById(R.id.flagPhoto);
				sharePhoto = (ImageView) findViewById(R.id.switcherShare);

				/*
				 * flagPhoto.setOnClickListener(new View.OnClickListener() {
				 * 
				 * public void onClick(View v) { postFlagPhoto(flagEndPoint,
				 * photo_id[i].toString()); } });
				 * 
				 * sharePhoto.setOnClickListener(new View.OnClickListener() {
				 * 
				 * public void onClick(View v) {
				 * sharePic("http://playcez.com/pics/"+photo_id[i].toString());
				 * } });
				 */

				viewFlipper.addView(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
			prog.dismiss();
		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
	}

	void postFlagPhoto(String endPoint, String value) {
		String addPlaceURL = endPoint;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(addPlaceURL);

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("uid", fbid));
			nameValuePairs.add(new BasicNameValuePair("picid", value));

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

				Log.d("Checking..", result + "");
				Toast.makeText(getApplicationContext(),
						"Report Sent Successfully", Toast.LENGTH_LONG).show();
				Log.d("the result", result);
			}
		} catch (Exception e) {

		}
	}

	private void sharePic(String imageUrl) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
		intent.putExtra(Intent.EXTRA_TEXT, imageUrl);
		startActivity(Intent.createChooser(intent, "Share"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.menu, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.sharePhoto:
			sharePic("http://playcez.com/pics/" + photo_id[i].toString());
			return (true);

		case R.id.flagPhoto:
			postFlagPhoto(flagEndPoint, photo_id[i].toString());
			return (true);
		}

		return super.onOptionsItemSelected(item);
	}

}
