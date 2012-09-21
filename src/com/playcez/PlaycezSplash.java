/*
 * 
 */
package com.playcez;

import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.opengl.texture.source.AssetTextureSource;
import org.anddev.andengine.opengl.texture.source.ITextureSource;
import org.anddev.andengine.ui.activity.BaseSplashActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;

// TODO: Auto-generated Javadoc
/**
 * The Class PlaycezSplash.
 */
public class PlaycezSplash extends BaseSplashActivity{

	/** The SPLAS h_ duration. */
	private final int SPLASH_DURATION = 2;
	
	/** The Constant SPLASH_SCALE_FROM. */
	private static final float SPLASH_SCALE_FROM = 1f;
	
	/* (non-Javadoc)
	 * @see org.anddev.andengine.ui.activity.BaseSplashActivity#getScreenOrientation()
	 */
	@Override
	protected ScreenOrientation getScreenOrientation() {
		// 
		return ScreenOrientation.PORTRAIT;
	}

	
	/* (non-Javadoc)
	 * @see org.anddev.andengine.ui.activity.BaseSplashActivity#onGetSplashTextureSource()
	 */
	@Override
	protected ITextureSource onGetSplashTextureSource() {
		// 
		return new AssetTextureSource(this, "gfx/splash.png");
	}

	/* (non-Javadoc)
	 * @see org.anddev.andengine.ui.activity.BaseSplashActivity#getSplashDuration()
	 */
	@Override
	protected float getSplashDuration() {
		// 
		return SPLASH_DURATION;
	}

	/* (non-Javadoc)
	 * @see org.anddev.andengine.ui.activity.BaseSplashActivity#getFollowUpActivity()
	 */
	@Override
	protected Class<? extends Activity> getFollowUpActivity() {
		//SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		//boolean firstTime = myData.getBoolean("firstTime", true);
		//return Tutorial1.class;
		
		/*if(firstTime)
			return Tutorial1.class;
		else*/
			return PlaycezFacebook.class;
	}

	/* (non-Javadoc)
	 * @see org.anddev.andengine.ui.activity.BaseSplashActivity#getSplashScaleFrom()
	 */
	protected float getSplashScaleFrom(){
		return SPLASH_SCALE_FROM;
	}
	
	/**
	 * Start home.
	 *
	 * @param view the view
	 */
	public void startHome(View view){
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}
	
}
