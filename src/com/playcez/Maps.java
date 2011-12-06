package com.playcez;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.playcez.bean.DirectionPathOverlay;
import com.playcez.bean.HelloItemizedOverlay;

public class Maps extends MapActivity implements LocationListener, OnTouchListener{

	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private long lastTouchTime = -1;
	private double minLat = (+81 * 1E6);
	private double maxLat = (-81 * 1E6);
	private double minLng = (+181 * 1E6);
	private double maxLng = (-181 * 1E6);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		try{
			setUpMap();
		}catch(Exception e){
			//finish();
		}
	}
	
	public void setUpOverlay(GeoPoint gp, Drawable drawable){
		List<Overlay> mapOverlays = mapView.getOverlays();
		//Drawable drawable = this.getResources().getDrawable(R.drawable.a);
		HelloItemizedOverlay itemizedOverlay = new HelloItemizedOverlay(drawable);
		
		OverlayItem overlayitem = new OverlayItem(gp, "", "");
		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);
		//mapController.zoomToSpan(overlayitem.get, lonSpanE6)
	}
		
	public void setUpMap(){
		
		//RelativeLayout rl = (RelativeLayout) findViewById(R.id.mapLayout);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		
		//mapController.setZoom(10);
		
		//GeoPoint point = getLocation();
		//mapController.animateTo(point);
		
		GeoPoint geoPoint = null;
		mapView.setSatellite(false);
		
		Intent mIntent = getIntent();
		String street1 = mIntent.getStringExtra("street1");
		String street2 = mIntent.getStringExtra("street2");
		
		String pairs[] = getDirectionData(street1, street2);
		Log.d("length", pairs.length + "");
		String[] lngLat = pairs[0].split(",");
		
		//Starting point
		GeoPoint startGP = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6), (int)(Double.parseDouble(lngLat[0])*1E6));
		
		Drawable drawable = this.getResources().getDrawable(R.drawable.a);
		setUpOverlay(startGP, drawable);
		
		geoPoint = startGP;
		//mapController.setCenter(geoPoint);
		//mapController.setZoom(15);
		//mapController.zoomToSpan(overlay.getLatSpanE6(), overlay.getLonSpanE6());
		mapView.getOverlays().add(new DirectionPathOverlay(startGP, startGP));
		
		//Navigate path
		GeoPoint gp1;
		GeoPoint gp2 = startGP;
		
		Log.d("length", pairs.length + "");
		for(int i = 1; i < pairs.length; i++){
			lngLat = pairs[i].split(",");
			gp1 = gp2;
			//Watch out! for GeoPoint first:latitude, second:longitude
			double lat = Double.parseDouble(lngLat[1])*1E6;
			double lng = Double.parseDouble(lngLat[0])*1E6;
			gp2 = new GeoPoint((int)(lat), (int)(lng));
			
			minLat = (minLat > lat) ? lat : minLat;
			minLng = (minLng > lng) ? lng : minLng;
			
			maxLat = (maxLat < lat) ? lat : maxLat;
			maxLng = (maxLng < lng) ? lng : maxLng;
			
			/*if(i == pairs.length/2) 
				mapController.setCenter(gp2);
			*/
			mapView.getOverlays().add(new DirectionPathOverlay(gp1,gp2));
			Log.d("xxx", "pair:" + pairs[i]);
		}
		
		drawable = this.getResources().getDrawable(R.drawable.b);
		setUpOverlay(gp2, drawable);
		
		mapController.zoomToSpan((int)(maxLat - minLat), (int)(maxLng - minLng));
		
		//End point
		mapView.getOverlays().add(new DirectionPathOverlay(gp2,gp2));
		
		mapController.animateTo(new GeoPoint((int)(maxLat+minLat)/2, (int)(maxLng+minLng)/2));
	}
		
	private String[] getDirectionData(String srcPlace, String destPlace){
		String urlString = "http://maps.google.com/maps?f=d&hl=en&saddr=" + srcPlace + "&daddr=" + destPlace + "&ie=UTF8&o&om=o&output=kml";
		//String urlString = "http://maps.google.com/maps?f=d&hl=en&saddr=trichy&daddr=thanjavur&ie=UTF8&o&om=o&output=kml";
		Log.d("URL", urlString);
		Document doc = null;
		
		String pathConn = "";
		try{
			
			HttpClient httpclient = new DefaultHttpClient();
	    	HttpGet httpget = new HttpGet(urlString);
	    	
	    	httpget.setHeader("User-Agent", "xxxx");
	    	
	    	HttpResponse response = httpclient.execute(httpget);
	    	
    		// for JSON:
    		if(response != null){
    			InputStream is = response.getEntity().getContent();
    			
    			InputSource isource = new InputSource();
    			isource.setByteStream(is);
    			isource.setEncoding("UTF-8");
    			
    			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    			DocumentBuilder db = dbf.newDocumentBuilder();
    			
    			Log.d("Response", "response is not null");
    			
    			doc = db.parse(is);
    			Log.d("Response", "response is not null");
    		}

		} catch(Exception e){
			e.printStackTrace();
		}
		
		NodeList nl = null;
		
		nl = doc.getElementsByTagName("LineString");
		for(int s = 0; s < nl.getLength(); s++){
			Node rootNode = nl.item(s);
			NodeList configItems = rootNode.getChildNodes();
			for(int x = 0; x < configItems.getLength(); x++){
				Node lineStringNode = configItems.item(x);
				NodeList path = lineStringNode.getChildNodes();
				pathConn = path.item(0).getNodeValue();
			}
		}
		Log.d("okk", "okkkkkkkkkkkk");
		String[] tempContent = pathConn.split(" ");
		return tempContent;
	}
	
	public GeoPoint getLocation(){
		int lat = (int)(17 * 1E6);
		int lng = (int) (78 * 1E6);
		GeoPoint point = new GeoPoint(lat, lng);
		return point;
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			
			long thisTime = System.currentTimeMillis();
			if(thisTime - lastTouchTime < 250){
				
			}
		}
		return false;
	}
	
	
	
}
