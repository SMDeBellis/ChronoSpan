package com.SDRockstarStudios.stopwatch;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class StartScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_screen);
		
		Thread timer = new Thread(){
			
			public void run(){
				
				try{
					
					sleep(3000);
				}
				catch(InterruptedException e){
					
					e.printStackTrace();
				}
				
				Intent intent = new Intent("com.SDRockstarStudios.MainActivity");
				startActivity(intent);
			}
		};
		
		timer.start();		
	}

	//-----------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_screen, menu);
		return true;
	}

}
