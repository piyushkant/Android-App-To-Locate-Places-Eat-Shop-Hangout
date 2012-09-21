/*
 * 
 */
package com.playcez;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.playcez.bean.Constants;
import com.playcez.bean.Filter;

// TODO: Auto-generated Javadoc
/**
 * The Class PhotoEditor2.
 */
public class PhotoEditor2 extends Activity {

	/** The Constant TAG. */
	private static final String TAG = "PhotoEditor";

	/** The image flip flop. */
	private LinearLayout imageFlipFlop;

	/** The bitmap. */
	private Bitmap bitmap;

	/** The pics d. */
	private Drawable[] picsD;

	/** The filtered image. */
	private Drawable filteredImage;

	/** The preview. */
	private ImageView preview;

	/** The prog dialog. */
	private ProgressDialog progDialog;

	/** The index. */
	private int index;

	/** The _path. */
	private String _path;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_editor2);

		try {
			initialize();
			getBitmap();
			addImagesToFlipFlop();
		} catch (Exception e) {
		}
	}

	/**
	 * Initialize.
	 */
	public void initialize() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		imageFlipFlop = (LinearLayout) findViewById(R.id.flip_flop);
		preview = (ImageView) findViewById(R.id.preview);
		progDialog = ProgressDialog.show(PhotoEditor2.this, "Loading...",
				"please wait", true);
	}

	/**
	 * Gets the bitmap.
	 * 
	 * @return the bitmap
	 */
	public void getBitmap() {
		Bundle extras = getIntent().getExtras();
		_path = extras.getString("path");
		bitmap = BitmapFactory.decodeFile(_path);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width < 300 || height < 250) {
			Toast.makeText(getApplicationContext(),
					"Sorry, your image is too small to be uploaded!",
					Toast.LENGTH_LONG).show();
			finish();
		}
		float scaleWidth = ((float) 900) / width;
		float scaleHeight = (float) scaleWidth;// ((float) newHeight) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// recreate the new Bitmap
		bitmap = Bitmap
				.createBitmap(bitmap, 0, 0, width, height, matrix, false);
		new LoadEffects().execute("" + 0);
		setName(0);
	}

	/**
	 * Gets the thumbnail.
	 * 
	 * @return the thumbnail
	 */
	@Deprecated
	public void getThumbnail() {

		Bitmap myThumbnail = new ThumbnailUtils().extractThumbnail(bitmap, 50,
				50);
		picsD = new Drawable[Constants.totNoOfEffects];

		for (int i = 0; i < Constants.totNoOfEffects; i++) {
			Drawable d = new Filter().effects(myThumbnail, i);
			picsD[i] = d;
		}
	}

	/**
	 * Adds the images to flip flop.
	 */
	public void addImagesToFlipFlop() {

		Bitmap myThumbnail = new ThumbnailUtils().extractThumbnail(bitmap, 50,
				50);

		WindowManager mWindowManager = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		;
		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

		for (int i = 0; i < (Constants.totNoOfEffects - 2); i++) {

			Drawable d = new Filter().effects(myThumbnail, i);

			ImageView im = new ImageView(this);
			im.setImageDrawable(d);
			im.setBackgroundResource(R.drawable.light_gradient);

			LayoutParams params = new LayoutParams(50, 50);
			if (screenWidth > 200 && screenHeight > 500) {
				params = new LayoutParams(100, 100);
			}
			params.setMargins(10, 10, 10, 10);
			im.setLayoutParams(params);
			im.setContentDescription(setName(i));

			final int position = i;
			im.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					//
					progDialog = ProgressDialog.show(PhotoEditor2.this,
							"Adding Effects!", "please wait", true);
					index = position;
					setName(position);
					new LoadEffects().execute("" + position);
				}

			});
			setName(0);
			imageFlipFlop.addView(im);
		}
		Button done = (Button) findViewById(R.id.done);
		done.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String new_path = Environment.getExternalStorageDirectory()
						+ File.separator + "DCIM" + File.separator + "Camera"
						+ File.separator + System.currentTimeMillis() + ".jpg";
				try {
					FileOutputStream out = new FileOutputStream(new_path);
					Bitmap bmp = ((BitmapDrawable) filteredImage).getBitmap();
					bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
				} catch (Exception e) {
					e.printStackTrace();
				}

				Intent mIntent = new Intent(getApplicationContext(),
						AddSmallTip.class);
				// mIntent.putExtra("class", "photo_editor");
				mIntent.putExtra("path", new_path);
				SharedPreferences myData = getSharedPreferences("myData",
						MODE_PRIVATE);
				Editor edit = myData.edit();
				edit.putString("path", new_path);
				edit.commit();
				try {
					String prev_class = myData.getString("tip_class", "false");
					if (prev_class.equals("true")) {
						Log.d(TAG, "hererererer");
						setResult(6, getIntent());
						finish();
					} else {
						startActivity(mIntent);
					}
				} catch (Exception e) {
					startActivity(mIntent);
				}
			}
		});

		Button cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * The Class LoadEffects.
	 * 
	 * @author anmol
	 * 
	 *         It loads effects in background and update the gallery view.
	 */
	private class LoadEffects extends AsyncTask<String, Integer, Long> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Long doInBackground(String... position) {
			// Looper.prepare();
			try {
				Filter filter = new Filter();
				filteredImage = filter.effects(bitmap,
						Integer.parseInt(position[0]));
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
				preview.setImageDrawable(filteredImage);
				progDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//
		// finish();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Sets the name.
	 * 
	 * @param position
	 *            the position
	 * @return true, if successful It sets the name of the photo based on the
	 *         effect selected
	 */
	public CharSequence setName(int position) {

		try {
			CharSequence cs = "";
			TextView name = (TextView) findViewById(R.id.name);

			switch (position) {

			case 0:
				cs = "Original image";
				break;
			case 1:
				cs = "Gamma image";
				break;
			case 2:
				cs = "Contrast image";
				break;
			case 3:
				cs = "Dull image";
				break;
			case 4:
				cs = "Saturated image";
				break;
			case 5:
				cs = "Red filtered image";
				break;
			case 6:
				cs = "Green filtered image";
				break;
			case 7:
				cs = "Sunlight filtered image";
				break;
			case 8:
				cs = "Grayscale image";
				break;
			case 9:
				cs = "Bright image";
				break;
			case 10:
				cs = "Snowy image";
				break;
			case 11:
				cs = "Right tilted image";
				break;
			case 12:
				cs = "Left tilted image";
				break;
			default:
				cs = "Original image";
				break;
			}

			name.setText(cs);

			return cs;
		} catch (Exception e) {
			return "Original image";
		}

	}

	/**
	 * Generates a uniqueId for a photo to be uploaded.
	 * 
	 * @return the string
	 */
	public String uniqueId() {
		/*
		 * String time = System.currentTimeMillis() + ""; time =
		 * time.substring(1,10); String server="1009"; Random rand = new
		 * Random(); String random = "" + (long)((99999 - 10000 + 1) *
		 * rand.nextDouble()); Log.d(TAG, random); String id = (server + time +
		 * random);
		 */

		Random rand = new Random();
		String random = "" + (long) 100 * rand.nextDouble();
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		String id = myData.getString("uid", "") + random;

		return id;

	}

}
