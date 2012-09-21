/*
 * 
 */
package com.playcez;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class SettingsActivity.
 */
public class SettingsActivity extends ListActivity 
{
	
	/** The share subject. */
	private String shareSubject;
	
	/** The share content. */
	private String shareContent;
	
	/** The rate link. */
	private String rateLink;
	
	/** The contact. */
	private String [] contact;
	
	/** The accounts. */
	final CharSequence [] accounts={"Facebook", "Twitter"};
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingslayout);
        	
		String[] values = new String[] { "Accounts", "Rate PlayCez", "Share PlayCez", "Contact Us"};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
		
		 SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
	        String fbId = myData.getString("fbuid", "" );
	        String twId = myData.getString("twid", "" );
	        
	        Log.d("fbid", fbId+"");
	        Log.d("twid", twId+"");
	}

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		//String item = (String) getListAdapter().getItem(position);
		shareSubject = getString(R.string.shareSubject);
		shareContent = getString(R.string.shareContent);
		rateLink = getString(R.string.rateLink);
		contact = new String[]{getString(R.string.contactUs)};
	
		switch (position) 
		{
		case 0:
			customDialog();
			break;

		case 1:
			openRateLink(rateLink);
			break;
		
		case 2:
			share(shareSubject, shareContent);
			break;
			
		case 3:
			Log.d("contact", contact+"");
			contactUs(contact);
			break;
		}
	}
	
	/**
	 * Share.
	 *
	 * @param subject the subject
	 * @param text the text
	 */
	private void share(String subject, String text)
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(intent, "Share"));
	}
	
	/**
	 * Contact us.
	 *
	 * @param myEmail the my email
	 */
	private void contactUs(String [] myEmail)
	{
		final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, myEmail);
        startActivity(intent);
	}
	
	/**
	 * Open rate link.
	 *
	 * @param rateLink the rate link
	 */
	private void openRateLink(String rateLink)
	{
		Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rateLink));
		startActivity(myIntent);
	}
	
	/**
	 * Custom dialog.
	 */
	void customDialog ()
	{
		Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialog);
        dialog.setCanceledOnTouchOutside(true);
        
        TextView fbConnected = (TextView)dialog.findViewById(R.id.fbConnected);
        TextView twConnected = (TextView)dialog.findViewById(R.id.twConnected);
        Button fbButton = (Button)dialog.findViewById(R.id.fbConnectButton);
        Button twButton = (Button)dialog.findViewById(R.id.twConnectButton);
        
        try{
        	twButton.setVisibility(View.GONE);
        	twConnected.setVisibility(View.GONE);
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        SharedPreferences myData = getSharedPreferences("myData", MODE_PRIVATE);
        String fbId = myData.getString("fbuid", "1" );
        String twId = myData.getString("twid", "1" );
        
        Log.d("fbid", fbId+" this is fbuid");
        Log.d("twid", twId+" this is twid");
       
        if (fbId.equals("1"))
        {
        	Log.d("hey", "eheyeyye");
        	fbConnected.setText("Not Connected");
        	fbConnected.setTextColor(Color.parseColor("#e50004"));
        	fbButton.setVisibility(View.VISIBLE);
        	
        	fbButton.setOnClickListener(new View.OnClickListener()
        	{
				public void onClick(View v) 
				{	
					//*********Add Account Here *******
				}
			});
        }
        else 
        {
        	Log.d("hey", "sjajsjseheyeyye");
        	fbConnected.setText("Connected");
        	fbConnected.setTextColor(Color.parseColor("#0000ff"));
        	fbButton.setVisibility(View.GONE);
        }
        
    /*   if (twId == null)
        {
        	twConnected.setText("Not Connected");
        	twConnected.setTextColor(Color.parseColor("#F0F8FF"));
        	twButton.setVisibility(View.VISIBLE);
        	
        	twButton.setOnClickListener(new View.OnClickListener()
        	{
				public void onClick(View v) 
				{	
					//*********Add Account Here *******
				}
			});
        }
        
        else 
        {
        	twConnected.setText("Connected");
        	twConnected.setTextColor(Color.parseColor("#0000ff"));
        	twButton.setVisibility(View.GONE);
        }
  */      
        dialog.show();
	}
}