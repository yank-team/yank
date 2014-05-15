package com.yankteam.yank.app;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import java.util.List;
import java.util.Vector;

public class LobbyActivity extends ActionBarActivity implements ActionBar.TabListener {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    // Fragment manager
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // set up the action bar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // set up the view pager
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

        // establish the fragment manager
        fragmentManager = getSupportFragmentManager();
    }

    private void initializePager() {
        // create a list to hold fragments
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, MapFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, SavedFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, ListFragment.class.getName()));

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
}
