package com.playcez;
		
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
		
		public class Json_Fetch  {
			public static String json;
			public Json_Fetch(String url){
			 try{
		        	
		        	//System.setProperty("http.proxyHost", android.net.Proxy.getDefaultHost());
		        	//System.setProperty("http.proxyPort",Integer.toString(android.net.Proxy.getDefaultPort()));
		        	//System.setProperty("http.proxyHost", "hproxy.iith.ac.in");
		        	//System.setProperty("http.proxyPort","3128");
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