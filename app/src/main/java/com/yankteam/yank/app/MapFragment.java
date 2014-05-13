package com.yankteam.yank.app;

import android.app.Dialog;
import android.location.Criteria;
import android.location.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by root on 5/12/14.
 */
public class MapFragment extends Fragment {


    private GoogleMap map;
    private Location location;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (container == null)
            return null;
        /*int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }
*/
        View v = inflater.inflate(R.layout.fragment_lobby_map, container, false);

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

      /*  LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        location = locationManager.getLastKnownLocation(provider);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                drawMarker(location);
            }
        };

        locationManager.requestLocationUpdates(provider, 20000, 0, locationListener);*/

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location location) {
                map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
                        location.getLongitude())).title("You're Here"));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(location.getLatitude(), location.getLongitude())).zoom(12).build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }
}