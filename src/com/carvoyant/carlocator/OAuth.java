package com.carvoyant.carlocator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
/*
 * This is the activity that is launched from the both the laucher activity and the intent filter defined in the manifest.
 */
public class OAuth extends Activity {
	
	String token;
	
	//Called by the login button to direct a browser to the Carvoyant OAuth login page
	public void getToken(View view)
	{
		String uri = "https://auth.carvoyant.com/OAuth/authorize?client_id="+ getString(R.string.clientId) + "&redirect_uri=" + getString(R.string.redirectUri) + "&response_type=token";
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(browserIntent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		
		String ttlDays = "";
		
	    SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
	    String token = sharedPref.getString("token", null);
	    
	    //attempts to retrieve the token from the URL if this activity was launched by the browser
    	try{
    		token = getIntent().getData().getQueryParameter("access_token");
    		int ttl = Integer.parseInt(getIntent().getData().getQueryParameter("expires_in"));
    		ttlDays = Integer.toString((int)Math.floor(ttl/86400));
    		System.out.println("ttl " + ttl + "days " + ttlDays);
    		SharedPreferences.Editor editor = sharedPref.edit();
    		editor.putString("token", token);
    		editor.commit();
    	}
    	catch(Exception e){
    		System.out.println(e);
    	}	    	
	    
    	//displays login prompt
	    if(token==null)
	    {
	    	setContentView(R.layout.activity_oauth);
	    }
		
	    //have token, launch app
	    else
	    {
	    	TextView msg = new TextView(this);
	    	msg.setText("Your credentials have been verified and will be valid for " + ttlDays + " day(s).");
	    	msg.setPadding(10, 10, 10, 10);
	    	msg.setGravity(Gravity.CENTER);
	    	
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

				alertDialogBuilder.setTitle("Authentication Successful");
	 
				alertDialogBuilder
					.setView(msg)
					.setCancelable(false)
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							launchApp();
						}
					  });
	
					AlertDialog alertDialog = alertDialogBuilder.create();

					alertDialog.show();
				}
	    	

    }

	private void launchApp()
	{
	    Intent i =  new Intent(this, CarLocator.class);
	    startActivity(i);
	    finish();
	}
}
