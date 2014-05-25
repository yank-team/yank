package com.yankteam.yank.app;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.yankteam.yank.app.lobbyfragments.MapFragment;
import com.yankteam.yank.app.lobbyfragments.NearbyFragment;
import com.yankteam.yank.app.lobbyfragments.SavedFragment;

import java.util.List;
import java.util.Vector;

public class LobbyActivity extends ActionBarActivity implements ActionBar.TabListener {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    AppInfo appInfo;

    // Fragment manager
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // set up the action bar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);


        // set up the view pager
        initializePager();
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // Set up tabs
        ActionBar.Tab[] lobbyTabs = {
                actionBar.newTab()
                        .setIcon(R.drawable.ic_action_map)
                        .setTabListener(this),
                actionBar.newTab()
                        .setIcon(R.drawable.ic_action_place)
                        .setTabListener(this),
                actionBar.newTab()
                        .setIcon(R.drawable.ic_action_save)
                        .setTabListener(this)
        };

        // append the tabs to their adapter
        for(int i=0; i<mSectionsPagerAdapter.getCount(); i++){
            actionBar.addTab(lobbyTabs[i]);
        }

        // establish the fragment manager
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lobby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_newyank:
                gotoCreation();
                break;
            case R.id.action_settings:
                // Intent to go to settings
                break;
        }
        return true;
    }

    private void initializePager() {
        // create a list to hold fragments
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, MapFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, NearbyFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, SavedFragment.class.getName()));


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager = (ViewPager) findViewById(R.id.lobby_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    // stubs necessitated by implementing TabListener
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}

    // the pager adapter
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
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

    // Intercept the back button so we don't go back to the login screen unless the
    // user logs out on purpose
    @Override
    public void onBackPressed(){
        // nothing yet...
    }

    private void gotoCreation() {
        Intent intent = new Intent(this, CreationActivity.class);
        startActivity(intent);
    }
}
