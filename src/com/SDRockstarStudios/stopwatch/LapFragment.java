package com.SDRockstarStudios.stopwatch;

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

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class LapFragment extends Fragment {

	ScrollView scrollView = null;
	LinearLayout linearLayout = null;
	
	public LapFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		View layoutView = inflater.inflate(R.layout.fragment_lap_fagment, container, false);
		
		scrollView = (ScrollView) layoutView.findViewById(R.id.lap_frag_scroll_view);
		linearLayout = (LinearLayout) layoutView.findViewById(R.id.lap_frag_linear_layout);
				
		TextView startingTextView = null;
		startingTextView = new TextView(getActivity());
		startingTextView.setText("Press Start to Start the Timer");
		startingTextView.setTextColor(Color.WHITE);
		startingTextView.setTextSize(40);
		startingTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		startingTextView.setGravity(Gravity.CENTER);
		
		linearLayout.addView(startingTextView);		
		
		return layoutView;
	}

	//------------------------------------------------------------------------
	
	public void addLap(String time, int lapNumber){
		
		TextView toAdd = new TextView(getActivity());
		toAdd.setText("Lap " + String.valueOf(lapNumber) + " - " + time);
		toAdd.setTextColor(Color.LTGRAY);
		toAdd.setTextSize(30);
		toAdd.setGravity(Gravity.CENTER);
		toAdd.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		linearLayout.addView(toAdd);
		
		// scrolls the the bottom of the scroll view when new time is added.
		scrollView.post(new Runnable(){

			@Override
			public void run() {
				
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				
			}});
	}
	
	//------------------------------------------------------------
	public void removeAllLaps(){
		
		linearLayout.removeAllViews();
	}
	
	//------------------------------------------------------------
	public void resetDefaultLaps(boolean timerIsRunning){
	
		removeAllLaps();
		
		TextView startingTextView = (TextView) new TextView(getActivity());
		
		
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
