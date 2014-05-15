package com.yankteam.yank.app;

import android.app.Dialog;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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


/**
 * Created by root on 5/12/14.
 */
public class MapFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{

	private GoogleMap map;
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private Location mCurrentLocation;
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private final static int UPDATE_INTERVAL = 5000;
	private final static int 	FASTEST_UPDATE_INTERVAL = 1000;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
		if (container == null)
			return null;
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if(status!=ConnectionResult.SUCCESS){

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
			dialog.show();
		}
		

		View v = inflater.inflate(R.layout.fragment_lobby_map, container, false);

		return v;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		map.setMyLocationEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.getUiSettings().setRotateGesturesEnabled(true);

		mLocationClient = new LocationClient(getActivity(), this, this);
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(
				LocationRequest.PRIORITY_HIGH_ACCURACY);
	
		mLocationRequest.setInterval(UPDATE_INTERVAL);

		mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);

		mLocationClient.connect();
		

		/*      map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
			// deprecated!!
            @Override
            public void onMyLocationChange(Location location) {
                map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
                        location.getLongitude())).title("You're Here"));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(location.getLatitude(), location.getLongitude())).zoom(12).build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });*/
	}

	public void onLocationChanged(Location location) {
		mLocationClient.removeLocationUpdates(this);
		map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
				location.getLongitude())).title("You're Here"));
		CameraPosition cameraPosition = new CameraPosition.Builder().target(
				new LatLng(location.getLatitude(), location.getLongitude())).zoom(12).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	

	@Override
	public void onConnected(Bundle dataBundle) {
		mCurrentLocation = mLocationClient.getLastLocation();
		if (mCurrentLocation == null)
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		
		
		// Display the connection status
		else {
			Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
			map.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(),
					mCurrentLocation.getLongitude())).title("You're Here"));
			CameraPosition cameraPosition = new CameraPosition.Builder().target(
					new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).zoom(12).build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
	}

	/*
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(getActivity(), "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						getActivity(),
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {

			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the
			 * user with the error.
			 */
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	public static class ErrorDialogFragment extends DialogFragment {
		private Dialog mDialog;
		public ErrorDialogFragment() {
			super();
			mDialog = null;  // Default constructor. Sets the dialog field to null
		}
		public void setDialog(Dialog dialog) {
			mDialog = dialog; // Set the dialog to display
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog; // Return a Dialog to the DialogFragment.
		}
	}

	private boolean showErrorDialog(int errorCode) {
		int resultCode =
				GooglePlayServicesUtil.
				isGooglePlayServicesAvailable(getActivity());
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,getActivity(),
					CONNECTION_FAILURE_RESOLUTION_REQUEST);
			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment =  new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getFragmentManager(),"Location Updates");

			} return false;
		}
	}
}
