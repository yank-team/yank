package com.yankteam.yank.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yankteam.yank.app.AppInfo;
import com.yankteam.yank.app.EntityProfileActivity;
import com.yankteam.yank.app.ORM.YankORM;
import com.yankteam.yank.app.R;
import com.yankteam.yank.app.components.Entity;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    public static final String LOG_TAG = "MapFragment";
    AppInfo appInfo = AppInfo.getInstance();
    Entity entity;
    YankORM orm;

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    // location objects
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    LocationManager lm;
    Location location;
    double latitude = 0.0;
    double longitude = 0.0;


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

        View view = inflater.inflate(R.layout.fragment_lobby_map, container, false);
        Context context = view.getContext();

        lm = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.d(MapFragment.LOG_TAG, "found lon: " + longitude + " found lat: " + latitude);
            new MapPopulateTask().execute(5000.0 , latitude, longitude);
        } else {
            longitude = 0.0;
            latitude = 0.0;
        }

        return view;
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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(MapFragment.LOG_TAG, "marker clicked: " + marker.getTitle());
                Log.d(MapFragment.LOG_TAG, "passing eid:" + marker.getSnippet());
                int eid = Integer.parseInt(marker.getSnippet());
                gotoEntityProfile(eid);
                return true;
            }
        });

        // set up location client
        mLocationClient  = new LocationClient(getActivity(), this, this);
        mLocationRequest = LocationRequest.create();

        // use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);

        // connect to location services
        mLocationClient.connect();
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
                        mCurrentLocation.getLongitude())).zoom(20).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cPos));
    }


    //need radius, lat, lon
    private class MapPopulateTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... args) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postReq = new HttpPost(getString(R.string.jheron_api) + "entity/radius/");
            HttpResponse response;

            // Set up a JSON request body
            JSONObject json = new JSONObject();
            try {
                // Prepare post data
                json.put("radius", args[0] );
                json.put("lat", args[1] );
                json.put("lng", args[2] );

                // Assemble request
                StringEntity entity = new StringEntity(json.toString());

                Log.d(MapFragment.LOG_TAG, "sending: " + json.toString());

                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                postReq.setEntity(entity);

                // Send request
                response = httpClient.execute(postReq);

                // Pull apart the request with an input stream
                if (response != null){
                    // set up Java's bullshit
                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder = new StringBuilder();

                    // Let Java do some bullshit
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    // return Java's bullshit
                    return builder.toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO: JSON exception
                // basically, quit
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                // TODO: String entity exception
            } catch (ClientProtocolException e) {
                // TODO: incorrect protocol exception
                e.printStackTrace();
            } catch (IOException e) {
                // TODO: I/O exception
                e.printStackTrace();
            }

            // Return null if failure occurs
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Log.d(MapFragment.LOG_TAG, "map json: " + s);
                JSONObject mainObject = new JSONObject(s);
                Boolean success_response = mainObject.getBoolean("success");
                String msg_response = mainObject.getString("msg");
                JSONArray data_response = mainObject.getJSONArray("data");
                if(success_response){
                    // Clear the map
                    mMap.clear();
                    for(int n = 0; n < data_response.length(); n++){
                        JSONObject object = data_response.getJSONObject(n);

                        int eid     = object.getInt("eid");
                        double lat  = object.getDouble("lat");
                        double lng  = object.getDouble("lng");
                        String name = object.getString("name");

                       Marker mark = mMap.addMarker(new MarkerOptions()
                               .position(new LatLng(lat, lng))
                               .title(name));
                       mark.setSnippet(Integer.toString(eid));
                    }
                }
            } catch (JSONException e) {
                //ask user to retry login
                e.printStackTrace();
                Log.d(MapFragment.LOG_TAG, "map failed: likely at server");
            }
        }
    }

    private void gotoEntityProfile(int eid) {
        Intent intent = new Intent(getActivity(), EntityProfileActivity.class);
        intent.putExtra("eid", eid);
        startActivity(intent);
    }

}