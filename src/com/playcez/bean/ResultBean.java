package com.playcez.bean;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ViewFlipper;

public class ResultBean {

	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 400;

    private ViewFlipper flipper;
	private int no;
	private JSONArray array;
    
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    
    private ProgressDialog progDialog;
    
    private Bitmap bitmap;
    private JSONArray reviews;
    
    public JSONArray getReviews() {
		return reviews;
	}

	public void setReviews(JSONArray reviews) {
		this.reviews = reviews;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public static int getSwipeMinDistance() {
		return SWIPE_MIN_DISTANCE;
	}

	public static int getSwipeMaxOffPath() {
		return SWIPE_MAX_OFF_PATH;
	}

	public static int getSwipeThresholdVelocity() {
		return SWIPE_THRESHOLD_VELOCITY;
	}

	public ViewFlipper getFlipper() {
		return flipper;
	}

	public void setFlipper(ViewFlipper flipper) {
		this.flipper = flipper;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public JSONArray getArray() {
		return array;
	}

	public void setArray(JSONArray array) {
		this.array = array;
	}

	public GestureDetector getGestureDetector() {
		return gestureDetector;
	}

	public void setGestureDetector(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}

	public View.OnTouchListener getGestureListener() {
		return gestureListener;
	}

	public void setGestureListener(View.OnTouchListener gestureListener) {
		this.gestureListener = gestureListener;
	}

	public ProgressDialog getProgDialog() {
		return progDialog;
	}

	public void setProgDialog(ProgressDialog progDialog) {
		this.progDialog = progDialog;
	}

	private String url;
    
    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
