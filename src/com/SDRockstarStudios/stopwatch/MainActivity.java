//Copyright 2013 Sean DeBellis
//In association with SDRockstarStudios

package com.SDRockstarStudios.stopwatch;

import android.os.Bundle;
import android.os.SystemClock;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class MainActivity extends FragmentActivity {

	Chrono chrono = null;
	
	Button startButton = null;
	Button stopButton = null;
	Button resetButton = null;
	Button lapButton = null;
	
	TabHost tabs = null;
	LapPagerAdapter lapPagerAdapter = null;
	ViewPager viewPager = null;
		
	ScrollView lapFragScrollView = null;
	ScrollView elapsedFragScrollView = null;
	
	long elapsedTime = 0;
	long startPause = 0;
	long endPause = 0;
	int lapNumber = 1;
	
	boolean running = false; // tells if the timer is running
	boolean firstRun = true; // tells if any laps have been recorded
	
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
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
								
				syncFragmentScrolls();
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				tabs.setCurrentTab(arg0);
		}});// end of OnPageChangeListener
		
			
		//chrono.setText("00:00:00:00");
		
		setButtonOnClickListeners();		
	}
	
	//-----------------------------------------------------------------------
	public LapFragment getLapFragment(){
		
		LapFragment lapFrag = (LapFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.swipe_panel_pager+":0");
		return lapFrag;
	}

	//--------------------------------------------------------------------------
	public ElapsedFragment getElapsedFragment(){
		
		ElapsedFragment elapsedFrag = (ElapsedFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.swipe_panel_pager+":1");
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
					//Log.w("chrono output", chrono.getText().toString());
					String array[] = chronoText.split(":");
								      
					if (array.length == 2){ 
						elapsedTime = Integer.parseInt(array[0]) * 60 * 1000
								+ Integer.parseInt(array[1]) * 1000;
					} else if (array.length == 3) {
						elapsedTime = Integer.parseInt(array[0]) * 60 * 60 * 1000 
								+ Integer.parseInt(array[1]) * 60 * 1000
								+ Integer.parseInt(array[2]) * 1000;
					}
			      
					LapFragment lapFrag = getLapFragment();
					ElapsedFragment elapsedFrag = getElapsedFragment();
					
					running = true;
					
					if(firstRun){
						chrono.setBase(SystemClock.elapsedRealtime() - elapsedTime);
						lapFrag.resetDefaultLaps(running);
						elapsedFrag.resetDefaultView(running);
						firstRun = false;
					} else {
						endPause = SystemClock.elapsedRealtime();
						chrono.setBase(chrono.getBase() + (endPause - startPause));
					}
						
					chrono.start();					
				}
		}});
		
		//-----------------------------------------------------------------
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				chrono.stop();
				startPause = SystemClock.elapsedRealtime();
				running = false;
				
				if(firstRun){
					LapFragment lapFrag = getLapFragment();
					lapFrag.resetDefaultLaps(running);
					ElapsedFragment elapsedFrag = getElapsedFragment();
					elapsedFrag.resetDefaultView(running);
				}
			}});
		
		//-----------------------------------------------------------------
		resetButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				//reset chronometer to zero
				elapsedTime = 0;
				chrono.setBase(SystemClock.elapsedRealtime() - elapsedTime);
												
				//remove all elapsed lap times
				
				
				//redisplay the default lap message
				LapFragment lapFrag = getLapFragment();
				lapFrag.resetDefaultLaps(running);
				
				ElapsedFragment elapsedFrag = getElapsedFragment();
				elapsedFrag.resetDefaultView(running);
								
				lapNumber  = 1;
				firstRun = true;
											
			}});
		
		//--------------------------------------------------------------------
		lapButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
							
				if(running == true){
					
					String timeString = chrono.getText().toString();
					//Log.d("timeString", timeString);
					LapFragment lapFrag = getLapFragment();
					ElapsedFragment elapsedFrag = getElapsedFragment();
					
					if(firstRun){
						lapFrag.removeAllLaps();
						elapsedFrag.removeAllLaps();
					}
					
					lapFrag.addLap(timeString, lapNumber);
					elapsedFrag.addLaps(timeString, lapNumber);
										
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
