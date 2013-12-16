package com.SDRockstarStudios.stopwatch;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

	Chronometer chrono = null;
	
	Button startButton = null;
	Button stopButton = null;
	Button resetButton = null;
	Button lapButton = null;

	TextView defaultLapTextView = null;
	ScrollView lapScrollView = null;
	LinearLayout lapTimes = null;
	
	long elapsedTime = 0;
	int lapNumber = 1;
	
	boolean running = false; // tells if the timer is running
	boolean firstRun = true; // tells if any laps have been recorded
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		chrono = (Chronometer) findViewById(R.id.chronometer);
		
		startButton = (Button) findViewById(R.id.chrono_start_button);
		stopButton = (Button) findViewById(R.id.chrono_stop_button);
		resetButton = (Button) findViewById(R.id.chrono_reset_button);
		lapButton = (Button) findViewById(R.id.chrono_lap_button);
		
		// create the default message for the lap scroll view
		defaultLapTextView = new TextView(this);
		defaultLapTextView.setText("Press the Start Button to Begin the Timer");
		defaultLapTextView.setTextSize(20);
		defaultLapTextView.setTextColor(Color.WHITE);
		defaultLapTextView.setGravity(Gravity.CENTER);
		
		lapScrollView = (ScrollView) findViewById(R.id.elapsed_time_scroll_view);
		
		lapTimes = (LinearLayout) findViewById(R.id.lap_time_linear_layout);
		lapTimes.addView(defaultLapTextView);
		
		chrono.setText("00:00");
		
		setButtonOnClickListeners();		
	}
	
	//-----------------------------------------------------------------------
	public void setButtonOnClickListeners(){
		
		//-------------------------------------------------------------------
		startButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if(running == false){
					String chronoText = chrono.getText().toString();
					Log.w("chrono output", chrono.getText().toString());
					String array[] = chronoText.split(":");
			      
					if (array.length == 2){ 
						elapsedTime = Integer.parseInt(array[0]) * 60 * 1000
								+ Integer.parseInt(array[1]) * 1000;
					} else if (array.length == 3) {
						elapsedTime = Integer.parseInt(array[0]) * 60 * 60 * 1000 
								+ Integer.parseInt(array[1]) * 60 * 1000
								+ Integer.parseInt(array[2]) * 1000;
					}
			      
					chrono.setBase(SystemClock.elapsedRealtime() - elapsedTime);
					chrono.start();
			     
					defaultLapTextView.setText("Press lap to save elapsed time");
					running = true;
				}
			}});
		
		//-----------------------------------------------------------------
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				chrono.stop();
				running = false;
				
			}});
		
		//-----------------------------------------------------------------
		resetButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
						
				elapsedTime = 0;
				chrono.setBase(SystemClock.elapsedRealtime() - elapsedTime);
				
				if(firstRun == false){
								
					//remove all elapsed lap times
					lapTimes = (LinearLayout) findViewById(R.id.lap_time_linear_layout);
					lapTimes.removeAllViews();
					
					//redisplay the default lap message
					defaultLapTextView = new TextView(MainActivity.this);
					
					if(running == true){
						defaultLapTextView.setText("Press lap to save elapsed time");
					}
					else{
						defaultLapTextView.setText("Press Start Button to Start the Timer");
					}
					
					defaultLapTextView.setTextSize(20);
					defaultLapTextView.setTextColor(Color.WHITE);
					defaultLapTextView.setGravity(Gravity.CENTER);
					
					lapTimes.addView(defaultLapTextView);
					lapNumber  = 1;
					firstRun = true;
				}
								
			}});
		
		//--------------------------------------------------------------------
		lapButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
							
				if(running == true){
					
					defaultLapTextView.setVisibility(View.GONE);
					
					TextView toAdd = new TextView(MainActivity.this);
					toAdd.setText("Lap " + String.valueOf(lapNumber) + ": "+ chrono.getText().toString());
					toAdd.setTextSize(40);
					toAdd.setTextColor(Color.LTGRAY);
					toAdd.setGravity(Gravity.CENTER);
					
					lapTimes.addView(toAdd);
					
					// scrolls to the bottom of the ScrollView when the new TextView is added
					lapScrollView.post(new Runnable(){

						@Override
						public void run() {
							
							lapScrollView.fullScroll(ScrollView.FOCUS_DOWN);
						}});
					
					lapNumber++;
					
					firstRun = false;
				}
			}});
	}
	
	//----------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
