package com.playcez;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;

import com.playcez.Result.MyGestureDetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class Review extends Activity implements TabContentFactory {
	
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabview);
		String x = "xxxxxxxxx";
		Log.d("xxxxxxxxx", x+"");	
		Drawable mySelector = getResources().getDrawable(R.drawable.tabselector);
		Drawable mySelector1 = getResources().getDrawable(R.drawable.tabselector1);
		Drawable mySelector2 = getResources().getDrawable(R.drawable.tabselector2);
		Drawable mySelector3 = getResources().getDrawable(R.drawable.tabselector3);
		
		final TabHost tabs=(TabHost)findViewById(R.id.tabhost);
		tabs.setup();
		TabHost.TabSpec spec=tabs.newTabSpec("tag1");
		Log.d("jjjjjjjjjj", x+"");
		spec.setContent( this);
		Log.d("xxxxxxxxx", x+"");
		spec.setIndicator("", mySelector);
		tabs.addTab(spec);
		/*
		spec=tabs.newTabSpec("tag2");
		spec.setContent((TabContentFactory) this);
		spec.setIndicator("", mySelector1);
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("tag3");
		spec.setContent((TabContentFactory) this);
		spec.setIndicator("", mySelector2);
		tabs.addTab(spec);
		
		spec=tabs.newTabSpec("tag4");
		spec.setContent((TabContentFactory) this);
		spec.setIndicator("", mySelector3);
		tabs.addTab(spec);
		
		tabs.setOnTabChangedListener(new OnTabChangeListener() {

			public void onTabChanged(String tabId) {

			int i = tabs.getCurrentTab();
			 Log.i("@@@@@@@@ ANN CLICK TAB NUMBER", "------" + i);

			    if (i == 0) {
			                    Log.i("@@@@@@@@@@ Inside onClick tab 0", "onClick tab");
			         }
			    else if (i ==1) {
			                    Log.i("@@@@@@@@@@ Inside onClick tab 1", "onClick tab");
			         }
			    else if (i ==2) {
                    Log.i("@@@@@@@@@@ Inside onClick tab 2", "onClick tab");
			    }
			    else if (i ==3) {
                    Log.i("@@@@@@@@@@ Inside onClick tab 3", "onClick tab");
			    }
			}
		});*/
	}

	public View createTabContent(String tag) {
		// 
		
		return null;
	}
}
	
	
	
	
	
	
	
	
	
