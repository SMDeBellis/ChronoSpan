package com.SDRockstarStudios.stopwatch;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ElapsedFragment extends Fragment {
	
	ScrollView scrollView = null;
	LinearLayout linearLayout = null;
	boolean isDualPane = false;
	
	private static String lastRecordedTime = "00:00:00:00"; 
	
	public ElapsedFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View layoutView = inflater.inflate(R.layout.fragment_elapsed, container, false);
		
		scrollView = (ScrollView) layoutView.findViewById(R.id.elapsed_frag_scroll_view);
		linearLayout = (LinearLayout) layoutView.findViewById(R.id.elapsed_time_linear_layout);
				
		
		
		return layoutView;
	}
	
	//------------------------------------------------------
	@SuppressLint("NewApi")
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		isDualPane = ((MainActivity)getActivity()).getIsDualPane();
		if(isDualPane == false){
			TextView startingTextView = null;
			startingTextView = new TextView(getActivity());
			startingTextView.setText("Press Start to Start the Timer");
			startingTextView.setTextColor(Color.WHITE);
			startingTextView.setTextSize(40);
			startingTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			startingTextView.setGravity(Gravity.CENTER);
			linearLayout.addView(startingTextView);
			scrollView.setBackground(getResources().getDrawable(R.drawable.rounded_view_small));
		}
		else{
			scrollView.setBackgroundColor(getResources().getColor(R.color.black));
		}
		
	}
	
	//---------------------------------------------------
	public void setScrollPosition(int xPos, int yPos){
		
		scrollView.scrollTo(xPos, yPos);
	}
	
	//-----------------------------------------------------
	public int getScrollViewXPos(){
		
		return scrollView.getScrollX();
	}
	
	//----------------------------------------------------
	public int getScrollViewYPos(){
		
		return scrollView.getScrollY();
		
	}
	//-------------------------------------------------------
	private long getTimeInMilliSec(String time){
				
		long elapsedTime = 0;
		
		String timeArray[] = time.split(":");
		
		elapsedTime = (Integer.parseInt(timeArray[0]) * 60 * 60 * 1000 
					+ Integer.parseInt(timeArray[1]) * 60 * 1000
					+ Integer.parseInt(timeArray[2]) * 1000		
					+ Integer.parseInt(timeArray[3]) * 10);
		
		return elapsedTime;
	}
	
	//-------------------------------------------------------
	private String convertMilliSecToTime(long timeInMilliSec){
		
		String timeString = null;
		
		long time = timeInMilliSec;
		long hrs = 0;
		long mins = 0;
		long secs = 0;
		long millis = 0;
		
		millis = (time/ 10) % 100;
		secs = (time / 1000) % 60;
		mins = (time / (60 * 1000)) % 60;
		hrs = (time/ (60 * 60 * 1000)) % 100;
		
		timeString = String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", hrs, mins, secs, millis);
			
		return timeString;
	}
	//-------------------------------------------------------
	
	public void addLaps(String time, int lapNumber){
		
		long startingTime = getTimeInMilliSec(time);
		long elapsedTime = getTimeInMilliSec(lastRecordedTime);
		
		String elapsedLapTimeString = convertMilliSecToTime(startingTime - elapsedTime);
		
		lastRecordedTime = time;
		
		//Log.d("time", elapsedTime);
		TextView toAdd = new TextView(getActivity());
		toAdd.setText("Lap " + String.valueOf(lapNumber) + " - " + elapsedLapTimeString);
		toAdd.setTextColor(Color.LTGRAY);
		toAdd.setTextSize(30);
		toAdd.setGravity(Gravity.CENTER);
		toAdd.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		linearLayout.addView(toAdd);
		
		scrollView.post(new Runnable(){

			@Override
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);				
			}});
	}
	
	//----------------------------------------------------
	public void removeAllLaps(){
		
		linearLayout.removeAllViews();
		lastRecordedTime = "00:00:00:00";
	}
	
	//-----------------------------------------------------
	public void resetDefaultView(boolean timerIsRunning){
		
		removeAllLaps();
		
		TextView startingTextView = (TextView) new TextView(getActivity());
		
		if(isDualPane == false){
			if(timerIsRunning){
				startingTextView.setText("Press Lap Button to Record Lap");
			}
			else
				startingTextView.setText("Press Start to Start the Timer");
		
			startingTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			startingTextView.setGravity(Gravity.CENTER);
			startingTextView.setTextColor(Color.WHITE);
			startingTextView.setTextSize(40);
		
			linearLayout.addView(startingTextView);
		}
	}
}
