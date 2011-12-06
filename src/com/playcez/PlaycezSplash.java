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

public class PlaycezSplash extends BaseSplashActivity{

	private final int SPLASH_DURATION = 3;
	private static final float SPLASH_SCALE_FROM = 1f;
	
	@Override
	protected ScreenOrientation getScreenOrientation() {
		// 
		return ScreenOrientation.PORTRAIT;
	}

	
	@Override
	protected ITextureSource onGetSplashTextureSource() {
		// 
		return new AssetTextureSource(this, "gfx/splash.png");
	}

	@Override
	protected float getSplashDuration() {
		// 
		return SPLASH_DURATION;
	}

	@Override
	protected Class<? extends Activity> getFollowUpActivity() {
		//
		SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
		
		
		boolean firstTime = myData.getBoolean("firstTime", true);
		
		//return Tutorial1.class;
		
		if(firstTime)
			return Tutorial1.class;
		else
			return PlaycezFacebook.class;
	}

	protected float getSplashScaleFrom(){
		return SPLASH_SCALE_FROM;
	}
	
	public void startHome(View view){
		startActivity(new Intent(getApplicationContext(), Start_Menu.class));
	}
	
}
