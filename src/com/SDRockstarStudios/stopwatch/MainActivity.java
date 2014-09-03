//Copyright 2013 Sean DeBellis
//In association with SDRockstarStudios

package com.SDRockstarStudios.stopwatch;

import android.os.Bundle;
import android.os.SystemClock;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	
	boolean isDualPane; //to differentiate between small and large screen functionality
	
	Chrono chrono = null;
	
	Button startButton = null;
	Button stopButton = null;
	Button resetButton = null;
	Button lapButton = null;
	
	TabHost tabs = null;
	LapPagerAdapter lapPagerAdapter = null;
	ViewPager viewPager = null;
	
	long elapsedTime = 0;
	long startPause = 0;
	long endPause = 0;
	int lapNumber = 1;
	
	boolean running = false; // tells if the timer is running
	boolean firstRun = true; // tells if its the first run since app start or reset
	boolean hasLap = false; // tells if any laps have been recorded 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		chrono = (Chrono) findViewById(R.id.chronometer);
		
		startButton = (Button) findViewById(R.id.chrono_start_button);
		stopButton = (Button) findViewById(R.id.chrono_stop_button);
		resetButton = (Button) findViewById(R.id.chrono_reset_button);
		lapButton = (Button) findViewById(R.id.chrono_lap_button);
		
		tabs = (TabHost) findViewById(android.R.id.tabhost);
		
		/**
		 * check to see if it is a large device
		 */
		isDualPane = tabs == null;
		
		/**
		 * small device layout
		 */
		if(isDualPane == false){
			tabs.setup(); 
			
			TabSpec lapTab = tabs.newTabSpec("0");
			lapTab.setContent(R.id.tab1);
			lapTab.setIndicator("Laps");
			tabs.addTab(lapTab);
			
			TabSpec elapsedTab = tabs.newTabSpec("1");
			elapsedTab.setContent(R.id.tab2);
			elapsedTab.setIndicator("Elapsed");
			tabs.addTab(elapsedTab);
			
			tabs.setBackgroundColor(Color.BLACK);
			tabs.setOnTabChangedListener(new OnTabChangeListener(){
	
				@Override
				public void onTabChanged(String tabId) {
					int intTabId = Integer.parseInt(tabId);
					syncFragmentScrolls();
					viewPager.setCurrentItem(intTabId);					
				}});
		
			lapPagerAdapter = new LapPagerAdapter(getSupportFragmentManager());
			
			viewPager = (ViewPager) findViewById(R.id.swipe_panel_pager);
			viewPager.setAdapter(lapPagerAdapter);
			viewPager.setOnPageChangeListener(new OnPageChangeListener(){
	
				@Override
				public void onPageScrollStateChanged(int arg0) {
					syncFragmentScrolls();
				}
	
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {}
	
				@Override
		 		public void onPageSelected(int arg0) {
					tabs.setCurrentTab(arg0);
			}});// end of OnPageChangeListener
		}
		else{
			/**
			 * Dual Pane Layout
			 * Set the beginning start message when the Activity is created
			 */
			LinearLayout linearLayout = (LinearLayout)findViewById(R.id.lap_label_layout);
			TextView tv = new TextView(this);
			tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			tv.setText("Press Start to Start the Timer");
			tv.setTextSize(getResources().getDimension(R.dimen.main_banner_text_dimension));
			tv.setTextColor(Color.WHITE);
			tv.setGravity(Gravity.CENTER);
			linearLayout.addView(tv);
		}
		setButtonOnClickListeners();		
	}
	
	//-----------------------------------------------------------------------
	public LapFragment getLapFragment(){
		
		LapFragment lapFrag = (LapFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.swipe_panel_pager+":0");
		if(lapFrag == null){
			lapFrag = (LapFragment) getSupportFragmentManager().findFragmentById(R.id.lap_fragment);
		}
		return lapFrag;
	}

	//--------------------------------------------------------------------------
	public ElapsedFragment getElapsedFragment(){
		
		ElapsedFragment elapsedFrag = (ElapsedFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.swipe_panel_pager+":1");
		if(elapsedFrag == null){
			elapsedFrag = (ElapsedFragment) getSupportFragmentManager().findFragmentById(R.id.elapsed_fragment);
		}
		return elapsedFrag;
	}
	
	//----------------------------------------------------------------------------
	private void syncFragmentScrolls(){
		
		LapFragment lapFrag = getLapFragment();
		ElapsedFragment elapsedFrag = getElapsedFragment();
		
		int currentPage = viewPager.getCurrentItem();
		int xPos;
		int yPos;
		
		if(currentPage == 0){
						
			xPos = lapFrag.getScrollViewXPos();
			yPos = lapFrag.getScrollViewYPos();
			
			elapsedFrag.setScrollPosition(xPos, yPos);
		}
		else if(currentPage == 1){
			
			xPos = elapsedFrag.getScrollViewXPos();
			yPos = elapsedFrag.getScrollViewYPos();
			
			lapFrag.setScrollPosition(xPos, yPos);					
		}
		
		
	}
	//-----------------------------------------------------------------------
	public void setButtonOnClickListeners(){
		
		//-------------------------------------------------------------------
		startButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if(running == false){
					String chronoText = chrono.getText().toString();
					String array[] = chronoText.split(":");
													      					
					elapsedTime = Integer.parseInt(array[0]) * 3600000 
							+ Integer.parseInt(array[1]) * 60000
							+ Integer.parseInt(array[2]) * 1000
					        + Integer.parseInt(array[3]) * 10;
					
					running = true;
					
					if(firstRun)
					/**
					 * Start the clock from 00:00:00:00
					 */
					{
						chrono.setBase(SystemClock.elapsedRealtime() - elapsedTime);
						firstRun = false;
					} else 
					/**
					 * Start the clock from previously stopped time
					 */
					{
						endPause = SystemClock.elapsedRealtime();
						chrono.setBase(chrono.getBase() + (endPause - startPause));
					}
					
					/**
					 * If no laps have been recorded switch message in view to its
					 * proper state:
					 * In this case it would be "press lap button to record laps"
					 */
					if(hasLap == false){
						resetLapWindows();
						if(isDualPane == true){
							LinearLayout ll = (LinearLayout) findViewById(R.id.lap_label_layout);
							TextView tv = (TextView) ll.getChildAt(0);
							tv.setText("Press Lap Button to Record Lap"); 
						}						
					}
					chrono.start();
				}
		}});
		
		//-----------------------------------------------------------------
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if(running){
					chrono.stop();
					
					/**
					 * Record the time at which the time has been stopped so 
					 * if the timer is started it can adjust for the time passed
					 * while paused
					 */
					startPause = SystemClock.elapsedRealtime();
					running = false;
					
					/**
					 * If no laps have been recorded switch message in view to its
					 * proper state:
					 * In this case it would be "press start button to start timer"
					 */
					if(hasLap == false){
						resetLapWindows();
						if(isDualPane){
							LinearLayout ll = (LinearLayout) findViewById(R.id.lap_label_layout);
							TextView tv = (TextView) ll.getChildAt(0);
							tv.setText("Press Start to Start the Timer");
						}
					}
				}
			}});
		
		//-----------------------------------------------------------------
		resetButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				/**
				 * reset milliseconds to zero and reset the chronometer to 
				 * 00:00:00:00
				 */
				elapsedTime = 0;
				chrono.setBase(SystemClock.elapsedRealtime() - elapsedTime);
												
				/**
				 * remove all lap times and redisplay the default lap messages
				 * the state of which depends on if the chronometer is still 
				 * running or if it is stopped.	
				 */
				resetLapWindows();
				if(isDualPane){
					LinearLayout ll = (LinearLayout) findViewById(R.id.lap_label_layout);
					ll.removeAllViews();
					TextView tv = new TextView(getBaseContext());
					tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					tv.setTextSize(getResources().getDimension(R.dimen.main_banner_text_dimension));
					tv.setTextColor(Color.WHITE);
					tv.setGravity(Gravity.CENTER);
					if(running){
						tv.setText("Press Lap Button to Record Lap");
					}
					else
						tv.setText("Press Start to Start the Timer");
					
					ll.addView(tv);
				}
				
				lapNumber  = 1; // reset the lap number back to 1
				firstRun = true; // reset to first run since reset
				hasLap = false; // restet to having no laps recorded
											
			}});
		
		//--------------------------------------------------------------------
		lapButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
							
				if(running == true){
					
					String timeString = chrono.getText().toString();
					LapFragment lapFrag = getLapFragment();
					ElapsedFragment elapsedFrag = getElapsedFragment();
					
					if(lapNumber == 1){
						lapFrag.removeAllLaps();
						elapsedFrag.removeAllLaps();
						if(isDualPane){
							LinearLayout ll = (LinearLayout) findViewById(R.id.lap_label_layout);
							ll.removeAllViews();
							TextView tv = null;
							for(int i=0; i < 2; ++i){
								tv = new TextView(getBaseContext());
								tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
								tv.setTextSize(getResources().getDimension(R.dimen.main_banner_text_dimension));
								tv.setTextColor(Color.WHITE);
								tv.setGravity(Gravity.CENTER);
								ll.addView(tv);
							}
							tv = (TextView)ll.getChildAt(0);
							tv.setText("Lap Time");
							tv = (TextView)ll.getChildAt(1);
							tv.setText("Elapsed Time");							
						}
					}
					
					lapFrag.addLap(timeString, lapNumber);
					elapsedFrag.addLaps(timeString, lapNumber);
										
					lapNumber++;
					hasLap = true;
				}
			}});
	}
	
	//---------------------------------------------------------------------------
	public boolean getIsDualPane(){
		return isDualPane;
	}
	//---------------------------------------------------------------------------
	public void resetLapWindows(){
		LapFragment lapFrag = getLapFragment();
		lapFrag.resetDefaultLaps(running);
		ElapsedFragment elapsedFrag = getElapsedFragment();
		elapsedFrag.resetDefaultView(running);		
	}
	
	//----------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
