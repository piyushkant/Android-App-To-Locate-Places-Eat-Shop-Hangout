package com.playcez;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Tutorial1 extends Activity{

	private ImageView tutorial;
	
	private Drawable tutorialImage;
	
	private int tutorialId;
	
	private ImageButton nextTutorial;
	
	private ImageButton prevTutorial;
	
	private TextView tutorialNo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.tutorial);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		tutorial = (ImageView) findViewById(R.id.tutorial);
		
		tutorialNo = (TextView) findViewById(R.id.tutorial_no);
		
		
		nextTutorial = (ImageButton) findViewById(R.id.nextTutorial);
		
		nextTutorial.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// 
				setTutorialId(tutorialId+1);
				
				
				setTutorialImage();
			}
			
		});
		
		prevTutorial = (ImageButton) findViewById(R.id.prevTutorial);
		
		prevTutorial.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// 
				setTutorialId(tutorialId-1);
				
				//idCheck();
				
				setTutorialImage();
			}
			
		});
		
		
		tutorialId = 1;
		setTutorialImage();
		
		
		
	}

	public void idCheck(){
		
		switch(tutorialId){
		
			case 1:
				prevTutorial.setVisibility(View.INVISIBLE);
				break;
				
			case 2:
				prevTutorial.setVisibility(View.VISIBLE);
				break;
								
			case 6:
				startActivity(new Intent(this, PlaycezFacebook.class));
				finish();
				break;
				
				
		}
				
	}
	
	public int getTutorialId() {
		return tutorialId;
	}



	public void setTutorialId(int tutorialId) {
		this.tutorialId = tutorialId;
	}



	public Drawable getTutorialImage() {
		return tutorialImage;
	}



	public void setTutorialImage() {
		Log.d("here", "here");
		idCheck();
		Log.d("here", "here");
		switch(tutorialId){
			case 1:
				this.tutorialImage = getResources().getDrawable(R.drawable.androidtutorial1);
				Log.d("here", "here");
				tutorialNo.setText("Tutorial 1 of 5");
				break;
				
			case 2:
				this.tutorialImage = getResources().getDrawable(R.drawable.androidtutorial2);
				tutorialNo.setText("Tutorial 2 of 5");
				break;
				
			case 3:
				this.tutorialImage = getResources().getDrawable(R.drawable.androidtutorial3);
				tutorialNo.setText("Tutorial 3 of 5");
				break;
				
			case 4:
				this.tutorialImage = getResources().getDrawable(R.drawable.androidtutorial4);
				tutorialNo.setText("Tutorial 4 of 5");
				break;
				
				
			case 5:
				this.tutorialImage = getResources().getDrawable(R.drawable.androidtutorial5);
				tutorialNo.setText("Tutorial 5 of 5");
				break;
				
		}
		Log.d("here", "here");
		tutorial.setBackgroundDrawable(tutorialImage);
	}

}
