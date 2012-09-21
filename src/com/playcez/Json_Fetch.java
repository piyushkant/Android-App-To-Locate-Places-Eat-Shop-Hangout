/*
 * 
 */
package com.playcez;
		
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.util.Log;
		
		// TODO: Auto-generated Javadoc
/**
		 * The Class Json_Fetch.
		 */
		public class Json_Fetch  {
			
			/** The json. */
			public static String json;
			
			/**
			 * Instantiates a new json_ fetch.
			 *
			 * @param url the url
			 */
			public Json_Fetch(String url, String uid, String playcez_token){
			 try{
		        	
				if(url.contains("?")){
					url += "&uid=" + uid + "&playcez_token=" + playcez_token;
				}else{
					url += "?uid=" + uid + "&playcez_token=" + playcez_token;
				}
			 
				Log.d("JSONFETCH", url + " this is url");
	        	HttpClient httpClient = new DefaultHttpClient();
	        	HttpContext localContext = new BasicHttpContext();
	        	HttpGet httpGet = new HttpGet(url);
	        	
	        	HttpResponse response = httpClient.execute(httpGet, localContext);
	        	String result = "";
	        		
	        	BufferedReader reader = new BufferedReader(
	        		    new InputStreamReader(
	        		      response.getEntity().getContent()
	        		    )
	        		  );
	        		 
	        	
	        		String line = null;
	        		while ((line = reader.readLine()) != null){
	        		  result += line + "\n";
	        		}
	        	//	Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
	        		
	        		
	        		this.json=result;
	        		
	        		
	        //		Toast.makeText(this, "Everything Works fine till now", Toast.LENGTH_SHORT).show();
		        }
		        catch(Exception e)
		        {
		        	
		        	System.out.println("\n\nNot able to connect\n\n");   
		        //	Toast.makeText(this, "Not able to connect", Toast.LENGTH_SHORT).show();
		       // 	Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
		        	System.out.println("\n\n"+e.toString()+"\n\n");
		        	e.printStackTrace();
		        }
			}
		}