package com.carvoyant.carlocator;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/*
 * This Activity exists only because a separate Activity was needed to distinguish the applications main launch 0Activity (This File), 
 * from the Activity that contains the browsable intent filter (OAuth.java)
 */

public class LaunchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//launch OAuth Activity to check for token
		Intent i = new Intent(this, OAuth.class);
		startActivity(i);
		finish();
	}

}
