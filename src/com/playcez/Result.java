/*
 * 
 */
package com.playcez;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

// TODO: Auto-generated Javadoc
// 
/**
 * The Class SuggestionResults.
 */
public class Result extends Activity implements OnClickListener{

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
    private static JSONArray reviewArray;

    
    /** The result array. */
    private static JSONArray resultArray;
    
	
    /** The name. */
    private LinkedList<String> name = new LinkedList<String>();
    
    /** The address. */
    private LinkedList<String> address = new LinkedList<String>();
    
    /** The cross_street. */
    private LinkedList<String> cross_street = new LinkedList<String>();
    
    /** The phone. */
    private LinkedList<String> phone = new LinkedList<String>();
    
    /** The num_of_reviews. */
    private LinkedList<String> num_of_reviews = new LinkedList<String>();
    
    
	/** The dialog. */
	private Dialog dialog;
	
	/** The pid. */
	private String[] pid;
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.result);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		view_no = 1;
		Log.d("view_no",view_no+"");
		progDialog = ProgressDialog.show(this, "Please wait!", "Loading...", true);
		
		try{
			//Define the flipping things
			flip();
			Log.d("view_no",view_no+"");
			//Download the results and provide the view
			new DownloadResultArray().execute();
				
			
		} catch(Exception e){
			
			e.printStackTrace();
			
			showAlert("Check your internet connection!");
			
		}
		
	}

	/**
	 * Fetch result array.
	 */
	public void fetchResultArray(){
		
		Intent mIntent = getIntent();
		
		int bkid = mIntent.getIntExtra("bkid", 700);
			   
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		
		String lat = myData.getString("lat", "");
		
		String lng = myData.getString("lng", "");
		
		String csid = myData.getString("csid", "9100401107066699");
		
		Log.d("csiddddddddddddd", csid+"");
		
		Json_Fetch a = new Json_Fetch("http://playcez.com/api_magicEndPoint.php?csid="+csid+"&id=5,6,7,9");
	   
		try {
			resultArray = new JSONArray(a.json);
			Log.d(TAG, resultArray.length()+" " + resultArray);
			pid = new String[resultArray.length()];
		
			for(int i = 0; i < resultArray.length(); i++){
				pid[0] = resultArray.getJSONObject(0).getString("pid");
				Log.d("pid", pid+"");
				Log.d("pid[0]", pid[0]+"");
			}
			
		} catch (JSONException e1) {
			
				e1.printStackTrace();
				
		}

	}
	
	/**
	 * Download.
	 */
	public void download(){
		
		fetchResultArray();
		
	}
	
	/**
	 * Fetch reviews.
	 */
	public void fetchReviews(){
		try {
		
			String rev_req = "http://playcez.com/api_getPlaceReviews.php?pid="+pid[0]+"&page=1";
		    
			Log.d("Result", pid[0]);
			
			Json_Fetch b = new Json_Fetch(rev_req);
			
			reviewArray = new JSONArray(b.json);
		
		} catch (JSONException e) {
	
			e.printStackTrace();
		
		}
	
	}
	
	
	/**
	 * Show alert.
	 *
	 * @param msg the msg
	 */
	public void showAlert(String msg){

		
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		
		dialog.setMessage(msg);
		
		LayoutInflater factory = LayoutInflater.from(this);
		
		final View view = factory.inflate(R.layout.firsttimealert, null);
		
		Drawable drawable = getResources().getDrawable(R.drawable.android_no_results);
		
		view.setBackgroundDrawable(drawable);
		
		dialog.setView(view);
		
		dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface dialogInterface, int which) {
				Intent i = new Intent();
				setResult(1,i);
				finish();
				//startActivity(new Intent(Result.this, QuickBite.class));
			}
			
		});
		
		dialog.show();

		
	}
	
	/**
	 * Sets the button click listeners.
	 * @throws JSONException 
	 */
	public void setButtonClickListeners() throws JSONException{
		
		ImageButton next = (ImageButton) findViewById(R.id.next); 
		
			next.setOnClickListener(new OnClickListener(){
	
				public void onClick(View v) {
					// 
					try {
						viewFlipper.setInAnimation(inFromRightAnimation());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					viewFlipper.setOutAnimation(outToLeftAnimation());
					
					viewFlipper.showNext();
				}
				
			});
		
		ImageButton prev = (ImageButton) findViewById(R.id.previous);
			
			prev.setOnClickListener(new OnClickListener(){
	
				public void onClick(View v) {
	
					try {
						viewFlipper.setInAnimation(inFromLeftAnimation());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                
					viewFlipper.setOutAnimation(outToRightAnimation());
	                
					viewFlipper.showPrevious();
				}
				
			});
		
		ImageButton map = (ImageButton) findViewById(R.id.directions); 

		map.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// 
				try {
					
					showMap();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});

		
		ImageButton share = (ImageButton) findViewById(R.id.share); 

		share.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// 
				
				share();
				
			}
			
		});

		
		
		ImageButton dialer = (ImageButton) findViewById(R.id.call); 
		
		dialer.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// 
				try {
				
					dialer();
					
				} catch (JSONException e) {
				
					e.printStackTrace();
				}
			}
			
		});

		onClickShowReviews();
		
	}

	/**
	 * Called to check if number of results are one or zero
	 * then make the next and previous buttons invisible.
	 * 
	 */
	public void setButtonVisibility(){
		
		
		if(resultArray.length() == 1 || resultArray.length() == 0){
			ImageButton next = (ImageButton) findViewById(R.id.next);
			next.setVisibility(View.INVISIBLE);
			
			ImageButton previous = (ImageButton) findViewById(R.id.previous);
			previous.setVisibility(View.INVISIBLE);
		}
		
	}
	
	/**
	 * Facade view.
	 *
	 * @throws JSONException the jSON exception
	 */
	public void facadeView() throws JSONException{
		
		setButtonClickListeners();
		
		setButtonVisibility();
		
		
		for(int i = 0; i < resultArray.length(); i++){
			
			view_no = i+1;
			
			createView();
		
			
			view_no++;
		}
		
		view_no = 1;
	}
	
	
	/**
	 * Response.
	 * 
	 * Creates the views dynamically whenever user uses viewflipper to flip
	 * to either right or left.
	 *
	 * @throws JSONException the jSON exception
	 */
	public void createView() throws JSONException{
		
		//Define the layout
        LayoutInflater layoutInflater = (LayoutInflater)(getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        
        //defining the views
        View v = layoutInflater.inflate(R.layout.result_item, null);

        //defining the text views for name, address, cross street, number and distance.
        TextView name = (TextView)v.findViewById(R.id.name);
        
        TextView address = (TextView)v.findViewById(R.id.address);
        
        TextView cross_street = (TextView)v.findViewById(R.id.cross_street);
        
        TextView number1 = (TextView)v.findViewById(R.id.number1);
		
        TextView distance=(TextView)v.findViewById(R.id.distance);
        
        
        ImageView num = (ImageView)v.findViewById(R.id.result_no);
        
        
        TextView resNum = (TextView)v.findViewById(R.id.result_text_no);
        
        
        if(view_no < 10){

        	resNum.setText("  " + view_no);
        	
        } else{
        	
        	resNum.setText(" "+view_no);
        	
        }
        
        resNum.setTextSize(30);

        Drawable drawable = getResources().getDrawable(R.drawable.rank_show);
		
        num.setImageDrawable(drawable);
        
        
        //setting name of the place
        name.setText(resultArray.getJSONObject(view_no-1).getString("name"));

        //Setting address
        address.setText(resultArray.getJSONObject(view_no-1).getString("address")); 

        //Setting cross street
        cross_street.setText(resultArray.getJSONObject(view_no-1).getString("cross_street"));

        //Setting numbers
        final String numbers = resultArray.getJSONObject(view_no-1).getString("phone");
        number1.setText(numbers);

        
        //Setting distance
        String[] placeCords = new String[2];
        placeCords[0] = resultArray.getJSONObject(view_no-1).getString("lat");
        placeCords[1] = resultArray.getJSONObject(view_no-1).getString("lng");
        

        
        SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
        String[] userCords = new String[2];
        userCords[0]= myData.getString("lat", "");
        userCords[1] = myData.getString("lng", "");
        
        distance.setText(new NearByPlaces().calculateDistance(userCords, placeCords));
        
        
        Button add_tips = (Button) v.findViewById(R.id.add_tip);
        
        add_tips.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				
				Intent mIntent = new Intent(getApplicationContext(), ReviewBySmily.class);
		
				startActivity(mIntent);
		
			}
        });
         
        viewFlipper.addView(v);
	}

	/**
	 * Adds the show reviews button.
	 *
	 * @param v the v
	 * @throws JSONException the jSON exception
	 */
	public void addShowReviewsButton(View v) throws JSONException{
			
		Button show_tips=new Button(this);
    	LinearLayout ll =(LinearLayout)v.findViewById(R.id.ll);
    	
    	show_tips.setText("See Reviews");
    	
    	show_tips.setTextSize(18);
    	
    	Drawable bg = getResources().getDrawable(R.drawable.button_gradient);
    	
    	show_tips.setBackgroundDrawable(bg);
    	
    	show_tips.setTextColor(Color.WHITE);
    	
    	
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,65);
    	
    	params.setMargins(8,8, 8, 0);
    	
    	show_tips.setLayoutParams(params);
    	
    	final String pid = resultArray.getJSONObject(view_no - 1).getString("pid");
    	
    	show_tips.setOnClickListener(new View.OnClickListener() { 
            
    		public void onClick(View view) { 
            	
            	Intent a = new Intent(getApplicationContext(),Tips.class);
            	
            	SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
				
            	Editor myEdit = myData.edit();
				
				try {

					myEdit.putString("pid", resultArray.getJSONObject(view_no-1).getString("pid"));
					
					myEdit.commit();
					
				} catch (JSONException e) {
				
					e.printStackTrace();
				}
				
				a.putExtra("pid", pid);
     		   	
				startActivity(a);
            } 
        });

    	ll.addView(show_tips,2);
	}

	/**
	 * On click show reviews.
	 *
	 * @throws JSONException the jSON exception
	 */
	public void onClickShowReviews() throws JSONException{
		
		ImageButton show_reviews = (ImageButton) findViewById(R.id.show_reviews);
		
		final String pid = resultArray.getJSONObject(view_no - 1).getString("pid");
    	
    	show_reviews.setOnClickListener(new View.OnClickListener() { 
            
    		/* (non-Javadoc)
		     * @see android.view.View.OnClickListener#onClick(android.view.View)
		     */
		    public void onClick(View view) { 
            	
            	Intent a = new Intent(getApplicationContext(),Tips.class);
            	
            	SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
				
            	Editor myEdit = myData.edit();
				
				try {

					myEdit.putString("pid", resultArray.getJSONObject(view_no-1).getString("pid"));
					
					myEdit.commit();
					
				} catch (JSONException e) {
				
					e.printStackTrace();
				}
				
				a.putExtra("pid", pid);
     		   	
				startActivity(a);
            } 
        });	
	}
	
	/**
	 * Flip.
	 */
	public void flip(){
			
		viewFlipper = (ViewFlipper)findViewById(R.id.result); 
		       
		//Handling gesture
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
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		// 
	}

	/**
	 * Dialer.
	 *
	 * @throws JSONException the jSON exception
	 */
	public void dialer() throws JSONException{
		
		final String numbers = resultArray.getJSONObject(view_no-1).getString("phone");
		Log.d("View", "view " + view_no);
		ImageButton call = (ImageButton) findViewById(R.id.call);
  	   
		call.setOnClickListener(new OnClickListener(){

  		public void onClick(View v) {
  			
  			dialog = new Dialog(Result.this);
  			dialog.setContentView(R.layout.buttondialog);
  			dialog.setCanceledOnTouchOutside(true);
  			
  			if(numbers.length() != 0)
  				dialog.setTitle("Select a number");
  			else
  				dialog.setTitle("Sorry, no number is available.");
  			
  			ListView l = (ListView) dialog.findViewById(R.id.numbers);
  			
  			final String[] nums = numbers.split(",");;
  			
  			
  			
  			l.setAdapter(new ArrayAdapter<String>(Result.this,android.R.layout.simple_list_item_1,nums));
  			
  			l.setOnItemClickListener(new OnItemClickListener(){

  				public void onItemClick(AdapterView<?> arg0, View arg1,
  						int position, long arg3) {
  					dialPhone(nums[position]);
  				}
  				
  			});

  			
  			OnTouchListener o = new OnTouchListener(){

					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
	
							dialog.dismiss();
							
						}

						return true;
					}
  				
  			};
  			
  			if(numbers.length() == 0 || nums.length != 1){
  				
  				dialog.show();
  				
  			}
  			else{
  			
  				dialPhone(nums[0]);
  				
  			}
  		}
  		   
  	   });

	}
	
	/**
	 * Dial phone.
	 *
	 * @param number the number
	 */
	private void dialPhone(String number) {
    
		Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
        
		startActivity(dialIntent);
		
    }
	
	/**
	 * Show map.
	 *
	 * @throws JSONException the jSON exception
	 */
	public void showMap() throws JSONException{
		
		final String street1 = resultArray.getJSONObject(view_no-1).getString("cross_street") + ", " + resultArray.getJSONObject(view_no-1).getString("city");
		
		loadMap(street1);
	}
	
	/**
	 * Load map.
	 *
	 * @param street1 the street1
	 */
	public void loadMap(String street1){

		Intent mIntent = new Intent(getApplicationContext(), Maps.class);
		
		street1 = encodespace(street1);
		street1 = encodecomma(street1);
		mIntent.putExtra("street1", street1);
		
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		String street2 = myData.getString("userStreet", "");
		street2 = encodespace(street2);
		street2 = encodecomma(street2);
		mIntent.putExtra("street2", street2);
		
		Log.d(street1, street2);
		
		startActivity(mIntent);
	}

	
	/**
	 * Encodespace.
	 *
	 * @param str the str
	 * @return the string
	 */
	public String encodespace(String str){
		return str.replace(" ", "%20");
	}

	/**
	 * Encodecomma.
	 *
	 * @param str the str
	 * @return the string
	 */
	public String encodecomma(String str){
		return str.replace(",", "%2C");
	}
	
	public void startHome(View v){
		
		startActivity(new Intent(this, Start_Menu.class));
		
	}
	
	/**
	 * Share.
	 *
	 * @param subject the subject
	 * @param text the text
	 */
	private void share(String subject,String text) {
	
			Intent intent = new Intent(Intent.ACTION_SEND);
	
			intent.setType("text/plain");
			
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			
			intent.putExtra(Intent.EXTRA_TEXT, text);
			
			startActivity(Intent.createChooser(intent, "Share"));
	
	}
		
		
	/**
	 * Share.
	 */
	public void share(){
		
		ImageButton share = (ImageButton)findViewById(R.id.share);
        
        final int index  = view_no-1;
		
        share.setOnClickListener(new OnClickListener(){
			

			public void onClick(View v) {
				// 
				String text;
				try {
					String place = resultArray.getJSONObject(index).getString("name");
					
					String subject = "checkout " + place + " at PlayCez";
			
					text = "Checkout " + place +
					" at PlayCez "+	"http://playcez.com/place/" + resultArray.getJSONObject(index).getString("pid") + 
						"\nSent via PlayCez Android app playcez.com/mobile";
			
					share(subject, text);
				} catch (JSONException e) {
					// 
					e.printStackTrace();
				}		
			}
			
			});
	}
	
	
	/**
	 * Adds the review.
	 */
	public void addReview(){
		
	}
	
	/**
	 * See reviews.
	 */
	public void seeReviews(){
		
	}
/**
 * The Class DownloadResultArray.
 *
 * @author anmol
 * 
 * Class defined for download of the results in the background and then create the view for the user.
 */
	private class DownloadResultArray extends AsyncTask<String, Integer, Long>{

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Long doInBackground(String... params) {
			//Looper.prepare();
			try{
				
				download();
				
			} catch(Exception e){
				
				e.printStackTrace();
				
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Long result) {
			// 
			try{
				
				if(resultArray != null && resultArray.length() != 0){
					
					//Fetch reviews if there are some results
					new DownloadReviews().execute();
									
				} else{
					
					//Show alert if there are no resluts.
					showAlert("Sorry, none of the type around!");
					
				}
				
				facadeView();
				
			} catch(Exception e){
				
				e.printStackTrace();
				
			}
			progDialog.dismiss();
		}
	}


	/**
	 * The Class DownloadReviews.
	 */
	private class DownloadReviews extends AsyncTask<String, Integer, Long>{

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Long doInBackground(String... params) {
			//Looper.prepare();
			try{
				fetchReviews();
			} catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			try{
					
			} catch(Exception e){
				e.printStackTrace();
			}
			progDialog.dismiss();
		}
	}

	//Setting Animation
	/**
	 * In from left animation.
	 *
	 * @return the animation
	 * @throws JSONException 
	 */
	public Animation inFromLeftAnimation() throws JSONException {
		
		if(view_no == 1){
			
			view_no = resultArray.length();
			
			Log.d("on prev click", "prev click " + view_no);
			
		} else{
			
			view_no--;
			
			Log.d("on prev click", "prev click " + view_no);
			
		}
		
		setButtonClickListeners();
		
		Animation inFromLeft = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
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
		Log.d(TAG, "OuttoRightAnimation");
		Animation outtoRight = new TranslateAnimation(
		  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
		  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
		}
		
	/**
	 * In from right animation.
	 *
	 * @return the animation
	 * @throws JSONException 
	 */
	public Animation inFromRightAnimation() throws JSONException {
		
		if(view_no == resultArray.length()){
			
			view_no = 1;
			
			Log.d("on next click", "next click " + view_no);
			
		} else{
			
			view_no++;
			
			Log.d("on next click", "next click " + view_no);
			
		}
						
		setButtonClickListeners();
		
			Animation inFromRight = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
			);
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
		Log.d(TAG, "OuttoLeftAnimation");
		Animation outtoLeft = new TranslateAnimation(
		  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
		  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	//Handling Gesture
	/**
	 * The Class MyGestureDetector.
	 */
	public class MyGestureDetector extends SimpleOnGestureListener {
	    
    	/* (non-Javadoc)
    	 * @see android.view.GestureDetector.SimpleOnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
    	 */
    	@Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	        try {
	            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
	                return false;
	            // right to left swipe
	            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	            	viewFlipper.setInAnimation(inFromRightAnimation());
	            	viewFlipper.setOutAnimation(outToLeftAnimation());
	            	viewFlipper.showNext();  
	            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
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
	
}

