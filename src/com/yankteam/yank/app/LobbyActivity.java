package com.yankteam.yank.app;


import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by nick on 4/15/2014.
 */
public class LobbyActivity extends ActionBarActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_lobby);

    	// Set up the action bar

    	final ActionBar actionBar = getSupportActionBar();
    	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    	initializePager();

    	mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
    		@Override
    		public void onPageSelected(int position) {
    			actionBar.setSelectedNavigationItem(position);
    		}
    	});

    	for(int i=0; i<mSectionsPagerAdapter.getCount(); i++){
    		actionBar.addTab(
    				actionBar.newTab()
    				.setText(mSectionsPagerAdapter.getPageTitle(i))
    				.setTabListener(this));
    	}

    	WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
    	if (!wifi.isWifiEnabled()){
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("You need a network connection to see the map. Please turn on mobile network or Wi-Fi in Settings.")
    		.setTitle("Unable to connect")
    		.setCancelable(false)
    		.setPositiveButton("Settings",
    				new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
    				startActivity(i);
    			}
    		}).setNegativeButton("Cancel",
    						new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
    						// do nothing
    					}
    				});
    		AlertDialog alert = builder.create();
    		alert.show();
    	}
    }
    	
    private void initializePager() {

        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, MapFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, ListFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, SavedFragment.class.getName()));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager = (ViewPager) findViewById(R.id.lobby_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);

    }
    

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a fragment in the fragment container
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return this.fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MAP VIEW";
                case 1:
                    return "YANK LIST";
                case 2:
                    return "SAVED YANKS";
            }
            return null;
        }
    }

   /* public static class PlaceholderFragment extends Fragment {
        *//**
         * The fragment argument representing the section number for this
         * fragment.
         *//*
        private static final String ARG_SECTION_NUMBER = "section_number";

        *//**
         * Returns a new instance of this fragment for the given section
         * number.
         *//*
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lobby_map, container, false);
            return rootView;
        }
    }*/

}
