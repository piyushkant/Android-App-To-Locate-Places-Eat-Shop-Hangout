/*
 * 
 */
package com.playcez;

import static com.playcez.Start_Menu.prog;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.playcez.base64.Base64;

// TODO: Auto-generated Javadoc
/**
 * Adds reviews about places with an option to add photo of that place.
 * 
 * @author anmol
 * 
 */
public class AddSmallTip extends Activity {

	/** The check fb. */
	private CheckBox checkFB;

	/** The position. */
	private String position;

	/** The fb check. */
	private int fbCheck = 0;

	/** The review edit text. */
	private EditText reviewEditText;
	// Button submit1;
	/** The fb share. */
	private ImageView fbShare;

	/** The tw share. */
	private ImageView twShare;

	/** The attach button. */
	private Button attachButton;

	/** The post button. */
	private Button postButton;

	/** The text area. */
	private EditText textArea;

	/** The text count. */
	private TextView textCount;

	/** The emoticon view. */
	private ImageView emoticonView;

	/** The _path. */
	private String _path;

	/** The click fb share. */
	private boolean clickFbShare = false;

	/** The click tw share. */
	private boolean clickTwShare = false;

	/** The Constant ID_TAKE. */
	private static final int ID_TAKE = 0;

	/** The Constant ID_SELECT. */
	private static final int ID_SELECT = 1;

	/** The Constant SELECT_PICTURE. */
	private static final int SELECT_PICTURE = 1;

	/** The selected image path. */
	private String selectedImagePath;

	/** The Constant CAMERA_PIC_REQUEST. */
	private static final int CAMERA_PIC_REQUEST = 2;

	/** The upload_preview. */
	private ImageView upload_preview;

	/** The place_selected. */
	private boolean place_selected;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_small_tip);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// //////////////
		ActionItem takeItem = new ActionItem(ID_TAKE, "Take photo");
		ActionItem selectItem = new ActionItem(ID_SELECT, "Select photo");
		// takeItem.setSticky(true);
		// selectItem.setSticky(true);

		Intent mIntent = getIntent();
		_path = mIntent.getStringExtra("path");

		// place_selected = true;

		try {
			if (_path.length() > 5) {
				TextView topLogoo = (TextView) findViewById(R.id.topLogoo);
				topLogoo.setText("Add Comment");

				ImageView emoticonView = (ImageView) findViewById(R.id.emoticonView);
				emoticonView.setVisibility(View.INVISIBLE);

				Button attachButton = (Button) findViewById(R.id.attachButton);
				attachButton.setVisibility(View.INVISIBLE);

				/*
				 * Button location_detector = (Button)
				 * findViewById(R.id.location_detector);
				 * location_detector.setVisibility(View.VISIBLE);
				 */
			}
		} catch (Exception e) {

		}
		Log.d("add small tip", "hererre");
		Bitmap bmp = BitmapFactory.decodeFile(_path);
		bmp = new ThumbnailUtils().extractThumbnail(bmp, 50, 50);
		Log.d("add small tip", "hererre");
		upload_preview = (ImageView) findViewById(R.id.upload_preview);
		upload_preview.setImageBitmap(bmp);
		Log.d("add small tip", "hererre");
		final QuickAction quickAction = new QuickAction(this,
				QuickAction.VERTICAL);

		quickAction.addActionItem(takeItem);
		quickAction.addActionItem(selectItem);

		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						if (actionId == ID_TAKE) {
							_path = Environment.getExternalStorageDirectory()
									+ File.separator + "DCIM" + File.separator
									+ "Camera" + File.separator
									+ System.currentTimeMillis() + ".jpg";
							Log.d("path", _path);
							File file = new File(_path);
							Uri outputFileUri = Uri.fromFile(file);

							Intent cameraIntent = new Intent(
									android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
									outputFileUri);
							// request code

							startActivityForResult(cameraIntent,
									CAMERA_PIC_REQUEST);
						} else if (actionId == ID_SELECT) {
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(Intent.createChooser(intent,
									"Select Picture"), SELECT_PICTURE);
						}
					}
				});

		// //////Buttons Functioning/////////////////////////
		attachButton = (Button) findViewById(R.id.attachButton);
		postButton = (Button) findViewById(R.id.shareButton);
		fbShare = (ImageView) findViewById(R.id.fbShare);
		twShare = (ImageView) findViewById(R.id.twShare);
		textArea = (EditText) findViewById(R.id.smalltip);
		emoticonView = (ImageView) findViewById(R.id.emoticonView);
		textCount = (TextView) findViewById(R.id.textCount);

		attachButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				quickAction.show(v);
			}
		});

		fbShare.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (clickFbShare == false) {
					fbShare.setImageResource(R.drawable.facebooklogo);
					clickFbShare = true;
					fbCheck = 1;
				} else if (clickFbShare == true) {
					fbShare.setImageResource(R.drawable.fadefacebooklogo);
					clickFbShare = false;
					fbCheck = 0;
				}
			}
		});

		twShare.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (clickTwShare == false) {
					twShare.setImageResource(R.drawable.twitterlogo);
					clickTwShare = true;
				} else if (clickTwShare == true) {
					twShare.setImageResource(R.drawable.fadetwitterlogo);
					clickTwShare = false;
				}
			}
		});

		textArea.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				120) });
		textCount.setText("/120");
		postButton.setBackgroundResource(R.drawable.redbutton);
		postButton.setTextColor(Color.parseColor("#FFFFFF"));

		textArea.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				textCount.setText(textArea.getText().length() + "/120");
				if (textArea.getText().length() == 120) {
					textCount.setTextColor(Color.parseColor("#FF0000"));
				} else {
					textCount.setTextColor(Color.parseColor("#000000"));
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if (textArea.getText().toString().trim().equals("")) {
				}
			}
		});

		// TextView textCount = (TextView)findViewById(R.id.textCount);
		// textCount.setText(String.valueOf(textArea.length())+"/120");

		Bundle extras = getIntent().getExtras();
		position = extras.getInt("position") + "";

		if (position.equals("0")) {
			emoticonView.setImageResource(R.drawable.emotionless);
		} else if (position.equals("6")) {
			emoticonView.setImageResource(R.drawable.hateit);
		} else if (position.equals("2")) {
			emoticonView.setImageResource(R.drawable.loveit);
		}

		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		final String uid = myData.getString("uid", "");
		final String pid = myData.getString("pid", "");
		final String fbuid = myData.getString("fbuid", "");
		final String twid = myData.getString("twid", "");

		/*
		 * try{ if(pid.equals("")){ place_selected = false; } }catch(Exception
		 * e){ place_selected = false; }
		 */
		postButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				/*
				 * submitReview(uid, position, smallReview + " ", pid, fbuid,
				 * twid, fbCheck); try{ if(_path.length() > 5){ prog =
				 * ProgressDialog.show(AddSmallTip.this, "Uplaoding image!",
				 * "please wait", true); new Uploader().execute("");
				 * Log.d("Uploading image", "image e e"); } }catch(Exception e){
				 * }
				 */
				final String smallReview = textArea.getText().toString();
				prog = ProgressDialog.show(AddSmallTip.this, "Adding review!",
						"please wait", true);
				new Uploader().execute(uid, position, smallReview, pid, fbuid,
						twid, fbCheck + "");
			}
		});
	}

	/**
	 * Submit review.
	 * 
	 * @param uid
	 *            the uid
	 * @param sentiment
	 *            the sentiment
	 * @param review
	 *            the review
	 * @param pid
	 *            the pid
	 * @param fbuid
	 *            the fbuid
	 * @param twid
	 *            the twid
	 * @param toPublish
	 *            the to publish
	 */
	@Deprecated
	public void submitReview(String uid, String sentiment, String review,
			String pid, String fbuid, String twid, int toPublish) {
		String revURL = "https://playcez.com/api_addReview.php";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(revURL);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			nameValuePairs.add(new BasicNameValuePair("sentiment", sentiment));
			nameValuePairs.add(new BasicNameValuePair("review", review));
			nameValuePairs.add(new BasicNameValuePair("pid", pid));
			nameValuePairs.add(new BasicNameValuePair("fbuid", fbuid));
			nameValuePairs.add(new BasicNameValuePair("twid", twid));
			nameValuePairs.add(new BasicNameValuePair("toPublish", toPublish
					+ ""));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

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
				alertSuccess();
				Log.d("the result", result);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Alert success.
	 */
	public void alertSuccess() {

		/*Toast.makeText(getApplicationContext(),
				"Your review is successfully uploaded!", Toast.LENGTH_LONG)
				.show();*/
		Intent i = new Intent();
		setResult(1, i);
		try {
			if (_path.length() < 5) {
				startActivity(new Intent(getApplicationContext(),
						Start_Menu.class));
				finish();
			}
		} catch (Exception e) {
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
		/*
		 * if(resultCode == 1){ place_selected = true; }
		 */
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		Editor edit = myData.edit();
		edit.putString("tip_class", "true");
		edit.commit();
		try {
			if (requestCode == CAMERA_PIC_REQUEST) {
				Bitmap bitmap = BitmapFactory.decodeFile(_path);
				if (bitmap != null) {
					Intent act = new Intent(getApplicationContext(),
							PhotoEditor2.class);
					act.putExtra("path", _path);
					startActivityForResult(act, 6);
				}
			} else if (resultCode == RESULT_OK) {
				if (requestCode == SELECT_PICTURE) {
					Uri selectedImageUri = data.getData();
					selectedImagePath = getPath(selectedImageUri);
					Log.d(selectedImagePath, selectedImagePath);
					
					Intent act = new Intent(getApplicationContext(),
							PhotoEditor2.class);
					act.putExtra("path", selectedImagePath);
					startActivityForResult(act, 6);
					
					Log.d(selectedImagePath, selectedImagePath);
				}
			} else {
				_path = myData.getString("path", null);
				Bitmap bmp = new ThumbnailUtils().extractThumbnail(BitmapFactory.decodeFile(_path), 50, 50);
				upload_preview = (ImageView) findViewById(R.id.upload_preview);
				upload_preview.setImageBitmap(bmp);
				edit.putString("tip_class", "false");
				edit.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onActivityResult(requestCode, resultCode, data);
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
	 * Class for uploading image in background and then updating the view.
	 * 
	 * @author anmol
	 * 
	 */
	public class Uploader extends AsyncTask<String, Integer, Long> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Long doInBackground(String... params) {
			// Looper.prepare();
			try {
				Looper.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				/*
				 * Log.d("check out", "" + params[0] + params[1] + params[2] +
				 * params[3] + params[4] + params[5] + params[6]);
				 */
				uploadImage(BitmapFactory.decodeFile(_path), params[0],
						params[1], params[2], params[3], params[4], params[5],
						params[6]);
				// Log.d("my image", "this is my image " +
				// BitmapFactory.decodeFile(_path));
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
				// Toast.makeText(getApplicationContext(),
				// "Image was successfully uploaded", Toast.LENGTH_LONG).show();
				File file = new File(_path);
				boolean deleted = file.delete();
				/*
				 * if(deleted) Log.d("deleted", "file deleted");
				 
				startActivity(new Intent(getApplicationContext(),
						Start_Menu.class));
				finish();
				*/
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				prog.dismiss();
			} catch (Exception e) {

			}
			// Toast.makeText(getApplicationContext(),
			// " Your photo has been successfully uploaded",
			// Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Given a bitmap uploads the image to playcez server plus its reviews about
	 * a place.
	 * 
	 * @param bitmapOrg
	 *            the bitmap org
	 * @param uid
	 *            the uid
	 * @param sentiment
	 *            the sentiment
	 * @param review
	 *            the review
	 * @param pid
	 *            the pid
	 * @param fbuid
	 *            the fbuid
	 * @param twid
	 *            the twid
	 * @param toPublish
	 *            the to publish
	 */
	public void uploadImage(Bitmap bitmapOrg, String uid, String sentiment,
			String review, String pid, String fbuid, String twid,
			String toPublish) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {
			Log.d("add small tip", (bitmapOrg.getHeight() + " " + bitmapOrg.getWidth())); 
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 90, bao);
			// byte [] ba = bao.toByteArray();
			String ba1 = Base64.encodeBytes(bao.toByteArray());
			nameValuePairs.add(new BasicNameValuePair("image_file", ba1));
			bitmapOrg.recycle();
		} catch (Exception e) {
		}

		// ///////////////review///////////////////////////////////////
		// Log.d("all values", uid + " " + review + " pid " + pid + " " + fbuid
		// + " " + twid + " " + toPublish);
		nameValuePairs.add(new BasicNameValuePair("uid", uid));
		nameValuePairs.add(new BasicNameValuePair("sentiment", sentiment));
		nameValuePairs.add(new BasicNameValuePair("review", review));
		nameValuePairs.add(new BasicNameValuePair("pid", pid));
		nameValuePairs.add(new BasicNameValuePair("fbuid", fbuid));
		nameValuePairs.add(new BasicNameValuePair("twid", twid));
		nameValuePairs.add(new BasicNameValuePair("toPublish", toPublish));
		// //////////////////////////////////////////////////////////////////

		try {
			// Log.d("AddSmallTip", "Inside Upload");
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"https://playcez.com/api_imageUploader.php");// (getString(R.string.WebServiceURL));http://192.168.1.4:8085/xampp/myprogram/playcez/image_upload.php
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
				Log.d("add small tip", result);
				try {
					Intent i = new Intent();
					setResult(1, i);		
					try {
						finish();
					} catch (Exception e) {
					}
				} catch (Exception e) {
				}
				// Log.d("PhotoEditor image", result);
			}

			// Toast.makeText(getApplicationContext(), "Review added s",
			// Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), "Error in uploading!",
			// Toast.LENGTH_LONG).show();
			Log.e("log_tag", "Error in http connection " + e.toString());
		}
	}

}
