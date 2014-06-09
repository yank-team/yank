package com.yankteam.yank.app;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.view.MenuItem;
import android.widget.Toast;
import com.yankteam.yank.app.components.Entity;
import com.yankteam.yank.app.fragments.MapFragment;
import com.yankteam.yank.app.fragments.NearbyFragment;
import com.yankteam.yank.app.fragments.SavedFragment;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class LobbyActivity extends ActionBarActivity
        implements ActionBar.TabListener {

    // some constants
    public final static String LOG_TAG              = "LobbyActivity";
    public static final int OUT_OF_SERVICE          = 0;
    public static final int TEMPORARILY_UNAVAILABLE = 1;
    public static final int AVAILABLE               = 2;

    private List<Fragment> fragments;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Boolean locationSet;
    Location oldLocation;
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(LoginActivity.LOG_TAG, "location changed" + location.toString());
            // Do work with new location. Implementation of this method will be covered later.
            doWorkWithNewLocation(location);
        }
    };

    // Location manager
    // Double my_lat, my_lng;
    AppInfo appInfo;
    LocationManager locationMgmt;

    // Fragment manager
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        appInfo = AppInfo.getInstance();

        // Retrieve location manager
        locationSet  = false;
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationMgmt = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String provider = locationMgmt.getBestProvider(criteria, true);
        oldLocation = locationMgmt.getLastKnownLocation(provider);
        storeLocation(oldLocation);
        locationMgmt.requestLocationUpdates(provider, 100, 0, locationListener);

        // set up the action bar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        Log.d(LOG_TAG,"Map created");

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
        fragments = new Vector<Fragment>();
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

    void doWorkWithNewLocation(Location location) {
        if(isBetterLocation(oldLocation, location)) {
            // If location is better, do some user preview.
            storeLocation(location);
        }

        storeLocation(oldLocation);
    }

    /**
     * Time difference threshold set for one minute.
     */
    static final int TIME_DIFFERENCE_THRESHOLD = 1 * 60 * 1000;

    /**
     * Decide if new location is better than older by following some basic criteria.
     * This algorithm can be as simple or complicated as your needs dictate it.
     * Try experimenting and get your best location strategy algorithm.
     *
     * @param oldLocation Old location used for comparison.
     * @param newLocation Newly acquired location compared to old one.
     * @return If new location is more accurate and suits your criteria more than the old one.
     */
    boolean isBetterLocation(Location oldLocation, Location newLocation) {
        // If there is no old location, of course the new location is better.
        if(oldLocation == null) {
            return true;
        }

        // Check if new location is newer in time.
        boolean isNewer = newLocation.getTime() > oldLocation.getTime();

        // Check if new location more accurate. Accuracy is radius in meters, so less is better.
        boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation.getAccuracy();
        if(isMoreAccurate && isNewer) {
            // More accurate and newer is always better.
            return true;
        } else if(isMoreAccurate && !isNewer) {
            // More accurate but not newer can lead to bad fix because of user movement.
            // Let us set a threshold for the maximum tolerance of time difference.
            long timeDifference = newLocation.getTime() - oldLocation.getTime();

            // If time difference is not greater then allowed threshold we accept it.
            if(timeDifference > -TIME_DIFFERENCE_THRESHOLD) {
                return true;
            }
        }

        return false;
    }


    public void storeLocation(Location location) {

        Log.d(LobbyActivity.LOG_TAG, "storing location " + location.toString());
        if (location != null) {
            appInfo.my_lat = location.getLatitude();
            appInfo.my_lng = location.getLongitude();
            //  updateNearbyYanks();
        } else {
            appInfo.my_lat = 0.0;
            appInfo.my_lng = 0.0;
            Log.d(LobbyActivity.LOG_TAG, "location is shitting itself");
        }
    }

    private void updateNearbyYanks(){
        // assuming 1 is the nearby list (THIS IS A HACK. WE NEED TO IMPROVE THIS)
        NearbyFragment frag = (NearbyFragment) fragments.get(1);
        frag.onRefresh();
    }

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
        moveTaskToBack(true);
    }

    private void gotoCreation() {
        Intent intent = new Intent(this, CreationActivity.class);
        startActivity(intent);
    }
}
