package com.playcez;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

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
import android.graphics.BitmapFactory;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.playcez.bean.ResultBean;

public class PreviousResult extends Activity implements OnClickListener{
	
	final static String TAG = "Result";
	private Dialog dialog;
	private ResultBean resultBeanObj;
	private int currentViewNumber;
	private String[] pid;
	private int[] numOfReviews;
	
	public int getCurrentViewNumber() {
		return currentViewNumber;
	}

	public void setCurrentViewNumber(int currentViewNumber) {
		this.currentViewNumber = currentViewNumber;
	}

	public ResultBean getResultBeanObj() {
		return resultBeanObj;
	}

	public void setResultBeanObj(ResultBean resultBeanObj) {
		this.resultBeanObj = resultBeanObj;
	}

	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		Log.d("Result.java", "Activity is about to start");
		resultBeanObj = new ResultBean();
		
		resultBeanObj.setProgDialog(ProgressDialog.show(PreviousResult.this, "Please wait", "Loading...", true));
		
		try{
			flip();
		} catch(Exception e){
			e.printStackTrace();
			Log.e("Error: ", "Exception thrown in flip()");
		}
		
		int resultArrayLength = resultBeanObj.getArray().length();
		//for(int i = 0; i < resultBeanObj.getArray().length(); i++){
			setCurrentViewNumber(0);
			
			//Log.d("here", resultBeanObj.getArray().length() + "");
			
			if(resultArrayLength != 0)
				new DownloadActivityTask().execute();
		//}
		//Log.d("hi", "hi");
		
		//Log.d("Result", "The result is empty");
		if(resultArrayLength == 0){
			
			resultBeanObj.getProgDialog().dismiss();
			
			final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			
			dialog.setMessage("Sorry, none of the type around!");
			
			
			//Log.d("Result", "The result is empty");
			
			
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
				
		//handler.sendEmptyMessage(0);
	}
	
	private class DownloadActivityTask extends AsyncTask<String, Integer, Long>{

		@Override
		protected Long doInBackground(String... params) {
			//Looper.prepare();
			try{
				PreviousResult.this.download();
			} catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			// 
			try{
				PreviousResult.this.createView(getCurrentViewNumber());
			} catch(Exception e){
				e.printStackTrace();
			}
			resultBeanObj.getProgDialog().dismiss();
		}
	}
	
	public void download(){
		
		numOfReviews = new int[pid.length];
		
		try {
			
			String lat = resultBeanObj.getArray().getJSONObject(0).getString("lat");
			
			String lng = resultBeanObj.getArray().getJSONObject(0).getString("lng");
	        
			resultBeanObj.setBitmap(loadImageFromNetwork
	        		("http://maps.google.com/maps/api/staticmap?size=80x80&zoom=12&markers=color:red%7C"+lat+","+lng+"%7C&sensor=false"));
			
		} catch (JSONException e1) {

			e1.printStackTrace();

		}
		
		//for(int i = 0; i < pid.length; i++){
			
			String rev_req = "http://playcez.com/api_getPlaceReviews.php?pid="+pid[0]+"&page=1";
	    
			Json_Fetch b = new Json_Fetch(rev_req);
	        
			try {
			
				resultBeanObj.setReviews((new JSONArray(b.json)));
			
			} catch (JSONException e) {
		
				e.printStackTrace();
			
			}
			/*try {
				numOfReviews[i] = resultBeanObj.getArray().getJSONObject(i).length();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			//Log.d("result.java", i+" "+numOfReviews[i]);
		//}
	}
		
	public void share(String subject,String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(intent, "Share"));
	}
	
	public void createView(int someint){
		try {
			
			for(int i = 0; i < resultBeanObj.getArray().length(); i++){
				
				boolean reviews=false;
				
				//Inflating Layout
	            LayoutInflater layoutInflater = (LayoutInflater)(getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
	            
	            View v = layoutInflater.inflate(R.layout.result_item, null);
	            
	            ImageButton share = (ImageButton)v.findViewById(R.id.share);
	            
	            final int index  = i;
	    		
	            share.setOnClickListener(new OnClickListener(){
	    			

	    			public void onClick(View v) {
	    				// 
	    				String text;
						try {
							String place = resultBeanObj.getArray().getJSONObject(index).getString("name");
							
							String subject = "checkout " + place + " at PlayCez";
					
							text = "Checkout " + place +
							" at PlayCez "+	"http://playcez.com/place/" + resultBeanObj.getArray().getJSONObject(index).getString("pid") + 
								"\nSent via PlayCez Android app playcez.com/mobile";
					
							share(subject, text);
						} catch (JSONException e) {
							// 
							e.printStackTrace();
						}

	    				
	    			}
	    			
	    			});

	    		int resultArrayLength = resultBeanObj.getArray().length();
	    		if(resultArrayLength == 1 || resultArrayLength == 0){
	    			ImageButton next = (ImageButton) v.findViewById(R.id.next);
	    			next.setVisibility(View.INVISIBLE);
	    			
	    			ImageButton previous = (ImageButton) v.findViewById(R.id.previous);
	    			previous.setVisibility(View.INVISIBLE);
	    		}
	    		
	            TextView name = (TextView)v.findViewById(R.id.name);
	            TextView address = (TextView)v.findViewById(R.id.address);
	            TextView cross_street = (TextView)v.findViewById(R.id.cross_street);
	            //TextView city = (TextView)v.findViewById(R.id.city);
	            //TextView state = (TextView)v.findViewById(R.id.state);
				
	            name.setText(resultBeanObj.getArray().getJSONObject(i).getString("name").toString());
	            address.setText(resultBeanObj.getArray().getJSONObject(i).getString("address").toString()); 
	            cross_street.setText(resultBeanObj.getArray().getJSONObject(i).getString("cross_street").toString());
	            //city.setText(resultBeanObj.getArray().getJSONObject(i).getString("city").toString());
	            //state.setText(resultBeanObj.getArray().getJSONObject(i).getString("state").toString());
	            
	//          TextView result_no=(TextView)v.findViewById(R.id.address);
	//          result_no.setText();
	           
	            ImageButton next=(ImageButton)v.findViewById(R.id.next); 
	            
	            //Handling on clicking next
	            Log.d("here", "here");
	            setButtonClickListener(next, "next");
	            Log.d("here", "here");
	           
	            //Handling on clicking prev
	            ImageButton prev =(ImageButton)v.findViewById(R.id.previous); 
	            setButtonClickListener(prev, "prev");
	            
	            
	           final ImageView map_image=(ImageView)v.findViewById(R.id.map_image);
	           final String street1 = cross_street.getText().toString() + ", " + resultBeanObj.getArray().getJSONObject(i).getString("city");
	           map_image.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						loadMap(street1);
					}
	            	
	           });
	           map_image.setImageBitmap(resultBeanObj.getBitmap());
	            
	     	   ImageButton suggest_directions = (ImageButton) v.findViewById(R.id.directions);
	    	   suggest_directions.setOnClickListener(new OnClickListener(){

	    		   public void onClick(View v) {
	    			   loadMap(street1);
	    		   }	    		   
	    	   });
	           
	           TextView number1 = (TextView)v.findViewById(R.id.number1);
	           //TextView number2 = (TextView)v.findViewById(R.id.number2);
	           final String numbers = resultBeanObj.getArray().getJSONObject(i).getString("phone");
	           number1.setText(numbers);
	            
	           ImageButton call = (ImageButton) v.findViewById(R.id.call);
	     	   
	           /*if(numbers.length() == 0)
	        	   call.setVisibility(View.INVISIBLE);
	           */
	     	   call.setOnClickListener(new OnClickListener(){

	     		public void onClick(View v) {
	     			// 
	     			
	     			
	     		//	Log.d("here", "yessssssss");
	     			//AlertDialog.Builder b = new AlertDialog.Builder(Result.this);
	     			dialog = new Dialog(PreviousResult.this);
	     			dialog.setContentView(R.layout.buttondialog);
	     			dialog.setCanceledOnTouchOutside(true);
	     			
	     			if(numbers.length() != 0)
	     				dialog.setTitle("Select a number");
	     			else
	     				dialog.setTitle("Sorry, no number is available.");
	     			//Log.d("here", "yessssssss");
	     			ListView l = (ListView) dialog.findViewById(R.id.numbers);
	     			
	     			final String[] nums = numbers.split(",");;
	     			//Log.d("here", "yessssssss");
	     			l.setAdapter(new ArrayAdapter<String>(PreviousResult.this,android.R.layout.simple_list_item_1,nums));
	     			
	     			l.setOnItemClickListener(new OnItemClickListener(){

	     				public void onItemClick(AdapterView<?> arg0, View arg1,
	     						int position, long arg3) {
	     					dialPhone(nums[position]);
	     				}
	     				
	     			});
	     	        //b.setView(l);
	     	        //dialog = b.create();
	     			//setContentView(l);
	     			//Log.d("here", "yessssssss");
	     			OnTouchListener o = new OnTouchListener(){

						public boolean onTouch(View v, MotionEvent event) {
							if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
							//	Log.d("hey", "heyyyyy");
								dialog.dismiss();
							}
						//	Log.d("hey", "okkkkk");
							return true;
						}
	     				
	     			};
	     			if(numbers.length() == 0 || nums.length != 1)
	     				dialog.show();
	     			else
	     				dialPhone(nums[0]);
	     		}
	     		   
	     	   });

	            /*final String placePhoneNumber2 = number2.getText().toString(); 
	            number1.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						Log.d("phonemuber", placePhoneNumber2);
						dialPhone(placePhoneNumber2);
					}
	            	
	            });
	             */
	     	   
	     	   
	            String[] placeCords = new String[2];
	            placeCords[0] = resultBeanObj.getArray().getJSONObject(i).getString("lat").toString();
	            placeCords[1] = resultBeanObj.getArray().getJSONObject(i).getString("lng").toString();
	            
	            SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
	            String[] userCords = new String[2];
	            userCords[0]= myData.getString("lat", "");
	            userCords[1] = myData.getString("lng", "");
	            
	            TextView distance=(TextView)v.findViewById(R.id.distance);
	            distance.setText(new NearByPlaces().calculateDistance(userCords, placeCords));
	            resultBeanObj.setNo(resultBeanObj.getNo() + 1);
	            	                
	            ImageView num = (ImageView)v.findViewById(R.id.result_no);
	            TextView resNum = (TextView)v.findViewById(R.id.result_text_no);
	            //Log.d("here", "here" + " " + resultBeanObj.getNo());
	            if(resultBeanObj.getNo() < 10){
	            	resNum.setText("  " + resultBeanObj.getNo());
	            } else{
	            	resNum.setText(" "+resultBeanObj.getNo());
	            }
	            resNum.setTextSize(30);
	            //Log.d("here", "here");
	            //int id=getResources().getIdentifier("rank"+resultBeanObj.getNo(), "drawable", Result.this.getPackageName());
	            //Toast.makeText(this,"rank"+resultBeanObj.getNo() , Toast.LENGTH_SHORT).show();
	            
	            Drawable drawable = getResources().getDrawable(R.drawable.rank_show);
				num.setImageDrawable(drawable);
	           
	            //Fetching Tips
	            //setUpLocationProvider();
	            
	            if(resultBeanObj.getReviews().length()!=0 && !reviews || 1==1){
	            	reviews=true;
	            	Button show_tips=new Button(PreviousResult.this);
	            	RelativeLayout ll =(RelativeLayout)v.findViewById(R.id.ll);
	            	
	            	show_tips.setText("See Reviews");
	            	show_tips.setTextSize(18);
	            	
	            	Drawable bg = getResources().getDrawable(R.drawable.button_gradient);
	            	show_tips.setBackgroundDrawable(bg);
	            	show_tips.setTextColor(Color.WHITE);
	            	
	            	
	            	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,65);
	            	params.setMargins(8,8, 8, 0);
	            	show_tips.setLayoutParams(params);
	            	
	            	final String pid = resultBeanObj.getArray().getJSONObject(i).getString("pid");
	            	//index = i; declared above
	            	show_tips.setOnClickListener(new View.OnClickListener() { 
	    	            public void onClick(View view) { 
	    	            	Intent a = new Intent(getApplicationContext(),Tips.class);
	    	            	SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
							Editor myEdit = myData.edit();
							try {
								myEdit.putString("pid", resultBeanObj.getArray().getJSONObject(index).getString("pid"));
								myEdit.commit();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							a.putExtra("pid", pid);
	    	     		   	startActivity(a);
	    	            } 
	    	        });
	
	            	ll.addView(show_tips,2);
	            }
	            
	            Button add_tips = (Button) v.findViewById(R.id.add_tip);
	            add_tips.setOnClickListener(new OnClickListener(){
	
					public void onClick(View v) {
						Intent mIntent = new Intent(getApplicationContext(), ReviewBySmily.class);
				
						startActivity(mIntent);
					}
	            });
	            
	            resultBeanObj.getFlipper().addView(v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void dialPhone(String number) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
        startActivity(dialIntent);
    }
	
	public void setButtonClickListener(ImageButton b, String side){
		if(side.equals("next")){
			b.setOnClickListener(new View.OnClickListener() { 
	            public void onClick(View view) { 
	            	resultBeanObj.getFlipper().setInAnimation(inFromRightAnimation());
	                resultBeanObj.getFlipper().setOutAnimation(outToLeftAnimation());
	                resultBeanObj.getFlipper().showNext();     
	            } 
	        });
		} else if(side.equals("prev")){
			b.setOnClickListener(new View.OnClickListener() { 
	            public void onClick(View view) { 
	            	resultBeanObj.getFlipper().setInAnimation(inFromLeftAnimation());
	                resultBeanObj.getFlipper().setOutAnimation(outToRightAnimation());
	                resultBeanObj.getFlipper().showPrevious();
	            } 
	        });
		}
	}
	
	public void flip(){
	   resultBeanObj.setFlipper((ViewFlipper)findViewById(R.id.result)); 
	   
       //Log.d("here", "here");
       resultBeanObj.setNo(0);
        
      //Handling gesture
       resultBeanObj.setGestureDetector(new GestureDetector(new MyGestureDetector()));
       resultBeanObj.setGestureListener(new View.OnTouchListener() {
           public boolean onTouch(View v, MotionEvent event) {
               if (resultBeanObj.getGestureDetector().onTouchEvent(event)) {
                   return true;
               }
               return false;
           }
       });
       
       resultBeanObj.getFlipper().setOnClickListener(this); 
       resultBeanObj.getFlipper().setOnTouchListener(resultBeanObj.getGestureListener());
        
        //Fetching location
       //setUpLocationProvider();
       
        //Fetching JSON
       Intent mIntent = getIntent();
       int bkid = mIntent.getIntExtra("bkid", 700);
       
       SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		
       String lat = myData.getString("lat", "");
		
       String lng = myData.getString("lng", "");
		
       String csid = myData.getString("csid", "9100401107066699");
       
		
       Json_Fetch a = new Json_Fetch("http://playcez.com/api_getBucket.php?csid="+csid+"&bkid="+bkid+"&lat="+lat+"&lng="+lng);
       
		
       try {
			resultBeanObj.setArray(new JSONArray(a.json));
			
			pid = new String[resultBeanObj.getArray().length()];
		
			for(int i = 0; i < resultBeanObj.getArray().length(); i++){
				
				pid[0] = resultBeanObj.getArray().getJSONObject(i).getString("pid");
				
			}
		} catch (JSONException e1) {
				e1.printStackTrace();
		}
	}
	
	//Setting Animation
	public Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	public Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
		  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
		  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
		}
		
		
	public Animation inFromRightAnimation() {
			Animation inFromRight = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
			);
			inFromRight.setDuration(500);
			inFromRight.setInterpolator(new AccelerateInterpolator());
			return inFromRight;
	}

	public Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
		  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
		  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

//Handling Gesture
public class MyGestureDetector extends SimpleOnGestureListener {
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > resultBeanObj.getSwipeMaxOffPath())
                return false;
            // right to left swipe
            if(e1.getX() - e2.getX() > resultBeanObj.getSwipeMinDistance() && Math.abs(velocityX) > resultBeanObj.getSwipeThresholdVelocity()) {
                resultBeanObj.getFlipper().setInAnimation(inFromRightAnimation());
                resultBeanObj.getFlipper().setOutAnimation(outToLeftAnimation());
                resultBeanObj.getFlipper().showNext();  
            }  else if (e2.getX() - e1.getX() > resultBeanObj.getSwipeMinDistance() && Math.abs(velocityX) > resultBeanObj.getSwipeThresholdVelocity()) {
                resultBeanObj.getFlipper().setInAnimation(inFromLeftAnimation());
                resultBeanObj.getFlipper().setOutAnimation(outToRightAnimation());
                resultBeanObj.getFlipper().showPrevious();
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }

}

	public void onClick(View arg0) {
		
	}
	
	public Bitmap loadImageFromNetwork(String urlString){
		try {
			HttpGet httpRequest = new HttpGet(new URL(urlString).toURI());
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
			final long contentLength = bufHttpEntity.getContentLength();
			
			if(contentLength >= 0){
				InputStream is = bufHttpEntity.getContent();
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				return bitmap;
			} else{				
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
	
	public String encodespace(String str){
		return str.replace(" ", "%20");
	}

	public String encodecomma(String str){
		return str.replace(",", "%2C");
	}
	
	public void startHome(View view){
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}
}
