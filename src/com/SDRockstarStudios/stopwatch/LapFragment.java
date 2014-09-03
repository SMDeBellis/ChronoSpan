package com.SDRockstarStudios.stopwatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
	
	boolean isDualPane = false;
	
	public LapFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		View layoutView = inflater.inflate(R.layout.fragment_lap_fagment, container, false);
		
		scrollView = (ScrollView) layoutView.findViewById(R.id.lap_frag_scroll_view);
		linearLayout = (LinearLayout) layoutView.findViewById(R.id.lap_frag_linear_layout);
			
				
		
		return layoutView;
	}

	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		isDualPane = ((MainActivity)getActivity()).getIsDualPane();
		Log.d("onActivityCreated", String.valueOf(isDualPane));
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

	//------------------------------------------------------------------------
	public void setScrollPosition(int xPos, int yPos){
		
		scrollView.scrollTo(xPos, yPos);	
		
	}
	
	//---------------------------------------------
	public int getScrollViewXPos(){
		
		return scrollView.getScrollX();
	}
	
	//------------------------------------------------
	public int getScrollViewYPos(){
		
		return scrollView.getScrollY();
	}
	//--------------------------------------------------------------------
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
	public void setFragmentText(String toAdd, int color, int size){
		TextView startingTextView = (TextView) new TextView(getActivity());
		startingTextView.setText(toAdd);
		startingTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		startingTextView.setGravity(Gravity.CENTER);
		startingTextView.setTextColor(color);
		startingTextView.setTextSize(size);
	}
	//------------------------------------------------------------
	public void removeAllLaps(){
		
		linearLayout.removeAllViews();
	}
	
	//------------------------------------------------------------
	public void resetDefaultLaps(boolean timerIsRunning){
	
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
