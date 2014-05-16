package com.yankteam.yank.app.lobbyfragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yankteam.yank.app.R;

public class MapFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    // location objects
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    // Location data -- needed to make use of location service
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int UPDATE_INTERVAL = 5000;
    private final static int FASTEST_UPDATE_INTERVAL = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (container == null){ return null; }

        int playStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (playStatus != ConnectionResult.SUCCESS) {
            int reqCode = 10;

            // Toasts are less invasive than dialogs
            Toast.makeText(getActivity(), GooglePlayServicesUtil.getErrorString(playStatus),
                    Toast.LENGTH_SHORT).show();
        }
        return inflater.inflate(R.layout.fragment_lobby_map, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        mMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.lobby_map);

        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.lobby_map, mMapFragment)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // set up map
        mMap = mMapFragment.getMap();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // set up location client
        mLocationClient  = new LocationClient(getActivity(), this, this);
        mLocationRequest = LocationRequest.create();

        // use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);

        // connect to location services
        mLocationClient.connect();

        // TODO: implement map on-change listener

    }

    @Override
    public void onConnected(Bundle data) {
        mCurrentLocation = mLocationClient.getLastLocation();
        if (mCurrentLocation == null){
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        } else {
            // Recenter the camera upon the current location
            recenterCamera();
        }
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(getActivity(), "disconnected from location services", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationClient.removeLocationUpdates(this);
        recenterCamera();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()){
            // TODO: do something relevant here
        } else {
            // TODO: do something relevant here
        }
    }

    private void recenterCamera(){
        CameraPosition cPos = new CameraPosition.Builder()
                .target(new LatLng(mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude())).zoom(12).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cPos));
    }
}