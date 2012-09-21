/*
 * 
 */
package com.playcez;

import static com.playcez.Start_Menu.prog;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

// TODO: Auto-generated Javadoc
/**
 * The Class Tutorial3.
 */
public class Tutorial3 extends Activity implements OnClickListener {

	/** The tot photos. */
	private int totPhotos;

	/** The image flip flop. */
	private LinearLayout imageFlipFlop;

	/** The pics d. */
	private Drawable[] picsD;

	/** The photo_id. */
	private String[] photo_id;

	/** The user_image. */
	private Bitmap[] user_image;

	// ////////////////////View Flipper /////////////////////////////
	/** The view flipper. */
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

	/** The timer. */
	private Timer timer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_switcher);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ImageView flagPhoto = (ImageView) findViewById(R.id.flagPhoto);
		flagPhoto.setVisibility(View.GONE);

		TextView sendFlagText = (TextView) findViewById(R.id.sendFlagText);
		sendFlagText.setVisibility(View.GONE);

		ImageView switcherShare = (ImageView) findViewById(R.id.switcherShare);
		switcherShare.setVisibility(View.GONE);

		TextView sharePicText = (TextView) findViewById(R.id.sharePicText);
		sharePicText.setVisibility(View.GONE);

		totPhotos = 3;
		flip();
		view_no = 1;
		user_image = new Bitmap[totPhotos];
		download(0);
		for (int i = 0; i < totPhotos; i++) {
			new DownloadImages().execute(i + "");
		}

		final Handler mHandler = new Handler();
		// Create runnable for posting
		final Runnable mUpdateResults = new Runnable() {
			public void run() {
				AnimateandSlideShow();
			}
		};

		int delay = 2000; // delay
		int period = 10000; // repeat
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
	private void AnimateandSlideShow() {
		viewFlipper.setInAnimation(inFromRightAnimation());
		viewFlipper.setOutAnimation(outToLeftAnimation());
		viewFlipper.showNext();
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

		view_no--;
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
			timer.cancel();
			prog = ProgressDialog.show(Tutorial3.this, "Please wait",
					"Loading...", true);
			startActivity(new Intent(getApplicationContext(), Start_Menu.class));
			finish();
		} else {
			view_no++;
		}

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
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
						&& view_no != 1) {
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

	/**
	 * not Downloading just setting from resources.
	 * 
	 * @param i
	 *            the i
	 */
	public void download(int i) {
		user_image[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.tut1);
		user_image[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.tut2);
		user_image[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.tut3);

		// Log.d("PhotoSwitcher", photo_id[i] + " " + url);
	}

	/*
	 * public void initialize(){ imageFlipFlop =
	 * (LinearLayout)findViewById(R.id.switcher02); picsD = new
	 * Drawable[totPhotos]; }
	 * 
	 * public void addImagesToSwitcher(){
	 * 
	 * for(int i = 0; i < totPhotos; i++){ ImageView im = new ImageView(this);
	 * im.setImageBitmap(user_image[i]); Log.d("PhotoSwitcher", picsD[i]+"");
	 * LayoutParams params = new
	 * LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
	 * params.setMargins(10, 10, 10, 10); im.setLayoutParams(params);
	 * 
	 * imageFlipFlop.addView(im); } }
	 */
	/**
	 * The Class DownloadReviews.
	 */
	private class DownloadImages extends AsyncTask<String, Integer, Long> {

		/** The i. */
		private int i;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Long result) {
			try {
				LayoutInflater layoutInflater = (LayoutInflater) (getApplicationContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
				View v = layoutInflater.inflate(R.layout.image_item, null);
				ImageView photo = (ImageView) v.findViewById(R.id.big_photo);
				photo.setImageBitmap(user_image[i]);
				viewFlipper.addView(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// prog.dismiss();
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

}
