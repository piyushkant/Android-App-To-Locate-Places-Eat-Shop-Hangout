/*
 * 
 */
package com.playcez;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving customOnItemSelected events.
 * The class that is interested in processing a customOnItemSelected
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addCustomOnItemSelectedListener<code> method. When
 * the customOnItemSelected event occurs, that object's appropriate
 * method is invoked.
 *
 * @see CustomOnItemSelectedEvent
 */
public class CustomOnItemSelectedListener implements OnItemSelectedListener {
	
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
	 */
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		
		Toast.makeText(parent.getContext(), 
				"OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
				Toast.LENGTH_SHORT).show();
	}

	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
