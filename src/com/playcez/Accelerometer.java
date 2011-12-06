package com.playcez;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Accelerometer extends Activity{

	private TextView result;
	private SensorManager sensorManager;
	private Sensor sensor;
	private float x, y, z;
	
	String myResult = "";
	int count;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accelerometer);
		count = 0;
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
		
		result = (TextView) findViewById(R.id.result);
		result.setText("No Result Yet");
	}
	
	private void refreshDisplay() throws IOException{
		
		
		float acc = (float) Math.sqrt((x*x + y*y + z*z));
		String output = String.format("x is: %f/ y is: %f/ z is: %f/ acc is: %f", x, y, z, acc);
		
		
		myResult += "0 1:" + acc +"\n";
		count++;
		FileOutputStream fos = new FileOutputStream("/sdcard/data.txt");
		fos.write(myResult.getBytes());
		fos.close();
		
		/*
		 * if(count > 300)
			finish();
		*/	
		result.setText(output);
	}
	
	@Override
	protected void onResume() {
		// 
		super.onResume();
		sensorManager.registerListener(accelerationListener, sensor, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop() {
		// 
		sensorManager.unregisterListener(accelerationListener);
		super.onStop();
	}
	
	private SensorEventListener accelerationListener = new SensorEventListener(){

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		public void onSensorChanged(SensorEvent event) {
			// 
			x = event.values[0];
			y = event.values[1];
			z = event.values[2];
			try {
				refreshDisplay();
			} catch (IOException e) {
				// 
				e.printStackTrace();
			}
		}
		
	};
}
