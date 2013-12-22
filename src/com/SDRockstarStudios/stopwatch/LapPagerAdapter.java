package com.SDRockstarStudios.stopwatch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class LapPagerAdapter extends FragmentPagerAdapter{

	public LapPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		
		Fragment fragment = null;
		
		if(arg0 == 0){
			fragment = new LapFragment();
		}
		else
			fragment = new ElapsedFragment();
		return fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	

	

}
