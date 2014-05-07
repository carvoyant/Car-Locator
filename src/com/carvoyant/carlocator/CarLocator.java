package com.carvoyant.carlocator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;


/*
 * This is the main Activity of the application.  
 * It makes calls to the Carvoyant API and uses the 
 * driver's vehicle data to plot their location on a Google map.
 */

public class CarLocator extends ActionBarActivity {

	GoogleMap map;
	
	public void refreshLocationData(View view)
	{
	    new CallCarvoyantApi().execute(ApiCall.GETVEHICLES);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_locater);
        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        map.moveCamera(CameraUpdateFactory.zoomTo(13));
	}

	@Override
	public void onResume() {
		super.onResume();
		
		//starts asynchronous task to call Carvoyant API
	    new CallCarvoyantApi().execute(ApiCall.GETVEHICLES);

	}
	
	/*
	 * receives vehicle data from API call, decodes it to JSON, 
	 * and plots a marker on a map at the vehicle location(s)
	*/
	private void processVehicles(String result)
	{
		JSONObject jObject;
		try 
		{
			jObject = new JSONObject(result);
			JSONArray jArray = jObject.getJSONArray("vehicle"); 


	        map.clear();
	        map.setMyLocationEnabled(true);
	         
	    	for (int i=0; i < jArray.length(); i++)
			{
	    		JSONObject vehicleObj = jArray.getJSONObject(i);
	
	    		JSONObject lastWaypointObj = vehicleObj.getJSONObject("lastWaypoint");

		    	Double lat = lastWaypointObj.getDouble("latitude");
		    	Double lon = lastWaypointObj.getDouble("longitude");
		    	String timestamp = lastWaypointObj.getString("timestamp");
		    	SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
		    	Date date = dateFormat.parse(timestamp);
		    	 
		        LatLng vehiclePosition = new LatLng(lat, lon);
		        String label =  vehicleObj.getString("label");

		        if(label.equals("null"))
		        {
		        	label = vehicleObj.getString("name");
		        }

		        map.moveCamera(CameraUpdateFactory.newLatLng(vehiclePosition));
		
		        map.addMarker(new MarkerOptions()
		                .title(label)
		                .position(vehiclePosition)
		        		.snippet(date.toString()));
			}
		}
		catch (Exception e) {
 			e.printStackTrace();
 		}
	}

	private void invalidToken()
	{
	    Intent i =  new Intent(this, OAuth.class);
	    startActivity(i);
	}
	private enum ApiCall {
		 GETVEHICLES;
	}
	private class CallCarvoyantApi extends AsyncTask<ApiCall, Integer, String> {
		
		String result;
		ApiCall apiCall;
		
		/*
		 * builds the URL, appends the appropriate Authorization header, and calls the API
		*/
		protected String doInBackground(ApiCall... apiCalls) {
			String s = null;
			apiCall = apiCalls[0];
		    SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
		    String token = sharedPref.getString("token", null);
		    
			switch(apiCall)
			{
				case GETVEHICLES:
					s = "https://api.carvoyant.com/v1/api/vehicle/";
					break;
				default:
					break;		
			}
			
	    	 try
	    	 {
	    		 URL url = new URL(s);
	    		 HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	    		 urlConnection.setRequestProperty("Authorization", "Bearer " + token); 	 
	    		 int status = urlConnection.getResponseCode();

	    		 try 
		 	     {
	    			 if(status >= HttpStatus.SC_BAD_REQUEST)
	    			 {
	    				 if(urlConnection.getHeaderField("WWW-Authenticate").indexOf("invalid_token") >= 0)
	    				 {
	    					 sharedPref.edit().remove("token").commit();
	    					 return "invalid_token";
	    				 }
	    			 }
	    			 else
	    			 {
	    				 InputStream is = urlConnection.getInputStream();
			 	    	 BufferedReader in = new BufferedReader(new InputStreamReader(is));
			 	    	 StringBuilder builder = new StringBuilder();
			 	    	 String line;
			 	    	 while ((line = in.readLine()) != null) {
			 	    	    	builder.append(line);
			 	    	 }
			 	    	 result = builder.toString();
	    			 }
		 	       }
		 	       catch(Exception e)
		 	       {
		 	    	   System.out.println(e);
		 	       }
	 	    	   finally 
	 	    	   {
	 	    	     urlConnection.disconnect();
	 	    	   }
	    	 }
	    	 catch(Exception e)
	    	 {
	 	    	   System.out.println(e);
	    	 }

	    	 return result;
	     }

		/*
		 *  receives the result of the HTTP GET performed in the "doInBackground" method 
		 *  and sends it back to the main Activity thread to be processed.
		 */
	     protected void onPostExecute(String result) 
	     {
	    	 if(result!=null)
	    	 {
				if(result.equals("invalid_token"))
				{
					invalidToken();
					return;
				}
				else
				{
			    	switch(apiCall)
					{
						case GETVEHICLES:
							processVehicles(result);
							break;
						default:
							break;		
					}
				}
	    	}
	    }
	}
}
